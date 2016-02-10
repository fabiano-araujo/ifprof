package com.developer.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fabiano on 13/10/15.
 */
public class Turma implements Parcelable{
    private String nomeTurma;
    private int idTurma;
    private int idProfessor;
    private int position;
    public Turma(){}
    public Turma(int idTurma, int idProfessor,String nomeTurma){
        this.idTurma = idTurma;
        this.idProfessor = idProfessor;
        this.nomeTurma = nomeTurma;
    }

    protected Turma(Parcel in) {
        nomeTurma = in.readString();
        idTurma = in.readInt();
        idProfessor = in.readInt();
        position = in.readInt();
    }

    public static final Creator<Turma> CREATOR = new Creator<Turma>() {
        @Override
        public Turma createFromParcel(Parcel in) {
            return new Turma(in);
        }

        @Override
        public Turma[] newArray(int size) {
            return new Turma[size];
        }
    };

    public String getNomeTurma() {
        return nomeTurma;
    }

    public void setNomeTurma(String nomeTurma) {
        this.nomeTurma = nomeTurma;
    }

    public int getIdTurma() {
        return idTurma;
    }

    public void setIdTurma(int idTurma) {
        this.idTurma = idTurma;
    }

    public int getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(int idProfessor) {
        this.idProfessor = idProfessor;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nomeTurma);
        dest.writeInt(idTurma);
        dest.writeInt(idProfessor);
        dest.writeInt(position);
    }
}
