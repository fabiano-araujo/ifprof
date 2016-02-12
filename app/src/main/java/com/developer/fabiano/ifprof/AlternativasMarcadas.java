package com.developer.fabiano.ifprof;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Nota;
import com.developer.fabiano.ifprof.model.Prova;
import com.developer.fabiano.ifprof.model.Questao;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AlternativasMarcadas extends AppCompatActivity {
    private TextView txtTitle;
    private Repositorio repositorio;
    private FrameLayout flMais;
    private TextView txtAssunto;
    private TextView txtBimestre;
    private Toolbar tbAlternativas;
    private FloatingActionButton FABCorrigir;
    private ObservableScrollView svAlternativas;
    private LinearLayout llQuestoes;
    Prova provaSaved = null;
    List<RadioGroup> radioGroupList = new ArrayList<>();
    String mensage;
    AllInfo allInfo;
    List<View> viewList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternativas_marcadas);
        txtTitle = (TextView)findViewById(R.id.txtTitle);
        flMais = (FrameLayout)findViewById(R.id.flMais);
        txtAssunto = (TextView)findViewById(R.id.txtAssunto);
        txtBimestre = (TextView)findViewById(R.id.txtBimestre);
        tbAlternativas = (Toolbar)findViewById(R.id.tbAlternativas);
        FABCorrigir = (FloatingActionButton)findViewById(R.id.FABCorrigir);
        svAlternativas = (ObservableScrollView)findViewById(R.id.svAlternativas);
        llQuestoes = (LinearLayout)findViewById(R.id.llQuestoes);
        setSupportActionBar(tbAlternativas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        try{
            final Prova prova = getIntent().getExtras().getParcelable("prova");
            txtTitle.setText("Prova de "+prova.getDisciplina().getNomeDisciplina()+" para a turma "+prova.getAvaliacao().getTurma().getNomeTurma());
            repositorio = new Repositorio(this);
            try{
                provaSaved =  repositorio.getProva(prova.getAvaliacao(), "and " + DataBase.CODE + " = '" + prova.getCode()+"'");
            }catch (IndexOutOfBoundsException e){
                provaSaved = null;
            }
            repositorio.close();
            if (provaSaved == null){
                flMais.setVisibility(View.GONE);
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage("Essa prova não é sua, corrigir mesmo assim?").setPositiveButton("sim",null).setNegativeButton("não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlternativasMarcadas.this.finish();
                    }
                }).setTitle("Aviso").show();
            }
            if (provaSaved != null){
                txtBimestre.setText(provaSaved.getAvaliacao().getBimestre());
                txtAssunto.setText("Assunto: " + provaSaved.getAvaliacao().getAssunto());
            }

            for (int i = 0; i < prova.getAvaliacao().getQuestoes().size(); i++) {
                View view = getLayoutInflater().inflate(R.layout.item_alternativas_marcadas,null);
                RadioButton rbA = (RadioButton)view.findViewById(R.id.rbA);
                RadioButton rbB = (RadioButton)view.findViewById(R.id.rbB);
                RadioButton rbC = (RadioButton)view.findViewById(R.id.rbC);
                RadioButton rbD = (RadioButton)view.findViewById(R.id.rbD);
                RadioButton rbE = (RadioButton)view.findViewById(R.id.rbE);
                RadioGroup rgAlternativas = (RadioGroup)view.findViewById(R.id.rgAlternativas);
                TextView txtPergunta = (TextView)view.findViewById(R.id.txtPergunta);
                TextView txtValor = (TextView)view.findViewById(R.id.txtValor);

                Questao questao;
                if (provaSaved != null){
                    questao = provaSaved.getAvaliacao().getQuestoes().get(i);
                    String number = i+1+"";
                    if (Integer.parseInt(number) < 10){
                        number = "0"+number;
                    }
                    txtPergunta.setText(number+". "+questao.getPergunta());
                    rbA.setText(" a) " + questao.getAlternativaA());
                    rbB.setText(" b) "+questao.getAlternativaB());
                    rbC.setText(" c) "+questao.getAlternativaC());
                    rbD.setText(" d) "+questao.getAlternativaD());
                    rbE.setText(" e) "+questao.getAlternativaE());
                }else{
                    questao = prova.getAvaliacao().getQuestoes().get(i);
                }

                if (questao.getAlternativaCorreta().equals("a")||questao.getAlternativaCorreta().equals(" a)")){
                    rbA.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else if (questao.getAlternativaCorreta().equals("b")||questao.getAlternativaCorreta().equals(" b)")){
                    rbB.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else if (questao.getAlternativaCorreta().equals("c")||questao.getAlternativaCorreta().equals(" c)")){
                    rbC.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else if (questao.getAlternativaCorreta().equals("d")||questao.getAlternativaCorreta().equals(" d)")){
                    rbD.setTextColor(getResources().getColor(R.color.colorPrimary));
                }else if (questao.getAlternativaCorreta().equals("e")||questao.getAlternativaCorreta().equals(" e)")){
                    rbE.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                radioGroupList.add(rgAlternativas);
                viewList.add(view);
                llQuestoes.addView(view);
            }
            try {
                FABCorrigir.attachToScrollView(svAlternativas);
            }catch (Exception e){
                e.printStackTrace();
            }
            svAlternativas.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    View view = getLayoutInflater().inflate(R.layout.item_alternativas_marcadas,null);
                    float heightView = view.getHeight() * prova.getAvaliacao().getQuestoes().size();
                    Display display = getWindowManager().getDefaultDisplay();
                    int screenHeight = display.getHeight() - tbAlternativas.getHeight();
                    if (heightView < screenHeight) {
                        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbAlternativas.getLayoutParams();
                        params.setScrollFlags(0);
                    } else {
                        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbAlternativas.getLayoutParams();
                        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                    }
                    svAlternativas.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
            FABCorrigir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    float somaDaNota = 0;
                    for (int i = 0; i < radioGroupList.size(); i++) {
                        Questao questao = prova.getAvaliacao().getQuestoes().get(i);
                        View view = viewList.get(i);
                        RadioButton radioButton = (RadioButton) view.findViewById(radioGroupList.get(i).getCheckedRadioButtonId());
                        String marcada = radioButton.getText().toString().charAt(1) + "";
                        if (marcada.equals(questao.getAlternativaCorreta())) {
                            somaDaNota += Float.parseFloat(questao.getValor());
                        }
                    }
                    somaDaNota = (float) AlertsAndControl.round(somaDaNota, 1);
                    if (provaSaved != null) {
                        repositorio = new Repositorio(AlternativasMarcadas.this);
                        allInfo = repositorio.getAllInfoAvaliacoes(provaSaved.getProfessor().getId(), "and " + DataBase.ID_AVALIACAO + " = " + provaSaved.getAvaliacao().getIdAvaliacao()).get(0);
                        repositorio.close();
                        Nota nota = new Nota();
                        nota.setAvaliacao(provaSaved.getAvaliacao());
                        nota.setNota(somaDaNota);
                        allInfo.setNota(nota);
                        AlertDialog.Builder alert = new AlertDialog.Builder(AlternativasMarcadas.this);
                        mensage = "Esse aluno tirou " + somaDaNota + ". Salvar nota?";
                        alert();

                    }else{
                        AlertsAndControl.alert(AlternativasMarcadas.this,"Esse aluno tirou " + somaDaNota + ".","Nota");
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        if (savedInstanceState != null){
            radioGroupList = (List<RadioGroup>) savedInstanceState.getSerializable("rg");
            mensage = savedInstanceState.getString("mensage");
            if (mensage != null){
                alert();
            }
        }
    }
    public void alert(){
        AlertDialog.Builder alert = new AlertDialog.Builder(AlternativasMarcadas.this);
        alert.setMessage(mensage).setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(AlternativasMarcadas.this,ActivityAddNotas.class);
                it.putExtra("allInfo", allInfo);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(it);
                AlternativasMarcadas.this.finish();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlternativasMarcadas.this.finish();
            }
        }).setTitle("Nota").show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("rg", (Serializable) radioGroupList);
        outState.putString("mensage",mensage);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
