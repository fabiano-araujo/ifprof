package com.example.fabiano.ifprof;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.fabiano.ifprof.adapters.AlertsAndControl;
import com.example.fabiano.ifprof.adapters.Repositorio;
import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.Aluno;
import com.example.fabiano.ifprof.model.Professor;
import com.example.fabiano.ifprof.model.Turma;

import java.util.List;

public class ActivityAddAlunos extends AppCompatActivity {
    private NavigationView navigationView;
    private ImageView ivHeader;
    private Toolbar tbAlunoCd;
    private Button btnAddAluno;
    private EditText edtNomeAluno;
    private EditText edtMatriculaAluno;
    private TextInputLayout iptMatriculaAluno;
    private TextInputLayout iptNomeAluno;
    private Repositorio repositorio;
    private Aluno aluno;
    private AppCompatSpinner spnTurmaAluno;
    private ArrayAdapter<String> adtListTurma;
    private Professor professorLogged;
    private LinearLayout llAddAluno;
    private boolean alter = false;
    private Aluno alunoAlter;
    private Turma turma;
    int positionSpn = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alunos);
        tbAlunoCd = (Toolbar)findViewById(R.id.tbAlunoCd);
        setSupportActionBar(tbAlunoCd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        llAddAluno = (LinearLayout)findViewById(R.id.llAddAluno);
        spnTurmaAluno = (AppCompatSpinner)findViewById(R.id.spnTurmaAluno);
        btnAddAluno = (Button)findViewById(R.id.btnAddAluno);
        edtMatriculaAluno = (EditText)findViewById(R.id.edtMatriculaAluno);
        edtNomeAluno = (EditText)findViewById(R.id.edtNomeAluno);
        iptMatriculaAluno = (TextInputLayout)findViewById(R.id.iptMatriculaAluno);
        iptNomeAluno = (TextInputLayout)findViewById(R.id.iptNomeAluno);
        try{
            repositorio = new Repositorio(this);
            if (getIntent().getExtras().getParcelable("alter") != null){
                alter = true;
                alunoAlter  = getIntent().getExtras().getParcelable("alter");
                edtNomeAluno.setText(alunoAlter.getNomeAluno());
                edtMatriculaAluno.setText(alunoAlter.getMatriculaAluno());
                btnAddAluno.setText("Alterar");
            }
            turma = getIntent().getExtras().getParcelable("position");
        }catch (SQLiteException e){
            AlertsAndControl.alert(this,e.getMessage(),"Erro");
        }catch (NullPointerException npeNotAlter){

        }
        professorLogged = repositorio.getLogged();
        List<Turma> turmaList = repositorio.getTurmas(professorLogged, "");
        adtListTurma = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);

        for (int i = 0;i < turmaList.size();i++){
            adtListTurma.add(turmaList.get(i).getNomeTurma());
            if (turma != null){
                if (turmaList.get(i).getIdTurma() == turma.getIdTurma()){
                    positionSpn = i;
                }
            }else{
                if(alter && alunoAlter.getIdTurma() == turmaList.get(i).getIdTurma()){
                    positionSpn = i;
                }
            }
        }
        adtListTurma.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spnTurmaAluno.setAdapter(adtListTurma);
        spnTurmaAluno.setSelection(positionSpn);
        if (turmaList.size() > 0){
            btnAddAluno.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    repositorio = new Repositorio(ActivityAddAlunos.this);
                    try {
                        if (alter) {
                            try{
                                int id = repositorio.getTurmas(professorLogged, " and " + DataBase.NOME_TURMA + " == '" + spnTurmaAluno.getSelectedItem().toString() + "'").get(0).getIdTurma();
                                if (!alunoAlter.getNomeAluno().equals(edtNomeAluno.getText().toString())||!alunoAlter.getMatriculaAluno().equals(edtMatriculaAluno.getText().toString())||id != alunoAlter.getIdTurma() ){
                                    String more = "and " + DataBase.MATRICULA_ALUNO + " == '" + alunoAlter.getMatriculaAluno() + "'";
                                    String nome = edtNomeAluno.getText().toString().substring(0, 1).toUpperCase().concat(edtNomeAluno.getText().toString().substring(1));
                                    String dados = "'"+nome+"', "+DataBase.MATRICULA_ALUNO+" = '"+edtMatriculaAluno.getText().toString()+" ', "+DataBase.ID_TURMA+" = "+id;
                                    repositorio.update(DataBase.TABLE_ALUNO, DataBase.NOME_ALUNO,dados, DataBase.ID_PROFESSOR, professorLogged.getId(), more);
                                    Intent it = new Intent(ActivityAddAlunos.this, ShowAlunos.class);
                                    it.putExtra("insert", "alter");
                                    if (turma != null){
                                        it.putExtra("position",turma);
                                    }
                                    startActivity(it);
                                    repositorio.close();
                                    ActivityAddAlunos.this.finish();
                                }else{
                                    AlertsAndControl.snackBar(llAddAluno,"Nada foi alterado!");
                                }
                            }catch (SQLiteException e){
                                AlertsAndControl.alert(ActivityAddAlunos.this,e.getMessage(),"Erro");
                            }
                        }else if (AlertsAndControl.check(iptNomeAluno, edtNomeAluno, 1, "Digite o nome do aluno!") && AlertsAndControl.check(iptMatriculaAluno, edtMatriculaAluno, 1, "Digite a matrícula!")) {
                            int id = repositorio.getTurmas(professorLogged, " and " + DataBase.NOME_TURMA + " == '" + spnTurmaAluno.getSelectedItem().toString() + "'").get(0).getIdTurma();
                            String nome = edtNomeAluno.getText().toString().substring(0, 1).toUpperCase().concat(edtNomeAluno.getText().toString().substring(1));
                            aluno = new Aluno(id, nome, edtMatriculaAluno.getText().toString(),professorLogged.getId());
                            String more ="where " + DataBase.ID_PROFESSOR + " == " + professorLogged.getId();
                            if (repositorio.insert(aluno, more)) {
                                if (getIntent().getStringExtra("new") == null){
                                    Intent it = new Intent(ActivityAddAlunos.this,ShowAlunos.class);
                                    it.putExtra("insert", "ok");
                                    if (turma != null){
                                        it.putExtra("position",turma);
                                    }
                                    startActivity(it);
                                }else{
                                    AlertsAndControl.vericaClass(ActivityAddAlunos.this,getIntent().getStringExtra("new") );
                                }
                                repositorio.close();
                                ActivityAddAlunos.this.finish();
                            } else {
                                AlertsAndControl.snackBar(llAddAluno, "Já tem um aluno com a matrícula " + aluno.getMatriculaAluno() + "!");
                            }
                        } else {
                            AlertsAndControl.snackBar(llAddAluno, "Informe todos os dados corretamente!");
                        }

                    } catch (SQLiteException e) {
                        AlertsAndControl.alert(ActivityAddAlunos.this, e.getMessage(),"Erro");
                    }
                }
            });
        }else{
            AlertsAndControl.noNeedData(this, ActivityAddTurmas.class, "Para adicionar um aluno é necessário criar uma turma antes!",AlertsAndControl.ALUNOS,null);
        }
        edtNomeAluno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(40)});
        edtMatriculaAluno.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
        repositorio.close();
    }
    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("new") == null){
            startActivity(new Intent(ActivityAddAlunos.this,ShowAlunos.class));
        }else{
            AlertsAndControl.vericaClass(ActivityAddAlunos.this,getIntent().getStringExtra("new") );
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
            repositorio.close();
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}

