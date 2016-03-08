package com.developer.fabiano.ifprof;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Turma;

import java.util.List;

public class RecuperarSenha extends AppCompatActivity {

    private Toolbar tbRecuperar;
    private Button btnRecuperar;
    private EditText edtMatricula;
    private EditText edtNomeTurma;
    private EditText edtNomeDisciplina;
    private EditText edtQtdTurmas;
    private EditText edtQtdisciplinas;

    private TextInputLayout iptMatricula;
    private TextInputLayout iptNomeTurma;
    private TextInputLayout iptNomeDisciplina;
    private TextInputLayout iptQtdTurmas;
    private TextInputLayout iptQtdisciplinas;
    private LinearLayout llRecuperar;
    private boolean mostrar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_senha);
        instance();
        setSupportActionBar(tbRecuperar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btnRecuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean matricula = AlertsAndControl.check(iptMatricula, edtMatricula, 1, "Digite sua matrícula!");
                boolean nomeTurma;
                boolean nomeDisciplina;
                boolean qtdDisciplinas = AlertsAndControl.check(iptQtdisciplinas, edtQtdisciplinas, 1, "Digite a quantidade das suas disciplinas!");
                boolean qtdTurmas = AlertsAndControl.check(iptQtdTurmas, edtQtdTurmas, 1, "Digite a quantidade das suas turmas!");
                try{
                    if (Integer.parseInt(edtQtdisciplinas.getText().toString()) == 0){
                        nomeDisciplina = true;
                    }else{
                        nomeDisciplina  = AlertsAndControl.check(iptNomeDisciplina, edtNomeDisciplina, 1, "Digite o nome de uma das suas disciplinas!");
                    }
                }catch (Exception e){
                    nomeDisciplina  = AlertsAndControl.check(iptNomeDisciplina, edtNomeDisciplina, 1, "Digite o nome de uma das suas disciplinas!");
                }
                try {
                    if (Integer.parseInt(edtQtdTurmas.getText().toString()) == 0){
                        nomeTurma = true;
                    }else{
                        nomeTurma = AlertsAndControl.check(iptNomeTurma, edtNomeTurma, 1, "Digite o nome de uma das suas turmas!");
                    }
                }catch (Exception e){
                    nomeTurma = AlertsAndControl.check(iptNomeTurma, edtNomeTurma, 1, "Digite o nome de uma das suas turmas!");
                }
                if (matricula && nomeDisciplina && nomeTurma && qtdDisciplinas && qtdTurmas) {
                    Repositorio repositorio = new Repositorio(RecuperarSenha.this);
                    Professor professor = repositorio.getProfessor("where " + DataBase.MATRICULA_PROFESSOR + " = " + edtMatricula.getText());
                    if (professor != null) {
                        List<Disciplina> disciplinas = repositorio.getDisciplinas(professor, "");
                        List<Turma> turmas = repositorio.getTurmas(professor, "");
                        int qtdTurma = Integer.parseInt(edtQtdTurmas.getText().toString());
                        int qtdDisciplina = Integer.parseInt(edtQtdisciplinas.getText().toString());

                        boolean disciplinaValida = false;
                        boolean turmaValida = false;
                        boolean qtdTurmasValida = false;
                        boolean qtdDisciplinaValida = false;
                        if (qtdDisciplina == disciplinas.size()){
                            qtdDisciplinaValida = true;

                        }
                        if (qtdTurma == turmas.size()){
                            qtdTurmasValida = true;
                        }
                        if (qtdDisciplina != 0) {
                            for (int i = 0; i < disciplinas.size(); i++) {
                                if(edtNomeDisciplina.getText().toString().trim().equalsIgnoreCase(disciplinas.get(i).getNomeDisciplina())){
                                    disciplinaValida = true;
                                }
                            }
                        }else{
                            disciplinaValida = true;
                        }
                        if (qtdTurma != 0) {
                            for (int i = 0; i < turmas.size(); i++) {
                                if(edtNomeTurma.getText().toString().trim().equalsIgnoreCase(turmas.get(i).getNomeTurma())){
                                    turmaValida = true;
                                }
                            }
                        }else{
                            turmaValida = true;
                        }
                        if (turmaValida && disciplinaValida && qtdDisciplinaValida && qtdTurmasValida){
                            AlertsAndControl.alert(RecuperarSenha.this,"Sua senha é "+professor.getSenha(),"Senha recuperada");
                        }else{
                            AlertsAndControl.alert(RecuperarSenha.this,"Essa matrícula tem um registro salvo, mas nem todos os dados informados conferem !","Senha não recuperada");
                        }
                    } else {
                        AlertsAndControl.snackBar(llRecuperar, "Não existe nenhum registro com essa matrícula!");
                    }
                    repositorio.close();
                } else {
                    AlertsAndControl.snackBar(llRecuperar, "Informe todas informações corretamente!");
                }
            }
        });
        edtQtdTurmas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    try {
                        int qtdTurmas = Integer.parseInt(s.toString());
                        if (qtdTurmas == 0) {
                            iptNomeTurma.setVisibility(View.GONE);
                        } else {
                            iptNomeTurma.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {

                    }
                } else {
                    iptNomeTurma.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtQtdisciplinas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0){
                    try {
                        int qtdDisciplinas = Integer.parseInt(s.toString());
                        if (qtdDisciplinas == 0){
                            iptNomeDisciplina.setVisibility(View.GONE);
                        }else{
                            iptNomeDisciplina.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){}
                }else{
                    iptNomeDisciplina.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    public void instance(){
        tbRecuperar = (Toolbar)findViewById(R.id.tbRecuperar);
        btnRecuperar = (Button)findViewById(R.id.btnRecuperar);
        iptMatricula = (TextInputLayout)findViewById(R.id.iptMatricula);
        iptNomeDisciplina = (TextInputLayout)findViewById(R.id.iptNomeDisciplina);
        iptNomeTurma = (TextInputLayout)findViewById(R.id.iptNomeTurma);
        iptQtdisciplinas = (TextInputLayout)findViewById(R.id.iptQtdisciplinas);
        iptQtdTurmas = (TextInputLayout)findViewById(R.id.iptQtdTurmas);

        edtMatricula = (EditText)findViewById(R.id.edtMatricula);
        edtNomeDisciplina = (EditText)findViewById(R.id.edtNomeDisciplina);
        edtNomeTurma = (EditText)findViewById(R.id.edtNomeTurma);
        edtQtdisciplinas = (EditText)findViewById(R.id.edtQtdDisciplinas);
        edtQtdTurmas = (EditText)findViewById(R.id.edtQtdTurmas);
        llRecuperar = (LinearLayout)findViewById(R.id.llRecuperar);
    }
}
