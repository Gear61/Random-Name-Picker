package com.randomappsinc.studentpicker.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.common.StandardActivity;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.importdata.ImportFromTextFileActivity;
import com.randomappsinc.studentpicker.listpage.ListActivity;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.randomappsinc.studentpicker.listpage.ListActivity.START_ON_EDIT_PAGE;

public class HomeActivity extends StandardActivity implements
        BottomNavigationView.Listener, CreateListDialog.Listener {

    private static final int IMPORT_FILE_REQUEST_CODE = 1;

    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigation;
    @BindView(R.id.bottom_sheet) View bottomSheet;

    private HomepageFragmentController navigationController;
    private CreateListDialog createListDialog;
    private BottomSheetBehavior bottomSheetBehavior;
    private DataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        navigationController = new HomepageFragmentController(getSupportFragmentManager(), R.id.container);
        navigationController.loadHomeInitially();

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {}

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Set state to HIDDEN on slideOffset being 0 (fully hidden),
                // because if you expand/collapse it super fast, the state machine is broken
                if (slideOffset == 0) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                }
                bottomNavigation.onAddSheetSlideOffset(slideOffset);
            }
        });
        bottomNavigation.setListener(this);

        PreferencesManager preferencesManager = new PreferencesManager(this);
        if (preferencesManager.rememberAppOpen() == 5) {
            showPleaseRateDialog();
        }

        createListDialog = new CreateListDialog(this, this);
        dataSource = new DataSource(this);
    }

    @Override
    public void onCreateNewListConfirmed(String newListName) {
        ListDO newListDO = dataSource.addNameList(newListName);

        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtra(Constants.LIST_ID_KEY, newListDO.getId());
        intent.putExtra(START_ON_EDIT_PAGE, true);
        startActivity(intent);
    }

    private void showPleaseRateDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.please_rate)
                .negativeText(R.string.no_im_good)
                .positiveText(R.string.will_rate)
                .onPositive((dialog, which) -> {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    if (!(getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                        UIUtils.showLongToast(R.string.play_store_error, this);
                        return;
                    }
                    startActivity(intent);
                })
                .show();
    }

    @Override
    public void onNavItemSelected(int viewId) {
        UIUtils.hideKeyboard(this);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        navigationController.onNavItemSelected(viewId);

        switch (viewId) {
            case R.id.home:
                setTitle(R.string.app_name);
                break;
            case R.id.settings:
                setTitle(R.string.settings);
                break;
        }
    }

    @Override
    public void onAddOptionsExpanded() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onAddOptionsContracted() {
        hideBottomSheet();
    }

    @OnClick(R.id.sheet_create_name_list)
    public void createNameList() {
        hideBottomSheet();
        createListDialog.show();
    }

    @OnClick(R.id.sheet_import_from_txt)
    public void importFromTextFile() {
        hideBottomSheet();
        Intent txtFileIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        txtFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        txtFileIntent.setType("text/*");
        startActivityForResult(txtFileIntent, IMPORT_FILE_REQUEST_CODE);
    }

    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomNavigation.setIsAddSheetExpanded(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMPORT_FILE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();

                // Persist ability to read from this file
                int takeFlags = data.getFlags()
                        & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags);

                String uriString = uri.toString();
                Intent intent = new Intent(this, ImportFromTextFileActivity.class);
                intent.putExtra(Constants.FILE_URI_KEY, uriString);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        createListDialog.cleanUp();
    }
}
