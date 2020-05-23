package com.randomappsinc.studentpicker.editing;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.ads.BannerAdManager;
import com.randomappsinc.studentpicker.choosing.NameChoosingActivity;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.NameDO;
import com.randomappsinc.studentpicker.photo.PhotoImportManager;
import com.randomappsinc.studentpicker.photo.PhotoImportOptionsDialog;
import com.randomappsinc.studentpicker.premium.BuyPremiumActivity;
import com.randomappsinc.studentpicker.speech.SpeechToTextManager;
import com.randomappsinc.studentpicker.utils.PermissionUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class EditNameListActivity extends AppCompatActivity implements
        NameEditChoicesDialog.Listener, RenameDialog.Listener, DeleteNameDialog.Listener,
        DuplicationDialog.Listener, SpeechToTextManager.Listener, EditNameListAdapter.Listener,
        PhotoImportOptionsDialog.Delegate, PhotoImportManager.Listener {

    private static final int RECORD_AUDIO_PERMISSION_CODE = 1;
    private static final int CAMERA_PERMISSION_CODE = 2;
    private static final int GALLERY_PERMISSION_CODE = 3;

    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int GALLERY_REQUEST_CODE = 2;

    @BindView(R.id.item_name_input) AutoCompleteTextView newNameInput;
    @BindView(R.id.no_content) TextView noContent;
    @BindView(R.id.num_names) TextView numNames;
    @BindView(R.id.content_list) RecyclerView namesList;
    @BindView(R.id.plus_icon) ImageView plus;
    @BindView(R.id.bottom_ad_banner_container) FrameLayout bannerAdContainer;

    @BindDrawable(R.drawable.line_divider) Drawable lineDivider;

    private EditNameListAdapter namesAdapter;
    private int listId;
    private DataSource dataSource;
    private NameEditChoicesDialog nameEditChoicesDialog;
    private RenameDialog renameDialog;
    private DeleteNameDialog deleteNameDialog;
    private DuplicationDialog duplicationDialog;
    private SpeechToTextManager speechToTextManager;
    private boolean listHasChanged = false;
    private PhotoImportOptionsDialog photoOptionsDialog;
    private PhotoImportManager photoImportManager;
    private BannerAdManager bannerAdManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_name_list);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar()
                .setHomeAsUpIndicator(new IconDrawable(this, IoniconsIcons.ion_android_close)
                        .colorRes(R.color.white)
                        .actionBarSize());

        listId = getIntent().getIntExtra(Constants.LIST_ID_KEY, 0);
        dataSource = new DataSource(this);
        setTitle(dataSource.getListName(listId));

        newNameInput.setAdapter(new NameCreationAutoCompleteAdapter(this));
        plus.setImageDrawable(new IconDrawable(
                this,
                IoniconsIcons.ion_android_add).colorRes(R.color.white));

        listId = getIntent().getIntExtra(Constants.LIST_ID_KEY, 0);
        dataSource = new DataSource(this);

        speechToTextManager = new SpeechToTextManager(this, this);
        speechToTextManager.setListeningPrompt(R.string.name_input_with_speech_prompt);

        List<NameDO> names = dataSource.getNamesInList(listId);
        namesAdapter = new EditNameListAdapter(noContent, numNames, names, this);
        namesList.setAdapter(namesAdapter);

        DividerItemDecoration itemDecorator =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(lineDivider);
        namesList.addItemDecoration(itemDecorator);

        nameEditChoicesDialog = new NameEditChoicesDialog(this, this);
        renameDialog = new RenameDialog(this, this);
        deleteNameDialog = new DeleteNameDialog(this, this);
        duplicationDialog = new DuplicationDialog(this, this);
        photoOptionsDialog = new PhotoImportOptionsDialog(this, this);
        photoImportManager = new PhotoImportManager(this);
        bannerAdManager = new BannerAdManager(bannerAdContainer);
        bannerAdManager.loadOrRemoveAd();
    }

    @OnClick(R.id.add_item)
    void addItem() {
        String newName = newNameInput.getText().toString().trim();
        newNameInput.setText("");
        if (newName.isEmpty()) {
            UIUtils.showLongToast(R.string.blank_name, this);
        } else {
            listHasChanged = true;
            dataSource.addNames(newName, 1, listId);
            List<NameDO> names = dataSource.getNamesInList(listId);
            namesAdapter.setNameList(names);
            String template = getString(R.string.added_name);
            UIUtils.showShortToast(String.format(template, newName), this);
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
            PermissionUtils.requestPermission(
                    this, Manifest.permission.RECORD_AUDIO, RECORD_AUDIO_PERMISSION_CODE);
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
        listHasChanged = true;
        dataSource.renamePeople(previousName, newName, listId, amountToRename);
        List<NameDO> names = dataSource.getNamesInList(listId);
        namesAdapter.setNameList(names);
    }

    @Override
    public void onDeletionSubmitted(String name, int amountToDelete) {
        listHasChanged = true;
        dataSource.removeNames(name, amountToDelete, listId);
        List<NameDO> names = dataSource.getNamesInList(listId);
        namesAdapter.setNameList(names);
        if (amountToDelete == 1) {
            UIUtils.showShortToast(getString(R.string.deleted_name, name), this);
        } else {
            UIUtils.showLongToast(getString(R.string.names_deleted, amountToDelete, name), this);
        }
    }

    @Override
    public void onDuplicationSubmitted(int nameId, String name, int amountToAdd) {
        listHasChanged = true;
        dataSource.addNames(name, amountToAdd, listId);
        List<NameDO> names = dataSource.getNamesInList(listId);
        namesAdapter.setNameList(names);
        UIUtils.showLongToast(R.string.clones_added, this);
    }

    @Override
    public void onTextSpoken(String spokenText) {
        newNameInput.setText(spokenText);
        newNameInput.setSelection(spokenText.length());
    }

    @Override
    public void showPhotoOptions() {
        photoOptionsDialog.showPhotoOptions();
    }

    @Override
    public void addWithCamera() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.CAMERA, this)) {
            startCameraPage();
        } else {
            PermissionUtils.requestPermission(this, Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE);
        }
    }

    private void startCameraPage() {
        Intent takePhotoIntent = photoImportManager.getPhotoTakingIntent(this);
        if (takePhotoIntent == null) {
            UIUtils.showLongToast(
                    R.string.take_photo_with_camera_failed, this);
        } else {
            startActivityForResult(takePhotoIntent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void addWithGallery() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE, this)) {
            openFilePicker();
        } else {
            PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    GALLERY_PERMISSION_CODE);
        }
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onAddPhotoFailure() {
        UIUtils.showLongToast(R.string.photo_processing_failure, this);
    }

    @Override
    public void onAddPhotoSuccess(Uri takenPhotoUri) {
        runOnUiThread(() -> {
            NameDO nameDO = namesAdapter.getCurrentlySelectedName();
            nameDO.setPhotoUri(takenPhotoUri.toString());
            namesAdapter.refreshSelectedItem();
            dataSource.updateNamePhoto(nameDO.getId(), takenPhotoUri.toString());
        });
    }

    @Override
    public void launchBuyPremiumPage() {
        Intent intent = new Intent(this, BuyPremiumActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        bannerAdManager.onOrientationChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case CAMERA_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    photoImportManager.processTakenPhoto(this);
                } else if (resultCode == RESULT_CANCELED) {
                    photoImportManager.deleteLastTakenPhoto();
                }
                break;
            case GALLERY_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    photoImportManager.processSelectedPhoto(this, data);
                }
                break;
        }
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        UIUtils.hideKeyboard(this);
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        switch (requestCode) {
            case CAMERA_PERMISSION_CODE:
                startCameraPage();
                break;
            case GALLERY_PERMISSION_CODE:
                openFilePicker();
                break;
            case RECORD_AUDIO_PERMISSION_CODE:
                speechToTextManager.startSpeechToTextFlow();
                break;
        }
    }

    @Override
    public void finish() {
        if (listHasChanged) {
            setResult(Constants.LIST_UPDATED_RESULT_CODE);
        }
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_from_top);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_name_list_menu, menu);
        UIUtils.loadMenuIcon(menu, R.id.open_choose_page, IoniconsIcons.ion_android_person, this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.open_choose_page:
                Intent choosingIntent = new Intent(this, NameChoosingActivity.class);
                choosingIntent.putExtra(Constants.LIST_ID_KEY, listId);
                choosingIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(choosingIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
