package com.randomappsinc.studentpicker.adapters;

import com.randomappsinc.studentpicker.fragments.EditNameListFragment;
import com.randomappsinc.studentpicker.fragments.NameChoosingFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

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

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return NameChoosingFragment.getInstance(listName);
            case 1:
                return EditNameListFragment.getInstance(listName);
            default:
                throw new IllegalArgumentException("There should only be 2 tabs!");
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
