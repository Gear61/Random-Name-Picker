package com.randomappsinc.studentpicker.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.randomappsinc.studentpicker.Adapters.OtherAppsAdapter;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by Alex Chiou on 12/8/15.
 */
public class OtherAppsActivity extends StandardActivity {
    public static final String[] OTHER_APP_PACKAGE_IDS =
           {"com.randomappsinc.mathrace",
            "com.randomappsinc.simpleflashcards",
            "com.randomappsinc.berniesanderssoundboard",
            "randomappsinc.com.sqlpracticeplus"};

    @Bind(R.id.other_apps) ListView otherApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_apps);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        otherApps.setAdapter(new OtherAppsAdapter(this));
    }

    @OnItemClick(R.id.other_apps)
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id=" + OTHER_APP_PACKAGE_IDS[position]));
        startActivity(intent);
    }
}
