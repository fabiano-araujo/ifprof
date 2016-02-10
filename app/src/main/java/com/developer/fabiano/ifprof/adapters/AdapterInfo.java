package com.developer.fabiano.ifprof.adapters;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.developer.fabiano.ifprof.ActivityAddAlunos;
import com.developer.fabiano.ifprof.ActivityAddAvaliacoes;
import com.developer.fabiano.ifprof.ActivityAddDisciplinas;
import com.developer.fabiano.ifprof.ActivityAddFaltas;
import com.developer.fabiano.ifprof.ActivityAddNotas;
import com.developer.fabiano.ifprof.ActivityAddTurmas;
import com.developer.fabiano.ifprof.Detalhes;
import com.developer.fabiano.ifprof.ActivityHistorico;
import com.developer.fabiano.ifprof.R;
import com.developer.fabiano.ifprof.SeeMore;
import com.developer.fabiano.ifprof.VerAluno;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Aluno;
import com.developer.fabiano.ifprof.model.Avaliacao;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Turma;

import java.util.List;

/**
 * Created by fabiano on 05/10/15.
 */
public class AdapterInfo extends RecyclerView.Adapter<AdapterInfo.MyViewHolder> {

    private List<AllInfo> mList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private int tipo;
    public static int SHOWTURMAS = 1;
    public static int SHOWDISCIPLINAS = 2;
    public static int SHOWALUNOS = 3;
    public static int SHOWFALTAS = 4;
    public static int SHOWAVALIACOES = 5;
    public static int SHOWNOTAS = 8;
    public static int SEEMORE = 6;
    public static int HISTORICO = 7;
    private Professor professorLogged;
    private Repositorio repositorio;
    private RecyclerView mRecyclerView;
    private ScrollView mScrollView;
    private FloatingActionButton floatingActionButton;
    private CharSequence optionsOnLongClick[] = {"Detalhes","Editar","Excluir"};

