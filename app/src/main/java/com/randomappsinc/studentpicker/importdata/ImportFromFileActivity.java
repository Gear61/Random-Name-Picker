package com.randomappsinc.studentpicker.importdata;

import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

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

public class ImportFromFileActivity extends StandardActivity implements FileParsingManager.Listener {

    public static final String FILE_TYPE = "fileType";

    @BindView(R.id.list_name) EditText listName;
    @BindView(R.id.names) EditText names;
    @BindView(R.id.bottom_ad_banner_container) FrameLayout bannerAdContainer;
    @BindView(R.id.loading) ProgressBar loading;

    private BannerAdManager bannerAdManager;
    private FileParsingManager fileParsingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_name_list);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bannerAdManager = new BannerAdManager(bannerAdContainer);
        bannerAdManager.loadOrRemoveAd();

        fileParsingManager = new FileParsingManager(this);
        extractNamesList(getIntent().getExtras().getInt(FILE_TYPE));

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bannerAdManager.onOrientationChanged();
    }

    private void extractNamesList(int fileType) {
        Uri fileUri = Uri.parse(getIntent().getStringExtra(Constants.FILE_URI_KEY));
        switch (fileType) {
            case FileImportType.TEXT:
                fileParsingManager.extractNameListFromText(this, fileUri);
                break;
            case FileImportType.CSV:
                fileParsingManager.extractNameListFromCsv(this, fileUri);
                break;
        }
    }

    @Override
    public void onFileParsingFailure(int fileParsingError) {
        UIUtils.showLongToast(fileParsingError, this);
    }

    @Override
    public void onFileParsingSuccess(String listNameText, String namesText) {
        loadUI(listNameText, namesText);
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
    public void importNameList(View view) {
        String newListName = listName.getText().toString().trim();
        if (newListName.isEmpty()) {
            UIUtils.showLongToast(R.string.blank_list_name, this);
        } else {
            loading.setVisibility(View.VISIBLE);
            view.setEnabled(false);
            fileParsingManager.saveNameList(this, newListName, names.getText().toString().split("\\r?\\n"));
        }
    }

    @Override
    public void onFileSaved() {
        runOnUiThread(() -> {
            loading.setVisibility(View.GONE);
            UIUtils.showShortToast(R.string.import_success, this);
            finish();
        });
    }
}
