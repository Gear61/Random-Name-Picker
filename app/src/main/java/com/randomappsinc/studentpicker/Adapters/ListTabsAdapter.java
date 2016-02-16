package com.randomappsinc.studentpicker.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.randomappsinc.studentpicker.Activities.MainActivity;
import com.randomappsinc.studentpicker.Fragments.EditNameListFragment;
import com.randomappsinc.studentpicker.Fragments.NameChoosingFragment;
import com.randomappsinc.studentpicker.R;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class ListTabsAdapter extends FragmentPagerAdapter {
    private NameChoosingFragment nameChoosingFragment;
    private String tabTitles[];
    private String listName;

    public ListTabsAdapter(FragmentManager fm, Context context, String listName) {
        super(fm);
        this.tabTitles = context.getResources().getStringArray(R.array.list_options);
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
                nameChoosingFragment = new NameChoosingFragment();
                nameChoosingFragment.setArguments(bundle);
                return nameChoosingFragment;
            case 1:
                EditNameListFragment editNameListFragment = new EditNameListFragment();
                editNameListFragment.setArguments(bundle);
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
