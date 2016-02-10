package com.developer.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiano on 20/11/15.
 */
public class Historico implements Parcelable{
    private List<Aluno> alunoList;
    private List<Falta> faltaList;
    private Disciplina disciplina;
    private AllInfo allInfo;
    private int position;
    public Historico(){
        alunoList = new ArrayList<>();
        faltaList = new ArrayList<>();
    }

    protected Historico(Parcel in) {
        alunoList = in.createTypedArrayList(Aluno.CREATOR);
        faltaList = in.createTypedArrayList(Falta.CREATOR);
        disciplina = in.readParcelable(Disciplina.class.getClassLoader());
        allInfo = in.readParcelable(AllInfo.class.getClassLoader());
        position = in.readInt();
    }

    public static final Creator<Historico> CREATOR = new Creator<Historico>() {
        @Override
        public Historico createFromParcel(Parcel in) {
            return new Historico(in);
        }

        @Override
        public Historico[] newArray(int size) {
            return new Historico[size];
        }
    };

    public List<Aluno> getAlunoList() {
        return alunoList;
    }

    public void setAlunoList(List<Aluno> alunoList) {
        this.alunoList = alunoList;
    }

    public List<Falta> getFaltaList() {
        return faltaList;
    }

    public void setFaltaList(List<Falta> faltaList) {
        this.faltaList = faltaList;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public AllInfo getAllInfo() {
        return allInfo;
    }

    public void setAllInfo(AllInfo allInfo) {
        this.allInfo = allInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(alunoList);
        dest.writeTypedList(faltaList);
        dest.writeParcelable(disciplina, flags);
        dest.writeParcelable(allInfo, flags);
        dest.writeInt(position);
    }
}
