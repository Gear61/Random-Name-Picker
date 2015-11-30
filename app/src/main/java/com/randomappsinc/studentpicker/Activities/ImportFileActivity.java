package com.randomappsinc.studentpicker.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.randomappsinc.studentpicker.Database.DataSource;
import com.randomappsinc.studentpicker.Misc.PreferencesManager;
import com.randomappsinc.studentpicker.Misc.Utils;
import com.randomappsinc.studentpicker.R;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alexanderchiou on 11/23/15.
 */
public class ImportFileActivity extends StandardActivity {
    public static final String FILE_PATH_KEY = "filePath";

    @Bind(R.id.parent) View parent;
    @Bind(R.id.list_name) EditText listName;
    @Bind(R.id.names) EditText names;
    @BindString(R.string.list_duplicate) String listDuplicate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.import_name_list);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String filePath = getIntent().getStringExtra(FILE_PATH_KEY);
        listName.setText(Utils.getFileName(filePath));
        try {
            names.setText(Utils.getNamesFromFile(filePath));
        }
        catch (Exception e) {
            Utils.showSnackbar(parent, getString(R.string.load_file_fail));
        }
    }

    @OnClick(R.id.add_list)
    public void importNameList(View view) {
        String newListName = listName.getText().toString().trim();
        if (newListName.isEmpty()) {
            Utils.showSnackbar(parent, getString(R.string.blank_list_name));
        }
        else if (PreferencesManager.get().getNameLists().contains(newListName)) {
            String dupeMessage = listDuplicate + " \"" + newListName + "\".";
            Utils.showSnackbar(parent, dupeMessage);
        }
        else {
            PreferencesManager.get().addNameList(newListName);
            DataSource dataSource = new DataSource(this);
            String[] allNames = names.getText().toString().split("\\r?\\n");
            for (String name : allNames) {
                String cleanName = name.trim();
                if (!cleanName.isEmpty()) {
                    dataSource.addName(cleanName, newListName);
                }
            }
            finish();
        }
    }
}
