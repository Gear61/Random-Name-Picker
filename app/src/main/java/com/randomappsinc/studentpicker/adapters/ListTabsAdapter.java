package com.randomappsinc.studentpicker.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.randomappsinc.studentpicker.activities.MainActivity;
import com.randomappsinc.studentpicker.fragments.EditNameListFragment;
import com.randomappsinc.studentpicker.fragments.NameChoosingFragment;

public class ListTabsAdapter extends FragmentStatePagerAdapter {

    private NameChoosingFragment nameChoosingFragment;
    private Fragment editNameListFragment;
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
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.LIST_NAME_KEY, listName);
        switch (position) {
            case 0:
                if (nameChoosingFragment == null) {
                    nameChoosingFragment = new NameChoosingFragment();
                    nameChoosingFragment.setArguments(bundle);
                }
                return nameChoosingFragment;
            case 1:
                if (editNameListFragment == null) {
                    editNameListFragment = new EditNameListFragment();
                    editNameListFragment.setArguments(bundle);
                }
                return editNameListFragment;
            default:
                return null;
        }
    }

    public NameChoosingFragment getNameChoosingFragment() {
        return nameChoosingFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
