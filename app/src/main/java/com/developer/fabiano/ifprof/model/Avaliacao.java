package com.developer.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiano on 05/11/15.
 */
public class Avaliacao implements Parcelable{
    private String assunto;
    private String valor;
    private String tipo;
    private String bimestre;
    private Turma turma;
    private int idDisciplina;
    private int idProfessor;
    private int idAvaliacao;
    private String data;
    private boolean alter;
    private int qtdNotificado;
    private String diaDoCadastro;

    private List<Questao> questoes = new ArrayList<>();;
    public Avaliacao(){
        alter = false;
    }

    protected Avaliacao(Parcel in) {
        assunto = in.readString();
        valor = in.readString();
        tipo = in.readString();
        bimestre = in.readString();
        turma = in.readParcelable(Turma.class.getClassLoader());
        idDisciplina = in.readInt();
        idProfessor = in.readInt();
        idAvaliacao = in.readInt();
        data = in.readString();
        alter = in.readByte() != 0;
        qtdNotificado = in.readInt();
        diaDoCadastro = in.readString();
        questoes = in.createTypedArrayList(Questao.CREATOR);
    }

    public static final Creator<Avaliacao> CREATOR = new Creator<Avaliacao>() {
        @Override
        public Avaliacao createFromParcel(Parcel in) {
            return new Avaliacao(in);
        }

        @Override
        public Avaliacao[] newArray(int size) {
            return new Avaliacao[size];
        }
    };

    public String getDiaDoCadastro() {
        return diaDoCadastro;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public void setDiaDoCadastro(String diaDoCadastro) {
        this.diaDoCadastro = diaDoCadastro;
    }

    public boolean isAlter() {
        return alter;
    }

    public void setAlter(boolean alter) {
        this.alter = alter;
    }

    public int getQtdNotificado() {
        return qtdNotificado;
    }

    public void setQtdNotificado(int qtdNotificado) {
        this.qtdNotificado = qtdNotificado;
    }

    public List<Questao> getQuestoes() {
        return questoes;
    }

    public void setQuestoes(List<Questao> questoes) {
        this.questoes = questoes;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getIdAvaliacao() {
        return idAvaliacao;
    }

    public void setIdAvaliacao(int idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        this.assunto = assunto;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getBimestre() {
        return bimestre;
    }

    public void setBimestre(String bimestre) {
        this.bimestre = bimestre;
    }

    public int getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(int idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public int getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(int idProfessor) {
        this.idProfessor = idProfessor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(assunto);
        dest.writeString(valor);
        dest.writeString(tipo);
        dest.writeString(bimestre);
        dest.writeParcelable(turma, flags);
        dest.writeInt(idDisciplina);
        dest.writeInt(idProfessor);
        dest.writeInt(idAvaliacao);
        dest.writeString(data);
        dest.writeByte((byte) (alter ? 1 : 0));
        dest.writeInt(qtdNotificado);
        dest.writeString(diaDoCadastro);
        dest.writeTypedList(questoes);
    }
}
