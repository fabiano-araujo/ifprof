package com.example.fabiano.ifprof.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.fabiano.ifprof.R;
import com.example.fabiano.ifprof.fragments.FragmentApresentation;
import com.example.fabiano.ifprof.fragments.TabAlunosDaTurma;
import com.example.fabiano.ifprof.fragments.TabFragment1;
import com.example.fabiano.ifprof.fragments.TabFragment2;
import com.example.fabiano.ifprof.model.AllInfo;
import com.example.fabiano.ifprof.model.Aluno;
import com.example.fabiano.ifprof.model.Turma;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fabiano on 02/10/15.
 */
public class AdapterMenuApresentation extends FragmentStatePagerAdapter implements Serializable {
    int mNumOfTabs;

    public AdapterMenuApresentation(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FragmentApresentation tab1 = new FragmentApresentation(R.layout.apresentation);
                return tab1;
            case 1:
                FragmentApresentation tab2 = new FragmentApresentation(R.layout.apresentation2);
                return tab2;
            case 2:
                FragmentApresentation tab3 = new FragmentApresentation(R.layout.apresentation3);
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
