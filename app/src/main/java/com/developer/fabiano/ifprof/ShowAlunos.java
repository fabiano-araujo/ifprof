package com.developer.fabiano.ifprof;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AdapterMenu;
import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.ImageUtil;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Turma;

import java.util.List;
public class ShowAlunos extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private Toolbar tbShowAlunos;
    private Repositorio repositorio;
    private NavigationView nvShowAlunos;
    private DrawerLayout dlShowAlunos;
    private Professor professorLogged;
    private ActionBarDrawerToggle toggle;
    private ImageView ivNavigation;
    private TabLayout tlShow;
    private ViewPager vpShow;
    private AdapterMenu myPagerAdapterMenu;
    List<Turma> turmaList;
    private CoordinatorLayout cdlSnack;
    private LinearLayout llSemTurma;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_with_tabs);
        instance();
        try{
            repositorio = new Repositorio(this);
            professorLogged = repositorio.getLogged();
            turmaList = repositorio.getTurmas(professorLogged, "");
            repositorio.close();
        }catch (Exception e){
            AlertsAndControl.alert(this,"Ocorreu um erro!","aviso");
        }
        View view = AlertsAndControl.configurationNavigationView(this,dlShowAlunos,getLayoutInflater(), professorLogged);
        nvShowAlunos.addHeaderView(view);
        ivNavigation = (ImageView)view.findViewById(R.id.ivHeader);
        toggle = new ActionBarDrawerToggle(this,dlShowAlunos, tbShowAlunos,R.string.drawer_open,R.string.drawer_close);
        dlShowAlunos.setDrawerListener(toggle);
        toggle.syncState();
        try{
            tlShow.setTabGravity(TabLayout.MODE_SCROLLABLE);
            myPagerAdapterMenu = new AdapterMenu(getSupportFragmentManager(),tlShow.getTabCount(),0,this,turmaList);
            vpShow.setAdapter(myPagerAdapterMenu);
            if (turmaList.size() == 0){
                tlShow.setVisibility(View.GONE);
                View semTurma = getLayoutInflater().inflate(R.layout.tab_aluno, null);

                ScrollView svShow = (ScrollView)semTurma.findViewById(R.id.svShow);
                RecyclerView rvShow = (RecyclerView)semTurma.findViewById(R.id.rvShow);
                FloatingActionButton FABAdd = (FloatingActionButton)semTurma.findViewById(R.id.FABAdd);

                svShow.setVisibility(View.VISIBLE);
                rvShow.setVisibility(View.GONE);
                FABAdd.setVisibility(View.GONE);
                TextView textView = (TextView)semTurma.findViewById(R.id.txtShowMensage);
                textView.setText(getResources().getString(R.string.semAluno));
                Button btnCriar = (Button)semTurma.findViewById(R.id.btnCriar);
                btnCriar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ShowAlunos.this, ActivityAddAlunos.class));
                        ShowAlunos.this.finish();
                    }
                });
                vpShow.setVisibility(View.GONE);
                llSemTurma.addView(semTurma);
                llSemTurma.setVisibility(View.VISIBLE);
            }
        }catch (Exception e){
            AlertsAndControl.alert(this,"Ocorreu um erro!","aviso");
        }

        for (int i = 0; i < turmaList.size(); i++) {
            tlShow.addTab(tlShow.newTab().setText(turmaList.get(i).getNomeTurma()));
        }
        vpShow.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlShow));
        tlShow.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(final TabLayout.Tab tab) {
                vpShow.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(getIntent().getStringExtra("finish") != null){
            this.finish();
        }
        nvShowAlunos.getMenu().findItem(R.id.itmAlunos).setChecked(true);

    }
    int count = 0;
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if(count == 0){
            if (getIntent().getStringExtra("insert") != null){

                Turma turma = getIntent().getExtras().getParcelable("position");
                try{
                    vpShow.setCurrentItem(turma.getPosition());
                    if (getIntent().getStringExtra("insert").equals("ok")){
                        Snackbar.make(vpShow.getChildAt(turma.getPosition()).findViewById(R.id.cdlSnack), "Inserido com sucesso!", Snackbar.LENGTH_LONG).show();
                    }else if(getIntent().getStringExtra("insert").equals("alter")){
                        Snackbar.make(vpShow.getChildAt(turma.getPosition()).findViewById(R.id.cdlSnack), "Alterado com sucesso!", Snackbar.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    vpShow.setCurrentItem(0);
                    if (getIntent().getStringExtra("insert").equals("ok")){
                        Snackbar.make(vpShow.getChildAt(0).findViewById(R.id.cdlSnack), "Inserido com sucesso!", Snackbar.LENGTH_LONG).show();
                    }else if(getIntent().getStringExtra("insert").equals("alter")){
                        Snackbar.make(vpShow.getChildAt(0).findViewById(R.id.cdlSnack), "Alterado com sucesso!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
            if(!professorLogged.getUriFoto().equals("null")) {
                Bitmap bitmap1 = ImageUtil.setPic(professorLogged.getUriFoto(), ivNavigation.getWidth(), ivNavigation.getHeight());
                if(bitmap1 != null){
                    ivNavigation.setImageBitmap(bitmap1);
                }
            }
            count++;
        }
    }

    public void onBackPressed() {
        if (dlShowAlunos.isDrawerOpen(GravityCompat.START)) {
            dlShowAlunos.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        AlertsAndControl.navigationViewItemSelectedListener(this,dlShowAlunos,id);
        return true;
    }
    public void instance(){
        tbShowAlunos = (Toolbar)findViewById(R.id.tbShow);
        setSupportActionBar(tbShowAlunos);
        dlShowAlunos = (DrawerLayout)findViewById(R.id.dlShow);
        nvShowAlunos = (NavigationView)findViewById(R.id.nvShow);
        nvShowAlunos.setNavigationItemSelectedListener(this);
        tlShow = (TabLayout)findViewById(R.id.tlShow);
        vpShow = (ViewPager)findViewById(R.id.vpShow);
        llSemTurma = (LinearLayout)findViewById(R.id.llSemTurma);
        cdlSnack = (CoordinatorLayout)findViewById(R.id.cdlSnack);
    }
}
