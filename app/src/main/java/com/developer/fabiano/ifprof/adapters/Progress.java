package com.developer.fabiano.ifprof.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import com.developer.fabiano.ifprof.R;
import com.developer.fabiano.ifprof.ShowAvaliacoes;
import com.developer.fabiano.ifprof.model.Historico;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Prova;
import com.developer.fabiano.ifprof.model.Turma;

import java.io.File;

/**
 * Created by fabiano on 08/01/16.
 */
public class Progress extends AsyncTask<Void, Void, String> {
    private ProgressDialog progress;
    private Context context;
    private Prova prova;
    private int numberFile;
    String mensage;
    private Historico historico;
    private Turma turma;
    private Professor professor;
    private String data;
    private boolean aulasMinistradasDiferente;

    public Progress(Context context, Prova prova, int numberFile) {
        this.context = context;
        this.prova = prova;
        this.numberFile = numberFile;
        mensage = "Criando prova";
    }
    public Progress(Context context, Historico historico,Professor professor,Turma turma,String data, int numberFile,boolean aulasMinistradasDiferente) {
        this.context = context;
        this.prova = prova;
        this.numberFile = numberFile;
        mensage = "gerando historico";
        this.historico = historico;
        this.turma = turma;
        this.professor =professor;
        if(data != null){
            String partes[] = data.split("a");
            this.data = partes[2]+"-"+partes[1]+"-"+partes[0];
        }
        this.aulasMinistradasDiferente = aulasMinistradasDiferente;
    }
    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(context);
        progress.setMessage(mensage);
        progress.show();
    }
    @Override
    protected String doInBackground(Void... params) {
        if ("Criando prova".equals(mensage)){
            try {
                if (numberFile == 0){
                    return AlertsAndControl.saveAvaliacao(prova, context, "");
                }else{
                    return AlertsAndControl.saveAvaliacao(prova, context, "(" + numberFile + ")");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{

            try {
                if (numberFile == 0){
                    return AlertsAndControl.saveHistorico(historico,professor,turma,data,"",aulasMinistradasDiferente);
                }else{
                    return AlertsAndControl.saveHistorico(historico, professor,turma,data,"("+numberFile+")",aulasMinistradasDiferente);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(String s) {
        progress.dismiss();
        avisoSendEmail(s);
    }
    public void avisoSendEmail(final String saveAt){
        AlertDialog.Builder save = new AlertDialog.Builder(context);
        save.setCancelable(false);
        save.setMessage("Arquivo salvo em "+saveAt+". Enviar para o seu email?").setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("Criando prova".equals(mensage)){
                    context.startActivity(new Intent(context, ShowAvaliacoes.class));
                    ((Activity)context).finish();
                }
            }
        }).setPositiveButton("enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if ("Criando prova".equals(mensage)){
                    sendEmail(prova, saveAt, prova.getProfessor().getEmail());
                }else{
                    String title = "Histórico de "+historico.getDisciplina().getNomeDisciplina()+" da turma "+turma.getNomeTurma()+".";
                    if (data != null){
                        if (data.length() > 0){
                            title = "Histórico de "+historico.getDisciplina().getNomeDisciplina()+" da turma "+turma.getNomeTurma()+"_"+data+".";
                        }
                    }
                    AlertsAndControl.sendEmail(saveAt,context,professor.getEmail(),title);
                }
            }
        }).setNeutralButton("Outro email", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                outroEmail(false, saveAt);
            }
        }).setTitle("Arquivo salvo").show();
    }
    public void sendEmail(final Prova prova, final String path,String email){
        try{
            File file = new File(path);
            Uri uri = Uri.fromFile(file);

            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setType("vnd.android.cursor.dir/email");
            String to[] = {email};
            emailIntent.putExtra(Intent.EXTRA_EMAIL, to);

            emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Prova de " + prova.getDisciplina().getNomeDisciplina() + "-" + prova.getAvaliacao().getAssunto() + " para a turma " + prova.getAvaliacao().getTurma().getNomeTurma());
            ((Activity)context).startActivityForResult(Intent.createChooser(emailIntent, "Enviar arquivo"), 1234);
        }catch (Exception e){
            AlertDialog.Builder save = new AlertDialog.Builder(context);
            save.setMessage("Desculpe ocorreu um erro, tentar novamente?").setNegativeButton("Não", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    context.startActivity(new Intent(context, ShowAvaliacoes.class));
                    ((Activity)context).finish();
                }
            }).setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sendEmail(prova, path, prova.getProfessor().getEmail());
                }
            }).setTitle("Erro ao enviar").show();
        }
    }
    public void outroEmail(boolean setErro,final String saveAt){
        try {
            final AlertDialog.Builder outroEmail = new AlertDialog.Builder(context);

            final View view = ((Activity)context).getLayoutInflater().inflate(R.layout.alert_another_email, null);
            final EditText edtOutroEmail = (EditText) view.findViewById(R.id.edtOutroEmail);
            final TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.iptOutroEmail);
            if (setErro){
                textInputLayout.setError("Email inválido!");
                edtOutroEmail.requestFocus();
            }
            outroEmail.setView(view).setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (AlertsAndControl.isValidEmailAddress(edtOutroEmail, textInputLayout)) {
                        if ("Criando prova".equals(mensage)){
                            sendEmail(prova, saveAt, edtOutroEmail.getText().toString());
                        }else{
                            String title = "Histórico de "+historico.getDisciplina()+" da turma "+turma.getNomeTurma()+".";
                            if(data != null){
                                if (data.length() > 0){
                                    title = "Histórico de "+historico.getDisciplina()+" da turma "+turma.getNomeTurma()+" - "+data+".";
                                }
                            }
                            AlertsAndControl.sendEmail(saveAt,context,edtOutroEmail.getText().toString(),title);
                        }
                    } else {
                        outroEmail(true,saveAt);
                    }
                }
            }).setNegativeButton("Cancelar", null).setTitle("Outro email").show();
        }catch (Exception e){
            AlertsAndControl.alert(context, "Desculpe ocorreu um erro, tente novamente!", "Aviso");
        }
    }
}
