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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AdapterInfo;
import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.ImageUtil;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Professor;

import java.util.ArrayList;
import java.util.List;

public class ShowNotas extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Button btnCriarAvaliacao;
    private Toolbar tbShowAvaliacoes;
    private RecyclerView rvAvaliacao;
    private List<AllInfo> mList;
    private Repositorio repositorio;
    private CoordinatorLayout cdlShowAvaliacoes;
    private ScrollView svShowAvaliacoes;
    private NavigationView nvShowAvaliacoes;
    private DrawerLayout dlShowAvaliacoes;
    private Professor professorLogged;
    private ActionBarDrawerToggle toggle;
    private ImageView ivNavigation;
    private AdapterInfo adapterInfo;
    private AllInfo allInfo;
    private FloatingActionButton FAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_content);
        instance();
        TextView txtMensage = (TextView)findViewById(R.id.txtShowMensage);
        txtMensage.setText(getResources().getString(R.string.semAvaliacao));
        btnCriarAvaliacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowNotas.this, ActivityAddAvaliacoes.class));
                ShowNotas.this.finish();
            }
        });
        try {
            repositorio = new Repositorio(this);
            professorLogged = repositorio.getLogged();
            if (savedInstanceState != null){
                mList = savedInstanceState.getParcelableArrayList("mList");
            }else{
                mList = repositorio.getAllInfoAvaliacoes(professorLogged.getId(), "");
            }
            repositorio.close();
        } catch (SQLiteException e) {
            AlertsAndControl.alert(this, e.getMessage(), "Erro");
        } catch (IndexOutOfBoundsException eb) {
            AlertsAndControl.alert(this, eb.getMessage(), "Erro");
        }

        View view = AlertsAndControl.configurationNavigationView(this, dlShowAvaliacoes, getLayoutInflater(), professorLogged);
        ivNavigation = (ImageView) view.findViewById(R.id.ivHeader);
        nvShowAvaliacoes.addHeaderView(view);
        toggle = new ActionBarDrawerToggle(this, dlShowAvaliacoes, tbShowAvaliacoes, R.string.drawer_open, R.string.drawer_close);
        dlShowAvaliacoes.setDrawerListener(toggle);
        toggle.syncState();

        if (mList.size() < 1) {
            rvAvaliacao.setVisibility(View.GONE);
        } else {
            svShowAvaliacoes.setVisibility(View.GONE);
            rvAvaliacao.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rvAvaliacao.setLayoutManager(llm);

            svShowAvaliacoes.setVisibility(View.GONE);
            adapterInfo = new AdapterInfo(this, mList, AdapterInfo.SHOWNOTAS, svShowAvaliacoes, rvAvaliacao,null);
            rvAvaliacao.setAdapter(adapterInfo);
        }
        if (getIntent().getStringExtra("insert") != null) {
            if (getIntent().getStringExtra("insert").equals("ok")) {
                Snackbar.make(cdlShowAvaliacoes, "Inserido com sucesso!", Snackbar.LENGTH_LONG).show();
            } else if (getIntent().getStringExtra("insert").equals("alter")) {
                Snackbar.make(cdlShowAvaliacoes, "Alterado com sucesso!", Snackbar.LENGTH_LONG).show();
            }
        }
        if (getIntent().getStringExtra("finish") != null) {
            this.finish();
        }

        nvShowAvaliacoes.getMenu().findItem(R.id.itmNotas).setChecked(true);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();
        menuItem.setChecked(true);
        AlertsAndControl.navigationViewItemSelectedListener(this, dlShowAvaliacoes, id);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (dlShowAvaliacoes.isDrawerOpen(GravityCompat.START)) {
            dlShowAvaliacoes.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    int count = 0;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (!professorLogged.getUriFoto().equals("null") && count == 0) {
            count = 1;
            Bitmap bitmap1 = ImageUtil.setPic(Uri.parse(professorLogged.getUriFoto()), ivNavigation.getWidth(), ivNavigation.getHeight());
            if (bitmap1 !=null){
                ivNavigation.setImageBitmap(bitmap1);
            }
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("mList", (ArrayList<AllInfo>) mList);
        super.onSaveInstanceState(outState);
    }
    public void instance() {
        cdlShowAvaliacoes = (CoordinatorLayout) findViewById(R.id.cdlShow);
        svShowAvaliacoes = (ScrollView) findViewById(R.id.svShow);
        tbShowAvaliacoes = (Toolbar) findViewById(R.id.tbShow);
        setSupportActionBar(tbShowAvaliacoes);
        rvAvaliacao = (RecyclerView) findViewById(R.id.rvShow);
        btnCriarAvaliacao = (Button) findViewById(R.id.btnCriar);
        dlShowAvaliacoes = (DrawerLayout) findViewById(R.id.dlShow);
        nvShowAvaliacoes = (NavigationView) findViewById(R.id.nvShow);
        nvShowAvaliacoes.setNavigationItemSelectedListener(this);
        FAB = (FloatingActionButton)findViewById(R.id.FABAdd);
        cdlShowAvaliacoes.removeView(FAB);
    }
}
