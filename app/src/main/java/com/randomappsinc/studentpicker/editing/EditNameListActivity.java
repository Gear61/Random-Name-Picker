package com.randomappsinc.studentpicker.editing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.BannerAdManager;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.SpeechToTextManager;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.NameDO;
import com.randomappsinc.studentpicker.utils.PermissionUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.SimpleDividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditNameListActivity extends StandardActivity implements
        NameEditChoicesDialog.Listener, RenameDialog.Listener, DeleteNameDialog.Listener,
        DuplicationDialog.Listener, SpeechToTextManager.Listener, EditNameListAdapter.Listener {

    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;

    @BindView(R.id.parent) View parent;
    @BindView(R.id.item_name_input) AutoCompleteTextView newNameInput;
    @BindView(R.id.no_content) TextView noContent;
    @BindView(R.id.num_names) TextView numNames;
    @BindView(R.id.content_list) RecyclerView namesList;
    @BindView(R.id.plus_icon) ImageView plus;
    @BindView(R.id.bottom_ad_banner_container) FrameLayout bannerAdContainer;

    private EditNameListAdapter namesAdapter;
    private int listId;
    private DataSource dataSource;
    private NameEditChoicesDialog nameEditChoicesDialog;
    private RenameDialog renameDialog;
    private DeleteNameDialog deleteNameDialog;
    private DuplicationDialog duplicationDialog;
    private SpeechToTextManager speechToTextManager;
    private BannerAdManager bannerAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_name_list);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listId = getIntent().getIntExtra(Constants.LIST_ID_KEY, 0);
        dataSource = new DataSource(this);
        setTitle(dataSource.getListName(listId));

        newNameInput.setAdapter(new NameCreationAutoCompleteAdapter(this));
        plus.setImageDrawable(new IconDrawable(
                this,
                IoniconsIcons.ion_android_add).colorRes(R.color.white));

        listId = getIntent().getIntExtra(Constants.LIST_ID_KEY, 0);
        dataSource = new DataSource(this);
        noContent.setText(R.string.no_names_for_edit);

        speechToTextManager = new SpeechToTextManager(this, this);
        speechToTextManager.setListeningPrompt(R.string.name_input_with_speech_prompt);

        List<NameDO> names = dataSource.getNamesInList(listId);
        namesAdapter = new EditNameListAdapter(noContent, numNames, names, this);
        namesList.setAdapter(namesAdapter);
        namesList.addItemDecoration(new SimpleDividerItemDecoration(this));

        nameEditChoicesDialog = new NameEditChoicesDialog(this, this);
        renameDialog = new RenameDialog(this, this);
        deleteNameDialog = new DeleteNameDialog(this, this);
        duplicationDialog = new DuplicationDialog(this, this);
        bannerAdManager = new BannerAdManager(bannerAdContainer);
    }

    @OnClick(R.id.add_item)
    void addItem() {
        String newName = newNameInput.getText().toString().trim();
        newNameInput.setText("");
        if (newName.isEmpty()) {
            UIUtils.showSnackbar(parent, getString(R.string.blank_name));
        } else {
            dataSource.addNames(newName, 1, listId);
            List<NameDO> names = dataSource.getNamesInList(listId);
            namesAdapter.setNameList(names);
            String template = getString(R.string.added_name);
            UIUtils.showSnackbar(parent, String.format(template, newName));
        }
    }

    @Override
    public void showNameOptions(NameDO nameDO) {
        nameEditChoicesDialog.showChoices(nameDO);
    }

    @OnClick(R.id.voice_entry_icon)
    void addNameWithVoice() {
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
                        .onPositive((dialog, which) ->
                                PermissionUtils.requestPermission(
                                        this,
                                        Manifest.permission.RECORD_AUDIO,
                                        RECORD_AUDIO_PERMISSION_CODE))
                        .show();
            } else {
                PermissionUtils.requestPermission(
                        this, Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRenameChosen(NameDO nameDO) {
        renameDialog.startRenamingProcess(nameDO);
    }

    @Override
    public void onDeleteChosen(NameDO nameDO) {
        deleteNameDialog.startDeletionProcess(nameDO);
    }

    @Override
    public void onDuplicationChosen(NameDO nameDO) {
        duplicationDialog.show(nameDO);
    }

    @Override
    public void onRenameSubmitted(int nameId, String previousName, String newName, int amountToRename) {
        dataSource.renamePeople(previousName, newName, listId, amountToRename);
        List<NameDO> names = dataSource.getNamesInList(listId);
        namesAdapter.setNameList(names);
    }

    @Override
    public void onDeletionSubmitted(String name, int amountToDelete) {
        dataSource.removeNames(name, amountToDelete, listId);
        List<NameDO> names = dataSource.getNamesInList(listId);
        namesAdapter.setNameList(names);
        if (amountToDelete == 1) {
            UIUtils.showSnackbar(parent, getString(R.string.deleted_name, name));
        } else {
            UIUtils.showSnackbar(parent, getString(R.string.names_deleted, amountToDelete, name));
        }
    }

    @Override
    public void onDuplicationSubmitted(int nameId, String name, int amountToAdd) {
        dataSource.addNames(name, amountToAdd, listId);
        List<NameDO> names = dataSource.getNamesInList(listId);
        namesAdapter.setNameList(names);
        UIUtils.showSnackbar(parent, R.string.clones_added);
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
    public void onTextSpoken(String spokenText) {
        newNameInput.setText(spokenText);
        newNameInput.setSelection(spokenText.length());
    }

    @Override
    public void onResume() {
        super.onResume();
        bannerAdManager.loadOrRemoveAd();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bannerAdManager.onOrientationChanged();
    }
}
