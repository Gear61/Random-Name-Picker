package com.randomappsinc.studentpicker.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.backupandrestore.BackupAndRestoreActivity;
import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.editing.EditNameListActivity;
import com.randomappsinc.studentpicker.importdata.FileImportType;
import com.randomappsinc.studentpicker.importdata.ImportFromFileActivity;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.premium.PaymentManager;
import com.randomappsinc.studentpicker.premium.PremiumFeatureOpener;
import com.randomappsinc.studentpicker.utils.PreferencesManager;
import com.randomappsinc.studentpicker.utils.UIUtils;
import com.randomappsinc.studentpicker.views.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.randomappsinc.studentpicker.importdata.ImportFromFileActivity.FILE_TYPE;

public class HomeActivity extends AppCompatActivity implements
        BottomNavigationView.Listener, CreateListDialog.Listener, PaymentManager.Listener {

    private static final String PREVIOUSLY_SELECTED_PAGE_ID = "previouslySelectedPageId";
    private static final int NUM_APP_OPENS_FOR_RATING_ASK = 5;

    private static final int IMPORT_TXT_REQUEST_CODE = 1;
    private static final int IMPORT_CSV_REQUEST_CODE = 2;

    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigation;
    @BindView(R.id.bottom_sheet) View bottomSheet;
    @BindView(R.id.title_import_from_csv) TextView importFromCsv;

    private HomepageFragmentController navigationController;
    private PreferencesManager preferencesManager;
    private CreateListDialog createListDialog;
    private BottomSheetBehavior bottomSheetBehavior;
    private DataSource dataSource;
    private PaymentManager paymentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        navigationController = new HomepageFragmentController(getSupportFragmentManager(), R.id.container);
        if (savedInstanceState == null) {
            navigationController.loadHomeInitially();
        } else {
            navigationController.restoreFragments();
            int previousSelectedId = savedInstanceState.getInt(PREVIOUSLY_SELECTED_PAGE_ID, R.id.home);
            navigationController.onNavItemSelected(previousSelectedId);
            bottomNavigation.setCurrentlySelected(previousSelectedId);
        }

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

        preferencesManager = new PreferencesManager(this);
        preferencesManager.increaseNumAppOpens();
        if (preferencesManager.getNumAppOpens() == NUM_APP_OPENS_FOR_RATING_ASK) {
            showPleaseRateDialog();
        }

        createListDialog = new CreateListDialog(this, this);
        dataSource = new DataSource(this);
        paymentManager = new PaymentManager(this, this);
        paymentManager.setUpAndCheckForPremium();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(PREVIOUSLY_SELECTED_PAGE_ID, navigationController.getCurrentViewId());
    }

    @Override
    public void onCreateNewListConfirmed(String newListName) {
        ListDO newListDO = dataSource.addNameList(newListName);
        Intent intent = new Intent(this, EditNameListActivity.class);
        intent.putExtra(Constants.LIST_ID_KEY, newListDO.getId());
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_from_bottom, R.anim.stay);
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
        txtFileIntent.setType("text/plain");
        startActivityForResult(txtFileIntent, IMPORT_TXT_REQUEST_CODE);
    }

    @OnClick(R.id.sheet_import_from_csv)
    public void importFromCsvFile() {
        hideBottomSheet();
        PremiumFeatureOpener.openFeature(R.string.import_from_csv_feature_name, this, () -> {
            UIUtils.showLongToast(R.string.csv_format_instructions, this);
            Intent csvIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            csvIntent.addCategory(Intent.CATEGORY_OPENABLE);
            csvIntent.setType("*/*");
            csvIntent.putExtra(Intent.EXTRA_MIME_TYPES, Constants.CSV_MIME_TYPES);
            startActivityForResult(csvIntent, IMPORT_CSV_REQUEST_CODE);
        });
    }

    @OnClick(R.id.sheet_restore_from_backup)
    public void restoreFromBackup() {
        hideBottomSheet();
        PremiumFeatureOpener.openFeature(R.string.restore_name_list_from_backup, this, () -> {
            Intent intent = new Intent(this, BackupAndRestoreActivity.class)
                    .putExtra(Constants.GO_TO_RESTORE_IMMEDIATELY_KEY, true);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
        });
    }

    private void hideBottomSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        bottomNavigation.setIsAddSheetExpanded(false);
    }

    @Override
    public void onPremiumPurchaseSuccessful() {}

    @Override
    public void onPremiumAlreadyOwned() {
        new MaterialDialog.Builder(this)
                .title(R.string.thank_you_for_support)
                .content(R.string.premium_detected_home)
                .positiveText(R.string.okay)
                .show();
    }

    @Override
    public void onPaymentFailed() {}

    @Override
    public void onStartupFailed() {}

    @Override
    protected void onResume() {
        super.onResume();
        importFromCsv.setText(preferencesManager.isOnFreeVersion()
                ? R.string.import_from_csv_file_premium
                : R.string.import_from_csv_file);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMPORT_TXT_REQUEST_CODE) {
            maybeOpenImportFileActivity(resultCode, data, FileImportType.TEXT);
        } else if (requestCode == IMPORT_CSV_REQUEST_CODE) {
            maybeOpenImportFileActivity(resultCode, data, FileImportType.CSV);
        }
    }

    private void maybeOpenImportFileActivity(int resultCode, Intent data, @FileImportType int fileType) {
        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            // Persist ability to read from this file
            int takeFlags = data.getFlags()
                    & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(uri, takeFlags);

            String uriString = uri.toString();
            Intent intent = new Intent(this, ImportFromFileActivity.class);
            intent.putExtra(Constants.FILE_URI_KEY, uriString);
            intent.putExtra(FILE_TYPE, fileType);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        paymentManager.cleanUp();
    }
}
