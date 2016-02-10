package com.developer.fabiano.ifprof.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.developer.fabiano.ifprof.ActivityAddAlunos;
import com.developer.fabiano.ifprof.R;
import com.developer.fabiano.ifprof.adapters.AdapterInfo;
import com.developer.fabiano.ifprof.model.AllInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiano on 21/12/15.
 */
public class TabAlunosDaTurma extends Fragment {
    private List<AllInfo> mList = new ArrayList<>();
    private boolean clear;
    private int position;
    private boolean data = true;
    @SuppressLint("ValidFragment")
    public TabAlunosDaTurma(List<AllInfo> l,int position,boolean clear){
        mList =  l;
        this.clear = clear;
        this.position = position;
    }
    public TabAlunosDaTurma(){}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_aluno, container, false);
        ScrollView svShow = (ScrollView)view.findViewById(R.id.svShow);
        RecyclerView rvShow = (RecyclerView)view.findViewById(R.id.rvShow);
        FloatingActionButton FABAdd = (FloatingActionButton)view.findViewById(R.id.FABAdd);

        rvShow.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvShow.setLayoutManager(llm);
        if(savedInstanceState != null){
            mList = savedInstanceState.getParcelableArrayList("alunos");
        }
        if (mList.size() < 1 || clear){
            data = false;
            rvShow.setVisibility(View.GONE);
            FABAdd.setVisibility(View.GONE);
        }else{
            svShow.setVisibility(View.GONE);
            FABAdd.setVisibility(View.VISIBLE);
            try{
                AdapterInfo adapterInfo = new AdapterInfo(getActivity(),mList,AdapterInfo.SHOWALUNOS,svShow,rvShow,FABAdd);
                rvShow.setAdapter(adapterInfo);
            }catch (Exception e){}
        }
        Button btnCriar = (Button)view.findViewById(R.id.btnCriar);
        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(), ActivityAddAlunos.class);
                it.putExtra("position",mList.get(0).getTurma());
                startActivity(it);
                ((Activity)getContext()).finish();
            }
        });
        FABAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getContext(), ActivityAddAlunos.class);
                it.putExtra("position",mList.get(0).getTurma());
                startActivity(it);
                ((Activity)getContext()).finish();
            }
        });
        view.setTag(position);
        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (!data){
            outState.putParcelableArrayList("alunos", new ArrayList<AllInfo>());
        }else{
            outState.putParcelableArrayList("alunos", (ArrayList<AllInfo>) mList);
        }
        super.onSaveInstanceState(outState);
    }
}