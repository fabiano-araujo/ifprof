package com.example.fabiano.ifprof;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.fabiano.ifprof.adapters.AdapterInfo;
import com.example.fabiano.ifprof.adapters.AlertsAndControl;
import com.example.fabiano.ifprof.adapters.ImageUtil;
import com.example.fabiano.ifprof.adapters.Repositorio;
import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.AllInfo;
import com.example.fabiano.ifprof.model.Aluno;
import com.example.fabiano.ifprof.model.Disciplina;
import com.example.fabiano.ifprof.model.Professor;
import com.example.fabiano.ifprof.model.Turma;

import java.util.ArrayList;
import java.util.List;

public class ShowFaltas extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Button btnCriarDisciplina;
    private Toolbar tbShowFaltas;
    private RecyclerView rvFaltas;
    private List<AllInfo> mList;
    private Repositorio repositorio;
    private CoordinatorLayout cdlShowFaltas;
    private ScrollView svShowDisciplinas;
    private FloatingActionButton FABAddDisciplina;
    private NavigationView nvShowFaltas;
    private DrawerLayout dlShowFaltas;
    private Professor professorLogged;
    private ActionBarDrawerToggle toggle;
    private ImageView ivNavigation;
    private AdapterInfo adapterInfo;
    private  AllInfo allInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content);
        instance();
        TextView txtMensage = (TextView)findViewById(R.id.txtShowMensage);
        txtMensage.setText(getResources().getString(R.string.semDisciplina));
        btnCriarDisciplina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowFaltas.this, ActivityAddDisciplinas.class));
                ShowFaltas.this.finish();
            }
        });
        cdlShowFaltas.removeView(FABAddDisciplina);
        try{
            repositorio = new Repositorio(this);
            professorLogged = repositorio.getLogged();
            if (savedInstanceState != null){
                mList = savedInstanceState.getParcelableArrayList("mList");
            }else{
                List<Disciplina> disciplinaList = repositorio.getDisciplinas(professorLogged, "");
                mList = new ArrayList<>();
                for (int i = 0;i < disciplinaList.size();i++){
                    allInfo = new AllInfo();
                    allInfo.setDisciplina(disciplinaList.get(i));
                    List<Turma> turmaList = repositorio.getTurmaDisciplinas(professorLogged.getId(), "and " + DataBase.NOME_DISCIPLINA + " == '" + allInfo.getDisciplina().getNomeDisciplina() + "'").getTurmas();
                    allInfo.setTurmas(turmaList);

                    List<Aluno> alunoList = new ArrayList<>();
                    for (int j = 0;j < turmaList.size();j++){
                        alunoList.addAll(repositorio.getAlunos(turmaList.get(j).getIdTurma()));
                    }
                    allInfo.setAlunos(alunoList);
                    allInfo.setQtdAlunos(alunoList.size());

                    mList.add(allInfo);
                }
            }
            repositorio.close();
        }catch (SQLiteException e){
            AlertsAndControl.alert(this, e.getMessage(), "Erro");
        }

        View view = AlertsAndControl.configurationNavigationView(this,dlShowFaltas,getLayoutInflater(), professorLogged);
        ivNavigation =(ImageView) view.findViewById(R.id.ivHeader);
        nvShowFaltas.addHeaderView(view);
        toggle = new ActionBarDrawerToggle(this,dlShowFaltas, tbShowFaltas,R.string.drawer_open,R.string.drawer_close);
        dlShowFaltas.setDrawerListener(toggle);
        toggle.syncState();

        if (mList.size() < 1){
            rvFaltas.setVisibility(View.GONE);
        }else{
            svShowDisciplinas.setVisibility(View.GONE);
            rvFaltas.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rvFaltas.setLayoutManager(llm);

            svShowDisciplinas.setVisibility(View.GONE);
            adapterInfo = new AdapterInfo(this,mList,AdapterInfo.SHOWFALTAS,svShowDisciplinas,rvFaltas,null);
            rvFaltas.setAdapter(adapterInfo);
        }

        FABAddDisciplina.setVisibility(View.GONE);
        if (getIntent().getStringExtra("insert") != null){
            if (getIntent().getStringExtra("insert").equals("ok")){
                Snackbar.make(cdlShowFaltas, "Inserido com sucesso!", Snackbar.LENGTH_LONG).show();
            }else if(getIntent().getStringExtra("insert").equals("alter")){
                Snackbar.make(cdlShowFaltas, "Alterado com sucesso!", Snackbar.LENGTH_LONG).show();
            }
        }
        if(getIntent().getStringExtra("finish") != null){
            this.finish();
        }

        nvShowFaltas.getMenu().findItem(R.id.itmFaltas).setChecked(true);
        //FABAddDisciplina.attachToRecyclerView(rvFaltas);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        menuItem.setChecked(true);
        AlertsAndControl.navigationViewItemSelectedListener(this,dlShowFaltas,id);

        return true;
    }
    @Override
    public void onBackPressed() {
        if (dlShowFaltas.isDrawerOpen(GravityCompat.START)) {
            dlShowFaltas.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    int count = 0;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(!professorLogged.getUriFoto().equals("null") && count == 0) {
            count = 1;
            Bitmap bitmap1 = ImageUtil.setPic(Uri.parse(professorLogged.getUriFoto()), ivNavigation.getWidth(), ivNavigation.getHeight());
            if (bitmap1 != null){
                ivNavigation.setImageBitmap(bitmap1);
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mList", (ArrayList<AllInfo>) mList);
        super.onSaveInstanceState(outState);
    }
    public void instance(){
        cdlShowFaltas = (CoordinatorLayout)findViewById(R.id.cdlShow);
        svShowDisciplinas = (ScrollView)findViewById(R.id.svShow);
        tbShowFaltas = (Toolbar)findViewById(R.id.tbShow);
        setSupportActionBar(tbShowFaltas);
        rvFaltas = (RecyclerView)findViewById(R.id.rvShow);
        FABAddDisciplina = (FloatingActionButton)findViewById(R.id.FABAdd);
        btnCriarDisciplina = (Button)findViewById(R.id.btnCriar);
        dlShowFaltas = (DrawerLayout)findViewById(R.id.dlShow);
        nvShowFaltas = (NavigationView)findViewById(R.id.nvShow);
        nvShowFaltas.setNavigationItemSelectedListener(this);
    }
}
