package com.developer.fabiano.ifprof;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.developer.fabiano.ifprof.adapters.Repositorio;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Aluno;
import com.developer.fabiano.ifprof.model.Avaliacao;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Questao;
import com.developer.fabiano.ifprof.model.Turma;

import java.util.ArrayList;
import java.util.List;


public class SearchableActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String pesquisa = "";
    private ScrollView svSearch;
    private Repositorio repositorio;
    private LinearLayout llSearch;
    private LinearLayout llNada;
    private List<Turma> turmaList = new ArrayList<>();
    private List<Disciplina> disciplinaList = new ArrayList<>();
    private List<AllInfo> allInfoAvaliacoes = new ArrayList<>();
    private List<Questao> questaoList = new ArrayList<>();
    private List<Aluno> alunoList = new ArrayList<>();
    private Professor professor;
    private EditText edtPesquisar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        mToolbar = (Toolbar)findViewById(R.id.tbSearch);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        llSearch = (LinearLayout)findViewById(R.id.llSearch);
        svSearch = (ScrollView)findViewById(R.id.svSearch);
        llNada = (LinearLayout)findViewById(R.id.llNada);
        edtPesquisar = (EditText)findViewById(R.id.edtPesquisar);
        if (savedInstanceState != null){
            alunoList = savedInstanceState.getParcelableArrayList("alunos");
            turmaList = savedInstanceState.getParcelableArrayList("turmas");
            questaoList = savedInstanceState.getParcelableArrayList("questoes");
            disciplinaList = savedInstanceState.getParcelableArrayList("disciplinas");
            allInfoAvaliacoes = savedInstanceState.getParcelableArrayList("avaliacoes");
            if (edtPesquisar.getText().toString().length() == 0){
                edtPesquisar.setText(savedInstanceState.getString("pesquisa"));
            }
            setView();
        }

    }
   /* public void hendleSearch( Intent intent ){
        if( Intent.ACTION_SEARCH.equalsIgnoreCase( intent.getAction() ) ){
            pesquisa = intent.getStringExtra( SearchManager.QUERY );

            getSupportActionBar().setTitle(pesquisa);
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_view, menu);

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem item = menu.findItem(R.id.action_search);

        edtPesquisar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                repositorio = new Repositorio(SearchableActivity.this);
                professor = repositorio.getLogged();
                String newText = s.toString();
                if (newText.length() > 0) {
                    turmaList = repositorio.getTurmas(professor, "and " + DataBase.NOME_TURMA + " like '%" + newText + "%' ");
                    disciplinaList = repositorio.getDisciplinas(professor, "and " + DataBase.NOME_DISCIPLINA + " like '%" + newText + "%' ");
                    allInfoAvaliacoes = repositorio.getAllInfoAvaliacoes(professor.getId(), "and (" + DataBase.ASSUNTO + " like '%" + newText + "%' or " + DataBase.DATA + " like '%" + newText + "%' or " + DataBase.BIMESTRE + " like '%" + newText + "%'  )");
                    questaoList = repositorio.getQuestoes(professor, "and " + DataBase.PERGUNTA + " like '%" + newText + "%'");
                    alunoList = repositorio.getAlunosDoProfessor(professor.getId(), "and (" + DataBase.NOME_ALUNO + " like '%" + newText + "%' or " + DataBase.MATRICULA_ALUNO + " like '%" + newText + "%' )");
                    setView();
                } else {
                    llSearch.removeAllViews();
                    llSearch.addView(llNada);
                }
                repositorio.close();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return true;
    }
    public void setView(){
        if (!(turmaList.size() == 0 && allInfoAvaliacoes.size() == 0 && disciplinaList.size() == 0 && questaoList.size() == 0 && alunoList.size()== 0)) {
            llSearch.removeAllViews();
            for (int i = 0; i <alunoList.size() ; i++) {
                if (i == 0) {
                    View tipo = getLayoutInflater().inflate(R.layout.layout_mensage, null);
                    TextView txtMensage = (TextView) tipo.findViewById(R.id.txtDisciplinaAvaliacao);
                    txtMensage.setText("Alunos");
                    llSearch.addView(tipo);
                }
                View view = getLayoutInflater().inflate(R.layout.item_see_more, null);

                TextView txtFirstLetter = (TextView) view.findViewById(R.id.txtFistLetter);
                TextView txtNome = (TextView) view.findViewById(R.id.txtNome);
                TextView txtMore = (TextView) view.findViewById(R.id.txtMore);

                final Aluno aluno = alunoList.get(i);
                txtNome.setText(aluno.getNomeAluno());
                txtMore.setText(aluno.getMatriculaAluno());
                txtFirstLetter.setText(aluno.getNomeAluno().charAt(0) + "");
                txtFirstLetter.setBackgroundResource(R.drawable.gray_circle);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(SearchableActivity.this, VerAluno.class);
                        it.putExtra("search","search");
                        it.putExtra("aluno",aluno);
                        startActivity(it);
                    }
                });
                llSearch.addView(view);
            }
            for (int i = 0; i < turmaList.size(); i++) {
                if (i == 0) {
                    View tipo = getLayoutInflater().inflate(R.layout.layout_mensage, null);
                    TextView txtMensage = (TextView) tipo.findViewById(R.id.txtDisciplinaAvaliacao);
                    txtMensage.setText("Turmas");
                    llSearch.addView(tipo);
                }
                View view = getLayoutInflater().inflate(R.layout.item_see_more, null);

                TextView txtFirstLetter = (TextView) view.findViewById(R.id.txtFistLetter);
                TextView txtNome = (TextView) view.findViewById(R.id.txtNome);
                TextView txtMore = (TextView) view.findViewById(R.id.txtMore);

                final Turma turma = turmaList.get(i);
                txtNome.setText(turma.getNomeTurma());
                txtMore.setVisibility(View.GONE);
                txtFirstLetter.setText(turma.getNomeTurma().charAt(0) + "");
                txtFirstLetter.setBackgroundResource(R.drawable.circle_second);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repositorio = new Repositorio(SearchableActivity.this);
                        AllInfo allInfo = repositorio.getTurmaDisciplinas(professor.getId(), "and " + DataBase.TABLE_TURMA_DISCIPLINA + "." + DataBase.ID_TURMA + " == " + turma.getIdTurma());
                        allInfo.setTurma(turma);
                        allInfo.setAlunos(repositorio.getAlunos(turma.getIdTurma()));
                        repositorio.close();
                        Intent it = new Intent(SearchableActivity.this, SeeMore.class);
                        it.putExtra("allInfo",allInfo);
                        startActivity(it);
                    }
                });
                llSearch.addView(view);
            }
            for (int i = 0; i < disciplinaList.size(); i++) {
                if (i == 0) {
                    View tipo = getLayoutInflater().inflate(R.layout.layout_mensage, null);
                    TextView txtMensage = (TextView) tipo.findViewById(R.id.txtDisciplinaAvaliacao);
                    txtMensage.setText("Disciplinas");
                    llSearch.addView(tipo);
                }
                View view = getLayoutInflater().inflate(R.layout.item_see_more, null);

                TextView txtFirstLetter = (TextView) view.findViewById(R.id.txtFistLetter);
                TextView txtNome = (TextView) view.findViewById(R.id.txtNome);
                TextView txtMore = (TextView) view.findViewById(R.id.txtMore);

                final Disciplina disciplina = disciplinaList.get(i);
                txtNome.setText(disciplina.getNomeDisciplina());
                txtMore.setVisibility(View.GONE);
                txtFirstLetter.setText(disciplina.getNomeDisciplina().charAt(0) + "");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        repositorio = new Repositorio(SearchableActivity.this);
                        AllInfo allInfo = repositorio.getTurmaDisciplinas(professor.getId(), "and " + DataBase.TABLE_TURMA_DISCIPLINA + "." + DataBase.ID_DISCIPLINA + " == " + disciplina.getIdDisciplina());
                        allInfo.setDisciplina(disciplina);
                        List<Aluno> alunoList = new ArrayList<>();
                        for (int j = 0;j < allInfo.getTurmas().size();j++){
                            alunoList.addAll(repositorio.getAlunos(allInfo.getTurmas().get(j).getIdTurma()));
                        }
                        repositorio.close();
                        allInfo.setAlunos(alunoList);
                        Intent it = new Intent(SearchableActivity.this, SeeMore.class);
                        it.putExtra("allInfo", allInfo);
                        startActivity(it);
                    }
                });
                llSearch.addView(view);
            }
            for (int i = 0; i < questaoList.size(); i++) {
                if (i == 0) {
                    View tipo = getLayoutInflater().inflate(R.layout.layout_mensage, null);
                    TextView txtMensage = (TextView) tipo.findViewById(R.id.txtDisciplinaAvaliacao);
                    txtMensage.setText("Questões");
                    llSearch.addView(tipo);
                }
                View view = getLayoutInflater().inflate(R.layout.item_see_more, null);

                TextView txtFirstLetter = (TextView) view.findViewById(R.id.txtFistLetter);
                TextView txtNome = (TextView) view.findViewById(R.id.txtNome);
                TextView txtMore = (TextView) view.findViewById(R.id.txtMore);

                final Questao questao = questaoList.get(i);
                txtNome.setText(questao.getPergunta());
                txtMore.setText(questao.getAlternativaCorreta());
                txtFirstLetter.setText(questao.getPergunta().charAt(0) + "");
                txtFirstLetter.setBackgroundResource(R.drawable.blue_circle);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(SearchableActivity.this, VerQuestao.class);
                        it.putExtra("verQuestao", questao);
                        startActivity(it);
                    }
                });
                llSearch.addView(view);
            }
            for (int i = 0; i < allInfoAvaliacoes.size(); i++) {
                if (i == 0) {
                    View tipo = getLayoutInflater().inflate(R.layout.layout_mensage, null);
                    TextView txtMensage = (TextView) tipo.findViewById(R.id.txtDisciplinaAvaliacao);
                    txtMensage.setText("Avaliações");
                    llSearch.addView(tipo);
                }
                View view = getLayoutInflater().inflate(R.layout.item_see_more, null);

                TextView txtFirstLetter = (TextView) view.findViewById(R.id.txtFistLetter);
                TextView txtNome = (TextView) view.findViewById(R.id.txtNome);
                TextView txtMore = (TextView) view.findViewById(R.id.txtMore);

                Avaliacao avaliacao = allInfoAvaliacoes.get(i).getAvaliacao();
                txtNome.setText(avaliacao.getAssunto());
                txtMore.setText(avaliacao.getBimestre());
                txtFirstLetter.setText(avaliacao.getAssunto().charAt(0) + "");
                txtFirstLetter.setBackgroundResource(R.drawable.red_circle);
                final int position = i;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(SearchableActivity.this,Detalhes.class);
                        it.putExtra("allInfo",allInfoAvaliacoes.get(position));
                        startActivity(it);
                    }
                });
                llSearch.addView(view);
            }
        } else {
            llSearch.removeAllViews();
            llSearch.addView(llNada);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("alunos", (ArrayList<Aluno>)alunoList);
        outState.putParcelableArrayList("turmas", (ArrayList<Turma>)turmaList);
        outState.putParcelableArrayList("questoes", (ArrayList<Questao>)questaoList);
        outState.putParcelableArrayList("disciplinas", (ArrayList<Disciplina>)disciplinaList);
        outState.putParcelableArrayList("avaliacoes", (ArrayList<AllInfo>)allInfoAvaliacoes);
        outState.putString("pesquisa", edtPesquisar.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.itmClear:
                edtPesquisar.setText("");
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

