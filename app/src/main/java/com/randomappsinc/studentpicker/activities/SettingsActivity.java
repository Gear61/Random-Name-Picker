package com.randomappsinc.studentpicker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.adapters.SettingsAdapter;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends StandardActivity implements SettingsAdapter.ItemSelectionListener {

    public static final String SUPPORT_EMAIL = "RandomAppsInc61@gmail.com";
    public static final String OTHER_APPS_URL = "https://play.google.com/store/apps/dev?id=9093438553713389916";
    public static final String REPO_URL = "https://github.com/Gear61/Random-Name-Picker";

    @BindView(R.id.settings_options) RecyclerView settingsOptions;
    @BindString(R.string.feedback_subject) String feedbackSubject;
    @BindString(R.string.send_email) String sendEmail;

    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferencesManager = new PreferencesManager(this);
        settingsOptions.addItemDecoration(new SimpleDividerItemDecoration(this));
        settingsOptions.setAdapter(new SettingsAdapter(this, this));
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = null;
        switch (position) {
            case 0:
                View firstCell = settingsOptions.getChildAt(0);
                Switch shakeToggle = firstCell.findViewById(R.id.shake_toggle);
                boolean currentState = shakeToggle.isChecked();
                shakeToggle.setChecked(!currentState);
                preferencesManager.setShakeEnabled(!currentState);
                return;
            case 1:
                String uriText = "mailto:" + SUPPORT_EMAIL + "?subject=" + Uri.encode(feedbackSubject);
                Uri mailUri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, mailUri);
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(Intent.createChooser(sendIntent, sendEmail));
                return;
            case 2:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(OTHER_APPS_URL));
                break;
            case 3:
                Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                    UIUtils.showLongToast(R.string.play_store_error, this);
                    return;
                }
                break;
            case 4:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL));
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
