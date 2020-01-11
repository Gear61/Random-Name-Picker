package com.randomappsinc.studentpicker.listpage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.randomappsinc.studentpicker.choosing.NameChoosingFragment;
import com.randomappsinc.studentpicker.editing.EditNameListFragment;
import com.randomappsinc.studentpicker.grouping.GroupingFragment;

public class ListTabsAdapter extends FragmentStatePagerAdapter {

    private final String[] tabTitles;
    private final String listName;

    ListTabsAdapter(FragmentManager fragmentManager, String listName, String[] tabTitles) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
                return GroupingFragment.getInstance();
            case 2:
                return EditNameListFragment.getInstance(listName);
            default:
                throw new IllegalArgumentException("There should only be 3 tabs!");
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
