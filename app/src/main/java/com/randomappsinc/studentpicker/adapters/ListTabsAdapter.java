package com.randomappsinc.studentpicker.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.activities.MainActivity;
import com.randomappsinc.studentpicker.fragments.EditNameListFragment;
import com.randomappsinc.studentpicker.fragments.NameChoosingFragment;
import com.randomappsinc.studentpicker.utils.MyApplication;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class ListTabsAdapter extends FragmentStatePagerAdapter {
    private NameChoosingFragment nameChoosingFragment;
    private Fragment editNameListFragment;
    private String tabTitles[];
    private String listName;

    public ListTabsAdapter(FragmentManager fragmentManager, Bundle bundle, String listName) {
        super(fragmentManager);
        this.tabTitles = MyApplication.getAppContext().getResources().getStringArray(R.array.list_options);
        this.listName = listName;

        if (bundle != null) {
            this.nameChoosingFragment = (NameChoosingFragment) fragmentManager.getFragment(bundle, NameChoosingFragment.SCREEN_NAME);
            this.editNameListFragment = fragmentManager.getFragment(bundle, EditNameListFragment.SCREEN_NAME);
        }
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

    public Fragment getEditNameListFragment() {
        return editNameListFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
