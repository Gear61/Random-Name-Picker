package com.randomappsinc.studentpicker.listpage;

import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.tabs.TabLayout;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.home.MainActivity;
import com.randomappsinc.studentpicker.shake.ShakeManager;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.squareup.seismic.ShakeDetector;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListActivity extends StandardActivity implements ShakeDetector.Listener {

    private static final int EDIT_NAME_LIST_PAGE_POSITION = 1;
    public static final String START_ON_EDIT_PAGE = "startOnEditPage";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.name_list_pager) ViewPager nameListPager;
    @BindView(R.id.name_list_tabs) TabLayout nameListTabs;
    @BindArray(R.array.list_options) String[] listTabTitles;

    private ShakeDetector shakeDetector;
    private ShakeManager shakeManager = ShakeManager.get();
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String listName = getIntent().getStringExtra(MainActivity.LIST_NAME_KEY);
        setTitle(listName);

        preferencesManager = new PreferencesManager(this);
        ListTabsAdapter listTabsAdapter = new ListTabsAdapter(
                getSupportFragmentManager(),
                listName,
                listTabTitles);
        nameListPager.setAdapter(listTabsAdapter);
        if (getIntent().getBooleanExtra(START_ON_EDIT_PAGE, false)) {
            nameListPager.setCurrentItem(EDIT_NAME_LIST_PAGE_POSITION);
        }
        nameListTabs.setupWithViewPager(nameListPager);

        shakeDetector = new ShakeDetector(this);

        if (preferencesManager.shouldShowShake()) {
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
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.stop();
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void hearShake() {
        if (nameListPager.getCurrentItem() == 0) {
            shakeManager.onShakeDetected();
        }
    }

    @Override
    public void onResume() {
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.start((SensorManager) getSystemService(SENSOR_SERVICE));
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        if (preferencesManager.isShakeEnabled()) {
            shakeDetector.stop();
        }
        super.onDestroy();
    }
}
