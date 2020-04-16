package com.randomappsinc.studentpicker.listpage;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.database.DataSource;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ListLandingPageActivity extends StandardActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.name_list_pager) ViewPager nameListPager;
    @BindView(R.id.name_list_tabs) TabLayout nameListTabs;
    @BindArray(R.array.list_options) String[] listTabTitles;

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
    }
}
