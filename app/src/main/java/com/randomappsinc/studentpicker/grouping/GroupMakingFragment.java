package com.randomappsinc.studentpicker.grouping;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.BannerAdManager;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.database.NameListDataManager;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GroupMakingFragment extends Fragment implements NameListDataManager.Listener {

    public static GroupMakingFragment getInstance(int listId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.LIST_ID_KEY, listId);
        GroupMakingFragment groupMakingFragment = new GroupMakingFragment();
        groupMakingFragment.setArguments(bundle);
        return groupMakingFragment;
    }

    @BindView(R.id.no_groups) TextView noGroups;
    @BindView(R.id.groups_list) RecyclerView groupsList;
    @BindView(R.id.bottom_ad_banner_container) FrameLayout bannerAdContainer;

    private GroupMakingSettings settings;
    private GroupMakingSettingsDialog settingsDialog;
    private NameListDataManager nameListDataManager = NameListDataManager.get();
    private ListInfo listInfo;
    private DataSource dataSource;
    private GroupMakingAdapter groupsMakingListAdapter;
    private BannerAdManager bannerAdManager;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_maker, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        int listId = getArguments().getInt(Constants.LIST_ID_KEY, 0);
        dataSource = new DataSource(getContext());
        listInfo = dataSource.getListInfo(listId);
        nameListDataManager.registerListener(this);

        groupsMakingListAdapter = new GroupMakingAdapter();
        groupsList.setAdapter(groupsMakingListAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settings = new GroupMakingSettings(listInfo.getNumInstances());
        settingsDialog = new GroupMakingSettingsDialog(getActivity(), settings);
        bannerAdManager = new BannerAdManager(bannerAdContainer);
    }

    @Override
    public void onNameAdded(String name, int amount, int listId) {
        listInfo = dataSource.getListInfo(listId);
        settings.setNameListSize(listInfo.getNumInstances());
        settingsDialog.refreshSetting();
    }

    @Override
    public void onNameDeleted(String name, int amount, int listId) {
        listInfo = dataSource.getListInfo(listId);
        settings.setNameListSize(listInfo.getNumInstances());
        settingsDialog.refreshSetting();
    }

    @Override
    public void onNameChanged(String oldName, String newName, int amount, int listId) {
        listInfo = dataSource.getListInfo(listId);
        settings.setNameListSize(listInfo.getNumInstances());
        settingsDialog.refreshSetting();
    }

    @OnClick(R.id.make_groups)
    void makeGroups() {
        if (listInfo.getNumNames() == 0) {
            UIUtils.showLongToast(R.string.group_no_names_error_message, getContext());
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
    public void onResume() {
        super.onResume();
        bannerAdManager.maybeLoadAd();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bannerAdManager.onOrientationChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        nameListDataManager.unregisterListener(this);
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.groups_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, getActivity());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                settingsDialog.show();
                return true;
            case android.R.id.home:
                getActivity().finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
