package com.developer.fabiano.ifprof.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by fabiano on 26/09/15.
 */
public class Professor implements Parcelable {
    private int id;
    private String nomeProfessor;
    private String senha;
    private String outro;
    private String matricula;
    private String online;
    private String uriFoto;
    private String show;
    private String email;


    public Professor(){}

    protected Professor(Parcel in) {
        id = in.readInt();
        nomeProfessor = in.readString();
        senha = in.readString();
        outro = in.readString();
        matricula = in.readString();
        online = in.readString();
        uriFoto = in.readString();
        show = in.readString();
        email = in.readString();
    }

    public static final Creator<Professor> CREATOR = new Creator<Professor>() {
        @Override
        public Professor createFromParcel(Parcel in) {
            return new Professor(in);
        }

        @Override
        public Professor[] newArray(int size) {
            return new Professor[size];
        }
    };

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public String getNomeProfessor() {
        return nomeProfessor;
    }

    public void setNomeProfessor(String nomeProfessor) {
        this.nomeProfessor = nomeProfessor;
    }

    public String getOnline() {
        return online;
    }

    public String getUriFoto() {
        return uriFoto;
    }

    public void setUriFoto(String uriFoto) {
        this.uriFoto = uriFoto;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getMatricula() {

        return matricula;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getSenha() {
        return senha;
    }

    public String getOutro() {
        return outro;
    }

    public void setOutro(String outro) {
        this.outro = outro;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nomeProfessor);
        dest.writeString(senha);
        dest.writeString(outro);
        dest.writeString(matricula);
        dest.writeString(online);
        dest.writeString(uriFoto);
        dest.writeString(show);
        dest.writeString(email);
    }
}
