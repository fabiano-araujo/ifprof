package com.example.fabiano.ifprof.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.fabiano.ifprof.fragments.TabAlunosDaTurma;
import com.example.fabiano.ifprof.fragments.TabFragment1;
import com.example.fabiano.ifprof.fragments.TabFragment2;
import com.example.fabiano.ifprof.model.AllInfo;
import com.example.fabiano.ifprof.model.Aluno;
import com.example.fabiano.ifprof.model.Turma;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiano on 02/10/15.
 */
public class AdapterMenu extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private int tipo;
    private Repositorio repositorio;
    private Context context;
    private List<Turma> turmaList;
    AllInfo allInfo;
    public AdapterMenu(FragmentManager fm, int NumOfTabs, int tipo, Context context,List<Turma> turmaList) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.tipo = tipo;
        if (tipo != 1){
            this.context = context;
            this.turmaList = turmaList;
            this.mNumOfTabs = turmaList.size();
        }
    }

    @Override
    public Fragment getItem(int position) {
        if(tipo == 1){
            switch (position) {
                case 0:
                    TabFragment1 tab1 = new TabFragment1();
                    return tab1;
                case 1:
                    TabFragment2 tab2 = new TabFragment2();
                    return tab2;
                default:
                    return null;
            }
        }else{
            try{
                List<Aluno> alunoList = null;
                List<AllInfo> mList = new ArrayList<>();
                repositorio = new Repositorio(context);
                Turma turma = turmaList.get(position);
                alunoList = repositorio.getAlunos(turma.getIdTurma());
                repositorio.close();
                for (int j = 0;j < alunoList.size();j++){
                    allInfo = new AllInfo();
                    allInfo.setAluno(alunoList.get(j));
                    allInfo.setTurma(turma);
                    mList.add(allInfo);
                }
                if (alunoList.size() == 0 || mList.size() == 0){
                    allInfo = new AllInfo();
                    allInfo.setTurma(turma);
                    mList.add(allInfo);
                    return new TabAlunosDaTurma(mList,position,true);
                }else{
                    return new TabAlunosDaTurma(mList,position,false);
                }
            }catch (Exception e){
                AlertsAndControl.alert(context, e.getMessage(),"Erro");
            }
            return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
