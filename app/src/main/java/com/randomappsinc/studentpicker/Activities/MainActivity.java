package com.randomappsinc.studentpicker.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.randomappsinc.studentpicker.Adapters.NameListsAdapter;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.Utils.PermissionUtils;
import com.randomappsinc.studentpicker.Utils.PreferencesManager;
import com.randomappsinc.studentpicker.Utils.UIUtils;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class MainActivity extends StandardActivity {
    public static final String LIST_NAME_KEY = "listName";

    @Bind(R.id.item_name_input) EditText newListInput;
    @Bind(R.id.content_list) ListView lists;
    @Bind(R.id.no_content) TextView noContent;
    @Bind(R.id.coordinator_layout) View parent;
    @Bind(R.id.add_item) View addItem;
    @Bind(R.id.plus_icon) ImageView plus;
    @Bind(R.id.import_text_file) FloatingActionButton importFile;

    @BindString(R.string.list_duplicate) String listDuplicate;

    private NameListsAdapter nameListsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        newListInput.setHint(R.string.add_list_hint);
        noContent.setText(R.string.no_lists_message);
        plus.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_plus).colorRes(R.color.white));
        importFile.setImageDrawable(new IconDrawable(this, FontAwesomeIcons.fa_upload).colorRes(R.color.white));

        nameListsAdapter = new NameListsAdapter(this, noContent);
        lists.setAdapter(nameListsAdapter);

        if (PreferencesManager.get().getFirstTimeUser()) {
            PreferencesManager.get().setFirstTimeUser(false);
            new MaterialDialog.Builder(this)
                    .title(R.string.view_tutorial)
                    .content(R.string.tutorial_prompt)
                    .positiveText(R.string.accept_tutorial)
                    .negativeText(R.string.no_im_good)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            showTutorial(true);
                        }
                    })
                    .show();
        }

        if (PreferencesManager.get().rememberAppOpen() == 5) {
            showPleaseRateDialog();
        }
    }

    public void showTutorial(final boolean firstTime) {
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this);
        MaterialShowcaseView addListExplanation = new MaterialShowcaseView.Builder(this)
                .setTarget(addItem)
                .setDismissText(R.string.got_it)
                .setContentText(R.string.add_name_list_explanation)
                .setUseAutoRadius(false)
                .setRadius(UIUtils.getDpInPixels(40))
                .build();
        sequence.addSequenceItem(addListExplanation);

        MaterialShowcaseView importFileExplanation = new MaterialShowcaseView.Builder(this)
                .setTarget(importFile)
                .setDismissText(R.string.got_it)
                .setContentText(R.string.import_explanation)
                .setUseAutoRadius(false)
                .setRadius(UIUtils.getDpInPixels(40))
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
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                            UIUtils.showSnackbar(parent, getString(R.string.play_store_error));
                            return;
                        }
                        startActivity(intent);
                    }
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
        UIUtils.hideKeyboard(this);
        Intent intent = new Intent(this, ListActivity.class);
        String listName = nameListsAdapter.getItem(position);
        intent.putExtra(LIST_NAME_KEY, listName);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @OnClick(R.id.add_item)
    public void addItem() {
        String newList = newListInput.getText().toString().trim();
        newListInput.setText("");
        if (newList.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_list_name));
        } else if (PreferencesManager.get().getNameLists().contains(newList)) {
            String dupeMessage = listDuplicate + " \"" + newList + "\".";
            UIUtils.showSnackbar(parent, dupeMessage);
        } else {
            nameListsAdapter.addList(newList);
            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra(LIST_NAME_KEY, newList);
            startActivity(intent);
        }
    }

    @OnClick(R.id.import_text_file)
    public void importTextFile() {
        final Activity activity = this;
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Intent intent = new Intent(this, FilePickerActivity.class);
            startActivityForResult(intent, 1);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                new MaterialDialog.Builder(this)
                        .content(R.string.need_read_external)
                        .positiveText(android.R.string.yes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                PermissionUtils.requestPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, 1);
                            }
                        })
                        .show();
            } else {
                PermissionUtils.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.settings, FontAwesomeIcons.fa_gear, this);
        UIUtils.loadMenuIcon(menu, R.id.view_tutorial, FontAwesomeIcons.fa_info_circle, this);
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
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
