package com.randomappsinc.studentpicker.importdata;

import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.StringRes;

import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportFromFileActivity extends StandardActivity implements FileImportManager.Listener {

    private static final int NUM_NAMES_TO_TRIGGER_LOADING_STATE = 250;
    public static final String FILE_TYPE = "fileType";

    @BindView(R.id.list_name) EditText listName;
    @BindView(R.id.names) EditText namesInput;

    private FileImportManager fileImportManager;
    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_name_list);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.creating_your_name_list)
                .progress(true, 0)
                .cancelable(false)
                .build();

        fileImportManager = new FileImportManager(
                this,
                this,
                Uri.parse(getIntent().getStringExtra(Constants.FILE_URI_KEY)),
                getIntent().getIntExtra(FILE_TYPE, FileImportType.TEXT));
    }

    @Override
    public void onFileParsingFailure(@StringRes int fileParsingError) {
        runOnUiThread(() -> UIUtils.showLongToast(fileParsingError, this));
    }

    @Override
    public void onFileParsingSuccess(String listNameText, String namesListText) {
        runOnUiThread(() -> {
            listName.setText(listNameText);
            listName.setHint(R.string.add_list_hint);
            namesInput.setText(namesListText);
            namesInput.setHint(R.string.names);
        });
    }

    @OnClick(R.id.save)
    public void importNameList() {
        String newListName = listName.getText().toString().trim();
        if (newListName.isEmpty()) {
            UIUtils.showLongToast(R.string.blank_list_name, this);
        } else {
            String[] namesList = namesInput.getText().toString().split("\\r?\\n");
            if (namesList.length >= NUM_NAMES_TO_TRIGGER_LOADING_STATE) {
                progressDialog.show();
            }
            fileImportManager.saveNameList(
                    this, newListName, namesList);
        }
    }

    @Override
    public void onNameListCreated() {
        runOnUiThread(() -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            UIUtils.showShortToast(R.string.import_success, this);
            finish();
        });
    }
}
