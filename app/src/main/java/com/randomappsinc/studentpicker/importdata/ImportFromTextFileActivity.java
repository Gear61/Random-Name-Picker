package com.randomappsinc.studentpicker.importdata;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.BannerAdManager;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportFromTextFileActivity extends StandardActivity {

    @BindView(R.id.list_name) EditText listName;
    @BindView(R.id.names) EditText names;
    @BindView(R.id.bottom_ad_banner_container) FrameLayout bannerAdContainer;

    private BannerAdManager bannerAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_name_list);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bannerAdManager = new BannerAdManager(bannerAdContainer);
        bannerAdManager.loadOrRemoveAd();

        extractNameListInfo();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bannerAdManager.onOrientationChanged();
    }

    private void extractNameListInfo() {
        Handler handler = new Handler();
        handler.post(() -> {
            Uri fileUri = Uri.parse(getIntent().getStringExtra(Constants.FILE_URI_KEY));
            Cursor cursor = getContentResolver().query(
                    fileUri,
                    null,
                    null,
                    null,
                    null,
                    null);

            String listNameText = "";
            if (cursor != null && cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                listNameText = displayName.replace(".txt", "");
                cursor.close();
            }

            StringBuilder namesText = new StringBuilder();
            InputStream inputStream = null;
            BufferedReader reader = null;
            try {
                inputStream = getContentResolver().openInputStream(fileUri);
                if (inputStream == null) {
                    throw new IOException("Unable to find .txt file!");
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (namesText.length() > 0) {
                        namesText.append("\n");
                    }
                    namesText.append(line);
                }
                loadUI(listNameText, namesText.toString());
            } catch (IOException exception) {
                runOnUiThread(() -> UIUtils.showLongToast(
                        R.string.load_file_fail, getApplicationContext()));
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ignored) {}
            }
        });
    }

    private void loadUI(String listNameText, String namesListText) {
        runOnUiThread(() -> {
            listName.setText(listNameText);
            listName.setHint(R.string.add_list_hint);
            names.setText(namesListText);
            names.setHint(R.string.names);
        });
    }

    @OnClick(R.id.save)
    public void importNameList() {
        String newListName = listName.getText().toString().trim();
        if (newListName.isEmpty()) {
            UIUtils.showLongToast(R.string.blank_list_name, this);
        } else {
            DataSource dataSource = new DataSource(this);
            ListDO newList = dataSource.addNameList(newListName);

            String[] allNames = names.getText().toString().split("\\r?\\n");
            for (String name : allNames) {
                String cleanName = name.trim();
                if (!cleanName.isEmpty()) {
                    dataSource.addNames(cleanName, 1, newList.getId());
                }
            }
            UIUtils.showShortToast(R.string.import_success, this);
            finish();
        }
    }
}
