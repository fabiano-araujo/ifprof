package com.developer.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by fabiano on 21/10/15.
 */
public class AllInfo implements Parcelable{
    private int qtdAlunos;
    private String nome;
    private Turma turma;
    private Disciplina disciplina;
    private Aluno aluno;
    private List<Disciplina> Diciplinas;
    private List<Avaliacao> avaliacoes;
    private List<Turma> Turmas;
    private List<Aluno> alunos;
    private Avaliacao avaliacao;
    private Falta falta;
    private Nota nota;

    protected AllInfo(Parcel in) {
        qtdAlunos = in.readInt();
        nome = in.readString();
        turma = in.readParcelable(Turma.class.getClassLoader());
        disciplina = in.readParcelable(Disciplina.class.getClassLoader());
        aluno = in.readParcelable(Aluno.class.getClassLoader());
        Diciplinas = in.createTypedArrayList(Disciplina.CREATOR);
        avaliacoes = in.createTypedArrayList(Avaliacao.CREATOR);
        Turmas = in.createTypedArrayList(Turma.CREATOR);
        alunos = in.createTypedArrayList(Aluno.CREATOR);
        avaliacao = in.readParcelable(Avaliacao.class.getClassLoader());
        falta = in.readParcelable(Falta.class.getClassLoader());
        nota = in.readParcelable(Nota.class.getClassLoader());
    }

    public static final Creator<AllInfo> CREATOR = new Creator<AllInfo>() {
        @Override
        public AllInfo createFromParcel(Parcel in) {
            return new AllInfo(in);
        }

        @Override
        public AllInfo[] newArray(int size) {
            return new AllInfo[size];
        }
    };

    public Falta getFalta() {
        return falta;
    }

    public void setFalta(Falta falta) {
        this.falta = falta;
    }

    public AllInfo(){}

    public List<Avaliacao> getAvaliacoes() {
        return avaliacoes;
    }

    public Avaliacao getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Avaliacao avaliacao) {
        this.avaliacao = avaliacao;
    }

    public void setAvaliacoes(List<Avaliacao> avaliacoes) {
        this.avaliacoes = avaliacoes;
    }
    public List<Aluno> getAlunos() {
        return alunos;
    }

    public void setAlunos(List<Aluno> alunos) {
        this.alunos = alunos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Disciplina> getDiciplinas() {
        return  Diciplinas;
    }

    public void setDiciplinas(List<Disciplina> nomesDiciplinas) {
        this. Diciplinas = nomesDiciplinas;
    }

    public List<Turma> getTurmas() {
        return Turmas;
    }

    public void setTurmas(List<Turma> nomesTurmas) {
        this.Turmas = nomesTurmas;
    }

    public int getQtdAlunos() {
        return qtdAlunos;
    }

    public void setQtdAlunos(int qtdAlunos) {
        this.qtdAlunos = qtdAlunos;
    }

    public Turma getTurma() {
        return turma;
    }

    public void setTurma(Turma turma) {
        this.turma = turma;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(qtdAlunos);
        dest.writeString(nome);
        dest.writeParcelable(turma, flags);
        dest.writeParcelable(disciplina, flags);
        dest.writeParcelable(aluno, flags);
        dest.writeTypedList(Diciplinas);
        dest.writeTypedList(avaliacoes);
        dest.writeTypedList(Turmas);
        dest.writeTypedList(alunos);
        dest.writeParcelable(avaliacao, flags);
        dest.writeParcelable(falta, flags);
        dest.writeParcelable(nota, flags);
    }
}

