package com.developer.fabiano.ifprof.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.fabiano.ifprof.ActivityAddNotas;
import com.developer.fabiano.ifprof.R;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Aluno;
import com.developer.fabiano.ifprof.model.Avaliacao;
import com.developer.fabiano.ifprof.model.Nota;

import java.util.ArrayList;
import java.util.List;

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
    private boolean seleciionarAluno;
    private AllInfo allInfo;

    public AdapterAddNotas(Context c, List<Aluno> l,AllInfo allInfo,CoordinatorLayout coordinatorLayout,FloatingActionButton floatingActionButton,RecyclerView recyclerView,boolean seleciionarAluno){
        this.seleciionarAluno = seleciionarAluno;
        mList = l;
        this.floatingActionButton = floatingActionButton;
        mContext = c;
        editTexts = new EditText[mList.size()];
        clicked = new boolean[mList.size()];
        showAlert = new boolean[mList.size()];
        showSave = new boolean[mList.size()];
        editing = new boolean[mList.size()];
        mLayoutInflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.avaliacao = allInfo.getAvaliacao();
        this.coordinatorLayout = coordinatorLayout;
        this.recyclerView = recyclerView;
        this.allInfo = allInfo;
        int saves = 0;
        for (int i = 0; i <mList.size() ; i++) {
            if ( !mList.get(i).getNota().trim().equals("")){
                somethingSaved = true;
                saves++;
            }
        }

        if (saves == mList.size()) {
            try {
                ActivityAddNotas.getMenuItem().setVisible(false);
            } catch (Exception e) {}
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

        }
        if (somethingSaved && mList.get(position).getNota().trim().equals("")){
            myViewHolder.txtAlertNota.setVisibility(View.VISIBLE);
            alunoList.add(mList.get(position));
            showAlert[position] = true;
            notSaved = true;
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
            if (!seleciionarAluno){
                myViewHolder.edtNota.setVisibility(View.VISIBLE);
            }
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
                if (!seleciionarAluno){
                    myViewHolder.edtNota.setVisibility(View.VISIBLE);
                }
            }
        }
        if (seleciionarAluno){
            myViewHolder.edtNota.setVisibility(View.GONE);
            editTexts[position] =  myViewHolder.edtNota;
            myViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(myViewHolder.llShowNotas.getVisibility() == View.VISIBLE){
                        AlertsAndControl.snackBar(coordinatorLayout,"Esse aluno já tem nota!");
                    }else{
                        List<Nota> alunoSelecionado = new ArrayList<Nota>();
                        allInfo.getNota().setAluno(mList.get(position));
                        allInfo.getNota().getAluno().setNota(allInfo.getNota().getNota()+"");
                        alunoSelecionado.add(allInfo.getNota());
                        Repositorio repositorio = new Repositorio(mContext);
                        repositorio.insertNota(alunoSelecionado);

                        List<Nota> notaListSaved = repositorio.getNotas(allInfo.getAvaliacao().getIdAvaliacao(), allInfo.getTurma().getIdTurma());
                        for (int i = 0; i < notaListSaved.size(); i++) {
                            for (int j = 0; j < mList.size(); j++) {
                                if (notaListSaved.get(i).getAluno().getIdALuno() == mList.get(j).getIdALuno()) {
                                    mList.set(j, notaListSaved.get(i).getAluno());
                                }
                            }
                        }
                        repositorio.close();
                        recyclerView.setAdapter(new AdapterAddNotas(mContext, mList, allInfo, coordinatorLayout, floatingActionButton, recyclerView, false));
                    }
                }
            });
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
        private View view;
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
            view = itemView;
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