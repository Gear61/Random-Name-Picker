package com.randomappsinc.studentpicker.home;

import androidx.annotation.IdRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.settings.SettingsFragment;

class HomepageFragmentController {

    private FragmentManager fragmentManager;
    private int containerId;
    private HomepageFragment homepageFragment;
    private SettingsFragment settingsFragment;
    private @IdRes int currentViewId;

    HomepageFragmentController(FragmentManager fragmentManager, int containerId) {
        this.fragmentManager = fragmentManager;
        this.containerId = containerId;
    }

    void onNavItemSelected(@IdRes int viewId) {
        if (currentViewId == viewId) {
            return;
        }

        switch (currentViewId) {
            case R.id.home:
                hideFragment(homepageFragment);
                break;
            case R.id.settings:
                hideFragment(settingsFragment);
                break;
        }

        currentViewId = viewId;
        switch (viewId) {
            case R.id.home:
                showFragment(homepageFragment);
                break;
            case R.id.settings:
                if (settingsFragment == null) {
                    settingsFragment = SettingsFragment.getInstance();
                    addFragment(settingsFragment);
                } else {
                    showFragment(settingsFragment);
                }
                break;
        }
    }

    /** Called by the app upon start up to load the homepage */
    void loadHomeInitially() {
        this.homepageFragment = HomepageFragment.getInstance();
        currentViewId = R.id.home;
        addFragment(homepageFragment);
    }

    int getCurrentViewId() {
        return currentViewId;
    }

    private void addFragment(Fragment fragment) {
        fragmentManager.beginTransaction().add(containerId, fragment).commit();
    }

    private void showFragment(Fragment fragment) {
        fragmentManager.beginTransaction().show(fragment).commit();
    }

    private void hideFragment(Fragment fragment) {
        fragmentManager.beginTransaction().hide(fragment).commit();
    }

    void restoreFragments() {
        for (Fragment fragment : fragmentManager.getFragments()) {
            String fragmentName = fragment.getClass().getSimpleName();
            if (fragmentName.equals(HomepageFragment.class.getSimpleName())) {
                homepageFragment = (HomepageFragment) fragment;
            } else if ((fragmentName.equals(SettingsFragment.class.getSimpleName()))) {
                settingsFragment = (SettingsFragment) fragment;
            }
        }
    }
}
