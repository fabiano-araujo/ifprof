package com.example.fabiano.ifprof.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.fabiano.ifprof.Apresentation;
import com.example.fabiano.ifprof.Login;
import com.example.fabiano.ifprof.Perfil;
import com.example.fabiano.ifprof.R;
import com.example.fabiano.ifprof.ShowAvaliacoes;
import com.example.fabiano.ifprof.ShowDisciplinas;
import com.example.fabiano.ifprof.ShowQuestoes;
import com.example.fabiano.ifprof.adapters.Repositorio;
import com.example.fabiano.ifprof.database.DataBase;
import com.example.fabiano.ifprof.model.Professor;

public class TabFragment1 extends Fragment {
    private ScrollView svProf;
    private LinearLayout llTabProfessor;
    private ImageView ivMeuPerfil;
    private ImageView ivDisciplinas;
    private ImageView ivAvaliacoes;
    private ImageView ivProvas;
    private Professor professorLogged;
    private Repositorio repositorio;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1, container, false);
        llTabProfessor = (LinearLayout)view.findViewById(R.id.llTabProfessor);
        ivMeuPerfil = (ImageView)view.findViewById(R.id.imMeuPerfil);
        ivDisciplinas = (ImageView)view.findViewById(R.id.ivDisciplinas);
        ivAvaliacoes = (ImageView)view.findViewById(R.id.ivAvaliacoes);
        ivProvas = (ImageView)view.findViewById(R.id.ivProvas);

        clickIcon(ivMeuPerfil, Perfil.class);
        clickIcon(ivDisciplinas, ShowDisciplinas.class);
        clickIcon(ivProvas, ShowQuestoes.class);
        clickIcon(ivAvaliacoes, ShowAvaliacoes.class);


            professorLogged = new Professor();
            repositorio = new Repositorio(getActivity());
            professorLogged = repositorio.getLogged();
            boolean show = repositorio.search(DataBase.TABLE_PROFESSOR,"yes",8,"where "+DataBase.ID_PROFESSOR+" = "+professorLogged.getId());
            if (show){
                repositorio.update(DataBase.TABLE_PROFESSOR,DataBase.SHOWAPRESATATION,"'no'",DataBase.ID_PROFESSOR,professorLogged.getId(),"");
                startActivity(new Intent(getActivity(), Apresentation.class));
                repositorio.close();
                getActivity().finish();
            }else{
                repositorio.close();
                if (professorLogged.getOnline().equalsIgnoreCase("off")){
                    startActivity(new Intent(getActivity(),Login.class));
                    getActivity().finish();
                }
            }


        return view;
    }
    public void clickIcon(ImageView iv, final Class a){
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), a);
                startActivity(it);
            }
        });
    }
}
