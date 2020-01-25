package com.randomappsinc.studentpicker.importdata;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.EditText;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImportFromTextFileActivity extends StandardActivity {

    @BindView(R.id.parent) View parent;
    @BindView(R.id.list_name) EditText listName;
    @BindView(R.id.names) EditText names;
    @BindString(R.string.list_duplicate) String listDuplicate;

    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_name_list);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        preferencesManager = new PreferencesManager(this);
        extractNameListInfo();
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
                runOnUiThread(() -> UIUtils.showLongToast(R.string.load_file_fail, getApplicationContext()));
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
            names.setText(namesListText);
        });
    }

    @OnClick(R.id.add_list)
    public void importNameList() {
        String newListName = listName.getText().toString().trim();
        if (newListName.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_list_name));
        } else if (preferencesManager.getNameLists().contains(newListName)) {
            String dupeMessage = String.format(listDuplicate, newListName);
            UIUtils.showSnackbar(parent, dupeMessage);
        } else {
            preferencesManager.addNameList(newListName);
            DataSource dataSource = new DataSource(this);
            String[] allNames = names.getText().toString().split("\\r?\\n");
            for (String name : allNames) {
                String cleanName = name.trim();
                if (!cleanName.isEmpty()) {
                    dataSource.addNames(cleanName, newListName, 1);
                }
            }
            UIUtils.showShortToast(R.string.import_success, this);
            setResult(RESULT_OK);
            finish();
        }
    }
}
