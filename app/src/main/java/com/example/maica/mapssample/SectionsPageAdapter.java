package com.example.maica.mapssample;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Maica on 8/13/2017.
 */

public class SectionsPageAdapter extends FragmentStatePagerAdapter {

    int mNumofTabs;


    public SectionsPageAdapter(FragmentManager fm, int NumofTabs) {
        super(fm);
        this.mNumofTabs = NumofTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Home_Dashboard tab = new Home_Dashboard();
                return tab;
            case 1:
                ProfileActivity tab1 = new ProfileActivity();
                return tab1;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumofTabs;
    }

}
