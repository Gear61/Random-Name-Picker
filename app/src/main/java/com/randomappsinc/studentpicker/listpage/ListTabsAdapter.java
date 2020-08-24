package com.randomappsinc.studentpicker.listpage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class ListTabsAdapter extends FragmentStatePagerAdapter {

    private final String[] tabTitles;
    private final int listId;

    ListTabsAdapter(FragmentManager fragmentManager, int listId, String[] tabTitles) {
        super(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabTitles = tabTitles;
        this.listId = listId;
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
                return ChoosingOptionsFragment.getInstance(listId);
            case 1:
                return EditListOptionsFragment.getInstance(listId);
            case 2:
                return PremiumOptionsFragment.getInstance(listId);
            default:
                throw new IllegalArgumentException("There should only be 3 tabs!");
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
