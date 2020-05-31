package com.randomappsinc.studentpicker.grouping;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.NameUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupMakingActivity extends AppCompatActivity {

    @BindView(R.id.no_groups) TextView noGroups;
    @BindView(R.id.groups_list) RecyclerView groupsList;

    private GroupMakingSettings settings;
    private GroupMakingSettingsDialog settingsDialog;
    private ListInfo listInfo;
    private GroupMakingAdapter groupsMakingListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_maker);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                        .colorRes(R.color.white)
                        .actionBarSize());

        int listId = getIntent().getIntExtra(Constants.LIST_ID_KEY, 0);
        DataSource dataSource = new DataSource(this);
        setTitle(dataSource.getListName(listId));
        listInfo = dataSource.getListInfo(listId);

        groupsMakingListAdapter = new GroupMakingAdapter();
        groupsList.setAdapter(groupsMakingListAdapter);

        settings = new GroupMakingSettings(listInfo.getNumInstances());
        settingsDialog = new GroupMakingSettingsDialog(this, settings);
    }

    @OnClick(R.id.make_groups)
    void makeGroups() {
        if (listInfo.getNumNames() == 0) {
            UIUtils.showLongToast(R.string.group_no_names_error_message, this);
            return;
        }

        List<List<String>> listOfNamesPerGroup = NameUtils.createGroups(
                listInfo, settings.getNumOfNamesPerGroup(), settings.getNumOfGroups());
        groupsMakingListAdapter.setData(listOfNamesPerGroup);
        noGroups.setVisibility(View.GONE);
        groupsList.setVisibility(View.VISIBLE);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_from_top);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.groups_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.settings:
                settingsDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
