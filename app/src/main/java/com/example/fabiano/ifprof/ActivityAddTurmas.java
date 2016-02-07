package com.example.fabiano.ifprof;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.fabiano.ifprof.adapters.AlertsAndControl;
import com.example.fabiano.ifprof.adapters.Repositorio;
import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.Professor;
import com.example.fabiano.ifprof.model.Turma;

public class ActivityAddTurmas extends AppCompatActivity {
    private Toolbar tbAddTurma;
    private EditText edtNomeTurma;
    private TextInputLayout iptNomeTurma;
    private Button btnAddTurma;
    private Repositorio repositorio;
    private Turma turma = new Turma();
    private Professor professorLogged;
    private LinearLayout llAddTurma;
    private boolean alter = false;
    private String nameAlter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turmas);

        tbAddTurma = (Toolbar)findViewById(R.id.tbAddTurma);
        setSupportActionBar(tbAddTurma);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtNomeTurma = (EditText)findViewById(R.id.edtNomeTurma);
        iptNomeTurma = (TextInputLayout)findViewById(R.id.iptNomeTurma);
        btnAddTurma = (Button)findViewById(R.id.btnAddTurma);
        llAddTurma = (LinearLayout)findViewById(R.id.llAddTurma);


        try{
            repositorio = new Repositorio(this);
            professorLogged = repositorio.getLogged();
        }catch (Exception e){
            AlertsAndControl.alert(this,e.getMessage(),"Erro");
        }
        if (getIntent().getStringExtra("nomeTurma" ) != null){
            alter = true;
            nameAlter = getIntent().getStringExtra("nomeTurma");
            edtNomeTurma.setText(nameAlter);
            btnAddTurma.setText("Alterar");
        }
        btnAddTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkName()){
                    turma.setIdProfessor(professorLogged.getId());
                    turma.setNomeTurma(edtNomeTurma.getText().toString().substring(0, 1).toUpperCase().concat(edtNomeTurma.getText().toString().substring(1)));
                    try{
                        if(alter){
                            repositorio = new Repositorio(ActivityAddTurmas.this);
                            if (!nameAlter.equals(edtNomeTurma.getText().toString())){
                                String more = "and "+DataBase.NOME_TURMA+" == '"+nameAlter+"'";
                                String nome = edtNomeTurma.getText().toString().substring(0, 1).toUpperCase().concat(edtNomeTurma.getText().toString().substring(1)).trim();
                                repositorio.update(DataBase.TABLE_TURMA,DataBase.NOME_TURMA,"'"+nome+"'",DataBase.ID_PROFESSOR,professorLogged.getId(),more);
                                Intent it = new Intent(ActivityAddTurmas.this,ShowTurmas.class);
                                it.putExtra("insert", "alter");
                                startActivity(it);
                                repositorio.close();
                                ActivityAddTurmas.this.finish();
                            }else{
                                AlertsAndControl.snackBar(llAddTurma,"Nada foi alterado!");
                            }
                        }
                        else if (repositorio.insert(turma)){
                            if (getIntent().getStringExtra("new") == null){
                                Intent it = new Intent(ActivityAddTurmas.this,ShowTurmas.class);
                                it.putExtra("insert", "ok");
                                startActivity(it);
                            }else{
                                if (getIntent().getStringExtra("new").equals(AlertsAndControl.BACKTODISCIPLINA)){
                                    AlertsAndControl.vericaClass(ActivityAddTurmas.this,AlertsAndControl.BACKTOAVALIACAO);
                                }else{
                                    AlertsAndControl.vericaClass(ActivityAddTurmas.this, getIntent().getStringExtra("new"));
                                }
                            }
                            repositorio.close();
                            ActivityAddTurmas.this.finish();
                        }else{
                            AlertsAndControl.snackBar(llAddTurma,"você já tem uma turma com o nome "+turma.getNomeTurma()+"!");
                        }
                    }catch (Exception e){}
                }else{
                    AlertsAndControl.snackBar(llAddTurma, "Informe todos os dados corretamente!");
                }
            }
        });
        edtNomeTurma.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
    }
    public void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
    public boolean checkName(){
        boolean isValid = true;
        if(edtNomeTurma.getText().toString().trim().isEmpty()){
            iptNomeTurma.setError("Digite o nome da turma!");
            requestFocus(edtNomeTurma);
            isValid = false;
        }else{
            iptNomeTurma.setErrorEnabled(false);
        }
        return  isValid;
    }
    @Override
    public void onBackPressed() {
        this.finish();
        if (getIntent().getStringExtra("new") == null){
            startActivity(new Intent(this,ShowTurmas.class));
        }
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
