package com.developer.fabiano.ifprof.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.developer.fabiano.ifprof.ActivityAddAlunos;
import com.developer.fabiano.ifprof.ActivityAddAvaliacoes;
import com.developer.fabiano.ifprof.ActivityAddDisciplinas;
import com.developer.fabiano.ifprof.ActivityAddFaltas;
import com.developer.fabiano.ifprof.ActivityAddQuestao;
import com.developer.fabiano.ifprof.AlternativasMarcadas;
import com.developer.fabiano.ifprof.Perfil;
import com.developer.fabiano.ifprof.R;
import com.developer.fabiano.ifprof.SetPhoto;
import com.developer.fabiano.ifprof.ShowAlunos;
import com.developer.fabiano.ifprof.ShowAvaliacoes;
import com.developer.fabiano.ifprof.ShowDisciplinas;
import com.developer.fabiano.ifprof.ShowFaltas;
import com.developer.fabiano.ifprof.ShowNotas;
import com.developer.fabiano.ifprof.ShowQuestoes;
import com.developer.fabiano.ifprof.ShowTurmas;
import com.developer.fabiano.ifprof.model.AllInfo;
import com.developer.fabiano.ifprof.model.Avaliacao;
import com.developer.fabiano.ifprof.model.Disciplina;
import com.developer.fabiano.ifprof.model.Falta;
import com.developer.fabiano.ifprof.model.Historico;
import com.developer.fabiano.ifprof.model.Professor;
import com.developer.fabiano.ifprof.model.Prova;
import com.developer.fabiano.ifprof.model.Questao;
import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.image.ImageType;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.developer.fabiano.ifprof.model.Turma;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/*
import com.independentsoft.office.word.DrawingObject;
import com.independentsoft.office.word.Paragraph;
import com.independentsoft.office.word.Run;
import com.independentsoft.office.word.WordDocument;
import com.independentsoft.office.Unit;
import com.independentsoft.office.UnitType;
import com.independentsoft.office.drawing.Extents;
import com.independentsoft.office.drawing.Offset;
import com.independentsoft.office.drawing.Picture;
import com.independentsoft.office.drawing.PresetGeometry;
import com.independentsoft.office.drawing.ShapeType;
import com.independentsoft.office.drawing.Transform2D;
import com.independentsoft.office.word.drawing.DrawingObjectSize;
import com.independentsoft.office.word.drawing.Inline;
import com.independentsoft.office.word.StandardBorderStyle;
import com.independentsoft.office.word.tables.Cell;
import com.independentsoft.office.word.tables.Row;
import com.independentsoft.office.word.tables.Table;
import com.independentsoft.office.word.tables.TableWidthUnit;
import com.independentsoft.office.word.tables.Width;*/

public class AlertsAndControl {
    public static final String AVALIACOES = "avaliacoes";
    public static final String FALTAS = "faltas";
    public static final String ALUNOS = "alunos";
    public static final String QUESTOES = "questoes";
    public static final String SEMTURMA = "sem_turma";
    public static final String DISCIPLINAS = "disciplinas";
    public static final String BACKTOAVALIACAO = "back_to_Avaliacao";
    public static final String BACKTODISCIPLINA = "back_to_disciplina";