    public AdapterInfo(Context c, List<AllInfo> l,int tipo,ScrollView scrollView,RecyclerView recyclerView,FloatingActionButton floatingActionButton){
        mContext = c;
        mList = l;
        mLayoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tipo = tipo;
        mRecyclerView = recyclerView;
        this.floatingActionButton = floatingActionButton;
        mScrollView = scrollView;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
         View view = null;
         if (tipo == SHOWDISCIPLINAS ||tipo == SHOWTURMAS){
            view = mLayoutInflater.inflate(R.layout.item_show_data,viewGroup,false);
         }else if (tipo == SHOWALUNOS || tipo == SEEMORE){
            view = mLayoutInflater.inflate(R.layout.item_alunos,viewGroup,false);
         }else if(tipo == SHOWFALTAS){
             view = mLayoutInflater.inflate(R.layout.item_show_faltas,viewGroup,false);
         }else if (tipo == SHOWAVALIACOES || tipo == SHOWNOTAS){
             view = mLayoutInflater.inflate(R.layout.item_show_avaliacao,viewGroup,false);
         }
         MyViewHolder mvh =  new MyViewHolder(view);
        repositorio.close();
        return mvh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        if(tipo == SHOWFALTAS){
            final AllInfo allInfo = mList.get(position);
            final Avaliacao avaliacao = allInfo.getAvaliacao();
            myViewHolder.txtNome.setText(allInfo.getDisciplina().getNomeDisciplina());
            myViewHolder.txtQtdAlunos.setText(allInfo.getQtdAlunos() + "");
            myViewHolder.btnSeeMore.setText("Chamada");
            myViewHolder.btnEditarTurma.setText("Histórico");
            List<Turma> minhasTurmas = allInfo.getTurmas();
            String res ="";
            if (minhasTurmas.size() == 0){
                myViewHolder.txtInfo2More.setText("Nenhuma turma.");
            }else{
                for (int i = 0;i < minhasTurmas.size();i++){
                    res += minhasTurmas.get(i).getNomeTurma()+", ";
                }
                myViewHolder.txtInfo2More.setText(res.substring(0,res.length()-2)+".");
            }
            myViewHolder.btnEditarTurma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ActivityHistorico.class);
                    it.putExtra("allInfo", allInfo);
                    mContext.startActivity(it);
                }
            });
            myViewHolder.btnSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext,ActivityAddFaltas.class);
                    it.putExtra("allInfo", allInfo);
                    mContext.startActivity(it);
                    ((Activity) mContext).finish();
                }
            });
        }else if (tipo == SHOWAVALIACOES || tipo == SHOWNOTAS ){
            final AllInfo allInfo = mList.get(position);
            final Avaliacao avaliacao = allInfo.getAvaliacao();
            myViewHolder.txtNome.setText(avaliacao.getAssunto());
            myViewHolder.txtInfo2More.setText(allInfo.getDisciplina().getNomeDisciplina());
            myViewHolder.txtQtdInfo.setText(avaliacao.getBimestre());
            myViewHolder.txtSubTitle.setText(avaliacao.getTipo());
            myViewHolder.txtDate.setText(avaliacao.getData());
            final Activity activity = ((Activity) mContext);
            if (tipo == SHOWNOTAS){
                myViewHolder.btnEditarTurma.setText("Notas");
                myViewHolder.btnEditarTurma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(mContext, ActivityAddNotas.class);
                        it.putExtra("allInfo", allInfo);
                        mContext.startActivity(it);
                        activity.finish();
                    }
                });
                myViewHolder.tbItemInfo.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(android.view.MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.itmDelete:
                                break;
                            case R.id.itmEditar:
                                Intent it = new Intent(mContext, ActivityAddAvaliacoes.class);
                                it.putExtra("avaliacao", avaliacao);
                                mContext.startActivity(it);
                                activity.finish();
                        }
                        return true;

                    }
                });
            }else{
                myViewHolder.btnEditarTurma.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(mContext, ActivityAddAvaliacoes.class);
                        it.putExtra("avaliacao", avaliacao);
                        mContext.startActivity(it);
                        activity.finish();
                    }
                });
            }
            Turma turma = allInfo.getTurma();
            myViewHolder.txtInfo3More.setText(turma.getNomeTurma()+".");

            myViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mensage;
                    if (allInfo.getQtdAlunos() == 0){
                        mensage = "Você tem ser certeza que deseja excluir essa Avalição?";

                    }else{
                        mensage = "Há turmas com essa avaliação, excluir mesmo assim?";
                    }
                    AlertDialog.Builder delete = new AlertDialog.Builder(mContext);
                    delete.setTitle(avaliacao.getAssunto());
                    delete.setMessage(mensage).setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            repositorio = new Repositorio(mContext);
                            removeListItem(position);
                            itemIgualsToZero();
                            String where ="where "+DataBase.ID_AVALIACAO+" == "+avaliacao.getIdAvaliacao();
                            repositorio.delete(DataBase.TABLE_AVALIACAO,where);
                            repositorio.close();
                        }
                    }).setNegativeButton("Não",null).show();
                }
            });
            myViewHolder.btnSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, Detalhes.class);
                    allInfo.setAvaliacao(avaliacao);
                    it.putExtra("allInfo",allInfo);
                    mContext.startActivity(it);
                }
            });

        }else if (tipo == SHOWTURMAS){
            final AllInfo allInfo = mList.get(position);
            myViewHolder.txtNome.setText(allInfo.getTurma().getNomeTurma());
            myViewHolder.txtQtdAlunos.setText(allInfo.getQtdAlunos()+"");
            myViewHolder.btnEditarTurma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ActivityAddTurmas.class);
                    it.putExtra("nomeTurma", myViewHolder.txtNome.getText());
                    mContext.startActivity(it);
                    ((Activity) mContext).finish();
                }
            });
            List<Disciplina> minhasDiciplinas = allInfo.getDiciplinas();
            String res ="";
            if (minhasDiciplinas.size() == 0){
                myViewHolder.txtInfo2More.setText("Nenhuma disciplina.");
            }else{
                for (int i = 0;i < minhasDiciplinas.size();i++){
                    res += minhasDiciplinas.get(i).getNomeDisciplina()+", ";
                }
                myViewHolder.txtInfo2More.setText(res.substring(0,res.length()-2)+".");
            }
            myViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mensage;
                    if (allInfo.getQtdAlunos() == 0) {
                        mensage = "Você tem ser certeza que deseja excluir essa turma?";

                    } else {
                        mensage = "Há alunos nessa turma, excluir mesmo assim?";
                    }
                    AlertDialog.Builder delete = new AlertDialog.Builder(mContext);
                    delete.setTitle(allInfo.getTurma().getNomeTurma());
                    delete.setMessage(mensage).setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            repositorio = new Repositorio(mContext);
                            removeListItem(position);
                            itemIgualsToZero();
                            repositorio.delete(DataBase.TABLE_TURMA, "where " + DataBase.ID_TURMA + " == " + allInfo.getTurma().getIdTurma());
                            repositorio.delete(DataBase.TABLE_TURMA_DISCIPLINA, "where " + DataBase.ID_TURMA + " == " + allInfo.getTurma().getIdTurma());
                            repositorio.delete(DataBase.TABLE_FALTA, "where " + DataBase.ID_TURMA + " == " + allInfo.getTurma().getIdTurma());
                            repositorio.delete(DataBase.TABLE_AVALIACAO, "where " + DataBase.ID_TURMA + " == " + allInfo.getTurma().getIdTurma());
                            repositorio.delete(DataBase.TABLE_ALUNO, "where " + DataBase.ID_TURMA + " == " + allInfo.getTurma().getIdTurma());
                            repositorio.close();
                        }
                    }).setNegativeButton("Não", null).show();
                }
            });
            myViewHolder.btnSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, SeeMore.class);
                    it.putExtra("allInfo", allInfo);
                    mContext.startActivity(it);
                }
            });
        }else if(tipo == SHOWDISCIPLINAS){
            final AllInfo allInfo = mList.get(position);
            final Disciplina disciplina = allInfo.getDisciplina();
            myViewHolder.txtNome.setText(disciplina.getNomeDisciplina());
            myViewHolder.txtQtdAlunos.setText(allInfo.getQtdAlunos() + "");
            List<Turma> minhasTurmas = allInfo.getTurmas();

            myViewHolder.btnEditarTurma.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, ActivityAddDisciplinas.class);
                    it.putExtra("nomeDisciplina", allInfo);
                    mContext.startActivity(it);
                    ((Activity) mContext).finish();
                }
            });
            String res ="";
            if (minhasTurmas.size() == 0){
                myViewHolder.txtInfo2More.setText("Nenhuma turma.");
            }else{
                for (int i = 0;i < minhasTurmas.size();i++){
                    res += minhasTurmas.get(i).getNomeTurma()+", ";
                }
                myViewHolder.txtInfo2More.setText(res.substring(0,res.length()-2)+".");
            }
            myViewHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String mensage;
                    if (allInfo.getTurmas().size() == 0) {
                        mensage = "Você tem ser certeza que deseja excluir essa disciplina?";
                    } else {
                        mensage = "Há turmas com essa disciplina, excluir mesmo assim?";
                    }
                    AlertDialog.Builder delete = new AlertDialog.Builder(mContext);
                    delete.setTitle(allInfo.getDisciplina().getNomeDisciplina());
                    delete.setMessage(mensage).setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            repositorio = new Repositorio(mContext);
                            removeListItem(position);
                            itemIgualsToZero();
                            int idDisciplia = repositorio.getDisciplinas(professorLogged, "and " + DataBase.NOME_DISCIPLINA + " == '" + disciplina.getNomeDisciplina() + "'").get(0).getIdDisciplina();
                            repositorio.delete(DataBase.TABLE_DISCIPLINA, "where " + DataBase.ID_DISCIPLINA + " == " + idDisciplia);
                            repositorio.delete(DataBase.TABLE_TURMA_DISCIPLINA, "where " + DataBase.ID_DISCIPLINA + " == " + idDisciplia);
                            repositorio.delete(DataBase.TABLE_FALTA, "where " + DataBase.ID_DISCIPLINA + " == " + idDisciplia);
                            repositorio.delete(DataBase.TABLE_AVALIACAO,"where " + DataBase.ID_DISCIPLINA + " == " + idDisciplia);
                            repositorio.close();
                        }
                    }).setNegativeButton("Não", null).show();
                }
            });
            myViewHolder.btnSeeMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, SeeMore.class);
                    it.putExtra("allInfo", allInfo);
                    mContext.startActivity(it);
                }
            });
        }else if (tipo == SHOWALUNOS){
            final Aluno aluno = mList.get(position).getAluno();
            myViewHolder.txtNome.setText(aluno.getNomeAluno());
            myViewHolder.txtMatriculaAluno.setText(aluno.getMatriculaAluno());
            myViewHolder.txtFistLetter.setText(myViewHolder.txtNome.getText().charAt(0)+"");

            myViewHolder.tbItemInfo.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(android.view.MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.itmDelete:
                            AlertDialog.Builder delete = new AlertDialog.Builder(mContext);
                            delete.setTitle(aluno.getNomeAluno());
                            delete.setMessage("Excluir este aluno?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    repositorio = new Repositorio(mContext);
                                    removeListItem(position);
                                    itemIgualsToZero();
                                    repositorio.delete(DataBase.TABLE_ALUNO, " where " + DataBase.NOME_ALUNO + " == '" + aluno.getNomeAluno() + "' and " + DataBase.ID_PROFESSOR + " == " + professorLogged.getId());
                                    repositorio.close();
                                }
                            }).setNegativeButton("Não", null).show();
                            break;
                        case R.id.itmEditar:
                            Intent it = new Intent(mContext, ActivityAddAlunos.class);
                            it.putExtra("alter", aluno);
                            mContext.startActivity(it);
                            ((Activity) mContext).finish();
                            break;
                        case R.id.itmDetalhes:
                            Intent itAluno = new Intent(mContext, VerAluno.class);
                            itAluno.putExtra("aluno", aluno);
                            mContext.startActivity(itAluno);
                            ((Activity) mContext).finish();
                            break;
                    }
                    return true;

                }
            });
            myViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent itAluno = new Intent(mContext, VerAluno.class);
                    itAluno.putExtra("aluno", aluno);
                    mContext.startActivity(itAluno);
                    ((Activity) mContext).finish();
                }
            });
            myViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    android.app.AlertDialog.Builder options = new android.app.AlertDialog.Builder(mContext);
                    options.setItems(optionsOnLongClick, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 2:
                                            AlertDialog.Builder delete = new AlertDialog.Builder(mContext);
                                            delete.setTitle(aluno.getNomeAluno());
                                            delete.setMessage("Excluir este aluno?").setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    repositorio = new Repositorio(mContext);
                                                    removeListItem(position);
                                                    itemIgualsToZero();
                                                    repositorio.delete(DataBase.TABLE_ALUNO, " where " + DataBase.NOME_ALUNO + " == '" + aluno.getNomeAluno() + "' and " + DataBase.ID_PROFESSOR + " == " + professorLogged.getId());
                                                    repositorio.close();
                                                }
                                            }).setNegativeButton("Não", null).show();
                                            break;
                                        case 1:
                                            Intent it = new Intent(mContext, ActivityAddAlunos.class);
                                            it.putExtra("alter", aluno);
                                            mContext.startActivity(it);
                                            ((Activity) mContext).finish();
                                            break;
                                        case 0:
                                            Intent itAluno = new Intent(mContext, VerAluno.class);
                                            itAluno.putExtra("aluno", aluno);
                                            mContext.startActivity(itAluno);
                                            ((Activity) mContext).finish();
                                            break;
                                    }
                                }
                            }

                    ).show();
                    return true;
                }
            });
        }else if(tipo == SEEMORE){
            myViewHolder.txtNome.setText(mList.get(position).getNome());
            myViewHolder.txtMatriculaAluno.setVisibility(View.GONE);
            myViewHolder.txtFistLetter.setText(myViewHolder.txtNome.getText().charAt(0)+"");
        }
    }
    public boolean itemIgualsToZero(){
        if (getItemCount() == 0 && mScrollView != null){
            mRecyclerView.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            if (floatingActionButton != null){
                floatingActionButton.setVisibility(View.GONE);
            }
            return true;
        }else{
            return false;
        }
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }
    public void addlisItem(AllInfo allInfo,int position){
        mList.add(allInfo);
        notifyItemInserted(position);

    }
    public void removeListItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
        notifyDataSetChanged();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView txtNome;
        public Button btnEditarTurma;
        public Button btnSeeMore;
        public TextView txtQtdAlunos;
        public TextView txtQtdInfo;
        public TextView txtMatriculaAluno;
        public TextView txtFistLetter;
        public Toolbar tbItemInfo;
        public ImageView ivDelete;
        public TextView txtInfo3;
        public TextView txtInfo3More;
        public TextView txtInfo2;
        public TextView txtInfo2More;
        public TextView txtSubTitle;
        public LinearLayout llMoreAvaliacoes;
        public FrameLayout flSubTitle;
        public TextView txtDate;
        public View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            if (tipo == SHOWDISCIPLINAS ||tipo == SHOWTURMAS || tipo == SHOWAVALIACOES||tipo == SHOWFALTAS || tipo == SHOWNOTAS){
                txtNome = (TextView)itemView.findViewById(R.id.txtNomeTurma);
                txtQtdAlunos = (TextView)itemView.findViewById(R.id.txtQtdAlunos);
                btnEditarTurma = (Button)itemView.findViewById(R.id.btnEditarTurma);
                btnSeeMore = (Button)itemView.findViewById(R.id.btnSeeMore);
                txtQtdInfo = (TextView)itemView.findViewById(R.id.txtQtdInfo);
                tbItemInfo = (Toolbar)itemView.findViewById(R.id.tbItemInfo);
                tbItemInfo.inflateMenu(R.menu.aluno_menu);
                txtSubTitle = (TextView)itemView.findViewById(R.id.txtSubTitle);
                ivDelete = (ImageView)itemView.findViewById(R.id.ivDelete);
                txtInfo2 = (TextView)itemView.findViewById(R.id.txtInfo2);
                txtInfo2More = (TextView)itemView.findViewById(R.id.txtInfo2More);
                txtInfo3 = (TextView)itemView.findViewById(R.id.txtInfo3);
                txtInfo3More = (TextView)itemView.findViewById(R.id.txtInfo3More);
                llMoreAvaliacoes = (LinearLayout)itemView.findViewById(R.id.llMoreAvalicoes);
                flSubTitle = (FrameLayout)itemView.findViewById(R.id.flSubTitle);
                txtDate = (TextView)itemView.findViewById(R.id.txtDate);
                if (tipo != SHOWALUNOS){
                    tbItemInfo.setVisibility(View.GONE);
                }
                if (tipo == SHOWDISCIPLINAS|| tipo == SHOWFALTAS){
                    txtInfo2.setText("Turmas:");
                }
                if (tipo == SHOWAVALIACOES){
                    llMoreAvaliacoes.setVisibility(View.VISIBLE);
                    flSubTitle.setVisibility(View.VISIBLE);
                }
            }else if (tipo == SHOWALUNOS || tipo == SEEMORE){
                txtNome = (TextView)itemView.findViewById(R.id.txtNomeAluno);
                txtMatriculaAluno = (TextView)itemView.findViewById(R.id.txtMatriculaAluno);
                txtFistLetter = (TextView)itemView.findViewById(R.id.txtFistLetter);
                tbItemInfo = (Toolbar)itemView.findViewById(R.id.tbItemAluno);
                tbItemInfo.inflateMenu(R.menu.aluno_menu);
                view = itemView;
            }
            try{
                repositorio = new Repositorio(mContext);
                professorLogged = repositorio.getLogged();
            }catch (SQLiteException e){
                AlertsAndControl.alert(mContext,e.getMessage(),"Erro");
            }
        }
    }
}