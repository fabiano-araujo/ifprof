package com.example.fabiano.ifprof.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fabiano.ifprof.AlunoHistorico;
import com.example.fabiano.ifprof.R;
import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.Falta;
import com.example.fabiano.ifprof.model.Historico;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by fabiano on 14/11/15.
 */
public class AdapterHistorico extends RecyclerView.Adapter<AdapterHistorico.MyViewHolder> {
    private String dia[];
    private List<Falta> mList;
    private List<String> mListHistorico;
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private Historico mHistorico;
    public static int ALUNOHISTORICO = 0;
    public static int HISTORICO = 1;
    public View getView;
    private int tipo;
    private Falta falta;
    String month,day,year;
    private LayoutInflater layoutInflater;
    public AdapterHistorico(Context c, Historico historico,int tipo,LayoutInflater layoutInflater){
        mContext = c;
        this.layoutInflater = layoutInflater;
        mHistorico = historico;
        mLayoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.tipo = tipo;

        if (tipo == HISTORICO){
            mList = mHistorico.getFaltaList();
        }else if(tipo == ALUNOHISTORICO){
            falta = mHistorico.getFaltaList().get(mHistorico.getPosition());
            mListHistorico = falta.getDataList();
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = null;
        if (tipo == HISTORICO){
            view = mLayoutInflater.inflate(R.layout.item_historico, viewGroup, false);
        }else if(tipo == ALUNOHISTORICO){
            view = mLayoutInflater.inflate(R.layout.item_aluno_historico, viewGroup, false);
        }
        MyViewHolder mvh =  new MyViewHolder(view);
        return mvh;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {
        if (tipo == HISTORICO){
            final Falta falta = mList.get(position);
            myViewHolder.nome.setText(falta.getAluno().getNomeAluno());
            myViewHolder.matricula.setText(falta.getAluno().getMatriculaAluno());
            myViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, AlunoHistorico.class);
                    mHistorico.setPosition(position);
                    mHistorico.setFaltaList(mList);
                    it.putExtra("historico", mHistorico);
                    mContext.startActivity(it);
                    ((Activity)mContext).finish();
                }
            });
            try {
                int qtdFaltas = 0;
                for (int i = 0; i <falta.getQtdFaltasList().size() ; i++) {
                    qtdFaltas += Integer.parseInt(falta.getQtdFaltasList().get(i));
                }
                int aulasMinistradas = 0;
                for (int i = 0; i <falta.getAulasMinistradasList().size() ; i++) {
                    aulasMinistradas += Integer.parseInt(falta.getAulasMinistradasList().get(i));
                }
                myViewHolder.faltas.setText(qtdFaltas+"");
                float porcentagem = ((float)aulasMinistradas-qtdFaltas)/aulasMinistradas*100;
                myViewHolder.porcentgem.setText(String.format("%.1f", porcentagem) + "%");

            }catch (NumberFormatException e){

            }
        }else if(tipo == ALUNOHISTORICO){
            myViewHolder.faltas.setText(falta.getQtdFaltasList().get(position));
            dia = falta.getDataList().get(position).split("[" + Pattern.quote("a") + "]");
            myViewHolder.dia.setText("Dia: " + dia[2]+"/"+dia[1]+"/"+dia[0]);
            myViewHolder.aulas.setText("Aulas: "+falta.getAulasMinistradasList().get(position));

            myViewHolder.ivEditarNota.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder tela = new AlertDialog.Builder(mContext);
                    View view = layoutInflater.inflate(R.layout.alert_editar_falta, null);
                    final EditText edtFaltasAlHt =(EditText)view.findViewById(R.id.edtFaltasAlHt);
                    final EditText edtAulasAlHt =(EditText)view.findViewById(R.id.edtAulasAlHt);
                    final TextInputLayout iptFaltas = (TextInputLayout)view.findViewById(R.id.iptFaltas);
                    final TextInputLayout iptDia = (TextInputLayout)view.findViewById(R.id.iptDia);
                    final TextInputLayout iptAulas = (TextInputLayout)view.findViewById(R.id.iptAulas);
                    edtAulasAlHt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try{
                                if (Integer.parseInt(s.toString()) > 6){
                                    edtAulasAlHt.setText("6");
                                    iptAulas.setError("Máximo 6 aulas!");
                                    edtAulasAlHt.requestFocus();
                                }else{
                                    iptAulas.setErrorEnabled(false);
                                }

                            }catch (Exception e){}
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    edtFaltasAlHt.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            try{
                                if (Integer.parseInt(s.toString()) > Integer.parseInt(edtAulasAlHt.getText().toString())){
                                    edtFaltasAlHt.setText(edtAulasAlHt.getText().toString());
                                    iptFaltas.setError("O máximo de faltas tem que ser igual a o número de aulas ministradas!");
                                    edtFaltasAlHt.requestFocus();
                                }else{
                                    iptFaltas.setErrorEnabled(false);
                                }
                            }catch (Exception e){}
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    final EditText edtDiaAlHt =(EditText)view.findViewById(R.id.edtDiaAlHt);
                    edtDiaAlHt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Date date = new Date();
                            final DateFormat ano = new SimpleDateFormat("yyyy");
                            DateFormat mes = new SimpleDateFormat("MM");
                            DateFormat dia = new SimpleDateFormat("dd");
                            DatePickerDialog dlg = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int yea, int monthOfYear, int dayOfMonth) {
                                    edtDiaAlHt.setText(dayOfMonth + "/" + (monthOfYear + 1)+ "/" + yea);
                                    day = dayOfMonth+"";
                                    if (day.length() == 1){
                                        day = "0"+day;
                                    }
                                    month = (monthOfYear+1)+"";
                                    if (month.length() == 1){
                                        month = "0"+month;
                                    }
                                    year = yea+"";
                                }
                            }, Integer.parseInt(ano.format(date)), Integer.parseInt(mes.format(date)) - 1, Integer.parseInt(dia.format(date)));
                            dlg.show();
                        }
                    });
                    edtAulasAlHt.requestFocus();
                    edtAulasAlHt.setText(falta.getAulasMinistradasList().get(position));
                    edtDiaAlHt.setText(dia[2]+"/"+dia[1]+"/"+dia[0]);
                    edtFaltasAlHt.setText(falta.getQtdFaltasList().get(position));

                    tela.setView(view);
                    tela.setTitle("Editar").setPositiveButton("Alterar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (AlertsAndControl.check(iptAulas,edtAulasAlHt,1,"Digite a quantidade de aulas ministradas!") && AlertsAndControl.check(iptFaltas,edtFaltasAlHt,1,"Digite a quantidade de faltas!")&&AlertsAndControl.check(iptDia,edtDiaAlHt,1,"Digite o dia!")){
                                String and = "and "+DataBase.ID_PROFESSOR+" == "+falta.getAluno().getIdProfessor()+" and "+DataBase.ID_TURMA+" == "+falta.getAluno().getIdTurma()+" and "+DataBase.ID_DISCIPLINA+" == "+mHistorico.getDisciplina().getIdDisciplina()+" and "+DataBase.DATA+" == '"+falta.getDataList().get(position)+"'";
                                Repositorio repositorio = new Repositorio(mContext);
                                dia = edtDiaAlHt.getText().toString().split("/");
                                String dados = "'" + dia[2]+"a"+dia[1]+"a"+dia[0] + "', " + DataBase.AULAS_MINISTRADAS + " = " + edtAulasAlHt.getText().toString() + " , " + DataBase.QTD_FALTAS + " = " + edtFaltasAlHt.getText().toString();
                                repositorio.update(DataBase.TABLE_FALTA, DataBase.DATA, dados, DataBase.ID_ALUNO, falta.getAluno().getIdALuno(), and);

                                myViewHolder.faltas.setText(edtFaltasAlHt.getText().toString());
                                myViewHolder.dia.setText("Dia: " +edtDiaAlHt.getText());
                                myViewHolder.aulas.setText("Aulas: " + edtAulasAlHt.getText().toString());

                                falta.getQtdFaltasList().set(position, edtFaltasAlHt.getText().toString());
                                falta.getDataList().set(position, dia[2] + "a" + dia[1] + "a" + dia[0]);
                                falta.getAulasMinistradasList().set(position, edtAulasAlHt.getText().toString());
                            }
                        }
                    }).setNegativeButton("Cancelar",null).show();
                }
            });
        }
    }
    public View getView(){
        return getView;
    }
    @Override
    public int getItemCount() {
        int size = 0;
        if (tipo == HISTORICO){
            size = mList.size();
        }else if(tipo == ALUNOHISTORICO){
            size = mListHistorico.size();
        }
        return size;
    }
    public void addlisItem(Falta a,int position){
        mList.add(a);
        notifyItemInserted(position);
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nome;
        public TextView matricula;
        public TextView faltas;
        public TextView porcentgem;
        public View view;
        public ImageView ivEditarNota;
        public TextView aulas;
        public TextView dia;
        public MyViewHolder(View itemView) {
            super(itemView);
            if (tipo == HISTORICO){
                nome = (TextView)itemView.findViewById(R.id.txtNome);
                matricula = (TextView)itemView.findViewById(R.id.txtMatricula);
                porcentgem = (TextView)itemView.findViewById(R.id.txtPorcentagem);
                faltas = (TextView)itemView.findViewById(R.id.txtFaltas);
                view = itemView;
            }else if(tipo == ALUNOHISTORICO){
                aulas = (TextView)itemView.findViewById(R.id.txtAulasAlHt);
                dia = (TextView)itemView.findViewById(R.id.txtDiaAlHt);
                faltas = (TextView)itemView.findViewById(R.id.txtFaltasAluno);
                ivEditarNota = (ImageView)itemView.findViewById(R.id.ivEditarNota);
            }
        }
    }
}

