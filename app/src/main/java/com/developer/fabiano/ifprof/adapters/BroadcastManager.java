package com.developer.fabiano.ifprof.adapters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.developer.fabiano.ifprof.R;
import com.developer.fabiano.ifprof.ShowAvaliacoes;
import com.developer.fabiano.ifprof.database.DataBase;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Avaliacao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BroadcastManager extends BroadcastReceiver {
    private Repositorio repositorio;
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Date d = new Date();
            DateFormat ano = new SimpleDateFormat("yyyy");
            DateFormat mes = new SimpleDateFormat("MM");
            DateFormat dia = new SimpleDateFormat("dd");
            DateFormat h = new SimpleDateFormat("HH");
            int  hora =Integer.parseInt(h.format(d));

            String date = dia.format(d)+"/"+mes.format(d)+"/"+ano.format(d);
            repositorio = new Repositorio(context);
            List<AllInfo> allInfoAvaliacoesList = repositorio.getAllInfoAvaliacoes(repositorio.getLogged().getId(), "and " + DataBase.DATA + " == '" + date + "' and " + DataBase.QTD_NOTIFICADO + " == 0");
            List<AllInfo> allInfoList = new ArrayList<>();
            for (int i = 0; i < allInfoAvaliacoesList.size(); i++) {
                String diaDoCadastro[] = allInfoAvaliacoesList.get(i).getAvaliacao().getDiaDoCadastro().split("#");
                if (!(diaDoCadastro[0].equals(date)&& hora == Integer.parseInt(diaDoCadastro[1]))){
                    allInfoList.add(allInfoAvaliacoesList.get(i));
                }
            }
            if (allInfoList.size() > 0 && hora > 5 && hora < 20){
                String mensage;
                for (int i = 0; i < allInfoList.size(); i++) {
                    Avaliacao avaliacao = allInfoList.get(i).getAvaliacao();
                    repositorio.update(DataBase.TABLE_AVALIACAO, DataBase.QTD_NOTIFICADO, (avaliacao.getQtdNotificado()+1)+"",DataBase.ID_AVALIACAO,avaliacao.getIdAvaliacao(),"");
                }
                if (allInfoList.size() > 1){
                    mensage = "Você tem "+allInfoList.size()+" avaliações.";
                }else {
                    mensage = "Você tem "+allInfoList.size()+" avaliação.";
                }
                Intent it =  new Intent(context, ShowAvaliacoes.class);
                it.putParcelableArrayListExtra("avaliacoes", (ArrayList<AllInfo>) allInfoList);
                gerarNotificacao(context, it, "Nova mensagem", "avaliações de hoje!", mensage);
            }
            repositorio.close();
        }catch (Exception e){
            Log.i("data","erro == "+e.getMessage());
        }
    }
    public void gerarNotificacao(Context context, Intent intent, CharSequence ticker, CharSequence titulo, CharSequence descricao){
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent p = PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setTicker(ticker);
        builder.setContentTitle(titulo);
        builder.setContentText(descricao);
        builder.setSmallIcon(R.drawable.ic_event_available_white_24dp);
        builder.setContentIntent(p);

        Notification n = builder.build();
        n.vibrate = new long[]{150, 300, 150, 400};
        n.flags = Notification.FLAG_AUTO_CANCEL;
        nm.notify(R.drawable.ic_delete_purple_24dp, n);

        try{
            Uri som = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone toque = RingtoneManager.getRingtone(context, som);
            toque.play();
        }
        catch(Exception e){}
    }
}