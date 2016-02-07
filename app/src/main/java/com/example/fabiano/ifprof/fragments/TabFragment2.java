package com.example.fabiano.ifprof.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.fabiano.ifprof.R;
import com.example.fabiano.ifprof.ShowAlunos;
import com.example.fabiano.ifprof.ShowFaltas;
import com.example.fabiano.ifprof.ShowNotas;
import com.example.fabiano.ifprof.ShowTurmas;
import com.example.fabiano.ifprof.adapters.Repositorio;
import com.example.fabiano.ifprof.model.Professor;

public class TabFragment2 extends Fragment {
    private ImageView ivAlunos;
    private ImageView ivAddNotas;
    private ImageView ivAddFalta;
    private Professor professorLogged;
    private Repositorio repositorio;
    private ImageView ivTurmas;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2, container, false);
        ivAlunos = (ImageView)view.findViewById(R.id.ivAlunos);
        ivAddNotas = (ImageView)view.findViewById(R.id.ivAddNotas);
        ivAddFalta = (ImageView)view.findViewById(R.id.ivAddFaltas);
        ivTurmas = (ImageView)view.findViewById(R.id.ivTurmas);

        clickIcon(ivAddNotas, ShowNotas.class);
        clickIcon(ivAlunos,  ShowAlunos.class);
        clickIcon(ivAddFalta, ShowFaltas.class);
        clickIcon(ivTurmas, ShowTurmas.class);

        try{
            professorLogged = new Professor();
            repositorio = new Repositorio(getActivity());
            professorLogged = repositorio.getLogged();
            repositorio.close();
        }catch(Exception e){
            AlertDialog.Builder x = new AlertDialog.Builder(getActivity());
            x.setMessage(e.getMessage()).setNeutralButton("ok",null).show();
        }

        return view;
    }
    public void clickIcon(ImageView iv, final Class a){
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), a);
                it.putExtra("professor",professorLogged);
                startActivity(it);
            }
        });
    }
}
