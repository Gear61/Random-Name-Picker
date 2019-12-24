package com.randomappsinc.studentpicker.listpage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.randomappsinc.studentpicker.choosing.NameChoosingFragment;
import com.randomappsinc.studentpicker.editing.EditNameListFragment;
import com.randomappsinc.studentpicker.grouping.GroupsFragment;

import java.util.ArrayList;
import java.util.List;

public class ListTabsAdapter extends FragmentStatePagerAdapter {

    private String[] tabTitles;
    private String listName;
    private List<Fragment> fragmentList = new ArrayList<>();

    ListTabsAdapter(FragmentManager fragmentManager, String listName, String[] tabTitles) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabTitles = tabTitles;
        this.listName = listName;
        adapterSetup();
    }

    private void adapterSetup() {
        fragmentList.add(NameChoosingFragment.getInstance(listName));
        fragmentList.add(GroupsFragment.getInstance());
        fragmentList.add(EditNameListFragment.getInstance(listName));
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
