package com.developer.fabiano.ifprof;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AdapterInfo;
import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.ImageUtil;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.adapters.ScrollAwareFABBehavior;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Aluno;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Turma;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    private RelativeLayout llFabs;
    private LinearLayout llNova;
    private LinearLayout llSugeridas;
    Animation animation;
    Animation rotation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content);
        llNova = (LinearLayout)findViewById(R.id.llNova);
        llSugeridas = (LinearLayout)findViewById(R.id.llsugeridas);
        animation = AnimationUtils.loadAnimation(this, R.anim.slide);
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotation);
        llFabs = (RelativeLayout)findViewById(R.id.rlFabs);
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
            repositorio.close();

            if (getIntent().getStringExtra("sugeridas") != null){
                cdlShowTurmas.removeView(FABAddTurma);
                mList = getTurmas();
            }else{
                repositorio = new Repositorio(this);
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
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (mList.size() < 1){
            rvTurmas.setVisibility(View.GONE);
            FABAddTurma.setVisibility(View.GONE);
        }else{
            svShowTurmas.setVisibility(View.GONE);
            if (getIntent().getStringExtra("sugeridas") == null){
                FABAddTurma.setVisibility(View.VISIBLE);
            }
            AdapterInfo adapterInfo = new AdapterInfo(this,mList,AdapterInfo.SHOWTURMAS,svShowTurmas,rvTurmas,FABAddTurma);
            rvTurmas.setAdapter(adapterInfo);
        }
        FABAddTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                  if (llFabs.getVisibility() == View.GONE){
                    llFabs.setVisibility(View.VISIBLE);
                    llFabs.startAnimation(animation);
                    CoordinatorLayout.LayoutParams params =
                            (CoordinatorLayout.LayoutParams) FABAddTurma.getLayoutParams();
                    params.setBehavior(new AppBarLayout.ScrollingViewBehavior());
                    FABAddTurma.requestLayout();
                    FABAddTurma.startAnimation(rotation);
                    FABAddTurma.setImageResource(R.drawable.ic_close_white_24dp);
                }else{
                    llFabs.setVisibility(View.GONE);
                    CoordinatorLayout.LayoutParams params =
                            (CoordinatorLayout.LayoutParams) FABAddTurma.getLayoutParams();
                    params.setBehavior(new ScrollAwareFABBehavior(ShowTurmas.this));
                    FABAddTurma.requestLayout();
                    FABAddTurma.startAnimation(rotation);
                    FABAddTurma.setImageResource(R.drawable.ic_add_white_24dp);
                }
                 */
                startActivity(new Intent(ShowTurmas.this, ActivityAddTurmas.class));
                ShowTurmas.this.finish();
                llFabs.setVisibility(View.GONE);
                FABAddTurma.setImageResource(R.drawable.ic_add_white_24dp);
            }
        });
        rvTurmas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llFabs.setVisibility(View.GONE);
            }
        });
        llNova.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowTurmas.this, ActivityAddTurmas.class));
                ShowTurmas.this.finish();
                llFabs.setVisibility(View.GONE);
                FABAddTurma.setImageResource(R.drawable.ic_add_white_24dp);
            }
        });
        llSugeridas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ShowTurmas.this,ShowTurmas.class);
                it.putExtra("sugeridas","sugeridas");
                startActivity(it);
                llFabs.setVisibility(View.GONE);
                FABAddTurma.setImageResource(R.drawable.ic_add_white_24dp);
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
            Bitmap bitmap1 = ImageUtil.setPic(professorLogged.getUriFoto(), ivNavigation.getWidth(), ivNavigation.getHeight());
            if (bitmap1 != null){
                ivNavigation.setImageBitmap(bitmap1);
            }
        }
    }
    public List<AllInfo> getTurmas(){
        List<AllInfo> allInfoList = new ArrayList<>();
        String texto =  getResources().getString(R.string.turmas_sugeridas);
        String partes[] = texto.split("##");
        for (int i = 0; i < partes.length; i++) {
            AllInfo allInfo = new AllInfo();
            String turmaEaluno[] = partes[i].split("#");
            Turma turma = new Turma();
            turma.setNomeTurma(turmaEaluno[0]);
            allInfo.setTurma(turma);
            int qtdAlunos = 0;
            String alunos[] = turmaEaluno[1].split(";");
            List<Aluno> alunoList = new ArrayList<>();
            for (int j = 0; j < alunos.length ; j++) {
                Aluno aluno = new Aluno();
                String nomeEmatricula[] = alunos[j].split(",");
                aluno.setNomeAluno(nomeEmatricula[0]);
                aluno.setMatriculaAluno(nomeEmatricula[1]);
                alunoList.add(aluno);
                qtdAlunos++;
            }
            allInfo.setQtdAlunos(qtdAlunos);
            allInfo.setAlunos(alunoList);
            allInfoList.add(allInfo);
        }
        return allInfoList;
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