package com.randomappsinc.studentpicker.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.randomappsinc.studentpicker.fragments.EditNameListFragment;
import com.randomappsinc.studentpicker.fragments.NameChoosingFragment;

public class ListTabsAdapter extends FragmentStatePagerAdapter {

    private String tabTitles[];
    private String listName;

    public ListTabsAdapter(FragmentManager fragmentManager, String listName, String[] tabTitles) {
        super(fragmentManager);
        this.tabTitles = tabTitles;
        this.listName = listName;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return NameChoosingFragment.getInstance(listName);
            case 1:
                return EditNameListFragment.getInstance(listName);
            default:
                return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
