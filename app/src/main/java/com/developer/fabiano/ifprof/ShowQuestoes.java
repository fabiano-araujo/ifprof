package com.developer.fabiano.ifprof;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AdapterQuestoes;
import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.ImageUtil;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.Avaliacao;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Questao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ShowQuestoes extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnCriar;
    private Toolbar tbShow;
    private RecyclerView rvShow;
    private Repositorio repositorio;
    private CoordinatorLayout cdlShow;
    private ScrollView svShow;
    private FloatingActionButton FABAdd;
    private ActionBarDrawerToggle toggle;
    private Professor professorLogged;
    private NavigationView nvShow;
    private DrawerLayout dlShow;
    private ImageView ivNavigation;
    private boolean verQuestoes = false;
    private AdapterQuestoes adapterQuestoes;
    private Avaliacao avaliacao;
    private TextView txtMensage;
    private List<Questao> questaoList;
    private boolean questoesSalvas = false;
    HashMap<Integer,Questao> hashMapQuestoes = new HashMap();
    private String and = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content);
        txtMensage = (TextView)findViewById(R.id.txtShowMensage);
        txtMensage.setText("Você não tem nenhuma questão!");
        svShow = (ScrollView)findViewById(R.id.svShow);
        cdlShow = (CoordinatorLayout)findViewById(R.id.cdlShow);
        dlShow = (DrawerLayout)findViewById(R.id.dlShow);
        nvShow = (NavigationView)findViewById(R.id.nvShow);
        tbShow = (Toolbar)findViewById(R.id.tbShow);
        setSupportActionBar(tbShow);
        FABAdd = (FloatingActionButton)findViewById(R.id.FABAdd);
        btnCriar = (Button)findViewById(R.id.btnCriar);

        tbShow.setTitle("");
        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(ShowQuestoes.this, ActivityAddQuestao.class);
                if (verQuestoes) {
                    it.putExtra("verQuestoes", avaliacao);
                }
                startActivity(it);
                ShowQuestoes.this.finish();
            }
        });
        rvShow = (RecyclerView)findViewById(R.id.rvShow);
        rvShow.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        rvShow.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvShow.setLayoutManager(llm);
        nvShow.setNavigationItemSelectedListener(this);
        try {
            avaliacao = getIntent().getExtras().getParcelable("escolherQuestoes");
            if (avaliacao != null) {
                txtMensage.setText("Você não tem nenhuma questão dessa disciplina!");
                and = "and "+ DataBase.ID_DISCIPLINA+" = "+avaliacao.getIdDisciplina();
                verQuestoes = true;
                FABAdd.setImageResource(R.drawable.ic_check_white_24dp);
                Snackbar.make(cdlShow, "Selecione as questões!", Snackbar.LENGTH_LONG).show();
            }else{
                avaliacao = getIntent().getExtras().getParcelable("questoesSalvas");
                if (avaliacao != null) {
                    verQuestoes = true;
                    questoesSalvas = true;
                    FABAdd.setImageResource(R.drawable.ic_check_white_24dp);
                    Snackbar.make(cdlShow, "Selecione as questões!", Snackbar.LENGTH_LONG).show();
                }
            }
        }catch (NullPointerException e){
            try{
                avaliacao = getIntent().getExtras().getParcelable("questoesSalvas");
                if (avaliacao != null) {
                    questoesSalvas = true;
                    verQuestoes = true;
                    FABAdd.setImageResource(R.drawable.ic_check_white_24dp);
                    Snackbar.make(cdlShow, "Selecione as questões!", Snackbar.LENGTH_LONG).show();
                }
            }catch (NullPointerException nullE){
                nullE.printStackTrace();
            }
            e.printStackTrace();
        }
        try{
            repositorio = new Repositorio(this);
            professorLogged = repositorio.getLogged();
            repositorio.close();
            View view = AlertsAndControl.configurationNavigationView(this, dlShow, getLayoutInflater(), professorLogged);
            nvShow.addHeaderView(view);
            ivNavigation = (ImageView)view.findViewById(R.id.ivHeader);
            toggle = new ActionBarDrawerToggle(this,dlShow, tbShow,R.string.drawer_open,R.string.drawer_close);
            dlShow.setDrawerListener(toggle);
            toggle.syncState();
            try {
                repositorio = new Repositorio(ShowQuestoes.this);
                if (savedInstanceState != null){
                    try{
                        questaoList = savedInstanceState.getParcelableArrayList("mList");
                        List<Questao> listQuestoesSelecionada = savedInstanceState.getParcelableArrayList("selecionados");
                        for (int i = 0; i < listQuestoesSelecionada.size(); i++) {
                            Questao questao = listQuestoesSelecionada.get(i);
                            hashMapQuestoes.put(questao.getPosition(),questao);
                        }
                    }catch (NullPointerException e){}
                }else{
                    questaoList = repositorio.getQuestoes(professorLogged, and);
                    try{
                        if (questoesSalvas){
                            List<Questao> listQuestoesSelecionada = avaliacao.getQuestoes();
                            for (int i = 0; i < listQuestoesSelecionada.size() ; i++) {
                                for (int j = 0; j <questaoList.size() ; j++) {
                                    if(listQuestoesSelecionada.get(i).getIdQuestao() == questaoList.get(j).getIdQuestao()){
                                        questaoList.remove(j);
                                    }
                                }
                                Questao questao = listQuestoesSelecionada.get(i);
                                hashMapQuestoes.put(questao.getPosition(),questao);
                            }
                            listQuestoesSelecionada.addAll(questaoList);
                            questaoList = listQuestoesSelecionada;
                        }else{
                            List<Questao> listQuestoesSelecionada = avaliacao.getQuestoes();
                            for (int i = 0; i < listQuestoesSelecionada.size(); i++) {
                                Questao questao = listQuestoesSelecionada.get(i);
                                hashMapQuestoes.put(questao.getPosition(),questao);
                            }
                        }
                    }catch (NullPointerException e){}
                }
                if (questaoList.size() < 1){
                    rvShow.setVisibility(View.GONE);
                    FABAdd.setVisibility(View.GONE);
                }else{
                    svShow.setVisibility(View.GONE);
                    FABAdd.setVisibility(View.VISIBLE);
                    adapterQuestoes = new AdapterQuestoes(ShowQuestoes.this,questaoList,verQuestoes,hashMapQuestoes,svShow,rvShow,FABAdd);
                    rvShow.setAdapter(adapterQuestoes);
                }
                repositorio.close();
            }catch (Exception e){
                AlertsAndControl.alert(ShowQuestoes.this,e.getMessage(),"Erro");
                e.printStackTrace();
            }

        }catch (SQLiteException e){
            AlertsAndControl.alert(this,e.getMessage(),"Erro");
            e.printStackTrace();
        }
        FABAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verQuestoes){
                    HashMap hashMap = adapterQuestoes.getQuestoes();
                    Iterator itt = hashMap.values().iterator();
                    List<Questao> questoes = new ArrayList<Questao>();
                    while (itt.hasNext()){
                        questoes.add((Questao)itt.next());
                    }
                    avaliacao.setQuestoes(questoes);

                    Intent it = new Intent(ShowQuestoes.this,ActivityAddAvaliacoes.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    it.putExtra("verQuestoes", avaliacao);
                    startActivity(it);
                    ShowQuestoes.this.finish();
                }else{
                    startActivity(new Intent(ShowQuestoes.this, ActivityAddQuestao.class));
                    ShowQuestoes.this.finish();
                }
            }
        });
        if (getIntent().getStringExtra("insert") != null){
            if (getIntent().getStringExtra("insert").equals("ok")){
                Snackbar.make(cdlShow, "Inserido com sucesso!", Snackbar.LENGTH_LONG).show();
            }else if(getIntent().getStringExtra("insert").equals("alter")){
                Snackbar.make(cdlShow, "Alterado com sucesso!", Snackbar.LENGTH_LONG).show();
            }
        }
        if(getIntent().getStringExtra("finish") != null){
            this.finish();
        }
        nvShow.getMenu().findItem(R.id.itmQuestoes).setChecked(true);
        
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_historico, menu);
        MenuItem item = menu.findItem(R.id.itmCreatefile);
        if (verQuestoes){
            item.setIcon(R.drawable.ic_add_white_24dp);
        }else{
            item.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.itmCreatefile:
                Intent it = new Intent(ShowQuestoes.this, ActivityAddQuestao.class);
                if (verQuestoes){
                    it.putExtra("verQuestoes",avaliacao);
                }
                startActivity(it);
                ShowQuestoes.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onBackPressed() {
        if (dlShow.isDrawerOpen(GravityCompat.START)) {
            dlShow.closeDrawer(GravityCompat.START);

        } else {
            this.finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mList", (ArrayList<Questao>) questaoList);
        try{
            HashMap hashMap = adapterQuestoes.getQuestoes();
            Iterator itt = hashMap.values().iterator();
            List<Questao> questoes = new ArrayList<Questao>();
            while (itt.hasNext()){
                questoes.add((Questao)itt.next());
            }
            outState.putParcelableArrayList("selecionados", (ArrayList<Questao>) questoes);
        }catch (Exception e){}
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        AlertsAndControl.navigationViewItemSelectedListener(this,dlShow,id);
        return true;
    }
}
