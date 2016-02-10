package com.developer.fabiano.ifprof;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Avaliacao;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Prova;
import com.developer.fabiano.ifprof.model.Questao;
import com.developer.fabiano.ifprof.model.Turma;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityAddAvaliacoes extends AppCompatActivity{
    private Toolbar tbAvaliacoes;
    private EditText edtAssunto;
    private TextInputLayout  iptAssunto;
    private EditText edtValor;
    private TextInputLayout  iptValor;
    private AppCompatSpinner spnTipo;
    private AppCompatSpinner spnBimestre;
    private AppCompatSpinner spnDisciplinaAvaliacoes;
    private AppCompatSpinner spnTurmas;
    private Button btnAddAvaliacao;
    private Repositorio repositorio;
    private Professor professorLogged;
    private List<Disciplina> disciplinaList;
    private LinearLayout llAddAvaliacao;
    private Avaliacao avaliacao;
    private String and;
    private EditText edtAddData;
    private TextInputLayout iptDiaDaAvaliacao;
    private Button btnEscolherQuestoes;
    private Prova prova = new Prova();
    private Date date;
    private List<Turma> turmaList = new ArrayList<>();
    private Disciplina disciplina;
    private List<Questao> questoesSalvas = new ArrayList<>();
    private ArrayAdapter<String> adtSpnTipo;
    private ArrayAdapter<String> adtSpnBimestre;
    private ArrayAdapter<String> adtSpnDisciplina;
    private ArrayAdapter<String> adtTurmas;
    boolean callSetDados = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avaliacoes);
        instance();
        avaliacao = new Avaliacao();
        setSupportActionBar(tbAvaliacoes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        date = new Date();
        try {
            repositorio = new Repositorio(this);
            professorLogged = repositorio.getLogged();
            prova.setProfessor(professorLogged);
            if (getIntent().getExtras().getParcelable("semTurma") != null){
                avaliacao = getIntent().getExtras().getParcelable("semTurma");
                questoesSalvas = repositorio.getQuestoesDaProva(professorLogged, "and " + DataBase.ID_AVALIACAO + " == " + avaliacao.getIdAvaliacao());
                avaliacao.setQuestoes(questoesSalvas);
                callSetDados = true;
            }else if (getIntent().getExtras().getParcelable("avaliacao") != null){
                avaliacao = getIntent().getExtras().getParcelable("avaliacao");
                avaliacao.setAlter(true);
                btnAddAvaliacao.setText("Alterar");
                questoesSalvas = repositorio.getQuestoesDaProva(professorLogged, "and " + DataBase.ID_AVALIACAO + " == " + avaliacao.getIdAvaliacao());
                avaliacao.setQuestoes(questoesSalvas);
                setTextButton(avaliacao);
                callSetDados = true;
            }else{
                verQuestoes();
                if(avaliacao.isAlter()){
                    btnAddAvaliacao.setText("Alterar");
                }
            }
        }catch (SQLiteException e){
            AlertsAndControl.alert(this,e.getMessage(),"Erro");
        }catch (NullPointerException ex){
            verQuestoes();
        }
        setSpinner();
        if(disciplinaList.size() == 0){
            AlertsAndControl.noNeedData(ActivityAddAvaliacoes.this, ActivityAddDisciplinas.class, "Para adicionar uma avaliação é necessário criar uma disciplina antes!",AlertsAndControl.AVALIACOES,null);
        }
        spnTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    btnEscolherQuestoes.setVisibility(View.VISIBLE);
                    btnEscolherQuestoes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (questoesSalvas.size() > 0){
                                Intent it = new Intent(ActivityAddAvaliacoes.this, ShowQuestoes.class);
                                getAvaliacao();
                                avaliacao.setQuestoes(questoesSalvas);
                                it.putExtra("questoesSalvas", avaliacao);
                                startActivity(it);
                            }else{
                                Intent it = new Intent(ActivityAddAvaliacoes.this, ShowQuestoes.class);
                                getAvaliacao();
                                it.putExtra("escolherQuestoes", avaliacao);
                                startActivity(it);
                            }
                        }
                    });
                } else {
                    btnEscolherQuestoes.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spnDisciplinaAvaliacoes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    repositorio = new Repositorio(ActivityAddAvaliacoes.this);
                    disciplina = repositorio.getDisciplinas(professorLogged, " and " + DataBase.NOME_DISCIPLINA + " == '" + spnDisciplinaAvaliacoes.getSelectedItem().toString() + "'").get(0);
                    turmaList = repositorio.getTurmaDisciplinas(professorLogged.getId(), " and " + DataBase.TABLE_TURMA_DISCIPLINA + "." + DataBase.ID_DISCIPLINA + " == " + disciplina.getIdDisciplina()).getTurmas();
                    prova.setDisciplina(disciplina);
                    adtTurmas = new ArrayAdapter<String>(ActivityAddAvaliacoes.this,android.R.layout.simple_spinner_item);
                    adtTurmas.setDropDownViewResource(android.R.layout.simple_list_item_1);
                    if (turmaList.size() == 0){
                        AlertsAndControl.noNeedData(ActivityAddAvaliacoes.this, ActivityAddDisciplinas.class, "Para criar uma avaliação dessa disciplina é necessário adicionar uma turma a ela!",AlertsAndControl.AVALIACOES,disciplina);
                    }
                    for (int i = 0;i < turmaList.size();i++){
                        adtTurmas.add(turmaList.get(i).getNomeTurma());
                    }
                    spnTurmas.setAdapter(adtTurmas);
                    repositorio.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnAddAvaliacao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if (disciplinaList.size() == 0) {
                        AlertsAndControl.noNeedData(ActivityAddAvaliacoes.this, ActivityAddDisciplinas.class, "Para adicionar uma avaliação é necessário criar uma disciplina antes!", AlertsAndControl.AVALIACOES,null);
                    } else if (AlertsAndControl.check(iptAssunto, edtAssunto, 1, "Digite o assunto!") && AlertsAndControl.check(iptValor, edtValor, 1, "Digite o valor da avaliação!") && (AlertsAndControl.check(iptDiaDaAvaliacao, edtAddData, 1, "Digite o dia da avaliação!"))) {
                        if (spnTurmas.getCount() == 0){
                            AlertDialog.Builder semTurma = new AlertDialog.Builder(ActivityAddAvaliacoes.this);
                            semTurma.setMessage("A disciplina "+spnDisciplinaAvaliacoes.getSelectedItem().toString()+" não tem nenhuma turma, adicione e tente novamente!").setTitle("Aviso").setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(ActivityAddAvaliacoes.this,ShowAvaliacoes.class));
                                }
                            }).setPositiveButton("adicionar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent it = new Intent(ActivityAddAvaliacoes.this,ActivityAddDisciplinas.class);
                                    AllInfo info = new AllInfo();
                                    info.setDisciplina(disciplina);
                                    getAvaliacao();
                                    info.setAvaliacao(avaliacao);
                                    it.putExtra("avaliacao","avaliacao");
                                    it.putExtra("nomeDisciplina",info);
                                    startActivity(it);
                                }
                            }).show();

                        }else if (spnTipo.getSelectedItemPosition() == 0 && avaliacao.getQuestoes().size() == 0 && !avaliacao.isAlter()){
                            AlertDialog.Builder screen = new AlertDialog.Builder(ActivityAddAvaliacoes.this);
                            screen.setMessage("Você não escolheu nenhuma questão, adicionar mesmo assim?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    insert(false);
                                }
                            }).setNegativeButton("Não",null).show();
                        }else{
                            prova.setAvaliacao(avaliacao);
                            if (avaliacao.isAlter()) {
                                update();
                            } else {
                                if (spnTipo.getSelectedItemPosition() == 0){
                                    insert(true);
                                }else{
                                    insert(false);
                                }
                            }
                        }
                    } else {
                        Snackbar.make(llAddAvaliacao, "Informe todos os dados corretamente!", Snackbar.LENGTH_LONG).setAction("Ajuda", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertsAndControl.alert(ActivityAddAvaliacoes.this, "Informe os dados de cada campo corretamente, não esqueça de nenhum!", "Ajuda");
                            }
                        }).show();
                    }
                }catch (Exception e){
                    AlertsAndControl.alert(ActivityAddAvaliacoes.this,"Ocorreu um erro tente novamente!","Aviso");
                }
            }
        });
        edtAddData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateFormat ano = new SimpleDateFormat("yyyy");
                DateFormat mes = new SimpleDateFormat("MM");
                DateFormat dia = new SimpleDateFormat("dd");
                DatePickerDialog dlg = new DatePickerDialog(ActivityAddAvaliacoes.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String dia = dayOfMonth+"";
                        String mes = (monthOfYear+1)+"";
                        String ano = year+"";
                        if (dia.length() == 1){
                            dia = "0"+dia;
                        }
                        if (mes.length() == 1){
                            mes = "0"+mes;
                        }
                        edtAddData.setText(dia + "/" + mes + "/" + ano);
                    }
                }, Integer.parseInt(ano.format(date)), Integer.parseInt(mes.format(date)) - 1, Integer.parseInt(dia.format(date)));
                dlg.show();
            }
        });
        edtValor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtValor.getText().toString().trim().length() > 0) {
                    if (Float.parseFloat(edtValor.getText().toString()) > 100) {
                        edtValor.setText("100");
                        AlertsAndControl.snackBar(llAddAvaliacao, "máximo 100 pontos!");
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtAssunto.setFilters(new InputFilter[]{new InputFilter.LengthFilter(33)});
        if (callSetDados){
            setDados();
        }
    }
    public void update(){
        repositorio = new Repositorio(ActivityAddAvaliacoes.this);
        String assunto = edtAssunto.getText().toString().substring(0, 1).toUpperCase().concat(edtAssunto.getText().toString().substring(1));
        String tipo = spnTipo.getSelectedItem().toString();
        String bimestre = spnBimestre.getSelectedItem().toString();
        String data = edtAddData.getText().toString();
        String valor = edtValor.getText().toString();
        boolean equalsLists = false;
        if (spnTipo.getSelectedItemPosition() == 0){
           equalsLists = AlertsAndControl.equalsList(avaliacao.getQuestoes(),questoesSalvas);
        }
        if (!avaliacao.getAssunto().equals(assunto) || !avaliacao.getValor().equals(valor) || !avaliacao.getTipo().equals(tipo) || !avaliacao.getBimestre().equals(bimestre) || disciplina.getIdDisciplina() != avaliacao.getIdDisciplina() || !avaliacao.getData().equals(data)||!avaliacao.getTurma().getNomeTurma().equals(spnTurmas.getSelectedItem().toString())|| !equalsLists) {
            String dados = "'" + assunto + "', " + DataBase.VALOR + " = " + valor + " , " + DataBase.BIMESTRE + " = '" + bimestre + "' , " + DataBase.ID_DISCIPLINA + " = " +  disciplina.getIdDisciplina() + " , " + DataBase.DATA + " = '" + data + "'"+ " , " + DataBase.ID_TURMA + " = " + turmaList.get(spnTurmas.getSelectedItemPosition()).getIdTurma()+ "";
            repositorio.update(DataBase.TABLE_AVALIACAO, DataBase.ASSUNTO, dados, DataBase.ID_PROFESSOR, professorLogged.getId(), "and " + DataBase.ID_AVALIACAO + " == " + avaliacao.getIdAvaliacao());
            if (spnTipo.getSelectedItemPosition() == 0 && !equalsLists) {
                repositorio.delete(DataBase.TABLE_PROVA, "where " + DataBase.ID_AVALIACAO + " == " + avaliacao.getIdAvaliacao());
                prova.setAvaliacao(avaliacao);
                Intent it = new Intent(ActivityAddAvaliacoes.this, ActivityProva.class);
                it.putExtra("prova", prova);
                startActivity(it);
            }else{
                Intent it = new Intent(ActivityAddAvaliacoes.this, ShowAvaliacoes.class);
                it.putExtra("insert", "alter");
                startActivity(it);
            }
            repositorio.close();
            ActivityAddAvaliacoes.this.finish();
        } else {
            AlertsAndControl.snackBar(llAddAvaliacao, "Nada foi alterado!");
            repositorio.close();
        }
    }
    public void insert(boolean tipoProva){
        getAvaliacao();
        repositorio = new Repositorio(ActivityAddAvaliacoes.this);
        if (repositorio.insert(avaliacao)) {
            and = " and " + DataBase.ID_DISCIPLINA + " == " + avaliacao.getIdDisciplina() + " and " + DataBase.BIMESTRE + " == '" + avaliacao.getBimestre() + "'" + " and " + DataBase.ASSUNTO + " == '" + avaliacao.getAssunto() + "'";
            avaliacao = repositorio.getAValiacoes(professorLogged.getId(), and).get(0);
            //repositorio.insertTurmaAvaliacao(avaliacao);
            if(tipoProva){
                prova.getAvaliacao().setIdAvaliacao(avaliacao.getIdAvaliacao());
                if (getIntent().getStringExtra("new") == null) {
                    Intent it = new Intent(ActivityAddAvaliacoes.this, ActivityProva.class);
                    it.putExtra("prova", prova);
                    startActivity(it);
                } else {
                    AlertsAndControl.vericaClass(ActivityAddAvaliacoes.this, getIntent().getStringExtra("new"));
                }
            }else{
                startActivity(new Intent(this,ShowAvaliacoes.class));
                this.finish();
            }
            ActivityAddAvaliacoes.this.finish();
        } else {
            AlertsAndControl.snackBar(llAddAvaliacao, "você já tem uma avaliação com o assunto " + avaliacao.getAssunto() + " da disciplina " + disciplina.getNomeDisciplina());
        }
        repositorio.close();
        try {

        } catch (Exception e) {}
    }
    public void verQuestoes(){
        try {
            if (getIntent().getExtras().getParcelable("verQuestoes") != null){
                avaliacao = getIntent().getExtras().getParcelable("verQuestoes");
                setTextButton(avaliacao);
                callSetDados = true;
            }
        }catch (NullPointerException e){}
    }
    public void setTextButton(Avaliacao avaliacao){
        int qtdQuestoes = avaliacao.getQuestoes().size();
        String mensagemQuestoes;
        if (qtdQuestoes != 0){
            if (qtdQuestoes > 1){
                mensagemQuestoes = qtdQuestoes+" questões selecionadas";
            }else{
                mensagemQuestoes = qtdQuestoes+" questão selecionada";
            }
            btnEscolherQuestoes.setText(mensagemQuestoes);
        }
    }
    public void setDados(){
        edtAssunto.setText(avaliacao.getAssunto());
        edtValor.setText(avaliacao.getValor());
        edtAddData.setText(avaliacao.getData());
        for (int i = 0; i < adtSpnTipo.getCount(); i++) {
            if (adtSpnTipo.getItem(i).equals(avaliacao.getTipo())){
                spnTipo.setSelection(i);
                break;
            }
        }
        for (int i = 0; i < adtSpnBimestre.getCount(); i++) {
            if (adtSpnBimestre.getItem(i).equals(avaliacao.getBimestre())){
                spnBimestre.setSelection(i);
                break;
            }
        }
        try{
            repositorio = new Repositorio(this);
            Disciplina disciplina = repositorio.getDisciplinas(professorLogged, "and " + DataBase.ID_DISCIPLINA + " == " + avaliacao.getIdDisciplina()).get(0);
            repositorio.close();
            for (int i = 0; i < adtSpnDisciplina.getCount(); i++) {
                if (adtSpnDisciplina.getItem(i).equals(disciplina.getNomeDisciplina())){
                    spnDisciplinaAvaliacoes.setSelection(i);
                    break;
                }
            }
            for (int i = 0; i < adtTurmas.getCount(); i++) {
                if (adtTurmas.getItem(i).equals(avaliacao.getTurma().getNomeTurma())){
                    spnTurmas.setSelection(i);
                    break;
                }
            }
        }catch (Exception e){}
    }
    public void getAvaliacao(){
        if(edtAssunto.getText().toString().trim().length() > 0){
            avaliacao.setAssunto(edtAssunto.getText().toString().substring(0, 1).toUpperCase().concat(edtAssunto.getText().toString().substring(1)));
        }
        try{
            avaliacao.setTurma(turmaList.get(spnTurmas.getSelectedItemPosition()));
        }catch (Exception e){}
        avaliacao.setValor(edtValor.getText().toString());
        avaliacao.setTipo(spnTipo.getSelectedItem().toString());
        avaliacao.setBimestre(spnBimestre.getSelectedItem().toString());
        avaliacao.setData(edtAddData.getText().toString());
        DateFormat h = new SimpleDateFormat("HH");
        avaliacao.setDiaDoCadastro(edtAddData.getText().toString() + "#" + h.format(date));
        avaliacao.setIdProfessor(professorLogged.getId());
        for (int i = 0; i < disciplinaList.size(); i++) {
            if (disciplinaList.get(i).getNomeDisciplina().equals(spnDisciplinaAvaliacoes.getSelectedItem().toString())) {
                avaliacao.setIdDisciplina(disciplinaList.get(i).getIdDisciplina());
                prova.setDisciplina(disciplinaList.get(i));
            }
        }
    }
    public void setSpinner(){
        adtSpnTipo = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);

        adtSpnTipo.add("Prova");
        adtSpnTipo.add("Trabalho");
        adtSpnTipo.add("Seminário");
        adtSpnTipo.add("Outro");
        adtSpnTipo.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spnTipo.setAdapter(adtSpnTipo);

        adtSpnBimestre = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        adtSpnBimestre.setDropDownViewResource(android.R.layout.simple_list_item_1);
        adtSpnBimestre.add("1° Bimestre");
        adtSpnBimestre.add("2° Bimestre");
        adtSpnBimestre.add("3° Bimestre");
        adtSpnBimestre.add("4° Bimestre");
        spnBimestre.setAdapter(adtSpnBimestre);

        adtSpnDisciplina = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);
        adtSpnDisciplina.setDropDownViewResource(android.R.layout.simple_list_item_1);
        disciplinaList = repositorio.getDisciplinas(professorLogged, "");
        for (int i = 0;i < disciplinaList.size();i++){
            adtSpnDisciplina.add(disciplinaList.get(i).getNomeDisciplina());
        }
        spnDisciplinaAvaliacoes.setAdapter(adtSpnDisciplina);
        repositorio.close();

    }
    public void instance(){
        tbAvaliacoes = (Toolbar)findViewById(R.id.tbAvaliacoesCd);
        edtAssunto = (EditText)findViewById(R.id.edtAssunto);
        iptAssunto = (TextInputLayout)findViewById(R.id.iptAssunto);
        edtValor = (EditText)findViewById(R.id.edtValor);
        edtAssunto = (EditText)findViewById(R.id.edtAssunto);
        iptValor = (TextInputLayout)findViewById(R.id.iptValor);
        spnTipo = (AppCompatSpinner)findViewById(R.id.spnTipo);
        spnTurmas = (AppCompatSpinner)findViewById(R.id.spnTurma);
        spnBimestre= (AppCompatSpinner)findViewById(R.id.spnBimestre);
        spnDisciplinaAvaliacoes = (AppCompatSpinner)findViewById(R.id.spnDisciplinaAvaliacoes);
        btnAddAvaliacao = (Button)findViewById(R.id.btnAddAvaliacao);
        llAddAvaliacao = (LinearLayout)findViewById(R.id.llAddAvaliacao);
        edtAddData = (EditText)findViewById(R.id.edtDiaDaAvaliacao);
        iptDiaDaAvaliacao = (TextInputLayout)findViewById(R.id.iptDiaDaAvaliacao);
        btnEscolherQuestoes = (Button)findViewById(R.id.btnEscolherQuestoes);
    }
    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("new") == null){
            startActivity(new Intent(ActivityAddAvaliacoes.this,ShowAvaliacoes.class));
        }else{
            AlertsAndControl.vericaClass(ActivityAddAvaliacoes.this,getIntent().getStringExtra("new") );
        }
        repositorio.close();
        this.finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home){
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    public void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

}

