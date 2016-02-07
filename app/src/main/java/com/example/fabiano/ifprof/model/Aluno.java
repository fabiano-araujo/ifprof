package com.example.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fabiano on 05/10/15.
 */
public class Aluno implements Parcelable{
    private int idALuno;
    private String nomeAluno;
    private String matriculaAluno;
    private int idTurma;
    private int idProfessor;
    private boolean isSelected;
    private String faltas ="";
    private String nota ="";

    protected Aluno(Parcel in) {
        idALuno = in.readInt();
        nomeAluno = in.readString();
        matriculaAluno = in.readString();
        idTurma = in.readInt();
        idProfessor = in.readInt();
        isSelected = in.readByte() != 0;
        faltas = in.readString();
        nota = in.readString();
    }

    public static final Creator<Aluno> CREATOR = new Creator<Aluno>() {
        @Override
        public Aluno createFromParcel(Parcel in) {
            return new Aluno(in);
        }

        @Override
        public Aluno[] newArray(int size) {
            return new Aluno[size];
        }
    };

    public String getNota() {
        return nota;
    }

    public void setNota(String nota) {
        this.nota = nota;
    }

    public Aluno(){
        isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getFaltas() {
        return faltas;
    }

    public void setFaltas(String faltas) {
        this.faltas = faltas;
    }

    public int getIdTurma() {
        return idTurma;
    }

    public void setIdTurma(int idTurma) {
        this.idTurma = idTurma;
    }

    public Aluno(String nomeAluno,String matriculaAluno){
        this.nomeAluno = nomeAluno;
        this.matriculaAluno = matriculaAluno;
    }

    public Aluno(int idTurma,String nomeAluno,String matriculaAluno,int idProfessor ){
        this.nomeAluno = nomeAluno;
        this.matriculaAluno = matriculaAluno;
        this.idTurma = idTurma;
        this.idProfessor = idProfessor;
    }

    public int getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(int idProfessor) {
        this.idProfessor = idProfessor;
    }

    public int getIdALuno() {
        return idALuno;
    }

    public void setIdALuno(int idALuno) {
        this.idALuno = idALuno;
    }

    public String getNomeAluno() {
        return nomeAluno;
    }

    public void setNomeAluno(String nomeAluno) {
        this.nomeAluno = nomeAluno;
    }

    public String getMatriculaAluno() {
        return matriculaAluno;
    }

    public void setMatriculaAluno(String matriculaAluno) {
        this.matriculaAluno = matriculaAluno;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idALuno);
        dest.writeString(nomeAluno);
        dest.writeString(matriculaAluno);
        dest.writeInt(idTurma);
        dest.writeInt(idProfessor);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeString(faltas);
        dest.writeString(nota);
    }
}
