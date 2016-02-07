package com.example.fabiano.ifprof.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {
    //banco
    public static final String NOME_DB = "ifProf";
    public static final int VERSAO =11;
    //professor
    public static final String TABLE_PROFESSOR = "professor";
    public static final String ID_PROFESSOR = "_id_professor";
    public static final String NOME_PROFESSOR = "nome_professor";
    public static final String EMAIL = "email";
    public static final String MATRICULA_PROFESSOR = "matricula_professor";
    public static final String SENHA = "senha";
    public static final String LOGGED= "getLogged";
    public static final String URIFOTO= "uri_foto";
    public static final String SHOW= "show";
    public static final String SHOWAPRESATATION= "show_apresentation";

    //turma
    public static final String TABLE_TURMA = "turma";
    public static final String ID_TURMA = "_id_turma";
    public static final String NOME_TURMA = "nome_turma";

    //aluno
    public static final String TABLE_ALUNO = "aluno";
    public static final String ID_ALUNO = "_id_aluno";
    public static final String NOME_ALUNO = "nome_aluno";
    public static final String MATRICULA_ALUNO = "matricula_aluno";

    //disciplina
    public static final String TABLE_DISCIPLINA = "disciplina";
    public static final String ID_DISCIPLINA = "_id_disciplina";
    public static final String NOME_DISCIPLINA = "nome_disciplina";

    //turma e disclina
    public static final String TABLE_TURMA_DISCIPLINA = "turma_disciplina";
    public DataBase(Context context) {
        super(context, NOME_DB, null,VERSAO );
    }

    public static final String TABLE_TURMA_AVALIACAO = "turma_avaliacao";

    //avaliacoes
    public static final String TABLE_AVALIACAO = "avaliacao";
    public static final String ID_AVALIACAO = "_id_avaliacao";
    public static final String ASSUNTO = "assunto";
    public static final String VALOR = "valor";
    public static final String TIPO = "tipo";
    public static final String BIMESTRE = "bimestre";
    public static final String QTD_NOTIFICADO = "qtd_notificado";
    public static final String DIA_DO_CADASTRO = "dia_do_cadastro";


    public static final String TABLE_FALTA = "falta";
    public static final String ID_FALTA = "_id_falta";
    public static final String QTD_FALTAS = "qtd_faltas";
    public static final String DATA = "data";
    public static final String ULTIMO = "ultimo";
    public static final String AULAS_MINISTRADAS = "aulas_ministradas";

    public static final String TABLE_NOTA = "table_nota";
    public static final String ID_NOTA = "_id_nota";
    public static final String NOTA = "Nota";

    public static final String TABLE_QUESTAO = "table_questao";
    public static final String ID_QUESTAO = "_id_questao";
    public static final String PERGUNTA = "pergunta";
    public static final String PATHIMAGE = "path_image";
    public static final String ALTERNATIVACORRETA = "alternativa_correta";
    public static final String ALTERNATIA = "alternativa_a";
    public static final String ALTERNATIB = "alternativa_b";
    public static final String ALTERNATIC = "alternativa_c";
    public static final String ALTERNATID = "alternativa_d";
    public static final String ALTERNATIE = "alternativa_e";

    public static final String TABLE_PROVA = "table_prova";
    public static final String ID_PROVA = "_id_prova";
    public static final String VALOR_QUESTAO = "Nota";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_PROFESSOR+" ("+
                ID_PROFESSOR+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                NOME_PROFESSOR+" text not null ,"+
                MATRICULA_PROFESSOR+" text not null,"+
                SENHA+" text not null,"+
                LOGGED+" text not null,"+
                URIFOTO+" text not null,"+
                SHOW+" text not null,"+
                EMAIL+" text not null,"+
                SHOWAPRESATATION+" text not null);");

        db.execSQL("CREATE TABLE "+TABLE_ALUNO+" ( "+
                ID_ALUNO+" INTEGER PRIMARY KEY AUTOINCREMENT ,"+
                ID_TURMA+" REFERENCES "+TABLE_TURMA+" ("+ID_TURMA+"),"+
                NOME_ALUNO+" text not null ,"+
                MATRICULA_ALUNO+" text not null ," +
                ID_PROFESSOR+" REFERENCES "+TABLE_PROFESSOR+" ("+ID_PROFESSOR+") "+");");

        db.execSQL("CREATE TABLE "+TABLE_TURMA+" ("+
                ID_TURMA+"  INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ID_PROFESSOR+" REFERENCES "+TABLE_PROFESSOR+" ("+ID_PROFESSOR+"), "+
                NOME_TURMA+" text not null);");

        db.execSQL("CREATE TABLE "+TABLE_DISCIPLINA+" ("+
                ID_DISCIPLINA+"  INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ID_PROFESSOR+" REFERENCES "+TABLE_PROFESSOR+" ("+ID_PROFESSOR+"), "+
                NOME_DISCIPLINA+" text not null);");

        db.execSQL("CREATE TABLE "+TABLE_TURMA_DISCIPLINA+" ("+
                ID_PROFESSOR+" REFERENCES "+TABLE_PROFESSOR+" ("+ID_PROFESSOR+"), "+
                ID_DISCIPLINA+"  REFERENCES "+TABLE_DISCIPLINA+" ("+ID_DISCIPLINA+"), "+
                ID_TURMA+" REFERENCES "+TABLE_TURMA+" ("+ID_TURMA+"));");

        db.execSQL("CREATE TABLE "+TABLE_TURMA_AVALIACAO+" ("+
                ID_PROFESSOR+" REFERENCES "+TABLE_PROFESSOR+" ("+ID_PROFESSOR+"), "+
                ID_AVALIACAO+"  REFERENCES "+TABLE_AVALIACAO+" ("+ID_AVALIACAO+"), "+
                ID_TURMA+" REFERENCES "+TABLE_TURMA+" ("+ID_TURMA+"));");

        db.execSQL("CREATE TABLE "+TABLE_AVALIACAO+" ("+
                ID_AVALIACAO+"  INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ASSUNTO+" text not null ,"+
                VALOR+" real not null ,"+
                TIPO+" text not null ,"+
                BIMESTRE+" text not null ,"+
                DATA+" text not null ,"+
                ID_PROFESSOR+" REFERENCES "+TABLE_PROFESSOR+" ("+ID_PROFESSOR+"), "+
                ID_DISCIPLINA+"  REFERENCES "+TABLE_DISCIPLINA+" ("+ID_DISCIPLINA+"), "+
                ID_TURMA+" REFERENCES "+TABLE_TURMA+" ("+ID_TURMA+"),"+
                QTD_NOTIFICADO+" text not null ,"+
                DIA_DO_CADASTRO+" text not null);");

        db.execSQL("CREATE TABLE "+TABLE_FALTA+" ("+
                ID_FALTA+"  INTEGER PRIMARY KEY AUTOINCREMENT,"+
                QTD_FALTAS+" text not null ,"+
                AULAS_MINISTRADAS+" text not null ,"+
                DATA+" text not null ,"+
                ID_ALUNO+" REFERENCES "+TABLE_ALUNO+" ("+ID_ALUNO+"), "+
                ID_PROFESSOR+" REFERENCES "+TABLE_PROFESSOR+" ("+ID_PROFESSOR+"), "+
                ID_DISCIPLINA+"  REFERENCES "+TABLE_DISCIPLINA+" ("+ID_DISCIPLINA+"), "+
                ID_TURMA+" REFERENCES "+TABLE_TURMA+" ("+ID_TURMA+") ,"+
                ULTIMO+" text not null );");
        db.execSQL("CREATE TABLE "+TABLE_NOTA+" ("+
                ID_NOTA+"  INTEGER PRIMARY KEY AUTOINCREMENT,"+
                NOTA+" text not null ,"+
                ID_AVALIACAO+" REFERENCES "+TABLE_AVALIACAO+" ("+ID_AVALIACAO+"), "+
                ID_ALUNO+" REFERENCES "+TABLE_ALUNO+" ("+ID_ALUNO+"), "+
                ID_TURMA+" REFERENCES "+TABLE_TURMA+" ("+ID_TURMA+"),"+
                ID_PROFESSOR+" REFERENCES "+TABLE_PROFESSOR+" ("+ID_PROFESSOR+"));");
        db.execSQL("CREATE TABLE "+TABLE_QUESTAO+" ("+
                ID_QUESTAO+"  INTEGER PRIMARY KEY AUTOINCREMENT,"+
                PERGUNTA+" text not null ,"+
                ALTERNATIVACORRETA+" text not null ,"+
                ID_PROFESSOR+" REFERENCES "+TABLE_PROFESSOR+" ("+ID_PROFESSOR+"), "+
                ALTERNATIA+" text not null ,"+
                ALTERNATIB+" text not null ,"+
                ALTERNATIC+" text not null ,"+
                ALTERNATID+" text not null ,"+
                ALTERNATIE+" text not null ,"+
                ID_DISCIPLINA+"  REFERENCES "+TABLE_DISCIPLINA+" ("+ID_DISCIPLINA+") ," +
                PATHIMAGE+" text);");

        db.execSQL("CREATE TABLE "+TABLE_PROVA+" ("+
                ID_PROVA+"  INTEGER PRIMARY KEY AUTOINCREMENT,"+
                ID_AVALIACAO+"  REFERENCES "+TABLE_AVALIACAO+" ("+ID_AVALIACAO+"), "+
                ID_PROFESSOR+" REFERENCES "+TABLE_PROFESSOR+" ("+ID_PROFESSOR+"), "+
                VALOR_QUESTAO+" text not null ,"+
                ID_QUESTAO+" REFERENCES "+TABLE_QUESTAO+" ("+ID_QUESTAO+"));");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PROFESSOR+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_ALUNO+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TURMA+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_DISCIPLINA+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TURMA_DISCIPLINA+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_TURMA_AVALIACAO+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_AVALIACAO+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FALTA+";");
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NOTA+";");
        onCreate(db);
    }
}

