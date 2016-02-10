package com.developer.fabiano.ifprof;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AdapterAddFaltas;
import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Aluno;
import com.developer.fabiano.ifprof.model.Falta;
import com.developer.fabiano.ifprof.model.Turma;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityAddFaltas extends AppCompatActivity {
    private Toolbar tbAddFaltas;
    private RecyclerView rvAddFaltas;
    private List<Aluno> mList;
    private EditText edtAulasMinis;
    private FloatingActionButton FABSendFaltas;
    private ArrayAdapter<String> adtListTurma;
    private AppCompatSpinner spnTurmasFaltas;
    private AdapterAddFaltas faltasAdapter;
    private EditText edtDia;
    final static int DATEPICKER = 1;
    String year,month,day;
    private CoordinatorLayout cdlAddFaltas;
    private AllInfo allInfo;
    private LinearLayout llEdits;
    private TextView txtMensage;
    private ScrollView svNoData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faltas);
        instance();
        txtMensage.setText("Essa turma não tem nenhum aluno!");
        rvAddFaltas.setHasFixedSize(true);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddFaltas.setLayoutManager(llm);
        if(getIntent().getExtras().getParcelable("allInfo") != null){
            allInfo = getIntent().getExtras().getParcelable("allInfo");
        }
        mList = new ArrayList<>();
        final List<Turma> turmaList = allInfo.getTurmas();
                adtListTurma = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);

        for (int i = 0;i < turmaList.size();i++){
            adtListTurma.add(turmaList.get(i).getNomeTurma());
        }
        adtListTurma.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spnTurmasFaltas.setAdapter(adtListTurma);
        if (adtListTurma.getCount() == 0){
            txtMensage.setText("Essa disciplina não tem nenhuma turma!");
        }
        spnTurmasFaltas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int idTurma;
                mList.clear();
                for (int i = 0; i < turmaList.size(); i++) {
                    if (spnTurmasFaltas.getSelectedItem().toString().equals(turmaList.get(i).getNomeTurma())) {
                        idTurma = turmaList.get(i).getIdTurma();
                        allInfo.setTurma(turmaList.get(i));
                        for (int j = 0; j < allInfo.getAlunos().size(); j++) {
                            if (allInfo.getAlunos().get(j).getIdTurma() == idTurma) {
                                mList.add(allInfo.getAlunos().get(j));
                            }
                        }
                        break;
                    }
                }
                faltasAdapter = new AdapterAddFaltas(ActivityAddFaltas.this, mList, cdlAddFaltas, edtAulasMinis);
                rvAddFaltas.setAdapter(faltasAdapter);
                rvAddFaltas.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (faltasAdapter.getItemCount() == 0) {
                            FABSendFaltas.setVisibility(View.GONE);
                            llEdits.setVisibility(View.GONE);
                            rvAddFaltas.setVisibility(View.GONE);
                            svNoData.setVisibility(View.VISIBLE);
                            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbAddFaltas.getLayoutParams();
                            params.setScrollFlags(0);
                        } else {
                            FABSendFaltas.setVisibility(View.VISIBLE);
                            llEdits.setVisibility(View.VISIBLE);
                            rvAddFaltas.setVisibility(View.VISIBLE);
                            svNoData.setVisibility(View.GONE);
                            float heightView = rvAddFaltas.getChildAt(0).getHeight() * mList.size();
                            Display display = getWindowManager().getDefaultDisplay();
                            int screenHeight = display.getHeight() - tbAddFaltas.getHeight();

                            if (heightView < screenHeight) {
                                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbAddFaltas.getLayoutParams();
                                params.setScrollFlags(0);
                            } else {
                                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbAddFaltas.getLayoutParams();
                                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                            }
                        }
                        rvAddFaltas.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        edtAulasMinis.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    if (edtAulasMinis.getText().toString().trim().length() > 0){
                        if (Integer.parseInt(edtAulasMinis.getText().toString()) > 6){
                            edtAulasMinis.setText("6");
                            AlertsAndControl.snackBar(cdlAddFaltas,"No máximo 6 aulas por dia!");
                        }
                        faltasAdapter.setValues();
                    }
                }catch (Exception e){}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                DateFormat ano = new SimpleDateFormat("yyyy");
                DateFormat mes = new SimpleDateFormat("MM");
                DateFormat dia = new SimpleDateFormat("dd");
                DatePickerDialog dlg = new DatePickerDialog(ActivityAddFaltas.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        ActivityAddFaltas.this.year = Integer.toString(year);
                        month = Integer.toString(monthOfYear+1);
                        if (month.length() == 1){
                            month = "0"+month;
                        }
                        day = Integer.toString(dayOfMonth);
                        if (day.length() == 1){
                            day = "0"+day;
                        }
                        edtDia.setText(day+"/"+month+"/"+year);
                    }
                }, Integer.parseInt(ano.format(date)), Integer.parseInt(mes.format(date)) - 1, Integer.parseInt(dia.format(date)));
                dlg.show();
            }
        });
        // alunoAdapter.setOnclickRecyclerView(this);

        FABSendFaltas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFaltas();
            }
        });

    }
    public void saveFaltas(){
        if(edtAulasMinis.getText().toString().trim().equals("")){
            AlertsAndControl.alert(ActivityAddFaltas.this, "digite o número de aulas que foram ministradas!", null);
        }else if (edtDia.getText().toString().equals("")){
            AlertsAndControl.alert(ActivityAddFaltas.this, "Digite o dia da aula!", "Dia inválido");
        }else{
            mList = faltasAdapter.getItems();
            List<Falta> faltaList = new ArrayList<Falta>();
            for (int i = 0; i < mList.size();i++) {
                Falta falta = new Falta();
                falta.setAulasMinistradas(edtAulasMinis.getText().toString());
                if (month.length() == 1){
                    month = "0"+month;
                }
                falta.setDate(year+"a"+month+"a"+day);
                falta.setIdDisciplina(allInfo.getDisciplina().getIdDisciplina());
                falta.setAluno(mList.get(i));

                if(mList.get(i).getFaltas().trim().equals("")){
                    falta.setQdtFaltas("0");
                }else{
                    falta.setQdtFaltas(mList.get(i).getFaltas());
                }
                faltaList.add(falta);
            }
            try {
                Repositorio repositorio = new Repositorio(this);
                faltaList = repositorio.insert(faltaList);
                String noSaves = "";
                int saves = 0;
                for (int i = 0; i < faltaList.size(); i++) {
                    if (!faltaList.get(i).isSave()){
                        noSaves += faltaList.get(i).getAluno().getNomeAluno()+", ";
                    }else{
                        saves++;
                    }
                }
                if (!noSaves.equals("")){
                    alertSave(saves+" registros foram salvos .\n"+noSaves+"não foram salvos, pois eles já tem um registro no dia "+edtDia.getText()+". Ver historico?");
                }else{
                   alertSave("Todos os registros foram salvos com sucesso. Ver histórico?");
                }
                repositorio.close();
            }catch (SQLiteException e){
                AlertsAndControl.alert(this,e.getMessage(),"erro");
            }
        }
    }
    public void alertSave(String mensage){
        AlertDialog.Builder salvos = new AlertDialog.Builder(this);
        salvos.setMessage(mensage)
                .setCancelable(false).setPositiveButton("sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent it = new Intent(ActivityAddFaltas.this, ActivityHistorico.class);
                it.putExtra("chamada", allInfo);
                startActivity(it);
                ActivityAddFaltas.this.finish();
            }
        }).setNegativeButton("não",null).setTitle("Salvos").show();
    }
    public void instance(){
        tbAddFaltas = (Toolbar)findViewById(R.id.tbAddFaltas);
        FABSendFaltas = (FloatingActionButton)findViewById(R.id.FABSendFaltas);
        setSupportActionBar(tbAddFaltas);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtAulasMinis = (EditText)findViewById(R.id.edtAulasMinis);
        edtDia = (EditText)findViewById(R.id.edtDia);
        rvAddFaltas = (RecyclerView)findViewById(R.id.rvAddFaltas);
        spnTurmasFaltas = (AppCompatSpinner)findViewById(R.id.spnTurmasFaltas);
        cdlAddFaltas = (CoordinatorLayout)findViewById(R.id.cdlAddFaltas);
        llEdits = (LinearLayout)findViewById(R.id.llEdits);
        txtMensage = (TextView)findViewById(R.id.txtMensage);
        svNoData = (ScrollView)findViewById(R.id.svNoData);
    }
    @Override
    public void onBackPressed() {
        this.finish();
        startActivity(new Intent(this, ShowFaltas.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_faltas, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.itmSend:
                saveFaltas();
                break;
            case  android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}

