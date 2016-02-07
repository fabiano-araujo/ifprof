package com.example.fabiano.ifprof.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.fabiano.ifprof.ActivityAddQuestao;
import com.example.fabiano.ifprof.R;
import com.example.fabiano.ifprof.VerQuestao;
import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.Questao;

import java.util.HashMap;
import java.util.List;

/**
 * Created by fabiano on 08/12/15.
 */
public class AdapterQuestoes extends RecyclerView.Adapter<AdapterQuestoes.MyViewHolder> {

    private List<Questao> mList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private static int QUESTOES = 0;
    private static int PROVAS = 1;
    private CharSequence optionsOnLongClick[] = {"Ver","Editar","Excluir"};
    private boolean verQuestoes;
    HashMap<Integer, Questao> questoesSelecionadas = new HashMap<>();
    private RecyclerView recyclerView;
    private ScrollView scrollView;
    private FloatingActionButton floatingActionButton;
    public AdapterQuestoes(Context c, List<Questao> l,boolean tipo,HashMap hashMap,ScrollView scrollView,RecyclerView recyclerView,FloatingActionButton floatingActionButton) {
        mList = l;
        mContext = c;
        mLayoutInflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        verQuestoes = tipo;
        questoesSelecionadas = hashMap;
        this.recyclerView = recyclerView;
        this.scrollView = scrollView;
        this.floatingActionButton = floatingActionButton;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.item_show_questoes, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(view);
        return mvh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        final Questao questao = mList.get(position);
        myViewHolder.txtPergunta.setText(questao.getPergunta());
        if (questao.getAlternativaCorreta().equals(" a)")){
            myViewHolder.txtAlternativaCorreta.setText(questao.getAlternativaCorreta()+" "+questao.getAlternativaA());
        }else if (questao.getAlternativaCorreta().equals(" b)")){
            myViewHolder.txtAlternativaCorreta.setText(questao.getAlternativaCorreta()+" "+questao.getAlternativaB());
        }else if (questao.getAlternativaCorreta().equals(" c)")){
            myViewHolder.txtAlternativaCorreta.setText(questao.getAlternativaCorreta()+" "+questao.getAlternativaC());
        }else if (questao.getAlternativaCorreta().equals(" d)")){
            myViewHolder.txtAlternativaCorreta.setText(questao.getAlternativaCorreta()+" "+questao.getAlternativaD());
        }else if (questao.getAlternativaCorreta().equals(" e)")){
            myViewHolder.txtAlternativaCorreta.setText(questao.getAlternativaCorreta()+" "+questao.getAlternativaE());
        }
        if (questoesSelecionadas.containsKey(position)){
            myViewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.colorThird));
        }else{
            myViewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
        }
        myViewHolder.txtLetra.setText(questao.getAlternativaCorreta());
        myViewHolder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verQuestoes){
                    if (questoesSelecionadas.containsKey(position)){
                        questoesSelecionadas.remove(position);
                        myViewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                    }else{
                        Questao questaosl = mList.get(position);
                        questaosl.setPosition(position);
                        questoesSelecionadas.put(position,questaosl);
                        myViewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.colorThird));
                    }
                }else{
                    Intent it = new Intent(mContext, VerQuestao.class);
                    it.putExtra("verQuestao",questao);
                    mContext.startActivity(it);
                }
            }
        });
        myViewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (verQuestoes){
                    if (questoesSelecionadas.containsKey(position)){
                        questoesSelecionadas.remove(position);
                        myViewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.colorAccent));
                    }else{
                        Questao questaosl = mList.get(position);
                        questaosl.setPosition(position);
                        questoesSelecionadas.put(position,questaosl);
                        myViewHolder.view.setBackgroundColor(mContext.getResources().getColor(R.color.colorThird));
                    }
                }else {
                    AlertDialog.Builder options = new AlertDialog.Builder(mContext);
                    options.setItems(optionsOnLongClick, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent ver = new Intent(mContext, VerQuestao.class);
                                    ver.putExtra("verQuestao", questao);
                                    mContext.startActivity(ver);
                                    break;
                                case 1:
                                    Intent it = new Intent(mContext, ActivityAddQuestao.class);
                                    questao.setIntent(1);
                                    it.putExtra("alter", questao);
                                    mContext.startActivity(it);
                                    break;
                                case 2:
                                    try {
                                        Repositorio repositorio = new Repositorio(mContext);
                                        repositorio.delete(DataBase.TABLE_QUESTAO, "where " + DataBase.ID_QUESTAO + " == " + questao.getIdQuestao());
                                        repositorio.close();
                                        removeListItem(position);
                                    }catch (Exception e){
                                        AlertsAndControl.alert(mContext,"Ocorreu um erro tente novamente!","Aviso");
                                    }

                                    break;
                            }
                            }
                        }

                        ).

                        show();
                    }
                    return true;
            }
        });
    }
    public boolean itemIgualsToZero(){
        if (getItemCount() == 0){
            recyclerView.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
            floatingActionButton.setVisibility(View.GONE);
            return true;
        }else{
            return false;
        }
    }
    public void removeListItem(int position){
        mList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mList.size());
        notifyDataSetChanged();
        itemIgualsToZero();
    }
    public HashMap getQuestoes(){
        return questoesSelecionadas;
    }
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addlisItem(Questao a, int position) {
        mList.add(a);
        notifyItemInserted(position);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtPergunta;
        private TextView txtAlternativaCorreta;
        private TextView txtLetra;
        private View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            txtPergunta = (TextView)itemView.findViewById(R.id.txtPergunta);
            txtAlternativaCorreta = (TextView)itemView.findViewById(R.id.txtAlternativaCorreta);
            txtLetra = (TextView)itemView.findViewById(R.id.txtLetra);
            view = itemView;
        }
    }
}