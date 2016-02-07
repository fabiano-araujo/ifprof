
package com.example.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Prova  implements Parcelable{
    private Avaliacao avaliacao;
    private Disciplina disciplina;
    private Professor professor;
    public Prova(){}

    protected Prova(Parcel in) {
        avaliacao = in.readParcelable(Avaliacao.class.getClassLoader());
        disciplina = in.readParcelable(Disciplina.class.getClassLoader());
        professor = in.readParcelable(Professor.class.getClassLoader());
    }

    public static final Creator<Prova> CREATOR = new Creator<Prova>() {
        @Override
        public Prova createFromParcel(Parcel in) {
            return new Prova(in);
        }

        @Override
        public Prova[] newArray(int size) {
            return new Prova[size];
        }
    };

    public Avaliacao getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Avaliacao avaliacao) {
        this.avaliacao = avaliacao;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(avaliacao, flags);
        dest.writeParcelable(disciplina, flags);
        dest.writeParcelable(professor, flags);
    }
}
