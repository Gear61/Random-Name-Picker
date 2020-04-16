package com.randomappsinc.studentpicker.listpage;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.home.DeleteListDialog;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.utils.UIUtils;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListLandingPageActivity extends StandardActivity implements DeleteListDialog.Listener {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.name_list_pager) ViewPager nameListPager;
    @BindView(R.id.name_list_tabs) TabLayout nameListTabs;
    @BindArray(R.array.list_options) String[] listTabTitles;

    private DeleteListDialog deleteListDialog;
    private DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_landing_page);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int listId = getIntent().getIntExtra(Constants.LIST_ID_KEY, 0);
        DataSource dataSource = new DataSource(this);
        setTitle(dataSource.getListName(listId));

        ListTabsAdapter listTabsAdapter = new ListTabsAdapter(
                getSupportFragmentManager(),
                listId,
                listTabTitles);
        nameListPager.setAdapter(listTabsAdapter);
        nameListTabs.setupWithViewPager(nameListPager);

        deleteListDialog = new DeleteListDialog(this, this);
        dataSource = new DataSource(this);
    }

    @Override
    public void onDeleteListConfirmed(int position, ListDO listDO) {
        dataSource.deleteList(listDO.getId());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_page_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.delete_list, IoniconsIcons.ion_android_delete, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_list) {
        }
        return super.onOptionsItemSelected(item);
    }
}
