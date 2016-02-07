package com.example.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fabiano on 11/11/15.
 */
public class Falta implements Parcelable{
    private int idFalta;
    private int idDisciplina;
    private String qdtFaltas;
    private String aulasMinistradas;
    private String date;
    private boolean save;
    private Aluno aluno;
    private List<String> dataList;
    private List<String> qtdFaltasList;
    private List<String> aulasMinistradasList;

    protected Falta(Parcel in) {
        idFalta = in.readInt();
        idDisciplina = in.readInt();
        qdtFaltas = in.readString();
        aulasMinistradas = in.readString();
        date = in.readString();
        save = in.readByte() != 0;
        aluno = in.readParcelable(Aluno.class.getClassLoader());
        dataList = in.createStringArrayList();
        qtdFaltasList = in.createStringArrayList();
        aulasMinistradasList = in.createStringArrayList();
    }
    public Falta(){
    }
    public static final Creator<Falta> CREATOR = new Creator<Falta>() {
        @Override
        public Falta createFromParcel(Parcel in) {
            return new Falta(in);
        }

        @Override
        public Falta[] newArray(int size) {
            return new Falta[size];
        }
    };

    public List<String> getDataList() {
        return dataList;
    }

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    public List<String> getQtdFaltasList() {
        return qtdFaltasList;
    }

    public void setQtdFaltasList(List<String> qtdFaltasList) {
        this.qtdFaltasList = qtdFaltasList;
    }

    public List<String> getAulasMinistradasList() {
        return aulasMinistradasList;
    }

    public void setAulasMinistradasList(List<String> aulasMinistradasList) {
        this.aulasMinistradasList = aulasMinistradasList;
    }

    public boolean isSave() {
        return save;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public void setSave(boolean save) {
        this.save = save;
    }


    public int getIdFalta() {
        return idFalta;
    }

    public void setIdFalta(int idFalta) {
        this.idFalta = idFalta;
    }

    public int getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(int idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public String getQdtFaltas() {
        return qdtFaltas;
    }

    public void setQdtFaltas(String qdtFaltas) {
        this.qdtFaltas = qdtFaltas;
    }

    public String getAulasMinistradas() {
        return aulasMinistradas;
    }

    public void setAulasMinistradas(String aulasMinistradas) {
        this.aulasMinistradas = aulasMinistradas;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idFalta);
        dest.writeInt(idDisciplina);
        dest.writeString(qdtFaltas);
        dest.writeString(aulasMinistradas);
        dest.writeString(date);
        dest.writeByte((byte) (save ? 1 : 0));
        dest.writeParcelable(aluno, flags);
        dest.writeStringList(dataList);
        dest.writeStringList(qtdFaltasList);
        dest.writeStringList(aulasMinistradasList);
    }
}
