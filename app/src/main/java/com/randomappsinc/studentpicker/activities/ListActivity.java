package com.randomappsinc.studentpicker.activities;

import android.app.Fragment;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.adapters.ListTabsAdapter;
import com.randomappsinc.studentpicker.fragments.EditNameListFragment;
import com.randomappsinc.studentpicker.fragments.NameChoosingFragment;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.views.SlidingTabLayout;
import com.squareup.seismic.ShakeDetector;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ListActivity extends StandardActivity implements ShakeDetector.Listener {

    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.sliding_tabs) SlidingTabLayout mSlidingTabLayout;

    private ListTabsAdapter mListTabsAdapter;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        String listName = getIntent().getStringExtra(MainActivity.LIST_NAME_KEY);
        setTitle(listName);

        mListTabsAdapter = new ListTabsAdapter(getFragmentManager(), savedInstanceState, listName);
        mViewPager.setAdapter(mListTabsAdapter);
        mSlidingTabLayout.setCustomTabView(R.layout.custom_tab, R.id.tab_name);
        mSlidingTabLayout.setViewPager(mViewPager);

        mShakeDetector = new ShakeDetector(this);

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
        NameChoosingFragment nameChoosingFragment = mListTabsAdapter.getNameChoosingFragment();
        if (nameChoosingFragment != null) {
            nameChoosingFragment.cacheListState();
            getFragmentManager().putFragment(savedInstanceState, NameChoosingFragment.SCREEN_NAME, nameChoosingFragment);
        }
        Fragment editNameListFragment = mListTabsAdapter.getEditNameListFragment();
        if (editNameListFragment != null) {
            getFragmentManager().putFragment(savedInstanceState, EditNameListFragment.SCREEN_NAME, editNameListFragment);
        }
        if (PreferencesManager.get().isShakeEnabled()) {
            mShakeDetector.stop();
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    public ListTabsAdapter getListTabsAdapter() {
        return mListTabsAdapter;
    }

    @Override
    public void hearShake() {
        if (mViewPager.getCurrentItem() == 0) {
            mListTabsAdapter.getNameChoosingFragment().choose();
        }
    }

    @Override
    public void onResume() {
        if (PreferencesManager.get().isShakeEnabled()) {
            mShakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (PreferencesManager.get().isShakeEnabled()) {
            mShakeDetector.stop();
        }
        super.onDestroy();
    }
}
