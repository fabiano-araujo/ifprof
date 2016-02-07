package com.example.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiano on 22/10/15.
 */
public class Disciplina implements Parcelable{
    private String nomeDisciplina;
    private int idDisciplina;
    private int idTurma;
    private List<Turma> Turmas = new ArrayList<>();
    private int idProfessor;

    public Disciplina(){}

    protected Disciplina(Parcel in) {
        nomeDisciplina = in.readString();
        idDisciplina = in.readInt();
        idTurma = in.readInt();
        Turmas = in.createTypedArrayList(Turma.CREATOR);
        idProfessor = in.readInt();
    }

    public static final Creator<Disciplina> CREATOR = new Creator<Disciplina>() {
        @Override
        public Disciplina createFromParcel(Parcel in) {
            return new Disciplina(in);
        }

        @Override
        public Disciplina[] newArray(int size) {
            return new Disciplina[size];
        }
    };

    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    public int getIdTurma() {
        return idTurma;
    }

    public int getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(int idDisciplina) {
        this.idDisciplina = idDisciplina;
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

    @Override
    public int describeContents() {
        return 0;
    }

    public List<Turma> getTurmas() {
        return Turmas;
    }

    public void setTurmas(List<Turma> turmas) {
        Turmas = turmas;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nomeDisciplina);
        dest.writeInt(idDisciplina);
        dest.writeInt(idTurma);
        dest.writeTypedList(Turmas);
        dest.writeInt(idProfessor);
    }
}
