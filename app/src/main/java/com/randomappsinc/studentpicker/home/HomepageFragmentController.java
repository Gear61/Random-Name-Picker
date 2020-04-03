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
        this.homepageFragment = HomepageFragment.getInstance();
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
        currentViewId = R.id.home;
        addFragment(homepageFragment);
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
}
