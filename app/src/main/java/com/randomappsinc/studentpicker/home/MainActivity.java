package com.randomappsinc.studentpicker.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.activities.ImportFileActivity;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.listpage.ListActivity;
import com.randomappsinc.studentpicker.settings.SettingsActivity;
import com.randomappsinc.studentpicker.utils.PermissionUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class MainActivity extends StandardActivity {

    public static final String LIST_NAME_KEY = "listName";
    public static final int NAME_CHOOSING_FRAGMENT = 0;
    public static final int EDIT_NAME_LIST_FARGMENT = 1;
    public static final String LIST_TYPE = "listType";

    @BindView(R.id.coordinator_layout) View parent;
    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.item_name_input) EditText newListInput;
    @BindView(R.id.content_list) ListView lists;
    @BindView(R.id.no_content) TextView noContent;
    @BindView(R.id.add_item) View addItem;
    @BindView(R.id.plus_icon) ImageView plus;
    @BindView(R.id.import_text_file) FloatingActionButton importFile;

    @BindString(R.string.list_duplicate) String listDuplicate;

    private PreferencesManager preferencesManager;
    private NameListsAdapter nameListsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kill activity if it's above an existing stack due to launcher bug
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        preferencesManager = new PreferencesManager(this);
        newListInput.setHint(R.string.add_list_hint);
        noContent.setText(R.string.no_lists_message);
        plus.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_add).colorRes(R.color.white));
        importFile.setImageDrawable(new IconDrawable(
                this,
                IoniconsIcons.ion_android_upload).colorRes(R.color.white));

        nameListsAdapter = new NameListsAdapter(this, noContent);
        lists.setAdapter(nameListsAdapter);

        if (preferencesManager.getFirstTimeUser()) {
            preferencesManager.setFirstTimeUser(false);
            new MaterialDialog.Builder(this)
                    .title(R.string.view_tutorial)
                    .content(R.string.tutorial_prompt)
                    .positiveText(R.string.accept_tutorial)
                    .negativeText(R.string.no_im_good)
                    .onPositive((dialog, which) -> showTutorial(true))
                    .show();
        }

        if (preferencesManager.rememberAppOpen() == 5) {
            showPleaseRateDialog();
        }
    }

    public void showTutorial(final boolean firstTime) {
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        MaterialShowcaseView addListExplanation = new MaterialShowcaseView.Builder(this)
                .setTarget(addItem)
                .setShapePadding(UIUtils.getDpInPixels(8, this))
                .setDismissText(R.string.got_it)
                .setContentText(R.string.add_name_list_explanation)
                .build();
        sequence.addSequenceItem(addListExplanation);

        MaterialShowcaseView importFileExplanation = new MaterialShowcaseView.Builder(this)
                .setTarget(importFile)
                .setShapePadding(UIUtils.getDpInPixels(8, this))
                .setDismissText(R.string.got_it)
                .setContentText(R.string.import_explanation)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {}

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        if (firstTime) {
                            showWelcomeDialog();
                        }
                    }
                })
                .build();
        sequence.addSequenceItem(importFileExplanation);
        sequence.start();
    }

    public void showWelcomeDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.welcome)
                .content(R.string.welcome_string)
                .positiveText(android.R.string.yes)
                .show();
    }

    public void showPleaseRateDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.please_rate)
                .negativeText(R.string.no_im_good)
                .positiveText(R.string.will_rate)
                .onPositive((dialog, which) -> {
                    Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                        UIUtils.showSnackbar(parent, getString(R.string.play_store_error));
                        return;
                    }
                    startActivity(intent);
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameListsAdapter.refreshList();
    }

    @OnItemClick(R.id.content_list)
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ListActivity.class);
        String listName = nameListsAdapter.getItem(position);
        intent.putExtra(LIST_NAME_KEY, listName);
        intent.putExtra(LIST_TYPE, NAME_CHOOSING_FRAGMENT);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @OnClick(R.id.add_item)
    public void addItem() {
        String newList = newListInput.getText().toString().trim();
        if (newList.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_list_name));
        } else if (preferencesManager.getNameLists().contains(newList)) {
            String dupeMessage = String.format(listDuplicate, newList);
            UIUtils.showSnackbar(parent, dupeMessage);
        } else {
            newListInput.setText("");
            nameListsAdapter.addList(newList);
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra(LIST_NAME_KEY, newList);
            intent.putExtra(LIST_TYPE, EDIT_NAME_LIST_FARGMENT);
            startActivity(intent);
        }
    }

    @OnClick(R.id.import_text_file)
    public void importTextFile() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, this)) {
            Intent intent = new Intent(this, FilePickerActivity.class);
            startActivityForResult(intent, 1);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new MaterialDialog.Builder(this)
                        .content(R.string.need_read_external)
                        .positiveText(android.R.string.yes)
                        .onPositive((dialog, which) -> requestReadExternal())
                        .show();
            } else {
                requestReadExternal();
            }
        }
    }

    private void requestReadExternal() {
        PermissionUtils.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, 1);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this, FilePickerActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            if (!filePath.endsWith(".txt")) {
                UIUtils.showSnackbar(parent, getString(R.string.invalid_file));
            } else {
                Intent intent = new Intent(this, ImportFileActivity.class);
                intent.putExtra(ImportFileActivity.FILE_PATH_KEY, filePath);
                startActivity(intent);
            }
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        focalPoint.requestFocus();
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.settings, IoniconsIcons.ion_android_settings, this);
        UIUtils.loadMenuIcon(menu, R.id.view_tutorial, IoniconsIcons.ion_information_circled, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.view_tutorial:
                showTutorial(false);
                UIUtils.hideKeyboard(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
