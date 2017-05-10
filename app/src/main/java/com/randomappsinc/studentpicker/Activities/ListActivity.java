package com.randomappsinc.studentpicker.Activities;

import android.app.Fragment;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.Adapters.ListTabsAdapter;
import com.randomappsinc.studentpicker.Fragments.EditNameListFragment;
import com.randomappsinc.studentpicker.Fragments.NameChoosingFragment;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.Utils.PreferencesManager;
import com.randomappsinc.studentpicker.Views.SlidingTabLayout;
import com.squareup.seismic.ShakeDetector;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class ListActivity extends StandardActivity implements ShakeDetector.Listener {
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.sliding_tabs) SlidingTabLayout mSlidingTabLayout;

    private ListTabsAdapter listTabsAdapter;
    private ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        String listName = getIntent().getStringExtra(MainActivity.LIST_NAME_KEY);
        setTitle(listName);

        listTabsAdapter = new ListTabsAdapter(getFragmentManager(), savedInstanceState, listName);
        mViewPager.setAdapter(listTabsAdapter);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, R.id.tab_name);
        mSlidingTabLayout.setViewPager(mViewPager);

        shakeDetector = new ShakeDetector(this);

        if (PreferencesManager.get().shouldShowShake()) {
            new MaterialDialog.Builder(this)
                    .title(R.string.shake_it)
                    .content(R.string.shake_now_supported)
                    .positiveText(android.R.string.yes)
                    .cancelable(false)
                    .show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        NameChoosingFragment nameChoosingFragment = listTabsAdapter.getNameChoosingFragment();
        if (nameChoosingFragment != null) {
            nameChoosingFragment.cacheListState();
            getFragmentManager().putFragment(savedInstanceState, NameChoosingFragment.SCREEN_NAME, nameChoosingFragment);
        }
        Fragment editNameListFragment = listTabsAdapter.getEditNameListFragment();
        if (editNameListFragment != null) {
            getFragmentManager().putFragment(savedInstanceState, EditNameListFragment.SCREEN_NAME, editNameListFragment);
        }
        shakeDetector.stop();
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    public ListTabsAdapter getListTabsAdapter() {
        return listTabsAdapter;
    }

    @Override
    public void hearShake() {
        listTabsAdapter.getNameChoosingFragment().choose();
    }

    @Override
    public void onResume() {
        shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        super.onResume();
    }

    @Override
    public void onDestroy() {
        shakeDetector.stop();
        super.onDestroy();
    }
}
