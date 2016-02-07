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
import com.example.fabiano.ifprof.model.Professor;
import com.example.fabiano.ifprof.model.Turma;

import java.util.ArrayList;
import java.util.List;

public class ShowTurmas extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Button btnCriarTurma;
    private Toolbar tbShowTurmas;
    private RecyclerView rvTurmas;
    private List<AllInfo> mList;
    private Repositorio repositorio;
    private CoordinatorLayout cdlShowTurmas;
    private ScrollView svShowTurmas;
    private FloatingActionButton FABAddTurma;
    private ActionBarDrawerToggle toggle;
    private Professor professorLogged;
    private NavigationView nvShowTurmas;
    private DrawerLayout dlShowTurmas;
    private ImageView ivNavigation;
    private TextView txtMensage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content);
        txtMensage = (TextView)findViewById(R.id.txtShowMensage);
        txtMensage.setText(getResources().getString(R.string.semTurma));
        svShowTurmas = (ScrollView)findViewById(R.id.svShow);
        cdlShowTurmas = (CoordinatorLayout)findViewById(R.id.cdlShow);
        dlShowTurmas = (DrawerLayout)findViewById(R.id.dlShow);
        nvShowTurmas = (NavigationView)findViewById(R.id.nvShow);
        tbShowTurmas = (Toolbar)findViewById(R.id.tbShow);
        setSupportActionBar(tbShowTurmas);
        FABAddTurma = (FloatingActionButton)findViewById(R.id.FABAdd);
        btnCriarTurma = (Button)findViewById(R.id.btnCriar);
        btnCriarTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowTurmas.this, ActivityAddTurmas.class));
                ShowTurmas.this.finish();
            }
        });
        rvTurmas = (RecyclerView)findViewById(R.id.rvShow);
        rvTurmas.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvTurmas.setLayoutManager(llm);
        nvShowTurmas.setNavigationItemSelectedListener(this);

        try{
            repositorio = new Repositorio(this);
            professorLogged = repositorio.getLogged();
            View view = AlertsAndControl.configurationNavigationView(this,dlShowTurmas,getLayoutInflater(), professorLogged);
            nvShowTurmas.addHeaderView(view);
            ivNavigation = (ImageView)view.findViewById(R.id.ivHeader);
            toggle = new ActionBarDrawerToggle(this,dlShowTurmas, tbShowTurmas,R.string.drawer_open,R.string.drawer_close);
            dlShowTurmas.setDrawerListener(toggle);
            toggle.syncState();
            if (savedInstanceState != null){
                mList = savedInstanceState.getParcelableArrayList("mList");
            }else{
                List<Turma> turmaList = repositorio.getTurmas(professorLogged, "");
                mList = new ArrayList<>();
                for (int i = 0;i < turmaList.size();i++){
                    AllInfo allInfo = new AllInfo();
                    allInfo.setTurma(turmaList.get(i));
                    allInfo.setDiciplinas(repositorio.getTurmaDisciplinas(professorLogged.getId(), "and " + DataBase.NOME_TURMA + " == '" + allInfo.getTurma().getNomeTurma() + "'").getDiciplinas());
                    List<Aluno> alunoList = repositorio.getAlunos(turmaList.get(i).getIdTurma());
                    allInfo.setAlunos(alunoList);
                    allInfo.setQtdAlunos(alunoList.size());
                    mList.add(allInfo);
                }
            }
            repositorio.close();
        }catch (SQLiteException e){
            AlertsAndControl.alert(this,e.getMessage(),"Erro");
        }
        if (mList.size() < 1){
            rvTurmas.setVisibility(View.GONE);
            FABAddTurma.setVisibility(View.GONE);
        }else{
            svShowTurmas.setVisibility(View.GONE);
            FABAddTurma.setVisibility(View.VISIBLE);
            AdapterInfo adapterInfo = new AdapterInfo(this,mList,AdapterInfo.SHOWTURMAS,svShowTurmas,rvTurmas,FABAddTurma);
            rvTurmas.setAdapter(adapterInfo);
        }
        FABAddTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowTurmas.this, ActivityAddTurmas.class));
                ShowTurmas.this.finish();
            }
        });
        if (getIntent().getStringExtra("insert") != null){
            if (getIntent().getStringExtra("insert").equals("ok")){
                Snackbar.make(cdlShowTurmas, "Inserido com sucesso!", Snackbar.LENGTH_LONG).show();
            }else if(getIntent().getStringExtra("insert").equals("alter")){
                Snackbar.make(cdlShowTurmas, "Alterado com sucesso!", Snackbar.LENGTH_LONG).show();
            }
        }
        if(getIntent().getStringExtra("finish") != null){
            this.finish();
        }
        nvShowTurmas.getMenu().findItem(R.id.itmTurmas).setChecked(true);
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
    public void onBackPressed() {
        if (dlShowTurmas.isDrawerOpen(GravityCompat.START)) {
            dlShowTurmas.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mList", (ArrayList<AllInfo>) mList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        AlertsAndControl.navigationViewItemSelectedListener(this,dlShowTurmas,id);
        return true;
    }
}
