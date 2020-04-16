package com.randomappsinc.studentpicker.grouping;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.BannerAdManager;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupMakingActivity extends StandardActivity {

    @BindView(R.id.no_groups) TextView noGroups;
    @BindView(R.id.groups_list) RecyclerView groupsList;
    @BindView(R.id.bottom_ad_banner_container) FrameLayout bannerAdContainer;

    private GroupMakingSettings settings;
    private GroupMakingSettingsDialog settingsDialog;
    private ListInfo listInfo;
    private GroupMakingAdapter groupsMakingListAdapter;
    private BannerAdManager bannerAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_maker);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int listId = getIntent().getIntExtra(Constants.LIST_ID_KEY, 0);
        DataSource dataSource = new DataSource(this);
        setTitle(dataSource.getListName(listId));
        listInfo = dataSource.getListInfo(listId);

        groupsMakingListAdapter = new GroupMakingAdapter();
        groupsList.setAdapter(groupsMakingListAdapter);

        settings = new GroupMakingSettings(listInfo.getNumInstances());
        settingsDialog = new GroupMakingSettingsDialog(this, settings);
        bannerAdManager = new BannerAdManager(bannerAdContainer);
        bannerAdManager.loadOrRemoveAd();
    }

    @OnClick(R.id.make_groups)
    void makeGroups() {
        if (listInfo.getNumNames() == 0) {
            UIUtils.showLongToast(R.string.group_no_names_error_message, this);
            return;
        }

        List<List<Integer>> listOfGroups = NameUtils.getRandomGroups(settings.getNumOfNamesPerGroup(),
                settings.getNumOfGroups(),
                listInfo.getNumInstances() - 1);
        List<List<String>> listOfNamesPerGroup = listInfo.groupNamesList(listOfGroups);
        groupsMakingListAdapter.setData(listOfNamesPerGroup);
        noGroups.setVisibility(View.GONE);
        groupsList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bannerAdManager.onOrientationChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groups_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
                settingsDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
