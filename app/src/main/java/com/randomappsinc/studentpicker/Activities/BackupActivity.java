package com.randomappsinc.studentpicker.Activities;

import android.os.Bundle;

import com.randomappsinc.studentpicker.R;

import butterknife.ButterKnife;

/**
 * Created by alexanderchiou on 5/1/16.
 */
public class BackupActivity extends StandardActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_data);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
