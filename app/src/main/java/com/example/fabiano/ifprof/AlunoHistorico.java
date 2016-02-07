package com.example.fabiano.ifprof;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.fabiano.ifprof.adapters.AdapterHistorico;
import com.example.fabiano.ifprof.adapters.Repositorio;
import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.Falta;
import com.example.fabiano.ifprof.model.Historico;
import com.example.fabiano.ifprof.model.Turma;

public class AlunoHistorico extends AppCompatActivity {
    private TextView txtMateriaHistorico;
    private TextView txtAlHtNome;
    private TextView txtAlHtMatricula;
    private TextView txtAlHtTurma;
    private TextView txtAlHtFaltas;
    private TextView txtAlHtAulas;
    private Toolbar tbAlunoHistorico;
    private RecyclerView rvAlHistorico;
    private Historico historico;
    private Falta falta;
    private Repositorio repositorio;
    private  Turma turma;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aluno_historico);
        instance();

        if (getIntent().getExtras().getParcelable("historico") != null){
            historico = getIntent().getExtras().getParcelable("historico");
            falta = historico.getFaltaList().get(historico.getPosition());
            try {
                repositorio = new Repositorio(this);
                turma = repositorio.getTurmas(repositorio.getLogged(), "and " + DataBase.ID_TURMA + " == " + falta.getAluno().getIdTurma()).get(0);
                repositorio.close();
            }catch (SQLiteException e){

            }
        }

        txtMateriaHistorico.setText("Histórico de "+historico.getDisciplina().getNomeDisciplina());
        txtAlHtNome.setText(falta.getAluno().getNomeAluno());
        txtAlHtMatricula.setText(falta.getAluno().getMatriculaAluno());
        txtAlHtTurma.setText(turma.getNomeTurma());

        rvAlHistorico.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvAlHistorico.setLayoutManager(llm);
        AdapterHistorico adapterHistorico = new AdapterHistorico(this,historico,AdapterHistorico.ALUNOHISTORICO,getLayoutInflater());
        rvAlHistorico.setAdapter(adapterHistorico);
        int totalDeFaltas = 0;
        int totalDeAulas = 0;
        for (int i = 0; i < falta.getQtdFaltasList().size() ; i++) {
            try{
                totalDeFaltas += Integer.parseInt(falta.getQtdFaltasList().get(i));
                totalDeAulas += Integer.parseInt(falta.getAulasMinistradasList().get(i));
            }catch (NumberFormatException e){
                System.out.println(e.getMessage());
            }
        }
        txtAlHtFaltas.setText("Total de faltas: "+totalDeFaltas+"");
        txtAlHtAulas.setText("Total de aulas: "+totalDeAulas+"");
    }
    public void instance(){
        txtAlHtAulas = (TextView)findViewById(R.id.txtAlHtAulas);
        txtMateriaHistorico = (TextView)findViewById(R.id.txtMateriaHistorico);
        txtAlHtNome = (TextView)findViewById(R.id.txtAlHtNome);
        txtAlHtMatricula = (TextView)findViewById(R.id.txtAlHtMatricula);
        txtAlHtTurma = (TextView)findViewById(R.id.txtAlHtTurma);
        txtAlHtFaltas = (TextView)findViewById(R.id.txtAlHtFaltas);
        tbAlunoHistorico = (Toolbar)findViewById(R.id.tbAlunoHistorico);
        rvAlHistorico = (RecyclerView)findViewById(R.id.rvAlHistorico);
        setSupportActionBar(tbAlunoHistorico);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent it = new Intent(this,ActivityHistorico.class);
        it.putExtra("historico",historico);
        startActivity(it);
        this.finish();
    }
}