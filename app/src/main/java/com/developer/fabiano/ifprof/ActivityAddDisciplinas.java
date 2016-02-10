
package com.developer.fabiano.ifprof;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Turma;

import java.util.ArrayList;
import java.util.List;

public class ActivityAddDisciplinas extends AppCompatActivity {
    private Toolbar tbAddDisciplinaCd;
    private Button btnAddDisciplina;
    private Repositorio repositorio;
    private Disciplina disciplina;
    private EditText edtNomeDisciplina;
    private TextInputLayout iptNomeDisciplina;
    private ArrayAdapter<String> adtListTurma;
    private Professor professorLogged;
    private boolean alter = false;
    private LinearLayout llAddDisciplina;
    private AllInfo allInfoAlter;
    int positionSpn = 0;
    private LinearLayout llAddOutraTurma;
    private Button btnOutraTurma;
    int itemCount = 0;
    List<View> allSpinners = new ArrayList<>();
    boolean callItemSelected = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disciplina);
        instance();
        setSupportActionBar(tbAddDisciplinaCd);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        disciplina = new Disciplina();
        repositorio = new Repositorio(this);
        professorLogged =repositorio.getLogged();

        final List<Turma> turmaList = repositorio.getTurmas(professorLogged, "");
        repositorio.close();
        adtListTurma = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);

        for (int i = 0;i < turmaList.size();i++){
            adtListTurma.add(turmaList.get(i).getNomeTurma());
        }
        adtListTurma.setDropDownViewResource(android.R.layout.simple_list_item_1);
        try {
            if (turmaList.size() == 0){
                if(getIntent().getStringExtra("new") != null){
                    AlertsAndControl.noNeedData(ActivityAddDisciplinas.this, ActivityAddTurmas.class, "Para adicionar uma disciplina é necessário criar uma turma antes!", AlertsAndControl.BACKTODISCIPLINA,null);
                }else{
                    AlertsAndControl.noNeedData(ActivityAddDisciplinas.this, ActivityAddTurmas.class, "Para adicionar uma disciplina é necessário criar uma turma antes!",AlertsAndControl.DISCIPLINAS,null);
                }
            }else if(getIntent().getExtras().getParcelable("nomeDisciplina")!= null){
                alter = true;
                allInfoAlter = getIntent().getExtras().getParcelable("nomeDisciplina");
                edtNomeDisciplina.setText(allInfoAlter.getDisciplina().getNomeDisciplina());
                btnAddDisciplina.setText("Alterar");
                if (allInfoAlter.getTurmas().size() == 0){
                    createSpinner(0,false);
                }else{
                    for (int i = 0; i < allInfoAlter.getTurmas().size(); i++) {
                        Turma turma = allInfoAlter.getTurmas().get(i);
                        for (int j = 0; j < adtListTurma.getCount(); j++) {
                            callItemSelected = true;
                            if (turma.getNomeTurma().equals(adtListTurma.getItem(j))){
                                if (llAddOutraTurma.getChildCount() == 0){
                                    createSpinner(j,false);
                                }else{
                                    createSpinner(j,true);
                                }
                            }
                        }
                    }
                }
            }else if(getIntent().getStringExtra("new")!= null){
                createSpinner(0,false);
            }

        }catch (Exception e){
            createSpinner(0,false);
        }
        btnOutraTurma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callItemSelected = true;
                if (turmaList.size() == 0){
                    AlertsAndControl.snackBar(llAddDisciplina, "Você não tem nenhuma turma!");
                }else if (turmaList.size() > 1) {
                    if (llAddOutraTurma.getChildCount() < turmaList.size()) {
                        createSpinner(0,true);
                    } else {
                        AlertsAndControl.snackBar(llAddDisciplina, "Você só tem " + turmaList.size() + " turmas!");
                    }
                } else {
                    AlertsAndControl.snackBar(llAddDisciplina, "Você só tem uma turma!");
                }
            }
        });
        btnAddDisciplina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (turmaList.size() > 0) {
                    if (AlertsAndControl.check(iptNomeDisciplina, edtNomeDisciplina, 1, "Digite o nome da disciplina!")) {
                        repositorio = new Repositorio(ActivityAddDisciplinas.this);
                        if (alter) {
                            Disciplina disciplina = allInfoAlter.getDisciplina();
                            String turmas = "";
                            for (int i = 0; i < allSpinners.size(); i++) {
                                Spinner spinner = (Spinner) allSpinners.get(i).findViewById(R.id.spnAddOutra);
                                turmas += DataBase.NOME_TURMA + " == '" + spinner.getSelectedItem().toString() + "' or ";
                            }
                            turmas = turmas.substring(0, turmas.length() - 4);
                            List<Turma> allTurmas = repositorio.getTurmas(professorLogged, " and ( " + turmas + " )");

                            if (!disciplina.getNomeDisciplina().equals(edtNomeDisciplina.getText().toString()) || !allTurmas.equals(allInfoAlter.getTurmas())) {
                                String more = "and " + DataBase.NOME_DISCIPLINA + " == '" + disciplina.getNomeDisciplina() + "'";
                                String nome = edtNomeDisciplina.getText().toString().substring(0, 1).toUpperCase().concat(edtNomeDisciplina.getText().toString().substring(1)).trim();
                                repositorio.update(DataBase.TABLE_DISCIPLINA, DataBase.NOME_DISCIPLINA, "'" + nome + "'", DataBase.ID_PROFESSOR, professorLogged.getId(), more);
                                repositorio.delete(DataBase.TABLE_TURMA_DISCIPLINA, "where " + DataBase.ID_DISCIPLINA + " == " + disciplina.getIdDisciplina());
                                disciplina.setTurmas(allTurmas);
                                repositorio.insertTurmaDisciplina(disciplina);
                                if (getIntent().getStringExtra("avaliacao") != null){
                                    Intent it = new Intent(ActivityAddDisciplinas.this, ActivityAddAvaliacoes.class);
                                    it.putExtra("semTurma", allInfoAlter.getAvaliacao());
                                    startActivity(it);
                                }else if(getIntent().getStringExtra("new") != null){
                                    AlertsAndControl.vericaClass(ActivityAddDisciplinas.this, getIntent().getStringExtra("new"));
                                } else{
                                    Intent it = new Intent(ActivityAddDisciplinas.this, ShowDisciplinas.class);
                                    it.putExtra("insert", "alter");
                                    startActivity(it);
                                }
                                ActivityAddDisciplinas.this.finish();
                            } else {
                                AlertsAndControl.snackBar(llAddDisciplina, "Nada foi alterado!");
                            }
                        } else {
                            String turmas = "";
                            for (int i = 0; i < allSpinners.size(); i++) {
                                Spinner spinner = (Spinner) allSpinners.get(i).findViewById(R.id.spnAddOutra);
                                turmas += DataBase.NOME_TURMA + " == '" + spinner.getSelectedItem().toString() + "' or ";
                            }
                            turmas = turmas.substring(0, turmas.length() - 4);
                            List<Turma> allTurmas = repositorio.getTurmas(professorLogged, " and ( " + turmas + " )");
                            disciplina.setNomeDisciplina(edtNomeDisciplina.getText().toString().substring(0, 1).toUpperCase().concat(edtNomeDisciplina.getText().toString().substring(1)).trim());
                            disciplina.setTurmas(allTurmas);
                            disciplina.setIdProfessor(professorLogged.getId());
                            if (repositorio.insert(disciplina)) {
                                int idDisciplia = repositorio.getDisciplinas(professorLogged, "and " + DataBase.NOME_DISCIPLINA + " == '" + disciplina.getNomeDisciplina() + "'").get(0).getIdDisciplina();
                                disciplina.setIdDisciplina(idDisciplia);
                                repositorio.insertTurmaDisciplina(disciplina);
                                if (getIntent().getStringExtra("new") == null) {
                                    Intent it = new Intent(ActivityAddDisciplinas.this, ShowDisciplinas.class);
                                    it.putExtra("insert", "ok");
                                    startActivity(it);
                                } else {
                                    AlertsAndControl.vericaClass(ActivityAddDisciplinas.this, getIntent().getStringExtra("new"));
                                }
                                ActivityAddDisciplinas.this.finish();
                            } else {
                                AlertsAndControl.snackBar(llAddDisciplina, "Você já tem uma disciplina com o nome " + disciplina.getNomeDisciplina());
                            }
                        }
                        repositorio.close();
                    } else {
                        AlertsAndControl.snackBar(llAddDisciplina, "Informe todos os dados corretamente!");
                    }
                } else {
                    AlertsAndControl.noNeedData(ActivityAddDisciplinas.this, ActivityAddTurmas.class, "Para adicionar uma disciplina é necessário criar uma turma antes!", AlertsAndControl.DISCIPLINAS,null);
                }
            }
        });
        edtNomeDisciplina.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
    }
    public AppCompatSpinner createSpinner(int position,boolean closeVisible){
        final View view = getLayoutInflater().inflate(R.layout.add_item,null);
        final AppCompatSpinner spnAddOutra = (AppCompatSpinner)view.findViewById(R.id.spnAddOutra);
        if (closeVisible){
            view.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llAddOutraTurma.removeView(view);
                    allSpinners.remove(view);
                }
            });
        }else{
            view.findViewById(R.id.ivClose).setVisibility(View.GONE);
        }
        spnAddOutra.setAdapter(adtListTurma);
        allSpinners.add(view);
        spnAddOutra.setSelection(position);
        checkPosition(position, spnAddOutra);
        spnAddOutra.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                callItemSelected = true;
                return false;
            }
        });
        spnAddOutra.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (callItemSelected) {
                    checkPosition(position, spnAddOutra);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        llAddOutraTurma.addView(view);
        return spnAddOutra;
    }
    public void checkPosition(int position, Spinner spnAddOutra){
        boolean ocupado[] = new boolean[adtListTurma.getCount()];
        for (int i = 0; i < allSpinners.size(); i++) {
            Spinner spinner = (Spinner)allSpinners.get(i).findViewById(R.id.spnAddOutra);
            if(!spinner.equals(spnAddOutra)){
                ocupado[spinner.getSelectedItemPosition()] = true;
            }
        }
        if (ocupado[position]){
            for (int i = position; i < allSpinners.size() ; i++) {
                if (!ocupado[i]){
                    callItemSelected = false;
                    spnAddOutra.setSelection(i);
                    break;
                }
            }
            for (int i = position; i >= 0; i--) {
                if (!ocupado[i]){
                    callItemSelected = false;
                    spnAddOutra.setSelection(i);
                    break;
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        this.finish();
        if (getIntent().getStringExtra("new") == null){
            startActivity(new Intent(this, ShowDisciplinas.class));
        }else{
            AlertsAndControl.vericaClass(this,getIntent().getStringExtra("new"));
        }
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
    public void instance(){
        btnAddDisciplina = (Button)findViewById(R.id.btnAddDisciplina);
        tbAddDisciplinaCd = (Toolbar)findViewById(R.id.tbAddDisciplinaCd);
        edtNomeDisciplina = (EditText)findViewById(R.id.edtNomeDisciplina);
        iptNomeDisciplina = (TextInputLayout)findViewById(R.id.iptNomeDisciplina);
        llAddDisciplina = (LinearLayout)findViewById(R.id.llAddDisciplina);
        llAddOutraTurma = (LinearLayout)findViewById(R.id.llAddOutraTurma);
        btnOutraTurma = (Button)findViewById(R.id.btnOutraTurma);
    }
}
