package com.randomappsinc.studentpicker.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.adapters.SettingsAdapter;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.rey.material.widget.Switch;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

public class SettingsActivity extends StandardActivity {

    public static final String SUPPORT_EMAIL = "chessnone@gmail.com";
    public static final String OTHER_APPS_URL = "https://play.google.com/store/apps/dev?id=9093438553713389916";
    public static final String REPO_URL = "https://github.com/Gear61/Random-Name-Picker";

    @BindView(R.id.parent) View parent;
    @BindView(R.id.settings_options) ListView settingsOptions;
    @BindString(R.string.play_store_error) String playStoreError;
    @BindString(R.string.feedback_subject) String feedbackSubject;
    @BindString(R.string.send_email) String sendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsOptions.setAdapter(new SettingsAdapter(this));
    }

    @OnItemClick(R.id.settings_options)
    public void onItemClick(int position) {
        Intent intent = null;
        switch (position) {
            case 0:
                View firstCell = settingsOptions.getChildAt(0);
                Switch shakeToggle = (Switch) firstCell.findViewById(R.id.shake_toggle);
                boolean currentState = shakeToggle.isChecked();
                shakeToggle.setChecked(!currentState);
                PreferencesManager.get().setShakeEnabled(!currentState);
                return;
            case 1:
                intent = new Intent(this, BackupActivity.class);
                break;
            case 2:
                String uriText = "mailto:" + SUPPORT_EMAIL + "?subject=" + Uri.encode(feedbackSubject);
                Uri mailUri = Uri.parse(uriText);
                Intent sendIntent = new Intent(Intent.ACTION_SENDTO, mailUri);
                sendIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(Intent.createChooser(sendIntent, sendEmail));
                return;
            case 3:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(OTHER_APPS_URL));
                break;
            case 4:
                Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                intent = new Intent(Intent.ACTION_VIEW, uri);
                if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                    UIUtils.showSnackbar(parent, playStoreError);
                    return;
                }
                break;
            case 5:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL));
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }
}
