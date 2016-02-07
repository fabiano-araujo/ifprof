package com.example.fabiano.ifprof.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fabiano.ifprof.R;
import com.example.fabiano.ifprof.model.Aluno;
import com.example.fabiano.ifprof.model.Disciplina;
import com.example.fabiano.ifprof.model.Falta;

import java.util.ArrayList;
import java.util.List;

public class AdapterAddFaltas extends RecyclerView.Adapter<AdapterAddFaltas.MyViewHolder> {

    private List<Aluno> mList;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private View view;
    private EditText edtAulasMis;
    private CoordinatorLayout coordinatorLayout;
    public EditText[] editTexts;

    public AdapterAddFaltas(Context c, List<Aluno> l,CoordinatorLayout coordinatorLayout,EditText edtAulasMinis){
        mContext = c;
        mList = l;
        editTexts = new EditText[mList.size()];
        mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        edtAulasMis = edtAulasMinis;
        this.coordinatorLayout = coordinatorLayout;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mLayoutInflater.inflate(R.layout.item_faltas, viewGroup, false);
        MyViewHolder mvh =  new MyViewHolder(view,new MyCustomEditTextListener());
        return mvh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        final Aluno aluno = mList.get(position);
        editTexts[position] = myViewHolder.edtFaltas;
        myViewHolder.nomeAluno.setText(aluno.getNomeAluno());
        myViewHolder.matricula.setText(aluno.getMatriculaAluno());

        myViewHolder.txtFirstLetter.setText(myViewHolder.nomeAluno.getText().charAt(0) + "");
        myViewHolder.myCustomEditTextListener.updatePosition(position);
        myViewHolder.edtFaltas.setText(mList.get(position).getFaltas());
        myViewHolder.checkBox.setChecked(aluno.isSelected());
        myViewHolder.checkBox.setTag(aluno);
        myViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                Aluno alunoInfo = (Aluno) cb.getTag();
                alunoInfo.setIsSelected(cb.isChecked());
                aluno.setIsSelected(cb.isChecked());
                if (cb.isChecked()){
                    if(edtAulasMis.getText().toString().equals("")){
                        myViewHolder.myCustomEditTextListener.updatePosition(position);
                        myViewHolder.edtFaltas.setText("0");
                    }else{
                        myViewHolder.myCustomEditTextListener.updatePosition(position);
                        myViewHolder.edtFaltas.setText(edtAulasMis.getText());
                    }
                }else{
                    myViewHolder.myCustomEditTextListener.updatePosition(position);
                    myViewHolder.edtFaltas.setText("0");
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    public List<Aluno> getItems(){
        return mList;
    }
    public void addlisItem(Aluno a,int position){
        mList.add(a);
        notifyItemInserted(position);

    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public EditText edtFaltas;
        public TextView nomeAluno;
        public TextView matricula;
        public TextView txtFirstLetter;
        public AppCompatCheckBox checkBox;
        public MyCustomEditTextListener myCustomEditTextListener;
        public MyViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            edtFaltas = (EditText)itemView.findViewById(R.id.edtFaltas);
            nomeAluno = (TextView)itemView.findViewById(R.id.txtNomeFalta);
            matricula = (TextView)itemView.findViewById(R.id.txtMatriculaFalta);
            checkBox = (AppCompatCheckBox)itemView.findViewById(R.id.ccbAddfaltas);
            txtFirstLetter = (TextView)itemView.findViewById(R.id.txtFistLetter);

            this.myCustomEditTextListener = myCustomEditTextListener;
            this.edtFaltas.addTextChangedListener(myCustomEditTextListener);
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
            if (!charSequence.toString().equals("") && !edtAulasMis.getText().toString().equals("")){
                if (Integer.parseInt(charSequence.toString())>Integer.parseInt(edtAulasMis.getText().toString())){
                    AlertsAndControl.snackBar(coordinatorLayout, "O máximo de faltas tem que ser igual a o número de aulas ministradas!");
                    editTexts[position].setText(edtAulasMis.getText());
                    mList.get(position).setFaltas(edtAulasMis.getText().toString());
                }else     mList.get(position).setFaltas(charSequence.toString());
                {
                  }
            }else{
                mList.get(position).setFaltas(charSequence.toString());
            }
        }
        @Override
        public void afterTextChanged(Editable editable) {
            // no op
        }
    }
    public void setValues(){
        try{
            for (int i = 0; i < mList.size() ; i++) {
                if (Integer.parseInt(editTexts[i].getText().toString()) > Integer.parseInt(edtAulasMis.getText().toString()) || mList.get(i).isSelected()){
                    editTexts[i].setText(edtAulasMis.getText());
                    mList.get(i).setFaltas(edtAulasMis.getText().toString());
                }
            }
        }catch (Exception e){}
    }
}
