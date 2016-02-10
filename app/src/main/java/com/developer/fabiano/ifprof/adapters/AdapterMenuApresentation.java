package com.developer.fabiano.ifprof.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.developer.fabiano.ifprof.R;
import com.developer.fabiano.ifprof.fragments.FragmentApresentation;

import java.io.Serializable;

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
