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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.SpeechToTextManager;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.importdata.ImportFromTextFileActivity;
import com.randomappsinc.studentpicker.listpage.ListActivity;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.settings.SettingsActivity;
import com.randomappsinc.studentpicker.utils.PermissionUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static com.randomappsinc.studentpicker.listpage.ListActivity.START_ON_EDIT_PAGE;

public class MainActivity extends StandardActivity
        implements NameListsAdapter.Delegate, RenameListDialog.Listener,
        DeleteListDialog.Listener, SpeechToTextManager.Listener {

    private static final int IMPORT_FILE_REQUEST_CODE = 1;
    private static final int SAVE_TXT_FILE_LIST_IMPORT_REQUEST_CODE = 2;

    private static final int READ_RECORD_AUDIO_PERMISSION_CODE = 2;

    @BindView(R.id.coordinator_layout) View parent;
    @BindView(R.id.focal_point) View focalPoint;
    @BindView(R.id.item_name_input) EditText newListInput;
    @BindView(R.id.user_lists) RecyclerView lists;
    @BindView(R.id.no_content) TextView noContent;
    @BindView(R.id.add_item) View addItem;
    @BindView(R.id.plus_icon) ImageView plus;
    @BindView(R.id.import_text_file) FloatingActionButton importFile;

    private PreferencesManager preferencesManager;
    private SpeechToTextManager speechToTextManager;
    private DataSource dataSource;
    private NameListsAdapter nameListsAdapter;
    private RenameListDialog renameListDialog;
    private DeleteListDialog deleteListDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        speechToTextManager = new SpeechToTextManager(this, this);
        speechToTextManager.setListeningPrompt(R.string.list_name_speech_input_prompt);
        preferencesManager = new PreferencesManager(this);
        renameListDialog = new RenameListDialog(this, this);
        deleteListDialog = new DeleteListDialog(this, this);
        dataSource = new DataSource(this);
        plus.setImageDrawable(new IconDrawable(this,
                IoniconsIcons.ion_android_add).colorRes(R.color.white));
        importFile.setImageDrawable(new IconDrawable(
                this,
                IoniconsIcons.ion_android_upload).colorRes(R.color.white));

        nameListsAdapter = new NameListsAdapter(this, dataSource.getNameLists());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechToTextManager.cleanUp();
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
    public void onItemClick(ListDO listDO) {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra(Constants.LIST_ID_KEY, listDO.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    public void onItemEditClick(int position, ListDO listDO) {
        renameListDialog.show(position, listDO);
    }

    @Override
    public void onRenameListConfirmed(int position, ListDO updatedList) {
        dataSource.renameList(updatedList);
        preferencesManager.renameList(nameListsAdapter.getItem(position).getName(), updatedList.getName());
        nameListsAdapter.renameItem(position, updatedList.getName());
    }

    @Override
    public void onItemDeleteClick(int position, ListDO listDO) {
        deleteListDialog.presentForList(position, listDO);
    }

    @Override
    public void onDeleteListConfirmed(int position, ListDO listDO) {
        dataSource.deleteList(listDO.getId());
        preferencesManager.removeNameList(listDO.getName());
        nameListsAdapter.deleteItem(position);
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
        } else {
            newListInput.setText("");

            ListDO newListDO = dataSource.addNameList(newList);
            nameListsAdapter.addList(newListDO);

            Intent intent = new Intent(this, ListActivity.class);
            intent.putExtra(Constants.LIST_ID_KEY, newListDO.getId());
            intent.putExtra(START_ON_EDIT_PAGE, true);
            startActivity(intent);
        }
    }

    @OnClick(R.id.voice_entry_icon)
    public void voiceEntry() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.RECORD_AUDIO, this)) {
            speechToTextManager.startSpeechToTextFlow();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.RECORD_AUDIO)) {
                new MaterialDialog.Builder(this)
                        .content(R.string.need_record_audio)
                        .positiveText(R.string.okay)
                        .negativeText(R.string.cancel)
                        .onPositive((dialog, which) -> requestRecordAudio())
                        .show();
            } else {
                requestRecordAudio();
            }
        }
    }

    @Override
    public void onTextSpoken(String spokenText) {
        newListInput.setText(spokenText);
        newListInput.setSelection(spokenText.length());
    }

    @OnClick(R.id.import_text_file)
    public void importTextFile() {
        Intent txtFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        txtFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        txtFileIntent.setType("text/*");
        startActivityForResult(txtFileIntent, IMPORT_FILE_REQUEST_CODE);
    }

    private void requestRecordAudio() {
        PermissionUtils.requestPermission(
                this, Manifest.permission.RECORD_AUDIO, READ_RECORD_AUDIO_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            speechToTextManager.startSpeechToTextFlow();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IMPORT_FILE_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                    Uri uri = data.getData();

                    // Persist ability to read from this file
                    int takeFlags = data.getFlags()
                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    getContentResolver().takePersistableUriPermission(uri, takeFlags);

                    String uriString = uri.toString();
                    Intent intent = new Intent(this, ImportFromTextFileActivity.class);
                    intent.putExtra(Constants.FILE_URI_KEY, uriString);
                    startActivityForResult(intent, SAVE_TXT_FILE_LIST_IMPORT_REQUEST_CODE);
                }
                break;
            case SAVE_TXT_FILE_LIST_IMPORT_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    nameListsAdapter.refresh(dataSource.getNameLists());
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
