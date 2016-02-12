package com.developer.fabiano.ifprof;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.AdapterHistorico;
import com.developer.fabiano.ifprof.adapters.AlertsAndControl;
import com.developer.fabiano.ifprof.adapters.Progress;
import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Aluno;
import com.developer.fabiano.ifprof.model.Falta;
import com.developer.fabiano.ifprof.model.Historico;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Turma;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActivityHistorico extends AppCompatActivity {
    private AppCompatSpinner spnTurmasHistorico;
    private Spinner spnOutro;
    private RecyclerView rvHistorico;
    private AdapterHistorico faltasAdapter;
    private ArrayAdapter<String> adtListTurma;
    private Historico historico;
    private TextView txtDisciplinaHistorico;
    private Toolbar tbHistorico;
    private RadioGroup rgOpcoes;
    private RadioButton rbTodos, rbultimo,rbOutro;
    private Repositorio repositorio;
    private MenuItem item;
    private LinearLayout llOptions;
    private TextView txtMensage;
    private LinearLayout llNoData;
    private LinearLayout llTurma;
    private View optionSave;
    private Turma turma = new Turma();
    private Professor professor;
    private ArrayAdapter daysAdapter;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);
        instance();
        try{
            repositorio = new Repositorio(this);
            professor = repositorio.getLogged();
            repositorio.close();
        }catch (Exception e){}
        rvHistorico.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvHistorico.setLayoutManager(llm);
        final AllInfo allInfo;
        if(getIntent().getExtras().getParcelable("chamada") != null){
            allInfo = getIntent().getExtras().getParcelable("chamada");
        }else if (getIntent().getExtras().getParcelable("allInfo") != null){
            allInfo = getIntent().getExtras().getParcelable("allInfo");
        }else{
            allInfo = ((Historico)getIntent().getExtras().getParcelable("historico")).getAllInfo();
        }
        txtDisciplinaHistorico.setText(allInfo.getDisciplina().getNomeDisciplina());
        final List<Turma> turmaList = allInfo.getTurmas();
        adtListTurma = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item);

        for (int i = 0;i < turmaList.size();i++){
            adtListTurma.add(turmaList.get(i).getNomeTurma());
        }
        if (adtListTurma.getCount() == 0){
            llTurma.setVisibility(View.GONE);
            llNoData.setVisibility(View.VISIBLE);
            rvHistorico.setVisibility(View.GONE);
            llOptions.setVisibility(View.GONE);
        }
        adtListTurma.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spnTurmasHistorico.setAdapter(adtListTurma);
        if (getIntent().getExtras().getParcelable("chamada") != null){
            for (int i = 0; i < adtListTurma.getCount(); i++) {
                if(adtListTurma.getItem(i).equals(allInfo.getTurma().getNomeTurma())){
                       spnTurmasHistorico.setSelection(i);
                }
            }
        }
        spnTurmasHistorico.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {
                    List<Aluno> alunoList = new ArrayList<Aluno>();
                    for (int i = 0; i < turmaList.size(); i++) {
                        if (spnTurmasHistorico.getSelectedItem().toString().equals(turmaList.get(i).getNomeTurma())) {
                            turma = turmaList.get(i);
                            for (int j = 0; j < allInfo.getAlunos().size(); j++) {
                                if (allInfo.getAlunos().get(j).getIdTurma() == turma.getIdTurma()) {
                                    alunoList.add(allInfo.getAlunos().get(j));
                                }
                            }
                            historico = new Historico();
                            historico.setAllInfo(allInfo);
                            historico.setDisciplina(allInfo.getDisciplina());
                            historico.setAlunoList(alunoList);
                            String and = "";
                            switch (rgOpcoes.getCheckedRadioButtonId()) {
                                case R.id.rbUltimo:
                                    and = "";
                                    break;
                                case R.id.rbOutro:
                                    try {
                                        and = " and " + DataBase.DATA + " == '" + spnOutro.getSelectedItem().toString() + "'";
                                    } catch (NullPointerException e) {}
                                    break;
                            }
                            try {
                                repositorio = new Repositorio(ActivityHistorico.this);
                                historico = repositorio.getHistorico(historico, and);
                                daysAdapter = repositorio.getDays(historico);
                                spnOutro.setAdapter(daysAdapter);
                                try{
                                    spnOutro.setSelection(daysAdapter.getCount()-1);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                repositorio.close();
                            } catch (IndexOutOfBoundsException iobe) {
                                ArrayAdapter<String> days = new ArrayAdapter<String>(ActivityHistorico.this, android.R.layout.simple_spinner_item);
                                days.add("Nenhum");
                                days.setDropDownViewResource(android.R.layout.simple_list_item_1);
                                daysAdapter = days;
                                spnOutro.setAdapter(days);
                                try{
                                    spnOutro.setSelection(daysAdapter.getCount()-1);
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                AlertsAndControl.alert(ActivityHistorico.this, e.getMessage(), "Erro");
                            }
                            if (count == 0) {
                                functionTodos();
                            }
                            break;
                        }
                    }
                    if (historico.getFaltaList().size() == 0) {
                        llOptions.setVisibility(View.GONE);
                    } else {
                        llOptions.setVisibility(View.VISIBLE);
                    }
                    faltasAdapter = new AdapterHistorico(ActivityHistorico.this, historico, AdapterHistorico.HISTORICO, null);
                    rvHistorico.setAdapter(faltasAdapter);
                    scrollFlags();
                    if (historico.getFaltaList().size() < 1) {
                        item.setVisible(false);
                    }else{
                        item.setVisible(true);
                    }
                } catch (Exception e) {}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        rbOutro.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               spnOutro.performClick();
           }
       });
        spnOutro.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                rbOutro.setChecked(true);
                return false;
            }
        });

        spnOutro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                functionSpn(spnOutro.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                functionSpn(spnOutro.getSelectedItem().toString());
            }
        });
        rbTodos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               functionTodos();
            }
        });
        rbultimo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               functionUltimo();
            }
        });
    }
    public void functionTodos(){
        repositorio = new Repositorio(ActivityHistorico.this);
        historico = repositorio.getHistorico(historico, "");
        rvHistorico.setAdapter(new AdapterHistorico(ActivityHistorico.this, historico, AdapterHistorico.HISTORICO, null));
        scrollFlags();
        repositorio.close();
    }
    public void functionUltimo(){
        repositorio = new Repositorio(ActivityHistorico.this);
        historico = repositorio.getHistorico(historico, "and "+DataBase.ULTIMO+" == 1"              );
        rvHistorico.setAdapter(new AdapterHistorico(ActivityHistorico.this, historico, AdapterHistorico.HISTORICO, null));
        scrollFlags();
        repositorio.close();
    }
    public void functionSpn(String itemSelected){
        try {
            String data[] = itemSelected.toString().split("/");
            String and = " and " + DataBase.DATA + " == '" + data[2] + "a" + data[1] + "a" + data[0] + "'";
            repositorio = new Repositorio(ActivityHistorico.this);
            historico = repositorio.getHistorico(historico, and);
            rvHistorico.setAdapter(new AdapterHistorico(ActivityHistorico.this, historico, AdapterHistorico.HISTORICO, null));
            scrollFlags();
            repositorio.close();
        } catch (Exception e) {
            AlertsAndControl.alert(ActivityHistorico.this, e.getMessage(), "Erro");
        }
    }
    public void scrollFlags(){
        try {
            rvHistorico.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (faltasAdapter.getItemCount() == 0){
                        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbHistorico.getLayoutParams();
                        params.setScrollFlags(0);
                        llNoData.setVisibility(View.VISIBLE);
                        rvHistorico.setVisibility(View.GONE);
                    }else{
                        float heightView = rvHistorico.getChildAt(0).getHeight()*historico.getFaltaList().size();
                        Display display = getWindowManager().getDefaultDisplay();
                        int screenHeight = display.getHeight()- tbHistorico.getHeight();
                        if (heightView < screenHeight){
                            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbHistorico.getLayoutParams();
                            params.setScrollFlags(0);
                        }else{
                            AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tbHistorico.getLayoutParams();
                            params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                        }
                        llNoData.setVisibility(View.GONE);
                        rvHistorico.setVisibility(View.VISIBLE);
                    }
                    rvHistorico.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }catch (Exception e){}
    }
    public void instance(){
        spnTurmasHistorico = (AppCompatSpinner)findViewById(R.id.spnTurmasHistorico);
        rvHistorico = (RecyclerView)findViewById(R.id.rvHistorico);
        txtDisciplinaHistorico = (TextView)findViewById(R.id.txtDisciplinaHistorico);
        tbHistorico = (Toolbar)findViewById(R.id.tbHistorico);
        setSupportActionBar(tbHistorico);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        rgOpcoes = (RadioGroup)findViewById(R.id.rgOpcoes);
        rbTodos = (RadioButton)findViewById(R.id.rbTodos);
        rbOutro = (RadioButton)findViewById(R.id.rbOutro);
        rbultimo = (RadioButton)findViewById(R.id.rbUltimo);
        llOptions = (LinearLayout)findViewById(R.id.llOptions);
        spnOutro = (Spinner)findViewById(R.id.spnOutro);
        txtMensage = (TextView)findViewById(R.id.txtMensage);
        llNoData = (LinearLayout)findViewById(R.id.llNoData);
        txtMensage.setText("Essa turma não tem nenhum registro salvo!");
        llTurma = (LinearLayout)findViewById(R.id.llTurma);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_historico, menu);
        item = menu.findItem(R.id.itmCreatefile);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.itmCreatefile:

                    functionTodos();
                    optionSave = getLayoutInflater().inflate(R.layout.day_options,null);

                    final Spinner spinner = (Spinner)optionSave.findViewById(R.id.spnOutro);
                    final RadioButton rbOutro = (RadioButton)optionSave.findViewById(R.id.rbOutro);
                    final RadioButton rbTodos = (RadioButton)optionSave.findViewById(R.id.rbTodos);
                    RadioButton rbultimo = (RadioButton)optionSave.findViewById(R.id.rbUltimo);
                    final RadioGroup rgOpcoes = (RadioGroup)findViewById(R.id.rgOpcoes);

                    rbOutro.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            spinner.performClick();
                        }
                    });
                    spinner.setAdapter(daysAdapter);
                    spinner.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            rbOutro.setChecked(true);
                            return false;
                        }
                    });
                    try{
                        spinner.setSelection(daysAdapter.getCount()-1);
                    }catch (Exception e){}
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            functionSpn(spinner.getSelectedItem().toString());
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            functionSpn(spinner.getSelectedItem().toString());
                        }
                    });
                    rbTodos.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            functionTodos();
                        }
                    });
                    rbultimo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            functionUltimo();
                        }
                    });

                    AlertDialog.Builder option = new AlertDialog.Builder(this);
                    option.setMessage("Escolha o dia para salvar:")
                            .setView(optionSave);
                    option.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String date = null;
                            boolean aulasMinistradasDiferente = false;
                            boolean apenasUmDia = true;
                            int sizeAulasMinistradas = 0;
                            for (int i = 0; i < historico.getFaltaList().size(); i++) {
                                Falta falta = historico.getFaltaList().get(i);

                                if (falta.getDataList().size() > 1){
                                    apenasUmDia = false;
                                }
                                if (i != 0 && sizeAulasMinistradas != falta.getAulasMinistradasList().size() ){
                                    aulasMinistradasDiferente = true;
                                }
                                sizeAulasMinistradas = falta.getAulasMinistradasList().size();
                            }
                            if (apenasUmDia){
                                date = historico.getFaltaList().get(0).getDataList().get(0);
                            }

                            String nomeDiretorio = Environment.getExternalStorageDirectory().getPath()+"/Ifprof/"+professor.getNomeProfessor()+"/"+historico.getDisciplina().getNomeDisciplina()+"/"+turma.getNomeTurma()+"/lista de presença";
                            String file = "";
                            if (apenasUmDia){
                                file = nomeDiretorio+"/"+turma.getNomeTurma()+" Lista de chamada-"+date+".pdf";
                            }else{
                                file = nomeDiretorio+"/"+turma.getNomeTurma()+" - histórico.pdf";
                            }
                            File path = new File(file);
                            boolean exist = false;
                            int numberFile = 0;
                            if(path.exists()){
                                for (int j = 1; true; j++) {
                                    if (apenasUmDia){
                                        file = nomeDiretorio+"/"+turma.getNomeTurma()+" Lista de chamada-"+date+"("+j+")"+".pdf";
                                    }else{
                                        file = nomeDiretorio+"/"+turma.getNomeTurma()+" - histórico("+j+").pdf";
                                    }
                                    File exists = new File(file);
                                    if (!exists.exists()) {
                                        numberFile = j;
                                        break;
                                    }
                                }
                            }

                            try {
                                Progress criarProva = new Progress(ActivityHistorico.this,historico,professor,turma,date,numberFile,aulasMinistradasDiferente);
                                criarProva.execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).setNegativeButton("Cancelar",null).show();
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
