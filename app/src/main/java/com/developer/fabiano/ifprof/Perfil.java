package com.developer.fabiano.ifprof;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.ImageUtil;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Aluno;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Turma;
import com.melnykov.fab.ObservableScrollView;

import java.util.ArrayList;
import java.util.List;

public class Perfil extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar tbmeuPerfil;
    private CollapsingToolbarLayout collapsingToolbar;
    private NavigationView nvPerfil;
    private FloatingActionButton FABPerfil;
    private DrawerLayout dlPerfil;
    private NestedScrollView nestedScrollView;
    private ActionBarDrawerToggle toggle;
    private Professor professorPerfil;
    private TextView txtNomePerfil;
    private TextView txtEmail;
    private TextView txtMatriculaPerfil;
    private ImageView ivPerfilCl;
    private ImageView ivNavigation;
    int count = 0;
    private Repositorio repositorio;
    private LinearLayout llPerfilTurmas;
    private LinearLayout llPerfilDisciplinas;
    private LinearLayout llSemTurma;
    private LinearLayout llSemDisciplina;
    private int screenOrientation;
    private ObservableScrollView observableScrollView;
    private com.melnykov.fab.FloatingActionButton FABLandScape;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        instance();
        try {
            repositorio = new Repositorio(this);
            professorPerfil = repositorio.getLogged();
        }catch (SQLiteException e){
            AlertsAndControl.alert(this,e.getMessage(),"Erro");
        }
        View view = AlertsAndControl.configurationNavigationView(this,dlPerfil,getLayoutInflater(), professorPerfil);
        nvPerfil.addHeaderView(view);
        toggle = new ActionBarDrawerToggle(this,dlPerfil, tbmeuPerfil,R.string.drawer_open,R.string.drawer_close);
        dlPerfil.setDrawerListener(toggle);
        toggle.syncState();

        ivNavigation = (ImageView)view.findViewById(R.id.ivHeader);
        collapsingToolbar.setTitle("Perfil");
        collapsingToolbar.setCollapsedTitleTextColor(getResources().getColor(R.color.colorAccent));
        collapsingToolbar.setExpandedTitleColor(getResources().getColor(R.color.colorAccent));
        txtNomePerfil.setText(professorPerfil.getNomeProfessor());
        txtEmail.setText(professorPerfil.getEmail());
        txtMatriculaPerfil.setText(professorPerfil.getMatricula());

        nvPerfil.setNavigationItemSelectedListener(this);
        nvPerfil.getMenu().findItem(R.id.itmMeuPerfil).setChecked(true);

        List<Disciplina> disciplinaList = repositorio.getDisciplinas(professorPerfil, "");
        List<Turma> turmaList = repositorio.getTurmas(professorPerfil, "");

        for(int i = 0;i<turmaList.size(); i++){
            llSemTurma.setVisibility(View.GONE);
            final Turma turma = turmaList.get(i);
            View view2 = getLayoutInflater().inflate(R.layout.item_see_more, null);
            TextView txtNome = (TextView)view2.findViewById(R.id.txtNome);
            TextView txtFirstLetter = (TextView)view2.findViewById(R.id.txtFistLetter);
            view2.findViewById(R.id.txtMore).setVisibility(View.GONE);

            txtNome.setText(turma.getNomeTurma());
            txtFirstLetter.setText((txtNome.getText().charAt(0) + "").toUpperCase());
            txtFirstLetter.setBackgroundResource(R.drawable.circle_second);
            view2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repositorio = new Repositorio(Perfil.this);
                    AllInfo allInfo = repositorio.getTurmaDisciplinas(professorPerfil.getId(), "and " + DataBase.TABLE_TURMA_DISCIPLINA + "." + DataBase.ID_TURMA + " == " + turma.getIdTurma());
                    allInfo.setTurma(turma);
                    allInfo.setAlunos(repositorio.getAlunos(turma.getIdTurma()));
                    repositorio.close();
                    Intent it = new Intent(Perfil.this, SeeMore.class);
                    it.putExtra("allInfo", allInfo);
                    startActivity(it);
                }
            });
            llPerfilTurmas.addView(view2);
        }
        for(int i = 0;i<disciplinaList.size(); i++){
            llSemDisciplina.setVisibility(View.GONE);
            final Disciplina disciplina = disciplinaList.get(i);
            final View view2 = getLayoutInflater().inflate(R.layout.item_see_more, null);
            TextView txtNome = (TextView)view2.findViewById(R.id.txtNome);
            TextView txtFirstLetter = (TextView)view2.findViewById(R.id.txtFistLetter);
            view2.findViewById(R.id.txtMore).setVisibility(View.GONE);
            txtNome.setText(disciplina.getNomeDisciplina());
            txtFirstLetter.setText((txtNome.getText().charAt(0) + "").toUpperCase());
            view2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repositorio = new Repositorio(Perfil.this);
                    AllInfo allInfo = repositorio.getTurmaDisciplinas(professorPerfil.getId(), "and " + DataBase.TABLE_TURMA_DISCIPLINA + "." + DataBase.ID_DISCIPLINA + " == " + disciplina.getIdDisciplina());
                    allInfo.setDisciplina(disciplina);
                    List<Aluno> alunoList = new ArrayList<>();
                    for (int j = 0; j < allInfo.getTurmas().size(); j++) {
                        alunoList.addAll(repositorio.getAlunos(allInfo.getTurmas().get(j).getIdTurma()));
                    }
                    repositorio.close();
                    allInfo.setAlunos(alunoList);
                    Intent it = new Intent(Perfil.this, SeeMore.class);
                    it.putExtra("allInfo", allInfo);
                    startActivity(it);
                }
            });
            llPerfilDisciplinas.addView(view2);
        }
        if (FABLandScape != null){
            FABLandScape.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(Perfil.this,EditePerfil.class);
                    it.putExtra("professor",professorPerfil);
                    startActivity(it);
                }
            });
        }else{
            FABPerfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(Perfil.this,EditePerfil.class);
                    it.putExtra("professor",professorPerfil);
                    startActivity(it);
                }
            });
        }
        if (getIntent().getBooleanExtra("EXIT", false)) {
            this.finish();
        }
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(!professorPerfil.getUriFoto().equals("null") && count == 0) {
            count = 1;
            Bitmap bitmap1 = ImageUtil.setPic(professorPerfil.getUriFoto(), ivNavigation.getWidth(), ivNavigation.getHeight());
            Bitmap bitmap2 = ImageUtil.setPic(professorPerfil.getUriFoto(), ivPerfilCl.getWidth(), ivPerfilCl.getHeight());
            if (bitmap1 != null && bitmap2 != null){
                ivNavigation.setImageBitmap(bitmap1);
                ivPerfilCl.setImageBitmap(bitmap2);
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.mudarFoto:
                Intent it = new Intent(this,SetPhoto.class);
                it.putExtra("image",professorPerfil.getUriFoto());
                startActivity(it);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        menuItem.setChecked(true);
        AlertsAndControl.navigationViewItemSelectedListener(this, dlPerfil,id);
        return true;
    }
    @Override
    public void onBackPressed() {
        repositorio.close();
        if (dlPerfil.isDrawerOpen(GravityCompat.START)) {
            dlPerfil.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    public void instance(){
        screenOrientation = getResources().getConfiguration().orientation;
        tbmeuPerfil = (Toolbar)findViewById(R.id.tbMeuPerfil);
        collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        dlPerfil = (DrawerLayout)findViewById(R.id.dlPerfil);
        nvPerfil = (NavigationView)findViewById(R.id.nvPerfil);
        txtMatriculaPerfil = (TextView)findViewById(R.id.txtMatriculaPerfil);
        txtNomePerfil = (TextView)findViewById(R.id.txtNomePerfil);
        ivPerfilCl = (ImageView)findViewById(R.id.ivPerfilCl);
        llPerfilDisciplinas = (LinearLayout)findViewById(R.id.llPerfilDisciplinas);
        llPerfilTurmas = (LinearLayout)findViewById(R.id.llPerfilTurmas);
        txtEmail = (TextView)findViewById(R.id.txtEmail);
        setSupportActionBar(tbmeuPerfil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        llSemDisciplina = (LinearLayout)findViewById(R.id.llSemDisciplina);
        llSemTurma = (LinearLayout)findViewById(R.id.llSemTurma);
        TextView txtSemTurma = (TextView)llSemTurma.findViewById(R.id.txtMensage);
        TextView txtSemDisciplina = (TextView)llSemDisciplina.findViewById(R.id.txtMensage);
        txtSemDisciplina.setText(getResources().getString(R.string.semDisciplina));
        txtSemTurma.setText(getResources().getString(R.string.semTurma));
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE){
            observableScrollView = (ObservableScrollView)findViewById(R.id.observableScrollView);
            FABLandScape = (com.melnykov.fab.FloatingActionButton)findViewById(R.id.FABPerfil);
            FABLandScape.attachToScrollView(observableScrollView);
        }else{
            FABPerfil = (FloatingActionButton)findViewById(R.id.FABPerfil);
        }
    }
}

