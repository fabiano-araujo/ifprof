package com.example.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fabiano on 08/12/15.
 */
public class Questao implements Parcelable{
    private String pergunta;
    private String alternativaCorreta;
    private String alternativaA;
    private String alternativaB;
    private String alternativaC;
    private String alternativaD;
    private String alternativaE;
    private int idProfessor;
    private int idQuestao;
    private int intent;
    private int position;
    private String valor;
    private String pathImage;
    private int idDisciplina;

    protected Questao(Parcel in) {
        pergunta = in.readString();
        alternativaCorreta = in.readString();
        alternativaA = in.readString();
        alternativaB = in.readString();
        alternativaC = in.readString();
        alternativaD = in.readString();
        alternativaE = in.readString();
        idProfessor = in.readInt();
        idQuestao = in.readInt();
        intent = in.readInt();
        position = in.readInt();
        valor = in.readString();
        pathImage = in.readString();
        idDisciplina = in.readInt();
    }

    public static final Creator<Questao> CREATOR = new Creator<Questao>() {
        @Override
        public Questao createFromParcel(Parcel in) {
            return new Questao(in);
        }

        @Override
        public Questao[] newArray(int size) {
            return new Questao[size];
        }
    };

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIntent() {
        return intent;
    }

    public void setIntent(int intent) {
        this.intent = intent;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public int getIdQuestao() {
        return idQuestao;
    }

    public void setIdQuestao(int idQuestao) {
        this.idQuestao = idQuestao;
    }

    public int getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(int idProfessor) {
        this.idProfessor = idProfessor;
    }

    public Questao(String pergunta, String alternativaCorreta,String alternativaA,String alternativaB,String alternativaC,String alternativaD,String alternativaE){
        this.pergunta = pergunta;
        this.alternativaCorreta = alternativaCorreta;
        this.alternativaA = alternativaA;
        this.alternativaB = alternativaB;
        this.alternativaC = alternativaC;
        this.alternativaD = alternativaD;
        this.alternativaE = alternativaE;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getAlternativaCorreta() {
        return alternativaCorreta;
    }

    public void setAlternativaCorreta(String alternativaCorreta) {
        this.alternativaCorreta = alternativaCorreta;
    }

    public int getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(int idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public String getAlternativaA() {
        return alternativaA;
    }

    public void setAlternativaA(String alternativaA) {
        this.alternativaA = alternativaA;
    }

    public String getAlternativaB() {
        return alternativaB;
    }

    public void setAlternativaB(String alternativaB) {
        this.alternativaB = alternativaB;
    }

    public String getAlternativaC() {
        return alternativaC;
    }

    public void setAlternativaC(String alternativaC) {
        this.alternativaC = alternativaC;
    }

    public String getAlternativaD() {
        return alternativaD;
    }

    public void setAlternativaD(String alternativaD) {
        this.alternativaD = alternativaD;
    }

    public String getAlternativaE() {
        return alternativaE;
    }

    public void setAlternativaE(String alternativaE) {
        this.alternativaE = alternativaE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pergunta);
        dest.writeString(alternativaCorreta);
        dest.writeString(alternativaA);
        dest.writeString(alternativaB);
        dest.writeString(alternativaC);
        dest.writeString(alternativaD);
        dest.writeString(alternativaE);
        dest.writeInt(idProfessor);
        dest.writeInt(idQuestao);
        dest.writeInt(intent);
        dest.writeInt(position);
        dest.writeString(valor);
        dest.writeString(pathImage);
        dest.writeInt(idDisciplina);
    }
}
