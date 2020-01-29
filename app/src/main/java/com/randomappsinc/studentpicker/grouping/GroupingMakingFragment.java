package com.randomappsinc.studentpicker.grouping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.home.MainActivity;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class GroupingMakingFragment extends Fragment {

    public static GroupingMakingFragment getInstance(String listName) {
        Bundle bundle = new Bundle();
        bundle.putString(MainActivity.LIST_NAME_KEY, listName);
        GroupingMakingFragment groupingMakingFragment = new GroupingMakingFragment();
        groupingMakingFragment.setArguments(bundle);
        return groupingMakingFragment;
    }

    @BindView(R.id.no_groups) TextView noGroups;

    private GroupingSettings settings;
    private GroupingSettingsDialog settingsDialog;
    private String listName;
    private ListInfo listInfo;
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
        listInfo = new DataSource(getContext()).getListInfo(listName);

        noGroups.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        settings = new GroupingSettings(
                getResources().getInteger(R.integer.default_number_of_names_per_group),
                getResources().getInteger(R.integer.default_number_of_groups));
        settingsDialog = new GroupingSettingsDialog(getActivity(), settings);
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
        Toast.makeText(getContext(), String.valueOf(listOfNamesPerGroup), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