    public static void alert(Context context, String mensage,String title){
        AlertDialog.Builder x = new AlertDialog.Builder(context);
        if (title != null){
            x.setTitle(title);
        }
        x.setMessage(mensage).setNeutralButton("ok", null).show();
    }
    public static boolean isValidEmailAddress(EditText editText,TextInputLayout textInputLayout) {
        String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

        boolean isValid = editText.getText().toString().matches(EMAIL_REGEX);
        if (isValid){
            textInputLayout.setErrorEnabled(false);
            return true;
        }else{
            textInputLayout.setError("Email inválido!");
            editText.requestFocus();
            return false;
        }
    }
    public static double round(double value, int scale) {
        BigDecimal bd1 = new BigDecimal(value);
        BigDecimal bd2 = bd1.setScale(scale, BigDecimal.ROUND_HALF_UP);
        return bd2.doubleValue();
    }
    public static void noNeedData(final Context context, final Class classe,String mensage, final String go, final Disciplina disciplina){
        AlertDialog.Builder info = new AlertDialog.Builder(context);
        info.setCancelable(false);
        info.setMessage(mensage).setNeutralButton("Criar",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent it = new Intent(context, classe);
                        it.putExtra("new", go);
                        if (disciplina != null) {
                            AllInfo allInfo = new AllInfo();
                            allInfo.setDisciplina(disciplina);
                            it.putExtra("nomeDisciplina", allInfo);
                        }
                        context.startActivity(it);
                        ((Activity) context).finish();
                    }
                }).show();
    }
    public static void vericaClass(Context context,String it){
        if(it.equals(AVALIACOES)){
            context.startActivity(new Intent(context, ActivityAddAvaliacoes.class));
        }else if(it.equals(FALTAS)){
            context.startActivity(new Intent(context, ActivityAddFaltas.class));
        }else if(it.equals(DISCIPLINAS)){
            context.startActivity(new Intent(context, ActivityAddDisciplinas.class));
        }else if(it.equals(ALUNOS)){
            context.startActivity(new Intent(context, ActivityAddAlunos.class));
        }else if(it.equals(QUESTOES)){
            context.startActivity(new Intent(context, ActivityAddQuestao.class));
        } else if(it.equals(BACKTOAVALIACAO)){
            Intent intent = new Intent(context, ActivityAddDisciplinas.class);
            intent.putExtra("new",AVALIACOES);
            context.startActivity(intent);
        }
    }
    public static boolean check(TextInputLayout textInputLayout,EditText editText,int condicao,String mensage){
        boolean isValid = true;
        if(editText.getText().toString().trim().length() < condicao){
            textInputLayout.setError(mensage);
            editText.requestFocus();
            isValid = false;
        }else{
            textInputLayout.setErrorEnabled(false);
        }
        return  isValid;
    }
    public static void snackBar(View view,String mensage){
        Snackbar.make(view, mensage, Snackbar.LENGTH_LONG).show();
    }
    public static View configurationNavigationView(final Context context, final DrawerLayout drawerLayout,LayoutInflater layoutInflater, final Professor professor) {
        View view = layoutInflater.inflate(R.layout.drawer_header, null);
        TextView txtNome = (TextView) view.findViewById(R.id.txtNome);
        TextView txtMatricula = (TextView) view.findViewById(R.id.txtMatricula);
        txtNome.setText(professor.getNomeProfessor());
        txtMatricula.setText(professor.getMatricula());
        view.findViewById(R.id.ivHeader).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent it = new Intent(context, SetPhoto.class);
                it.putExtra("image", professor.getUriFoto());
                context.startActivity(it);
            }
        });
        return view;
    }
    public static void navigationViewItemSelectedListener(Context context,DrawerLayout drawerLayout,int id){
        switch (id){
            case R.id.itmMeuPerfil:
                start(context, Perfil.class, drawerLayout);
                break;
            case R.id.itmTurmas:
                start(context, ShowTurmas.class, drawerLayout);
                break;
            case R.id.itmAvaliacoes:
                start(context, ShowAvaliacoes.class, drawerLayout);
                break;
            case R.id.itmDisciplinas:
                start(context, ShowDisciplinas.class, drawerLayout);
                break;
            case R.id.itmAlunos:
                start(context, ShowAlunos.class, drawerLayout);
                break;
            case R.id.itmNotas:
                start(context, ShowNotas.class, drawerLayout);
                break;
            case R.id.itmFaltas:
                start(context, ShowFaltas.class,drawerLayout);
                break;
            case R.id.itmQuestoes:
                start(context, ShowQuestoes.class,drawerLayout);
                break;
        }
    }
    public static void start(Context context,Class classe,DrawerLayout drawerLayout){
        drawerLayout.closeDrawer(GravityCompat.START);
        if (!context.getClass().getName().equals(classe.getName())){
            context.startActivity(new Intent(context, classe));
            ((Activity)context).finish();
        }

    }
    public static Paragraph getParagraph(int fntSize,String text,String fontFamaly){
        Paragraph p = new Paragraph();
        p.setFont(FontFactory.getFont(fontFamaly, fntSize));
        p.add(text);
        return p;
    }
    public static PdfPTable getTable(ArrayList<String> colunas) {
        PdfPTable table = new PdfPTable(colunas.size());
        table.setWidthPercentage(97);
        for (int i = 0; i < colunas.size(); i++) {
            table.addCell(colunas.get(i));
        }
        return table;
    }
    public static String saveHistorico(Historico historico,Professor professor,Turma turma,String data,String numberFile,boolean sizeAulasMinisDiferente){
        String file = null;
        String nomeDiretorio = Environment.getExternalStorageDirectory().getPath()+"/Ifprof/"+professor.getNomeProfessor()+"/"+historico.getDisciplina().getNomeDisciplina()+"/"+turma.getNomeTurma()+"/lista de presença";

        if (data == null){
            file = nomeDiretorio+"/"+turma.getNomeTurma()+" - vários dias"+numberFile+".pdf";
        }else{
            file = nomeDiretorio+"/"+turma.getNomeTurma()+" - dia_"+data+numberFile+".pdf";
        }
        if (!new File(nomeDiretorio).exists()) {
            (new File(nomeDiretorio)).mkdirs();
        }
        try{
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            Paragraph p = new Paragraph();
            p.setAlignment(Element.ALIGN_CENTER);
            p.setFont(FontFactory.getFont("Lohit Punjabi", 22));
            p.add("Lista de presença");
            document.add(p);

            document.add(getParagraph(13, " ", "Arial"));

            ArrayList<String> colunas = new ArrayList<>();

            colunas.add("Disciplina: " + historico.getDisciplina().getNomeDisciplina());
            colunas.add("Turma: " + turma.getNomeTurma());
            if (data != null){
                colunas.add("Data: " + data);
                colunas.add("Aulas ministradas: " + historico.getFaltaList().get(0).getAulasMinistradasList().get(0));
            }else if(!sizeAulasMinisDiferente){
                int aulasMinistradas = 0;
                Falta falta = historico.getFaltaList().get(0);
                for (int j = 0; j <falta.getAulasMinistradasList().size() ; j++) {
                    aulasMinistradas += Integer.parseInt(falta.getAulasMinistradasList().get(j));
                }
                colunas.add("Aulas ministradas: " + aulasMinistradas);
            }

            document.add(getTable(colunas));

            colunas.clear();
            colunas.add("Nome");
            colunas.add("Faltas");

            if (data == null && sizeAulasMinisDiferente){
                colunas.add("Aulas ministradas");
            }
            document.add(getTable(colunas));
            List<Falta> faltaList = historico.getFaltaList();
            for (int i = 0; i < faltaList.size(); i++) {
                String count = "";
                if (i < 10){
                    count = "0"+(i+1)+".  ";
                }else{
                    count = (i+1)+".  ";
                }

                int qtdFaltas = 0;
                for (int j = 0; j <faltaList.get(i).getQtdFaltasList().size() ; j++) {
                    qtdFaltas += Integer.parseInt(faltaList.get(i).getQtdFaltasList().get(j));
                }
                colunas.clear();
                colunas.add(count + faltaList.get(i).getAluno().getNomeAluno());
                colunas.add(qtdFaltas+"");
                if (data == null){
                    if(sizeAulasMinisDiferente){
                        int aulasMinistradas = 0;
                        for (int j = 0; j <faltaList.get(i).getAulasMinistradasList().size() ; j++) {
                            aulasMinistradas += Integer.parseInt(faltaList.get(i).getAulasMinistradasList().get(j));
                        }
                        colunas.add(aulasMinistradas+"");
                    }
                }
                document.add(getTable(colunas));
            }
            document.close();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        return file;
    }
    public static String saveAvaliacao(Prova prova, Context context, String mais) throws Exception {
        String file = null;
        String texto = "";
        String nomeDiretorio = Environment.getExternalStorageDirectory().getPath()+"/Ifprof/"+prova.getProfessor().getNomeProfessor()+"/"+prova.getDisciplina().getNomeDisciplina()+"/provas";
        file = nomeDiretorio+"/"+prova.getAvaliacao().getAssunto()+"-"+prova.getAvaliacao().getTurma().getNomeTurma()+mais+".pdf";
        if (!new File(nomeDiretorio).exists()) {
            (new File(nomeDiretorio)).mkdirs();
        }
        try{
            Document document = new Document();
            PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            Image logo = Image.getInstance(savePhoto(BitmapFactory.decodeResource(context.getResources(), R.drawable.logoifrn), context, prova.getProfessor(), true));
            logo.scaleAbsolute(200f, 90f);

            Image qrcode = Image.getInstance(gerarQrCode(prova));
            qrcode.setAbsolutePosition(450f, 705f);
            qrcode.scaleAbsolute(110f, 110f);

            document.add(logo);
            document.add(qrcode);
            document.add(getParagraph(13," ","Arial"));
            ArrayList<String> colunas = new ArrayList<>();

            try{
                colunas.add("Professor(a): " + prova.getProfessor().getNomeProfessor());
                colunas.add("Disciplina: " + prova.getDisciplina().getNomeDisciplina());
                colunas.add("Turma: " + prova.getAvaliacao().getTurma().getNomeTurma());
                colunas.add("Data: " + prova.getAvaliacao().getData());
            }catch (Exception e){
                e.printStackTrace();
            }
            document.add(getTable(colunas));

            colunas.clear();
            colunas.add("Aluno(a): ");
            document.add(getTable(colunas));

            Paragraph p = new Paragraph();
            p.setAlignment(Element.ALIGN_CENTER);
            p.setFont(FontFactory.getFont("Lohit Punjabi", 18));
            p.add(prova.getAvaliacao().getBimestre());
            document.add(getParagraph(9, " ", "Arial"));
            document.add(p);

            for (int i = 0; i < prova.getAvaliacao().getQuestoes().size(); i++) {
                String count = "";
                if (i < 10){
                    count = "0"+(i+1)+".  ";
                }else{
                    count = (i+1)+".  ";
                }
                Questao questao = prova.getAvaliacao().getQuestoes().get(i);
                document.add(getParagraph(12," ", "arial"));
                document.add(getParagraph(11," "+count + questao.getPergunta(), "Lohit Punjabi"));
                if(questao.getPathImage() != null){
                    Image image = Image.getInstance(questao.getPathImage());
                    float height = image.getScaledHeight();
                    float width = image.getScaledWidth();

                    width = (90*width)/height;
                    image.setAlignment(Element.ALIGN_CENTER);
                    image.scaleAbsolute(width,90f);
                    document.add(image);
                }
                document.add(getParagraph(11,"     (" + questao.getValor() + ")","Lohit Punjabi"));
                document.add(getParagraph(11,"     a)  " + questao.getAlternativaA(),"Lohit Punjabi"));
                document.add(getParagraph(11, "     b)  " + questao.getAlternativaB(), "Lohit Punjabi"));
                document.add(getParagraph(11,"     c)  " + questao.getAlternativaC(),"Lohit Punjabif"));
                document.add(getParagraph(11,"     d)  " + questao.getAlternativaD(),"Lohit Punjabi"));
                document.add(getParagraph(11, "     e)  " + questao.getAlternativaE(), "Lohit Punjabi"));
            }
            document.addCreator("Gerado pelo Ifprof.");
            document.addAuthor("Gerado pelo Ifprof.");
            document.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return file;
    }
    public static String gerarQrCode(Prova prova) throws IOException {
        String path = Environment.getExternalStorageDirectory().getPath()+"/Ifprof/"+prova.getProfessor().getNomeProfessor()+"/"+prova.getDisciplina().getNomeDisciplina()+"/qrcodes";
        if (!new File(path).exists()) {
            (new File(path)).mkdirs();
        }
        path = path+"/"+prova.getAvaliacao().getAssunto()+".jpg";
        FileOutputStream f = new FileOutputStream(path);
        List<Questao> questaoList = prova.getAvaliacao().getQuestoes();
        String res = "";
        for (int i = 0; i <questaoList.size(); i++) {
            Questao questao = questaoList.get(i);

            res += questao.getAlternativaCorreta().substring(1,2)+",,"+questao.getValor()+";;";
        }
        String info = prova.getAvaliacao().getIdAvaliacao()+"##";
        String mais = "##"+prova.getDisciplina().getNomeDisciplina()+"##"+prova.getAvaliacao().getTurma().getNomeTurma()+"##"+prova.getCode();
        ByteArrayOutputStream out = QRCode.from("!!>.<!!"+crip(info+res+mais)).to(ImageType.JPG).withSize(100,100).stream();


        f.write(out.toByteArray());
        f.close();
        return path;
    }
    public static void tipoQrcode(final Context context,String string){
        try {
            if (string.length() > 0){
                if (string.substring(0,7).equals("!!>.<!!")){
                    string = crip(string.substring(7,string.length()));
                    String parteObjs[] = string.split("##");

                    Avaliacao avaliacao = new Avaliacao();
                    avaliacao.setIdAvaliacao(Integer.parseInt(parteObjs[0]));

                    String questoes[] = parteObjs[1].split(";;");
                    List<Questao> questaoList = new ArrayList<>();
                    for (int i = 0; i < questoes.length; i++) {
                        String parteQuestoes[] = questoes[i].split(",,");
                        Questao questao = new Questao();
                        questao.setAlternativaCorreta(parteQuestoes[0]);
                        questao.setValor(parteQuestoes[1]);
                        questaoList.add(questao);
                    }
                    Disciplina disciplina = new Disciplina();
                    disciplina.setNomeDisciplina(parteObjs[2]);

                    Turma turma = new Turma();
                    turma.setNomeTurma(parteObjs[3]);

                    avaliacao.setQuestoes(questaoList);
                    avaliacao.setTurma(turma);

                    final Prova prova = new Prova();
                    prova.setAvaliacao(avaliacao);
                    prova.setDisciplina(disciplina);
                    prova.setCode(parteObjs[4]);

                    AlertDialog.Builder alternativas = new AlertDialog.Builder(context);
                    alternativas.setMessage("Escolha uma opção de correção:").setPositiveButton("Ler gabarito", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("Manual", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent it = new Intent(context, AlternativasMarcadas.class);
                            it.putExtra("prova",prova);
                            context.startActivity(it);
                        }
                    }).show();
                }else if(string.substring(0,8).equalsIgnoreCase("https://")||string.substring(0,7).equalsIgnoreCase("http://")){
                    try{
                        Intent it = new Intent(Intent.ACTION_VIEW);
                        it.setData(Uri.parse(string));
                        context.startActivity(it);
                    }catch (Exception e){
                        AlertsAndControl.alert(context, string, "QRCode");
                    }
                }else{
                    AlertsAndControl.alert(context, string, "QRCode");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static String crip(String string){
        String res = "";
        for (int i = string.length()-1; i >= 0  ; i--) {
            res += string.charAt(i);
        }
        return res;
    }
    public static boolean equalsList(List list1,List list2){
        boolean changed = false;
        if (list1.size() != list2.size()){
            changed = true;
        }else{
            for (int i = 0; i < list1.size(); i++) {
                boolean equals = false;
                for (int j = 0; j < list2.size(); j++) {
                    if (list1.get(i).equals(list2.get(j))){
                        equals = true;
                        break;
                    }
                    if (j == list2.size() - 1 && !equals){
                        changed = true;
                        break;
                    }
                }
                if (changed){
                    break;
                }
            }
        }
        return !changed;
    }
    public static String savePhoto(Bitmap bitmap,Context mContext, Professor professor,boolean logo){
        try{
            Date date = new Date();
            String path = "";
            String name = "";
            if (logo){
                path = Environment.getExternalStorageDirectory().getPath()+"/Ifprof/"+professor.getNomeProfessor()+"/logo";
                name = "logo.jpg";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          
            }else{
                path = Environment.getExternalStorageDirectory().getPath()+"/Ifprof/"+professor.getNomeProfessor()+"/fotos";
                name = "foto_"+date.getTime()+".jpg";
            }
            if (!new File(path).exists()) {
                (new File(path)).mkdirs();
            }
            OutputStream fOut = null;

            File file = new File(path, name);
            fOut = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(mContext.getContentResolver()
                    ,file.getAbsolutePath(),file.getName(),file.getName());
            return path+"/"+name;

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void sendEmail(String path,Context context,String email,String title){
        File file = new File(path);
        Uri uri = Uri.fromFile(file);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("vnd.android.cursor.dir/email");
        String to[] = {email};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to);

        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);

        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        context.startActivity(Intent.createChooser(emailIntent, "Enviar arquivo"));
    }
    public static String getPath(Uri uri,Context context) {
        if( uri == null ) {
            return null;
        }
        // Tenta recuperar a imagem da media store primeiro
        // Isto só irá funcionar para as imagens selecionadas da galeria
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = ((Activity)context).managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }

        return uri.getPath();
    }
}
