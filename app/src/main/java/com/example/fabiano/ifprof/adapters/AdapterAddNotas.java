package com.example.fabiano.ifprof.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fabiano.ifprof.R;
import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.Aluno;
import com.example.fabiano.ifprof.model.Avaliacao;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.util.Date;

/**
 * Created by fabiano on 05/10/15.
 */
public class AdapterAddNotas extends RecyclerView.Adapter<AdapterAddNotas.MyViewHolder> {

    private List<Aluno> mList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Avaliacao avaliacao;
    private EditText edtNotaAux;
    public EditText[] editTexts;
    private CoordinatorLayout coordinatorLayout;
    private boolean clicked[];
    private RecyclerView recyclerView;
    private FloatingActionButton floatingActionButton;
    private boolean showAlert[];
    private boolean editing[];
    private boolean showSave[];
    public AdapterAddNotas(Context c, List<Aluno> l,Avaliacao avaliacao,CoordinatorLayout coordinatorLayout,FloatingActionButton floatingActionButton,RecyclerView recyclerView){
        mList = l;
        this.floatingActionButton = floatingActionButton;
        mContext = c;
        editTexts = new EditText[mList.size()];
        clicked = new boolean[mList.size()];
        showAlert = new boolean[mList.size()];
        showSave = new boolean[mList.size()];
        editing = new boolean[mList.size()];
        mLayoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.avaliacao = avaliacao;
        this.coordinatorLayout = coordinatorLayout;
        this.recyclerView = recyclerView;
        for (int i = 0; i <mList.size() ; i++) {
            if ( !mList.get(i).getNota().trim().equals("")){
                somethingSaved = true;
                break;
            }
        }
        if (!somethingSaved){
            // floatingActionButton.attachToRecyclerView(recyclerView);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.item_notas,viewGroup,false);
        MyViewHolder mvh =  new MyViewHolder(view,new MyCustomEditTextListener());
        return mvh;
    }
    boolean somethingSaved = false;
    boolean notSaved = false;
    List<Aluno> alunoList = new ArrayList<>();
    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        editTexts[position] = myViewHolder.edtNota;

        if(clicked[position] == false && !mList.get(position).getNota().trim().equals("")){
            myViewHolder.ivEditarNota.setVisibility(View.VISIBLE);
            myViewHolder.llShowNotas.setVisibility(View.VISIBLE);
            myViewHolder.edtNota.setVisibility(View.GONE);
            myViewHolder.txtNota.setText(mList.get(position).getNota());
            showSave[position] = true;
            if (notSaved == false){
                floatingActionButton.setVisibility(View.GONE);
            }
        }
        if (somethingSaved && mList.get(position).getNota().trim().equals("")){
            myViewHolder.txtAlertNota.setVisibility(View.VISIBLE);
            alunoList.add(mList.get(position));
            showAlert[position] = true;
            notSaved = true;
            floatingActionButton.setVisibility(View.VISIBLE);
            //floatingActionButton.attachToRecyclerView(recyclerView);
        }
        final String nota = mList.get(position).getNota();
        myViewHolder.ivEnviarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String and = "and "+DataBase.ID_ALUNO+" == "+mList.get(position).getIdALuno();
                if (nota.equals(myViewHolder.edtNota.getText().toString())){
                    AlertsAndControl.snackBar(coordinatorLayout,"Mesma nota da anterior!");
                }else{
                    try {
                        Repositorio repositorio = new Repositorio(mContext);
                        repositorio.update(DataBase.TABLE_NOTA, DataBase.NOTA, myViewHolder.edtNota.getText().toString(), DataBase.ID_AVALIACAO, avaliacao.getIdAvaliacao(), and);
                        repositorio.close();
                        AlertsAndControl.snackBar(coordinatorLayout, "Atualizado com sucesso!");
                    }catch (SQLiteException e){
                        AlertsAndControl.alert(mContext,"Ocorreu um erro.","Erro");
                    }
                }
                myViewHolder.ivEditarNota.setVisibility(View.VISIBLE);
                myViewHolder.ivEnviarNota.setVisibility(View.GONE);
                myViewHolder.llShowNotas.setVisibility(View.VISIBLE);
                myViewHolder.edtNota.setVisibility(View.GONE);
                myViewHolder.edtNota.setHint("  Nota   ");
                myViewHolder.txtNota.setText(mList.get(position).getNota());
            }
        });

        myViewHolder.ivEditarNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewHolder.ivEnviarNota.setVisibility(View.VISIBLE);
                myViewHolder.ivEditarNota.setVisibility(View.GONE);
                myViewHolder.llShowNotas.setVisibility(View.GONE);
                myViewHolder.edtNota.setVisibility(View.VISIBLE);
                myViewHolder.edtNota.setHint("Nota");
                myViewHolder.edtNota.requestFocus();
                editing[position] = true;
            }
        });

        myViewHolder.nomeAluno.setText(mList.get(position).getNomeAluno());
        myViewHolder.matricula.setText(mList.get(position).getMatriculaAluno());
        myViewHolder.txtFistLetter.setText(myViewHolder.nomeAluno.getText().charAt(0)+"");
        myViewHolder.myCustomEditTextListener.updatePosition(position);
        myViewHolder.edtNota.setText(mList.get(position).getNota());
        if (editing[position]){
            myViewHolder.ivEnviarNota.setVisibility(View.VISIBLE);
            myViewHolder.ivEditarNota.setVisibility(View.GONE);
            myViewHolder.llShowNotas.setVisibility(View.GONE);
            myViewHolder.edtNota.setVisibility(View.VISIBLE);
            myViewHolder.edtNota.setHint("Nota");
            myViewHolder.edtNota.requestFocus();
        }else{
            if (showAlert[position]){
                myViewHolder.txtAlertNota.setVisibility(View.VISIBLE);
            }else{
                myViewHolder.txtAlertNota.setVisibility(View.GONE);
            }
            if (showSave[position]){
                myViewHolder.ivEditarNota.setVisibility(View.VISIBLE);
                myViewHolder.llShowNotas.setVisibility(View.VISIBLE);
                myViewHolder.edtNota.setVisibility(View.GONE);
                myViewHolder.ivEnviarNota.setVisibility(View.GONE);
                myViewHolder.txtNota.setText(mList.get(position).getNota());
            }else{
                myViewHolder.ivEditarNota.setVisibility(View.GONE);
                myViewHolder.llShowNotas.setVisibility(View.GONE);
                myViewHolder.edtNota.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    public void addlisItem(Aluno a,int position){
        mList.add(a);
        notifyItemInserted(position);

    }
    boolean newAluno = false;
    public List<Aluno> getItems(){
        if (alunoList.size() > 0 ){
            newAluno = true;
            return alunoList;
        }else{
            return mList;
        }
    }
    public boolean newAluno(){
        return  newAluno;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nomeAluno;
        public TextView matricula;
        public EditText edtNota;
        private LinearLayout llShowNotas;
        private ImageView ivEditarNota;
        private ImageView ivEnviarNota;
        private TextView txtNota;
        private TextView txtAlertNota;
        private TextView txtFistLetter;
        public MyCustomEditTextListener myCustomEditTextListener;
        public MyViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            nomeAluno = (TextView)itemView.findViewById(R.id.txtNomeNota);
            matricula = (TextView)itemView.findViewById(R.id.txtMatriculaNota);
            edtNota = (EditText)itemView.findViewById(R.id.edtNota);
            llShowNotas = (LinearLayout)itemView.findViewById(R.id.llShowNotas);
            ivEditarNota = (ImageView)itemView.findViewById(R.id.ivEditarNota);
            txtNota = (TextView)itemView.findViewById(R.id.txtNota);
            ivEnviarNota = (ImageView)itemView.findViewById(R.id.ivEnviarNota);
            txtAlertNota = (TextView)itemView.findViewById(R.id.txtAlertNota);
            txtFistLetter = (TextView)itemView.findViewById(R.id.txtFistLetter);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.edtNota.addTextChangedListener(myCustomEditTextListener);
        }
    }
    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            clicked[position] = true;
            if (!charSequence.toString().equals("")){
                if (Float.parseFloat(charSequence.toString()) > Float.parseFloat(avaliacao.getValor())) {
                    AlertsAndControl.snackBar(coordinatorLayout, "A nota máxima é "+avaliacao.getValor()+"!");
                    editTexts[position].setText(avaliacao.getValor());
                    mList.get(position).setNota(editTexts[position].getText().toString());
                }else {
                    mList.get(position).setNota(charSequence.toString());
                }
            } else {
                mList.get(position).setNota(charSequence.toString());
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
}