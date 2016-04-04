package com.developer.fabiano.ifprof;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Aluno;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Turma;

import java.util.ArrayList;
import java.util.List;

public class SeeMore extends AppCompatActivity {
    private TextView txtTitleInfo1;
    private TextView txtTitleInfo2;
    private AllInfo allInfo;
    private Toolbar tbSeeMore;
    private LinearLayout llSeeMore1;
    private LinearLayout llSeeMore2;
    private Professor professor;
    private Repositorio repositorio;
    private View noData1;
    private View noData2;
    private CardView cvFirstScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_more);
        allInfo = new AllInfo();
        tbSeeMore = (Toolbar)findViewById(R.id.tbSeeMore);
        llSeeMore1 = (LinearLayout)findViewById(R.id.llSeeMore1);
        llSeeMore2 = (LinearLayout)findViewById(R.id.llSeeMore2);
        setSupportActionBar(tbSeeMore);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        txtTitleInfo1 = (TextView)findViewById(R.id.txtTitleInfo1);
        txtTitleInfo2 = (TextView)findViewById(R.id.txtTitleInfo2);
        noData1 = (View)findViewById(R.id.noData1);
        noData2 = (View)findViewById(R.id.noData2);
        cvFirstScreen = (CardView)findViewById(R.id.cdFirstScreen);
        try{
            repositorio = new Repositorio(this);
            professor = repositorio.getLogged();
            repositorio.close();
        }catch (Exception e){}

        allInfo = getIntent().getExtras().getParcelable("allInfo");
        if (getIntent().getExtras().getParcelable("avaliacao") != null){
            allInfo = getIntent().getExtras().getParcelable("avaliacao");

        }else if (allInfo.getDisciplina() == null){
            if (allInfo.getDiciplinas() != null){
                TextView txtsemDisciplina = (TextView)noData1.findViewById(R.id.txtMensage);
                txtsemDisciplina.setText(getResources().getString(R.string.semDisciplina));
                txtTitleInfo1.setText("Disciplinas");
                for(int i = 0;i<allInfo.getDiciplinas().size(); i++){
                    noData1.setVisibility(View.GONE);
                    final Disciplina disciplina = allInfo.getDiciplinas().get(i);
                    final View view = getLayoutInflater().inflate(R.layout.item_see_more, null);
                    TextView txtNome = (TextView)view.findViewById(R.id.txtNome);
                    TextView txtFirstLetter = (TextView)view.findViewById(R.id.txtFistLetter);
                    view.findViewById(R.id.txtMore).setVisibility(View.GONE);
                    txtNome.setText(disciplina.getNomeDisciplina());
                    txtFirstLetter.setText(txtNome.getText().charAt(0) + "");
                    txtFirstLetter.setBackgroundResource(R.drawable.circle_second);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            repositorio = new Repositorio(SeeMore.this);
                            AllInfo allInfo = repositorio.getTurmaDisciplinas(professor.getId(), "and " + DataBase.TABLE_TURMA_DISCIPLINA + "." + DataBase.ID_DISCIPLINA + " == " + disciplina.getIdDisciplina());
                            allInfo.setDisciplina(disciplina);
                            List<Aluno> alunoList = new ArrayList<>();
                            for (int j = 0; j < allInfo.getTurmas().size(); j++) {
                                alunoList.addAll(repositorio.getAlunos(allInfo.getTurmas().get(j).getIdTurma()));
                            }
                            repositorio.close();
                            allInfo.setAlunos(alunoList);
                            Intent it = new Intent(SeeMore.this, SeeMore.class);
                            it.putExtra("allInfo", allInfo);
                            startActivity(it);
                        }
                    });
                    llSeeMore1.addView(view);
                }
            }else{
               cvFirstScreen.setVisibility(View.GONE);
            }
        }else{
            txtTitleInfo1.setText("Turmas");
            TextView txtsemDisciplina = (TextView)noData1.findViewById(R.id.txtMensage);
            txtsemDisciplina.setText(getResources().getString(R.string.semTurma));
            for(int i = 0;i<allInfo.getTurmas().size(); i++){
                noData1.setVisibility(View.GONE);
                final Turma turma = allInfo.getTurmas().get(i);
                final View view = getLayoutInflater().inflate(R.layout.item_see_more, null);
                TextView txtNome = (TextView)view.findViewById(R.id.txtNome);
                TextView txtFirstLetter = (TextView)view.findViewById(R.id.txtFistLetter);
                view.findViewById(R.id.txtMore).setVisibility(View.GONE);
                txtNome.setText(turma.getNomeTurma());
                txtFirstLetter.setText(txtNome.getText().charAt(0) + "");
                txtFirstLetter.setBackgroundResource(R.drawable.circle_second);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Repositorio repositorio = new Repositorio(SeeMore.this);
                        AllInfo allInfo = repositorio.getTurmaDisciplinas(repositorio.getLogged().getId(), "and " + DataBase.TABLE_TURMA_DISCIPLINA + "." + DataBase.ID_TURMA + " == " + turma.getIdTurma());
                        allInfo.setTurma(turma);
                        allInfo.setAlunos(repositorio.getAlunos(turma.getIdTurma()));
                        repositorio.close();
                        Intent it = new Intent(SeeMore.this, SeeMore.class);
                        it.putExtra("allInfo", allInfo);
                        startActivity(it);
                    }
                });
                llSeeMore1.addView(view);
            }
        }
        txtTitleInfo2.setText("Alunos");
        TextView txtsemDisciplina = (TextView)noData2.findViewById(R.id.txtMensage);
        txtsemDisciplina.setText(getResources().getString(R.string.semAluno));
        for(int i = 0;i<allInfo.getAlunos().size() ; i++){
            noData2.setVisibility(View.GONE);
            final Aluno aluno = allInfo.getAlunos().get(i);
            final View view = getLayoutInflater().inflate(R.layout.item_see_more, null);
            TextView txtNome = (TextView)view.findViewById(R.id.txtNome);
            TextView txtMore = (TextView)view.findViewById(R.id.txtMore);
            TextView txtFirstLetter = (TextView)view.findViewById(R.id.txtFistLetter);
            txtNome.setText(aluno.getNomeAluno());
            txtMore.setText(aluno.getMatriculaAluno());
            txtFirstLetter.setText(txtNome.getText().charAt(0) + "");

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(SeeMore.this, VerAluno.class);
                    it.putExtra("aluno", aluno);
                    startActivity(it);
                }
            });

            llSeeMore2.addView(view);
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

