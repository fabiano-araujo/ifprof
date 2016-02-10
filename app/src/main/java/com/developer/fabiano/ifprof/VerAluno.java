package com.developer.fabiano.ifprof;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.Aluno;
import com.developer.fabiano.ifprof.model.Avaliacao;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Professor;

import java.util.List;

public class VerAluno extends AppCompatActivity {
    private Toolbar tbVerAluno;
    private TextView txtMatricula;
    private TextView txtTurma;
    private LinearLayout llDisciplinas;
    private LinearLayout llAvaliacoes;
    private LinearLayout semAvaliacao;
    private LinearLayout semDisciplinas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_aluno);
        tbVerAluno = (Toolbar)findViewById(R.id.tbVerAluno);
        setSupportActionBar(tbVerAluno);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtMatricula = (TextView)findViewById(R.id.txtMatricula);
        txtTurma = (TextView)findViewById(R.id.txtTurma);
        llAvaliacoes = (LinearLayout)findViewById(R.id.llAvaliacoes);
        llDisciplinas = (LinearLayout)findViewById(R.id.llDisciplinas);
        semAvaliacao = (LinearLayout)findViewById(R.id.semAvaliacao);
        semDisciplinas = (LinearLayout)findViewById(R.id.semDisciplina);
        Aluno aluno = getIntent().getExtras().getParcelable("aluno");
        getSupportActionBar().setTitle(aluno.getNomeAluno());
        txtMatricula.setText(aluno.getMatriculaAluno());
        Repositorio repositorio = new Repositorio(this);
        Professor professor = repositorio.getLogged();
        String turma = repositorio.getTurmas(professor, "and " + DataBase.ID_TURMA + " == " + aluno.getIdTurma()).get(0).getNomeTurma();
        txtTurma.setText(turma);
        List<Disciplina> disciplinaList = repositorio.getTurmaDisciplinas(professor.getId(), " and " + DataBase.TABLE_TURMA_DISCIPLINA + "." + DataBase.ID_TURMA + " == " + aluno.getIdTurma()).getDiciplinas();

        TextView txtsemDisciplina = (TextView)semDisciplinas.findViewById(R.id.txtMensage);
        txtsemDisciplina.setText(getResources().getString(R.string.semDisciplina));

        TextView txtsemAvaliacao = (TextView)semAvaliacao.findViewById(R.id.txtMensage);
        txtsemDisciplina.setText(getResources().getString(R.string.semAvaliacao));

        try {
            for (int i = 0; i < disciplinaList.size() ; i++) {
                semDisciplinas.setVisibility(View.GONE);
                Disciplina disciplina = disciplinaList.get(i);
                View view = getLayoutInflater().inflate(R.layout.item_see_more, null);
                TextView txtNome = (TextView)view.findViewById(R.id.txtNome);
                TextView txtFirstLetter = (TextView)view.findViewById(R.id.txtFistLetter);
                view.findViewById(R.id.txtMore).setVisibility(View.GONE);
                txtNome.setText(disciplina.getNomeDisciplina());
                txtFirstLetter.setText(txtNome.getText().charAt(0) + "");
                llDisciplinas.addView(view);
                List<Avaliacao> avaliacaoList = repositorio.getAValiacoes(professor.getId(), "and " + DataBase.ID_DISCIPLINA + " == " + disciplina.getIdDisciplina());
                if(avaliacaoList.size() > 0){
                    View viewDisciplinaMensage = getLayoutInflater().inflate(R.layout.layout_mensage, null);
                    TextView txtDisciplina = (TextView)viewDisciplinaMensage.findViewById(R.id.txtDisciplinaAvaliacao);
                    txtDisciplina.setText(disciplina.getNomeDisciplina());
                    llAvaliacoes.addView(viewDisciplinaMensage);
                }
                for (int j = 0; j < avaliacaoList.size(); j++) {
                    semAvaliacao.setVisibility(View.GONE);
                    View viewAvaliacao = getLayoutInflater().inflate(R.layout.item_see_more, null);
                    TextView txtAssunto = (TextView)viewAvaliacao.findViewById(R.id.txtNome);
                    TextView txtFirst = (TextView)viewAvaliacao.findViewById(R.id.txtFistLetter);
                    TextView txtBimestre = (TextView)viewAvaliacao.findViewById(R.id.txtMore);
                    txtBimestre.setText(avaliacaoList.get(j).getBimestre());
                    txtAssunto.setText(avaliacaoList.get(j).getAssunto());
                    txtFirst.setText(txtAssunto.getText().charAt(0) + "");
                    txtFirst.setBackgroundResource(R.drawable.circle_second);
                    llAvaliacoes.addView(viewAvaliacao);
                }
            }
        }catch (Exception e){
            AlertDialog.Builder erro = new AlertDialog.Builder(this);
            erro.setTitle("Erro").setMessage("Ocorreu um erro, tente novamente!").setNeutralButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(VerAluno.this,ShowAlunos.class));
                }
            }).show();
        }
        repositorio.close();
    }
    @Override
    public void onBackPressed() {
        try {
            if (getIntent().getStringExtra("search").equals("search")){
                super.onBackPressed();
            }else{
                startActivity(new Intent(this, ShowAlunos.class));
                this.finish();
            }
        }catch (NullPointerException e){
            startActivity(new Intent(this, ShowAlunos.class));
            this.finish();
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
}

