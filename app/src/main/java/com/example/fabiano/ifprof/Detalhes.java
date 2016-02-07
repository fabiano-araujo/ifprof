package com.example.fabiano.ifprof;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fabiano.ifprof.adapters.AlertsAndControl;
import com.example.fabiano.ifprof.adapters.Progress;
import com.example.fabiano.ifprof.adapters.Repositorio;
import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.AllInfo;
import com.example.fabiano.ifprof.model.Avaliacao;
import com.example.fabiano.ifprof.model.Professor;
import com.example.fabiano.ifprof.model.Prova;
import com.example.fabiano.ifprof.model.Turma;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Detalhes extends AppCompatActivity {
    private TextView txtAssunto;
    private Toolbar tbDetalhes;
    private TextView txtTipo;
    private TextView txtValor;
    private TextView txtBimestre;
    private TextView txtDisciplina;
    private TextView txtData;
    private Avaliacao avaliacao;
    private LinearLayout llTurmasDetalhes;
    private AllInfo allInfo;
    private Button btnEditar;
    private Button btnEnviar;
    private Prova prova;
    String file;
    String title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);
        tbDetalhes = (Toolbar)findViewById(R.id.tbDetalhes);
        setSupportActionBar(tbDetalhes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnEditar = (Button)findViewById(R.id.btnEditar);
        txtAssunto = (TextView)findViewById(R.id.txtAssuntoDt);
        txtValor = (TextView)findViewById(R.id.txtValor);
        txtTipo = (TextView)findViewById(R.id.txtTipo);
        txtData = (TextView)findViewById(R.id.txtData);
        txtBimestre = (TextView)findViewById(R.id.txtBimestre);
        txtDisciplina = (TextView)findViewById(R.id.txtDisciplina);
        llTurmasDetalhes = (LinearLayout)findViewById(R.id.llTurmasDetalhes);
        btnEnviar = (Button)findViewById(R.id.btnEnviar);

        allInfo = getIntent().getExtras().getParcelable("allInfo");

        avaliacao = allInfo.getAvaliacao();
        txtAssunto.setText(avaliacao.getAssunto());
        txtDisciplina.setText(allInfo.getDisciplina().getNomeDisciplina());
        txtBimestre.setText(avaliacao.getBimestre());
        txtValor.setText(avaliacao.getValor()+"");
        txtData.setText(avaliacao.getData());
        txtTipo.setText(avaliacao.getTipo());
        final List<Turma> turmaList = new ArrayList<>();
        turmaList.add(allInfo.getTurma());
        for(int i = 0;i<turmaList.size(); i++){
            final Turma turma = turmaList.get(i);
            View view2 = getLayoutInflater().inflate(R.layout.item_see_more, null);
            TextView txtNome = (TextView)view2.findViewById(R.id.txtNome);
            TextView txtFirstLetter = (TextView)view2.findViewById(R.id.txtFistLetter);
            view2.findViewById(R.id.txtMore).setVisibility(View.GONE);
            view2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Repositorio repositorio = new Repositorio(Detalhes.this);
                    AllInfo allInfo = repositorio.getTurmaDisciplinas(repositorio.getLogged().getId(), "and " + DataBase.TABLE_TURMA_DISCIPLINA + "." + DataBase.ID_TURMA + " == " + turma.getIdTurma());
                    allInfo.setTurma(turma);
                    allInfo.setAlunos(repositorio.getAlunos(turma.getIdTurma()));
                    repositorio.close();
                    Intent it = new Intent(Detalhes.this, SeeMore.class);
                    it.putExtra("allInfo", allInfo);
                    startActivity(it);
                }
            });
            txtNome.setText(turma.getNomeTurma());
            txtFirstLetter.setText((txtNome.getText().charAt(0) + "").toUpperCase());
            txtFirstLetter.setBackgroundResource(R.drawable.circle_second);
            llTurmasDetalhes.addView(view2);
        }
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(Detalhes.this,ActivityAddAvaliacoes.class);
                it.putExtra("avaliacao",allInfo.getAvaliacao());
                startActivity(it);
            }
        });
        try{
            Repositorio repositorio = new Repositorio(Detalhes.this);
            prova = new Prova();
            prova.setAvaliacao(avaliacao);
            prova = repositorio.getProva(avaliacao,"");
            prova.setDisciplina(allInfo.getDisciplina());
            avaliacao.setTurma(allInfo.getTurma());
            repositorio.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if (!avaliacao.getTipo().equalsIgnoreCase("prova") || avaliacao.getQuestoes().size() == 0){
            btnEnviar.setVisibility(View.GONE);
        }
        title = "Prova de " + prova.getDisciplina().getNomeDisciplina() + "-" + prova.getAvaliacao().getAssunto() + " para a turma " + prova.getAvaliacao().getTurma().getNomeTurma();
        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    file = Environment.getExternalStorageDirectory().getPath()+"/Ifprof/"+prova.getProfessor().getNomeProfessor()+"/"+prova.getDisciplina().getNomeDisciplina()+"/provas/"+prova.getAvaliacao().getAssunto()+"-"+prova.getAvaliacao().getTurma().getNomeTurma()+".pdf";
                    File provaFile =  new File(file);
                    if (provaFile.exists()){
                        avisoSendEmail();
                    }else{
                        Progress criarProva = new Progress(Detalhes.this,prova,0);
                        criarProva.execute();
                    }
                }catch (Exception e){
                    AlertsAndControl.alert(Detalhes.this,"Desculpe ocorreu um erro, tente novamente!","Aviso");
                    e.printStackTrace();
                }
            }
        });
    }
    public void avisoSendEmail(){
        AlertDialog.Builder save = new AlertDialog.Builder(this);
        save.setCancelable(false);
        save.setMessage("Arquivo salvo em "+file+". Enviar para o seu email?").setNegativeButton("Não", null).setPositiveButton("enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertsAndControl.sendEmail(file, Detalhes.this,prova.getProfessor().getEmail(),title);
            }
        }).setNeutralButton("Outro email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                outroEmail(false, file);
            }
        }).setTitle("Arquivo salvo").show();
    }
    public void outroEmail(boolean setErro,final String saveAt){
        try {
            final AlertDialog.Builder outroEmail = new AlertDialog.Builder(this);

            final View view = getLayoutInflater().inflate(R.layout.alert_another_email, null);
            final EditText edtOutroEmail = (EditText) view.findViewById(R.id.edtOutroEmail);
            final TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.iptOutroEmail);
            if (setErro){
                textInputLayout.setError("Email inválido!");
                edtOutroEmail.requestFocus();
            }
            outroEmail.setView(view).setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (AlertsAndControl.isValidEmailAddress(edtOutroEmail, textInputLayout)) {
                        AlertsAndControl.sendEmail(file, Detalhes.this, prova.getProfessor().getEmail(), title);
                    } else {
                        outroEmail(true,saveAt);
                    }
                }
            }).setNegativeButton("Cancelar", null).setTitle("Outro email").show();
        }catch (Exception e){
            AlertsAndControl.alert(this, "Desculpe ocorreu um erro, tente novamente!", "Aviso");
            e.printStackTrace();
        }
    }
}
