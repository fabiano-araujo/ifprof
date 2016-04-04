package com.developer.fabiano.ifprof;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AdapterAddNotas;
import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.Progress;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Aluno;
import com.developer.fabiano.ifprof.model.Nota;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Turma;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;


public class ActivityAddNotas extends AppCompatActivity {
    private RecyclerView rvAddNotas;
    private List<Aluno> mList;
    private Toolbar tbAddNotas;
    private  AdapterAddNotas adapterAddNotas;
    private TextView txtAssuntoNota;
    private AllInfo allInfo;
    private FloatingActionButton FABSendNotas;
    private  Repositorio repositorio;
    private boolean empty = false;
    private Turma turma;
    private TextView txtTurma;
    private CoordinatorLayout cdlAddNotas;
    String stringQrcode;
    boolean alunoSemNota = false;
    private MenuItem itemSave;
    private static MenuItem itemNota;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notas);

        tbAddNotas = (Toolbar)findViewById(R.id.tbAddNotas);
        txtAssuntoNota = (TextView)findViewById(R.id.txtAssuntoNota);
        rvAddNotas = (RecyclerView)findViewById(R.id.rvAddNotas);
        rvAddNotas.setHasFixedSize(true);
        FABSendNotas = (FloatingActionButton)findViewById(R.id.FABSendNotas);
        txtTurma = (TextView)findViewById(R.id.txtTurma);
        cdlAddNotas =  (CoordinatorLayout) findViewById(R.id.cdlAddNotas);
        setSupportActionBar(tbAddNotas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddNotas.setLayoutManager(llm);

        if (getIntent().getExtras().getParcelable("allInfo") != null){
            allInfo = getIntent().getExtras().getParcelable("allInfo");
            txtAssuntoNota.setText(allInfo.getAvaliacao().getAssunto());
        }
        turma = allInfo.getTurma();
        txtTurma.setText(turma.getNomeTurma());
        mList = new ArrayList<>();
        repositorio = new Repositorio(ActivityAddNotas.this);
        mList = repositorio.getAlunos(turma.getIdTurma());

        List<Nota> notaListSaved = repositorio.getNotas(allInfo.getAvaliacao().getIdAvaliacao(), turma.getIdTurma());
        if (notaListSaved.size() == 0){
            alunoSemNota = true;
        }else{
            if (notaListSaved.size() == mList.size()){
                alunoSemNota = false;
            }else{
                alunoSemNota = true;
            }
        }
        for (int i = 0; i < notaListSaved.size(); i++) {
            for (int j = 0; j < mList.size(); j++) {
                if (notaListSaved.get(i).getAluno().getIdALuno() == mList.get(j).getIdALuno()) {
                    mList.set(j, notaListSaved.get(i).getAluno());
                }
            }
        }
        if (!alunoSemNota){
            cdlAddNotas.removeView(FABSendNotas);

        }
        final LinearLayout llSemAluno = (LinearLayout)findViewById(R.id.llSemAluno);
        if (mList.size() == 0){
            rvAddNotas.setVisibility(View.GONE);
            TextView txtMensage = (TextView)llSemAluno.findViewById(R.id.txtMensage);
            txtMensage.setText(getResources().getString(R.string.semAluno));
            FABSendNotas.setVisibility(View.GONE);
        }else{
            llSemAluno.setVisibility(View.GONE);
        }
        boolean selecionarAluno = false;
        if (allInfo.getNota() != null){
            selecionarAluno = true;
            AlertsAndControl.snackBar(cdlAddNotas,"Selecione o aluno que tirou essa nota.");
            cdlAddNotas.removeView(FABSendNotas);
        }
        adapterAddNotas = new AdapterAddNotas(ActivityAddNotas.this, mList, allInfo,cdlAddNotas, FABSendNotas,rvAddNotas,selecionarAluno);
        rvAddNotas.setAdapter(adapterAddNotas);
        repositorio.close();

        FABSendNotas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSave();
            }
        });
        rvAddNotas.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (adapterAddNotas.getItemCount() == 0) {
                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbAddNotas.getLayoutParams();
                    params.setScrollFlags(0);
                } else {
                    float heightView = rvAddNotas.getChildAt(0).getHeight() * mList.size();
                    Display display = getWindowManager().getDefaultDisplay();
                    int screenHeight = display.getHeight() - tbAddNotas.getHeight();
                    if (heightView < screenHeight) {
                        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbAddNotas.getLayoutParams();
                        params.setScrollFlags(0);
                    } else {
                        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbAddNotas.getLayoutParams();
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                    }
                }
                rvAddNotas.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        if (savedInstanceState != null){
            stringQrcode = savedInstanceState.getString("qrcode");
            AlertsAndControl.tipoQrcode(this,stringQrcode);
        }
    }
    public void empty(){
        if (empty) {
            AlertDialog.Builder emptyNota = new AlertDialog.Builder(ActivityAddNotas.this);
            emptyNota.setMessage("Um ou mais item contem espaços em branco, eles serão considerados como zero. Enviar mesmo assim?")
                    .setNegativeButton("Não", null).setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    saveNotas();
                }
            }).show();
        } else {
            saveNotas();
        }
    }
    public void clickSave(){
        mList = adapterAddNotas.getItems();
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getNota().trim().equals("")) {
                empty = true;
                break;
            }
        }
        if (adapterAddNotas.newAluno()) {
            AlertDialog.Builder screen = new AlertDialog.Builder(ActivityAddNotas.this);
            screen.setMessage("Apenas os alunos novos serão salvos!").setNeutralButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    empty();
                }
            }).show();
        } else {
            empty();
        }
    }
    public void saveNotas(){
        List<Nota> notaList = new ArrayList<Nota>();
        for (int i = 0; i < mList.size();i++) {
            Nota nota = new Nota();
            nota.setAluno(mList.get(i));
            nota.setAvaliacao(allInfo.getAvaliacao());
            if(mList.get(i).getNota().trim().equals("")){
                mList.get(i).setNota("0");
                nota.setNota(0);
            }else{
                nota.setNota(Float.parseFloat(mList.get(i).getNota()));
            }
            notaList.add(nota);
        }
        try {
            Repositorio repositorio = new Repositorio(this);
            notaList = repositorio.insertNota(notaList);
            String noSaves = "";
            int saves = 0;
            for (int i = 0; i < notaList.size(); i++) {
                if (!notaList.get(i).isSave()){
                    noSaves += notaList.get(i).getAluno().getNomeAluno()+", ";
                }else{
                    saves++;
                }
            }
            if (!noSaves.equals("")){
                AlertsAndControl.alert(this, saves + " registros foram salvos .\n" + noSaves + "não foram salvos, pois eles já tem um registro dessa avaliação.", "Salvos");
                List<Nota> notas = repositorio.getNotas(allInfo.getAvaliacao().getIdAvaliacao(),turma.getIdTurma());
                mList.clear();
                for (int i = 0; i < notas.size(); i++) {
                    mList.add(notas.get(i).getAluno());
                }
                adapterAddNotas = new AdapterAddNotas(ActivityAddNotas.this, mList, allInfo, cdlAddNotas, FABSendNotas,rvAddNotas,false);
                rvAddNotas.setAdapter(adapterAddNotas);
            }else{
                AlertsAndControl.snackBar(cdlAddNotas, "Todos os registros foram salvos com sucesso!");
                mList.clear();
                List<Nota> notas = repositorio.getNotas(allInfo.getAvaliacao().getIdAvaliacao(),turma.getIdTurma());

                for (int i = 0; i < notas.size(); i++) {
                    mList.add(notas.get(i).getAluno());
                }
                adapterAddNotas = new AdapterAddNotas(ActivityAddNotas.this, mList, allInfo, cdlAddNotas, FABSendNotas,rvAddNotas,false);
                rvAddNotas.setAdapter(adapterAddNotas);
                cdlAddNotas.removeView(FABSendNotas);
                alunoSemNota = false;
                itemNota.setVisible(false);
            }
            repositorio.close();
        }catch (SQLiteException e){
            AlertsAndControl.alert(this,e.getMessage(),"erro");
        }
    }
    @Override
    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(this, ShowNotas.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notas, menu);
        itemSave = menu.findItem(R.id.itmSalvar);
        itemNota = menu.findItem(R.id.itmNotas);
        if (!alunoSemNota){
            itemNota.setVisible(false);

        }
        if(mList.size() == 0){
            itemSave.setVisible(false);
        }
        return true;
    }
    public static MenuItem getMenuItem(){
        return itemNota;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();;
                break;
            case R.id.itmLerQrCode:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.setPrompt("Posicione a camera sobre o QR code");
                integrator.initiateScan();
                break;
            case R.id.itmSalvar:
                if (!alunoSemNota){
                    repositorio = new Repositorio(this);
                    Professor professor = repositorio.getLogged();
                    repositorio.close();
                    allInfo.setAlunos(mList);
                    Progress salvarNotas = new Progress(this,allInfo,professor);
                    salvarNotas.execute();
                }else{
                    AlertsAndControl.alert(this,"Ainda há alunos sem nota!","Aviso");
                }
                break;
            case R.id.itmNotas:
                clickSave();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanResult != null) {
                stringQrcode = scanResult.getContents();
                AlertsAndControl.tipoQrcode(this, stringQrcode);
            }
        }catch (Exception e){}
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("qrcode", stringQrcode);
        super.onSaveInstanceState(outState);
    }
}
