package com.randomappsinc.studentpicker.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.randomappsinc.studentpicker.Adapters.ListTabsAdapter;
import com.randomappsinc.studentpicker.Layouts.SlidingTabLayout;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class ListActivity extends AppCompatActivity
{
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.sliding_tabs) SlidingTabLayout mSlidingTabLayout;
    @BindString(R.string.list) String list;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String listName = intent.getStringExtra(NameListsActivity.LIST_NAME_KEY);
        setTitle(list + " " + listName);

        ListTabsAdapter profileTabsAdapter = new ListTabsAdapter(getSupportFragmentManager(), this, listName);
        mViewPager.setAdapter(profileTabsAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
