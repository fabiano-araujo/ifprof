package com.developer.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fabiano on 21/11/15.
 */
public class Nota implements Parcelable{
    private int idNota;
    private boolean save;
    private float nota;
    private Aluno aluno;
    private Avaliacao avaliacao;

    public Nota(){}

    protected Nota(Parcel in) {
        idNota = in.readInt();
        save = in.readByte() != 0;
        nota = in.readFloat();
        aluno = in.readParcelable(Aluno.class.getClassLoader());
        avaliacao = in.readParcelable(Avaliacao.class.getClassLoader());
    }

    public static final Creator<Nota> CREATOR = new Creator<Nota>() {
        @Override
        public Nota createFromParcel(Parcel in) {
            return new Nota(in);
        }

        @Override
        public Nota[] newArray(int size) {
            return new Nota[size];
        }
    };

    public boolean isSave() {
        return save;
    }

    public void setSave(boolean save) {
        this.save = save;
    }

    public int getIdNota() {
        return idNota;
    }

    public void setIdNota(int idNota) {
        this.idNota = idNota;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public Avaliacao getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Avaliacao avaliacao) {
        this.avaliacao = avaliacao;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idNota);
        dest.writeByte((byte) (save ? 1 : 0));
        dest.writeFloat(nota);
        dest.writeParcelable(aluno, flags);
        dest.writeParcelable(avaliacao, flags);
    }
}
