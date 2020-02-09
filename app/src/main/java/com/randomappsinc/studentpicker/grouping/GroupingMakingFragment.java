package com.randomappsinc.studentpicker.grouping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.database.NameListDataManager;
import com.randomappsinc.studentpicker.home.MainActivity;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GroupingMakingFragment extends Fragment implements NameListDataManager.Listener {

    public static GroupingMakingFragment getInstance(String listName) {
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.LIST_NAME_KEY, listName);
        GroupingMakingFragment groupingMakingFragment = new GroupingMakingFragment();
        groupingMakingFragment.setArguments(bundle);
        return groupingMakingFragment;
    }

    @BindView(R.id.no_groups) TextView noGroups;
    @BindView(R.id.groups_list) RecyclerView groupsList;

    private GroupingSettings settings;
    private GroupingSettingsDialog settingsDialog;
    private String listName;
    private NameListDataManager nameListDataManager = NameListDataManager.get();
    private ListInfo listInfo;
    private DataSource dataSource;
    private GroupsMakingAdapter groupsMakingListAdapter;
    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_grouping, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        listName = getArguments().getString(MainActivity.LIST_NAME_KEY, "");
        dataSource = new DataSource(getContext());
        listInfo = dataSource.getListInfo(listName);
        nameListDataManager.registerListener(this);

        groupsMakingListAdapter = new GroupsMakingAdapter();
        groupsList.setAdapter(groupsMakingListAdapter);
        setNoGroup();

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settings = new GroupingSettings(
                listInfo.getNumInstances(),
                getResources().getInteger(R.integer.default_number_of_names_per_group),
                getResources().getInteger(R.integer.default_number_of_groups));
        settingsDialog = new GroupingSettingsDialog(getActivity(), settings);
    }

    @Override
    public void onNameAdded(String name, int amount, String listName) {
        listInfo.addNames(name, amount);
        settings.setNameListSize(listInfo.getNumInstances());
        settingsDialog.refreshSetting();
    }

    @Override
    public void onNameDeleted(String name, int amount, String listName) {
        listInfo.removeNames(name, amount);
        settings.setNameListSize(listInfo.getNumInstances());
        settingsDialog.refreshSetting();
    }

    @Override
    public void onNameChanged(String oldName, String newName, int amount, String listName) {
        listInfo.renamePeople(oldName, newName, amount);
    }

    @Override
    public void onNameListsImported(Map<String, Integer> nameAmounts, String listName) {
        listInfo = dataSource.getListInfo(listName);
        settings.setNameListSize(listInfo.getNumInstances());
        settingsDialog.refreshSetting();
    }

    @OnClick(R.id.make_groups)
    void makeGroups() {
        if (listInfo.getNumNames() == 0) {
            return;
        }

        List<List<Integer>> listOfGroups = NameUtils.getRandomGroup(settings.getNumOfNamesPerGroup(),
                settings.getNumOfGroups(),
                listInfo.getNumInstances() - 1);
        List<List<String>> listOfNamesPerGroup = listInfo.groupNamesList(listOfGroups);
        groupsMakingListAdapter.setData(listOfNamesPerGroup);
        setNoGroup();
    }

    private void setNoGroup() {
        if (groupsMakingListAdapter.getItemCount() == 0) {
            groupsList.setVisibility(View.GONE);
            noGroups.setVisibility(View.VISIBLE);
        } else {
            noGroups.setVisibility(View.GONE);
            groupsList.setVisibility(View.VISIBLE);
        }
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
