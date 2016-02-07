package com.example.fabiano.ifprof.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.AllInfo;
import com.example.fabiano.ifprof.model.Aluno;
import com.example.fabiano.ifprof.model.Avaliacao;
import com.example.fabiano.ifprof.model.Disciplina;
import com.example.fabiano.ifprof.model.Falta;
import com.example.fabiano.ifprof.model.Historico;
import com.example.fabiano.ifprof.model.Nota;
import com.example.fabiano.ifprof.model.Professor;
import com.example.fabiano.ifprof.model.Prova;
import com.example.fabiano.ifprof.model.Questao;
import com.example.fabiano.ifprof.model.Turma;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiano on 26/09/15.
 */
public class Repositorio {
    private SQLiteDatabase db;
    private SQLiteDatabase dbr;
    private Context context;

    public Repositorio(Context context){
        db = new DataBase(context).getWritableDatabase();
        this.context = context;
    }
    public void alert(){
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_FALTA+";",null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
               AlertsAndControl.alert(context,cursor.getInt(0)+" dia "+cursor.getString(3)+" aluno"+cursor.getString(cursor.getColumnIndex(DataBase.ID_ALUNO)),"legal");
            }while (cursor.moveToNext());
        }
        cursor.close();
    }
    public List<Falta> insert(List<Falta> faltaList){
        boolean save = false;
        db.execSQL("UPDATE " + DataBase.TABLE_FALTA + " SET " + DataBase.ULTIMO + " = " + "0" + " WHERE " + DataBase.ULTIMO + " == '1' and "+DataBase.ID_TURMA+" == "+faltaList.get(0).getAluno().getIdTurma()+" and "+DataBase.ID_DISCIPLINA+" == "+faltaList.get(0).getIdDisciplina()+" ;");
        for (int i = 0; i < faltaList.size();i++){
            Falta falta = faltaList.get(i);
            Aluno aluno = falta.getAluno();
            String more = "where "+DataBase.ID_PROFESSOR+" == "+aluno.getIdProfessor()+" and "+DataBase.ID_TURMA+" == "+aluno.getIdTurma()+" and "+DataBase.ID_DISCIPLINA+" == "+falta.getIdDisciplina()+" and "+DataBase.ID_ALUNO+" == "+aluno.getIdALuno();
            if (search(DataBase.TABLE_FALTA, falta.getDate().toString(), 3, more)){
                faltaList.get(i).setSave(false);
            }else{
                ContentValues contentValues = new ContentValues();
                contentValues.put(DataBase.QTD_FALTAS, falta.getQdtFaltas());
                contentValues.put(DataBase.AULAS_MINISTRADAS, falta.getAulasMinistradas());
                contentValues.put(DataBase.DATA, falta.getDate());
                contentValues.put(DataBase.ID_PROFESSOR,aluno.getIdProfessor());
                contentValues.put(DataBase.ID_DISCIPLINA,falta.getIdDisciplina());
                contentValues.put(DataBase.ID_TURMA,aluno.getIdTurma());
                contentValues.put(DataBase.ID_ALUNO, aluno.getIdALuno());
                contentValues.put(DataBase.ULTIMO, "1");
                db.insert(DataBase.TABLE_FALTA, null, contentValues);
                faltaList.get(i).setSave(true);
            }
        }
        return faltaList;
    }
    public List<Nota> insertNota(List<Nota> notaList){
        boolean save = false;
        for (int i = 0; i < notaList.size();i++){
            Nota nota = notaList.get(i);
            Aluno aluno = nota.getAluno();
            String more = "where "+DataBase.ID_PROFESSOR+" == "+aluno.getIdProfessor()+" and "+DataBase.ID_AVALIACAO+" == "+nota.getAvaliacao().getIdAvaliacao()+" and "+DataBase.ID_TURMA+" == "+aluno.getIdTurma();
            if (search(DataBase.TABLE_NOTA, nota.getAluno().getIdALuno() + "", 3, more)){
                notaList.get(i).setSave(false);
            }else{
                ContentValues contentValues = new ContentValues();
                contentValues.put(DataBase.NOTA, aluno.getNota());
                contentValues.put(DataBase.ID_AVALIACAO, nota.getAvaliacao().getIdAvaliacao());
                contentValues.put(DataBase.ID_PROFESSOR,aluno.getIdProfessor());
                contentValues.put(DataBase.ID_TURMA,aluno.getIdTurma());
                contentValues.put(DataBase.ID_ALUNO, aluno.getIdALuno());
                db.insert(DataBase.TABLE_NOTA, null, contentValues);
                notaList.get(i).setSave(true);
            }
        }
        return notaList;
    }
    public List<Nota> getNotas(int idAvaliacao,int idTurma){
        List<Nota> notaList = new ArrayList<Nota>();
        String more = "where "+DataBase.TABLE_NOTA+"."+DataBase.ID_TURMA+" == "+idTurma+" and "+DataBase.ID_AVALIACAO+" == "+idAvaliacao+" order by "+DataBase.NOME_ALUNO+" asc ";
        String join = " join "+DataBase.TABLE_ALUNO+" on "+DataBase.TABLE_ALUNO+"."+DataBase.ID_ALUNO+" == "+DataBase.TABLE_NOTA+"."+DataBase.ID_ALUNO+" ";
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_NOTA+join+more+";",null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Aluno aluno = new Aluno(cursor.getInt(cursor.getColumnIndex(DataBase.ID_TURMA)),
                        cursor.getString(cursor.getColumnIndex(DataBase.NOME_ALUNO)),
                        cursor.getString(cursor.getColumnIndex(DataBase.MATRICULA_ALUNO)),
                        cursor.getInt(cursor.getColumnIndex(DataBase.ID_PROFESSOR)));
                aluno.setIdALuno(cursor.getInt(cursor.getColumnIndex(DataBase.ID_ALUNO)));
                aluno.setNota(cursor.getString(cursor.getColumnIndex(DataBase.NOTA)));
                Nota nota = new Nota();
                nota.setAluno(aluno);
                nota.setIdNota(cursor.getInt(cursor.getColumnIndex(DataBase.ID_NOTA)));
                nota.setNota(cursor.getFloat(cursor.getColumnIndex(DataBase.NOTA)));
                notaList.add(nota);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return notaList;
    }
    public ArrayAdapter<String> getDays(Historico historico){
        ArrayAdapter<String> days  = new ArrayAdapter<String>(context,android.R.layout.simple_spinner_item);
        try {
            Aluno aluno = historico.getAlunoList().get(0);
            String more = "where "+DataBase.ID_PROFESSOR+" == "+aluno.getIdProfessor()+" and "+DataBase.ID_TURMA+" == "+aluno.getIdTurma()+" and "+DataBase.ID_DISCIPLINA+" == "+historico.getDisciplina().getIdDisciplina()+" order by "+DataBase.DATA+" asc ";
            Cursor cursor = db.rawQuery("select DISTINCT "+DataBase.DATA+" from "+DataBase.TABLE_FALTA+" "+more+";",null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                do {
                    String data[] = cursor.getString(0).split("a");
                    days.add(data[2]+"/"+data[1]+"/"+data[0]);
                }while (cursor.moveToNext());
            }
            cursor.close();
        }catch (IndexOutOfBoundsException e){
            days.add("nenhum");
        }
        days.setDropDownViewResource(android.R.layout.simple_list_item_1);
        return days;
    }
    public Historico getHistorico(Historico historico,String and){
        List<Falta> faltaList = new ArrayList<>();
        for (int i = 0; i <historico.getAlunoList().size() ; i++) {
            Aluno aluno = historico.getAlunoList().get(i);
            String more = "where "+DataBase.ID_PROFESSOR+" == "+aluno.getIdProfessor()+" and "+DataBase.ID_TURMA+" == "+aluno.getIdTurma()+" and "+DataBase.ID_DISCIPLINA+" == "+historico.getDisciplina().getIdDisciplina()+" and "+DataBase.ID_ALUNO+" == "+aluno.getIdALuno()+" "+and+" order by "+DataBase.DATA+" asc ";
            Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_FALTA+" "+more+";",null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();

                Falta falta = new Falta();
                falta.setAluno(aluno);

                List<String> qtdFaltasList = new ArrayList<>();
                List<String> aulasMinistradasList = new ArrayList<>();
                List<String> dataList = new ArrayList<>();

                do {
                    String qtdFaltas = cursor.getString(cursor.getColumnIndex(DataBase.QTD_FALTAS));
                    String data = cursor.getString(cursor.getColumnIndex(DataBase.DATA));
                    String aulasMinistradas = cursor.getString(cursor.getColumnIndex(DataBase.AULAS_MINISTRADAS));

                    qtdFaltasList.add(qtdFaltas);
                    aulasMinistradasList.add(aulasMinistradas);
                    dataList.add(data);
                }while (cursor.moveToNext());
                falta.setAulasMinistradasList(aulasMinistradasList);
                falta.setQtdFaltasList(qtdFaltasList);
                falta.setDataList(dataList);
                faltaList.add(falta);
            }
            cursor.close();
        }
        historico.setFaltaList(faltaList);
        return historico;
    }
    public boolean insert(Avaliacao avaliacao){
        boolean save;
        String more = "where "+DataBase.ID_PROFESSOR+" == "+avaliacao.getIdProfessor()+" and "+DataBase.ID_DISCIPLINA+" == "+avaliacao.getIdDisciplina()+" and "+DataBase.BIMESTRE+" == '"+avaliacao.getBimestre()+"'";
        if (search(DataBase.TABLE_AVALIACAO,avaliacao.getAssunto().toString(),1,more)){
            save = false;
        }else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBase.ASSUNTO, avaliacao.getAssunto());
            contentValues.put(DataBase.VALOR, avaliacao.getValor());
            contentValues.put(DataBase.TIPO, avaliacao.getTipo());
            contentValues.put(DataBase.BIMESTRE, avaliacao.getBimestre());
            contentValues.put(DataBase.DATA, avaliacao.getData());
            contentValues.put(DataBase.ID_PROFESSOR,avaliacao.getIdProfessor());
            contentValues.put(DataBase.ID_DISCIPLINA,avaliacao.getIdDisciplina());
            contentValues.put(DataBase.ID_TURMA,avaliacao.getTurma().getIdTurma());
            contentValues.put(DataBase.QTD_NOTIFICADO,"0");
            contentValues.put(DataBase.DIA_DO_CADASTRO,avaliacao.getDiaDoCadastro());
            db.insert(DataBase.TABLE_AVALIACAO, null, contentValues);
            save = true;
        }
        return save;
    }
    public void insertProva(Avaliacao avaliacao){
        List<Questao> questaoList = avaliacao.getQuestoes();
        for (int i = 0; i <questaoList.size(); i++) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBase.ID_PROFESSOR,avaliacao.getIdProfessor());
            contentValues.put(DataBase.ID_QUESTAO,questaoList.get(i).getIdQuestao());
            contentValues.put(DataBase.ID_AVALIACAO, avaliacao.getIdAvaliacao());
            contentValues.put(DataBase.VALOR_QUESTAO, questaoList.get(i).getValor());
            db.insert(DataBase.TABLE_PROVA, null, contentValues);
        }
    }
    public List<Questao> getQuestoesDaProva(Professor professor,String and){
        List<Questao> questaoList = new ArrayList<>();
        String more = " where "+DataBase.TABLE_PROVA+"."+DataBase.ID_PROFESSOR+" == "+professor.getId()+" "+and;
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_PROVA
                +" join  "+DataBase.TABLE_QUESTAO
                +" on "+DataBase.TABLE_QUESTAO+"."+DataBase.ID_QUESTAO+" == "
                +DataBase.TABLE_PROVA+"."+DataBase.ID_QUESTAO
                +more+";",null);
        int count = 0;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Questao questao = new Questao(
                        cursor.getString(cursor.getColumnIndex(DataBase.PERGUNTA)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATIVACORRETA)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATIA)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATIB)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATIC)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATID)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATIE)));
                questao.setIdProfessor(professor.getId());
                questao.setIdQuestao(cursor.getInt(cursor.getColumnIndex(DataBase.ID_QUESTAO)));
                questao.setIdDisciplina(cursor.getInt(cursor.getColumnIndex(DataBase.ID_DISCIPLINA)));
                questao.setPathImage(cursor.getString(cursor.getColumnIndex(DataBase.PATHIMAGE)));
                questao.setPosition(count);
                count++;
                questaoList.add(questao);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return questaoList;
    }
    public List<Questao> getQuestoes(Professor professor,String and){
        List<Questao> questaoList = new ArrayList<>();
        String more = "where "+DataBase.ID_PROFESSOR+" == "+professor.getId()+" "+and;
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_QUESTAO+" "+more+";",null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Questao questao = new Questao(
                        cursor.getString(cursor.getColumnIndex(DataBase.PERGUNTA)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATIVACORRETA)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATIA)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATIB)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATIC)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATID)),
                        cursor.getString(cursor.getColumnIndex(DataBase.ALTERNATIE)));
                questao.setIdProfessor(professor.getId());
                questao.setIdQuestao(cursor.getInt(cursor.getColumnIndex(DataBase.ID_QUESTAO)));
                questao.setPathImage(cursor.getString(cursor.getColumnIndex(DataBase.PATHIMAGE)));
                questao.setIdDisciplina(cursor.getInt(cursor.getColumnIndex(DataBase.ID_DISCIPLINA)));
                questaoList.add(questao);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return questaoList;
    }
    public boolean insert(Questao questao){
        boolean save;
        String more = "where "+DataBase.ID_PROFESSOR+" == "+questao.getIdProfessor();
        if (search(DataBase.TABLE_QUESTAO, questao.getPergunta(), 1, more)){
            save = false;
        }else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBase.PERGUNTA, questao.getPergunta());
            contentValues.put(DataBase.ALTERNATIVACORRETA, questao.getAlternativaCorreta());
            contentValues.put(DataBase.ID_PROFESSOR, questao.getIdProfessor());
            contentValues.put(DataBase.ALTERNATIA, questao.getAlternativaA());
            contentValues.put(DataBase.ALTERNATIB, questao.getAlternativaB());
            contentValues.put(DataBase.ALTERNATIC, questao.getAlternativaC());
            contentValues.put(DataBase.ALTERNATID, questao.getAlternativaD());
            contentValues.put(DataBase.ALTERNATIE, questao.getAlternativaE());
            contentValues.put(DataBase.ID_DISCIPLINA, questao.getIdDisciplina());
            if (questao.getPathImage() != null){
                contentValues.put(DataBase.PATHIMAGE, questao.getPathImage());
            }
            db.insert(DataBase.TABLE_QUESTAO, null, contentValues);
            save = true;
        }
        return save;
    }
    public Boolean insert(Professor professor){
        boolean save;
        if (search(DataBase.TABLE_PROFESSOR,professor.getMatricula().toString(),2,"")){
            save = false;
        }else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBase.NOME_PROFESSOR, professor.getNomeProfessor());
            contentValues.put(DataBase.SENHA, professor.getSenha());
            contentValues.put(DataBase.MATRICULA_PROFESSOR, professor.getMatricula());
            contentValues.put(DataBase.LOGGED, professor.getOnline());
            contentValues.put(DataBase.URIFOTO,"null");
            contentValues.put(DataBase.SHOW,"yes");
            contentValues.put(DataBase.EMAIL,professor.getEmail());
            contentValues.put(DataBase.SHOWAPRESATATION,"yes");
            configutionShow();
            db.insert(DataBase.TABLE_PROFESSOR, null, contentValues);
            save = true;
        }
        return save;
    }
    public Boolean insert(Disciplina disciplina){
        String more = "where "+DataBase.ID_PROFESSOR+" == "+disciplina.getIdProfessor();
        if (search(DataBase.TABLE_DISCIPLINA,disciplina.getNomeDisciplina(),2,more)){
            return false;
        }else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBase.NOME_DISCIPLINA, disciplina.getNomeDisciplina());
            contentValues.put(DataBase.ID_PROFESSOR, disciplina.getIdProfessor());
            db.insert(DataBase.TABLE_DISCIPLINA, null, contentValues);
            return true;
        }
    }
    public Boolean insertTurmaDisciplina(Disciplina disciplina){
        boolean isValid = false;
        for (int i = 0; i < disciplina.getTurmas().size(); i++) {
            Turma turma = disciplina.getTurmas().get(i);
            String more = "where "+DataBase.ID_PROFESSOR+" == "+disciplina.getIdProfessor()+" and "+DataBase.ID_TURMA +" == "+turma.getIdTurma();
            if (!search(DataBase.TABLE_TURMA_DISCIPLINA, disciplina.getIdDisciplina()+"", 1, more)){
                ContentValues contentValues = new ContentValues();
                contentValues.put(DataBase.ID_PROFESSOR, disciplina.getIdProfessor());
                contentValues.put(DataBase.ID_DISCIPLINA, disciplina.getIdDisciplina());
                contentValues.put(DataBase.ID_TURMA, turma.getIdTurma());
                db.insert(DataBase.TABLE_TURMA_DISCIPLINA, null, contentValues);
                isValid = true;
            }
        }
        return isValid;
    }
    public Boolean insert(Turma turma){
        String more = "where "+DataBase.ID_PROFESSOR+" == "+turma.getIdProfessor();
        if (search(DataBase.TABLE_TURMA,turma.getNomeTurma().toString(),2,more)){
            return false;
        }else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBase.NOME_TURMA, turma.getNomeTurma());
            contentValues.put(DataBase.ID_PROFESSOR, turma.getIdProfessor());
            db.insert(DataBase.TABLE_TURMA, null, contentValues);
            return true;
        }
    }
    public boolean insert(Aluno aluno,String more){
        if (search(DataBase.TABLE_ALUNO, aluno.getMatriculaAluno().toString(), 3, more)){
            return false;
        }else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataBase.ID_TURMA, aluno.getIdTurma());
            contentValues.put(DataBase.NOME_ALUNO, aluno.getNomeAluno());
            contentValues.put(DataBase.MATRICULA_ALUNO, aluno.getMatriculaAluno());
            contentValues.put(DataBase.ID_PROFESSOR, aluno.getIdProfessor());
            db.insert("aluno", null, contentValues);
            return true;
        }
    }
    public void configutionShow(){
        db.execSQL("UPDATE " + DataBase.TABLE_PROFESSOR + " SET " + DataBase.SHOW + " = 'no';");;
    }
    public boolean login(String matricula, String senha,String show){
        boolean logged = false;
        //faz o login
        db = new DataBase(context).getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_PROFESSOR+";",null);
            if (cursor.getCount() > 0){
                cursor.moveToFirst();
                do {
                    if (matricula.equals(cursor.getString(2)) && senha.equals(cursor.getString(3))){
                        update(DataBase.TABLE_PROFESSOR,DataBase.LOGGED,"'on'",DataBase.ID_PROFESSOR,cursor.getInt(0),"");
                        if(show.equals("yes")){
                            configutionShow();
                            update(DataBase.TABLE_PROFESSOR,DataBase.SHOW,"'yes'",DataBase.ID_PROFESSOR,cursor.getInt(0),"");
                        }else{
                            update(DataBase.TABLE_PROFESSOR,DataBase.SHOW,"'no'",DataBase.ID_PROFESSOR,cursor.getInt(0),"");
                        }
                        logged = true;
                    }
                }while (cursor.moveToNext());
            }
        cursor.close();
        return logged;
    }
    public void exit(int id){
        update(DataBase.TABLE_PROFESSOR, DataBase.LOGGED, "'off'", DataBase.ID_PROFESSOR, id, "");
    }
    public Professor getLogged(){
        //returna o professor logado
        Professor professor = new Professor();
        professor.setOnline("off");
        Cursor cursor = db.rawQuery("select * from professor;",null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                if (cursor.getString(4).equalsIgnoreCase("on")){
                    professor.setId(cursor.getInt(0));
                    professor.setNomeProfessor(cursor.getString(1));
                    professor.setMatricula(cursor.getString(2));
                    professor.setSenha(cursor.getString(3));
                    professor.setOnline(cursor.getString(4));
                    professor.setUriFoto(cursor.getString(5));
                    professor.setShow(cursor.getString(6));
                    professor.setEmail(cursor.getString(cursor.getColumnIndex(DataBase.EMAIL)));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return professor;
    }
    public Professor showed(){
        //returna o professor que quer mostrar o login
        Professor professor = new Professor();
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_PROFESSOR+";",null);
        if (cursor.getCount() > 0){
            cursor.moveToFirst();
            do {
                if (cursor.getString(6).equalsIgnoreCase("yes")){
                    professor.setId(cursor.getInt(0));
                    professor.setMatricula(cursor.getString(2));
                    professor.setSenha(cursor.getString(3));
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return professor;
    }
    public void close(){
        db.close();
    }

    public List<Turma> getTurmas(Professor professor, String and){
        List<Turma> turmaList = new ArrayList<Turma>();
        String more = "where "+DataBase.ID_PROFESSOR+" == "+professor.getId() +" "+and+" order by "+DataBase.NOME_TURMA+" asc ";
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_TURMA+" "+more+";",null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Turma turma = new Turma(cursor.getInt(0),cursor.getInt(1),cursor.getString(2));
                turmaList.add(turma);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return turmaList;
    }
    public List<Aluno> getAlunos(int idTurma){
        List<Aluno> alunoList = new ArrayList<Aluno>();
        String more = "where "+DataBase.ID_TURMA+" == "+idTurma+" order by "+DataBase.NOME_ALUNO+" asc ";
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_ALUNO+" "+more+";",null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Aluno aluno = new Aluno(cursor.getInt(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4));
                aluno.setIdALuno(cursor.getInt(0));
                alunoList.add(aluno);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return alunoList;
    }
    public List<Aluno> getAlunosDoProfessor(int idProfessor, String and){
        List<Aluno> alunoList = new ArrayList<Aluno>();
        String more = "where "+DataBase.ID_PROFESSOR+" == "+idProfessor+" "+and+" order by "+DataBase.NOME_ALUNO+" asc ";
        Cursor cursor = db.rawQuery("select * from " + DataBase.TABLE_ALUNO + " " + more + ";", null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Aluno aluno = new Aluno(cursor.getInt(1),cursor.getString(2),cursor.getString(3),cursor.getInt(4));
                aluno.setIdALuno(cursor.getInt(0));
                alunoList.add(aluno);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return alunoList;
    }
    public Prova getProva(Avaliacao avaliacao,String and){
        String more = "where "+DataBase.ID_AVALIACAO+" = "+avaliacao.getIdAvaliacao()+" "+and;
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_PROVA+" "+more+";",null);

        Prova prova = new Prova();
        Professor professor = getLogged();
        prova.setProfessor(professor);
        List<Questao> questaoList = new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            prova.setDisciplina(getDisciplinas(professor,"and "+DataBase.ID_DISCIPLINA+" = "+avaliacao.getIdDisciplina()).get(0));
            do {
                Questao questao = getQuestoes(professor, "and " + DataBase.ID_QUESTAO + " = " + cursor.getInt(cursor.getColumnIndex(DataBase.ID_QUESTAO))).get(0);
                questao.setValor(cursor.getString(cursor.getColumnIndex(DataBase.VALOR_QUESTAO)));
                questaoList.add(questao);
            }while (cursor.moveToNext());
        }
        cursor.close();
        avaliacao.setQuestoes(questaoList);
        prova.setAvaliacao(avaliacao);
        return prova;
    }
    public List<AllInfo> getAllInfoAvaliacoes(int idProfessor,String more){
        List<AllInfo> allInfoList = new ArrayList<>();
        more = "where "+DataBase.ID_PROFESSOR+" == "+idProfessor+" "+more+" order by "+DataBase.ASSUNTO+" asc ";
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_AVALIACAO+" "+more+";",null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Avaliacao avaliacao = new Avaliacao();
                avaliacao.setIdAvaliacao(cursor.getInt(cursor.getColumnIndex(DataBase.ID_AVALIACAO)));
                avaliacao.setAssunto(cursor.getString(cursor.getColumnIndex(DataBase.ASSUNTO)));
                avaliacao.setValor(cursor.getString(cursor.getColumnIndex(DataBase.VALOR)));
                avaliacao.setBimestre(cursor.getString(cursor.getColumnIndex(DataBase.BIMESTRE)));
                avaliacao.setTipo(cursor.getString(cursor.getColumnIndex(DataBase.TIPO)));
                avaliacao.setIdProfessor(idProfessor);
                avaliacao.setData(cursor.getString(cursor.getColumnIndex(DataBase.DATA)));
                avaliacao.setIdDisciplina(cursor.getInt(cursor.getColumnIndex(DataBase.ID_DISCIPLINA)));
                avaliacao.setQtdNotificado(cursor.getInt(cursor.getColumnIndex(DataBase.QTD_NOTIFICADO)));
                avaliacao.setDiaDoCadastro(cursor.getString(cursor.getColumnIndex(DataBase.DIA_DO_CADASTRO)));
                Professor professor = getLogged();
                AllInfo allInfo = new AllInfo();
                allInfo.setTurma(getTurmas(professor, "and " + DataBase.ID_TURMA + " == " + cursor.getInt(cursor.getColumnIndex(DataBase.ID_TURMA))).get(0));
                avaliacao.setTurma(allInfo.getTurma());
                allInfo.setAvaliacao(avaliacao);
                allInfo.setDisciplina(getDisciplinas(professor, "and " + DataBase.ID_DISCIPLINA + " == " + avaliacao.getIdDisciplina()).get(0));
                allInfoList.add(allInfo);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return allInfoList;
    }
    public List<Avaliacao> getAValiacoes(int idProfessor, String more){
        List<Avaliacao> avaliacaoList = new ArrayList<Avaliacao>();
        more = "where "+DataBase.ID_PROFESSOR+" == "+idProfessor+" "+more+" order by "+DataBase.ASSUNTO+" asc ";
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_AVALIACAO+" "+more+";",null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Avaliacao avaliacao = new Avaliacao();
                avaliacao.setIdAvaliacao(cursor.getInt(cursor.getColumnIndex(DataBase.ID_AVALIACAO)));
                avaliacao.setAssunto(cursor.getString(cursor.getColumnIndex(DataBase.ASSUNTO)));
                avaliacao.setValor(cursor.getString(cursor.getColumnIndex(DataBase.VALOR)));
                avaliacao.setBimestre(cursor.getString(cursor.getColumnIndex(DataBase.BIMESTRE)));
                avaliacao.setTipo(cursor.getString(cursor.getColumnIndex(DataBase.TIPO)));
                avaliacao.setIdProfessor(idProfessor);
                avaliacao.setData(cursor.getString(cursor.getColumnIndex(DataBase.DATA)));
                avaliacao.setIdDisciplina(cursor.getInt(cursor.getColumnIndex(DataBase.ID_DISCIPLINA)));
                avaliacao.setQtdNotificado(cursor.getInt(cursor.getColumnIndex(DataBase.QTD_NOTIFICADO)));
                avaliacao.setDiaDoCadastro(cursor.getString(cursor.getColumnIndex(DataBase.DIA_DO_CADASTRO)));
                avaliacaoList.add(avaliacao);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return avaliacaoList;
    }
    public List<Disciplina> getDisciplinas(Professor professor, String and){
        List<Disciplina> disciplinaList = new ArrayList<Disciplina>();
        String more = "where "+DataBase.ID_PROFESSOR+" == "+professor.getId() +" "+and+" order by "+DataBase.NOME_DISCIPLINA+" asc ";
        Cursor cursor = db.rawQuery("select * from "+DataBase.TABLE_DISCIPLINA+" "+more+";",null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Disciplina disciplina = new Disciplina();
                disciplina.setIdDisciplina(cursor.getInt(0));
                disciplina.setIdProfessor(cursor.getInt(1));
                disciplina.setNomeDisciplina(cursor.getString(2));
                disciplinaList.add(disciplina);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return disciplinaList;
    }
    public AllInfo getTurmaDisciplinas(int idProfessor, String and){
        AllInfo allInfo = new AllInfo();
        final String nomeDisciplinaTable = DataBase.TABLE_DISCIPLINA;
        final String colunaDisciplinaId = DataBase.ID_DISCIPLINA;
        final String nomeTurmaTable = DataBase.TABLE_TURMA;
        final String colunaTurmaId = DataBase.ID_TURMA;
        final String nomeTurmaDisciplinaTable = DataBase.TABLE_TURMA_DISCIPLINA;
        String more = " where "+DataBase.TABLE_TURMA_DISCIPLINA+"."+DataBase.ID_PROFESSOR+" == "+idProfessor+" "+and+" order by "+DataBase.NOME_DISCIPLINA+" asc ";
        Cursor cursor = db.rawQuery("select * from "+nomeTurmaDisciplinaTable
                +" join  "+nomeTurmaTable
                +" on "+nomeTurmaTable+"."+colunaTurmaId+" == "
                +nomeTurmaDisciplinaTable+"."+colunaTurmaId
                +" join "+nomeDisciplinaTable+" on "
                +nomeDisciplinaTable+"."+colunaDisciplinaId+" == "
                +nomeTurmaDisciplinaTable+"."+colunaDisciplinaId
                +more+";",null);
        List<Turma> nomesTurmas = new ArrayList<>();
        List<Disciplina> nomesDisciplinas= new ArrayList<>();
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                Disciplina disciplina = new Disciplina();
                Turma turma = new Turma();

                disciplina.setNomeDisciplina(cursor.getString(cursor.getColumnIndex(DataBase.NOME_DISCIPLINA)));
                disciplina.setIdDisciplina(cursor.getInt(cursor.getColumnIndex(DataBase.ID_DISCIPLINA)));

                turma.setNomeTurma(cursor.getString(cursor.getColumnIndex(DataBase.NOME_TURMA)));
                turma.setIdTurma(cursor.getInt(cursor.getColumnIndex(DataBase.ID_TURMA)));

                nomesDisciplinas.add(disciplina);
                nomesTurmas.add(turma);
            }while (cursor.moveToNext());
        }
        allInfo.setDiciplinas(nomesDisciplinas);
        allInfo.setTurmas(nomesTurmas);
        cursor.close();
        return allInfo;
    }
    public void delete(String table,String more){
        db.execSQL("delete from " + table + " " + more);
    }
    public boolean search(String tableName,String data,int index,String where){
        //faz uma pesquisa no banco
        boolean exist = false;
        String indexData;
        Cursor cursor = db.rawQuery("select * from "+tableName+" "+where+";",null);
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                indexData = cursor.getString(index);
                if (indexData.equals(data)){
                    return true;
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        return exist;
    }
    public void update(String table ,String col, String data,String id_col,int id,String and){
        db.execSQL("UPDATE " + table + " SET " + col + " = " + data + " WHERE " + id_col + " == " + id + " " + and + ";");;
    }
}











