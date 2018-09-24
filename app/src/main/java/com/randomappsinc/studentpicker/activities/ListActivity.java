package com.randomappsinc.studentpicker.activities;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.adapters.ListTabsAdapter;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.squareup.seismic.ShakeDetector;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends StandardActivity implements ShakeDetector.Listener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.name_list_pager) ViewPager nameListPager;
    @BindView(R.id.name_list_tabs) TabLayout nameListTabs;

    private ListTabsAdapter mListTabsAdapter;
    private ShakeDetector mShakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String listName = getIntent().getStringExtra(MainActivity.LIST_NAME_KEY);
        setTitle(listName);

        mListTabsAdapter = new ListTabsAdapter(getSupportFragmentManager(), listName);
        nameListPager.setAdapter(mListTabsAdapter);
        nameListTabs.setupWithViewPager(nameListPager);

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
        if (PreferencesManager.get().isShakeEnabled()) {
            mShakeDetector.stop();
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    public ListTabsAdapter getListTabsAdapter() {
        return mListTabsAdapter;
    }

    @Override
    public void hearShake() {
        if (nameListPager.getCurrentItem() == 0) {
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
