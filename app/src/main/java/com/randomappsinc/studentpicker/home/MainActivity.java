package com.randomappsinc.studentpicker.home;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

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
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import java.util.List;
import java.util.Locale;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static com.randomappsinc.studentpicker.listpage.ListActivity.START_ON_EDIT_PAGE;

public class MainActivity extends StandardActivity implements NameListsAdapter.Delegate {

    public static final String LIST_NAME_KEY = "listName";
    private static final int SPEECH_REQUEST_CODE = 1;
    private static final int IMPORT_FILE_REQUEST_CODE = 2;

    @BindView(R.id.coordinator_layout) View parent;
    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.item_name_input) EditText newListInput;
    @BindView(R.id.user_lists) RecyclerView lists;
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
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        preferencesManager = new PreferencesManager(this);
        plus.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_add).colorRes(R.color.white));
        importFile.setImageDrawable(new IconDrawable(
                this,
                IoniconsIcons.ion_android_upload).colorRes(R.color.white));

        nameListsAdapter = new NameListsAdapter(this, this);
        lists.setAdapter(nameListsAdapter);
        lists.addItemDecoration(new SimpleDividerItemDecoration(this));

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

        setNoContent();
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
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                    }

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
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
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
    public void onItemClick(int position) {
        Intent intent = new Intent(this, ListActivity.class);
        String listName = nameListsAdapter.getItem(position);
        intent.putExtra(LIST_NAME_KEY, listName);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void setNoContent() {
        if (nameListsAdapter.getItemCount() == 0) {
            lists.setVisibility(View.GONE);
            noContent.setVisibility(View.VISIBLE);
        } else {
            noContent.setVisibility(View.GONE);
            lists.setVisibility(View.VISIBLE);
        }
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
            preferencesManager.addNameList(newList);
            newListInput.setText("");
            nameListsAdapter.addList(newList);

            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra(LIST_NAME_KEY, newList);
            intent.putExtra(START_ON_EDIT_PAGE, true);
            startActivity(intent);
        }
    }

    @OnClick(R.id.voice_entry_icon)
    public void voiceEntry() {
        showGoogleSpeechDialog();
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

    private void showGoogleSpeechDialog() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.list_name_input_speech_message));
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException exception) {
            UIUtils.showLongToast(R.string.speech_not_supported, this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_REQUEST_CODE:
                if (resultCode != RESULT_OK || data == null) {
                    return;
                }

                List<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result == null || result.isEmpty()) {
                    UIUtils.showLongToast(R.string.speech_unrecognized, this);
                    return;
                }
                String searchInput = result.get(0);
                newListInput.setText(searchInput);
                break;
            case IMPORT_FILE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
                    if (!filePath.endsWith(".txt")) {
                        UIUtils.showSnackbar(parent, getString(R.string.invalid_file));
                    } else {
                        Intent intent = new Intent(this, ImportFileActivity.class);
                        intent.putExtra(ImportFileActivity.FILE_PATH_KEY, filePath);
                        startActivity(intent);
                    }
                }
                break;
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
