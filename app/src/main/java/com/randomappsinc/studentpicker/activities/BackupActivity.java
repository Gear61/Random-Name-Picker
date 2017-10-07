package com.randomappsinc.studentpicker.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.utils.FileUtils;
import com.randomappsinc.studentpicker.utils.PermissionUtils;
import com.randomappsinc.studentpicker.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BackupActivity extends StandardActivity {

    @BindView(R.id.parent) View parent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.backup_data);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!PermissionUtils.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new MaterialDialog.Builder(this)
                    .content(R.string.backup_permission_ask)
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.no)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            requestWriteExternal();
                        }
                    })
                    .show();
        }
    }

    private void requestWriteExternal() {
        PermissionUtils.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            FileUtils.createExternalDirectory();
            FileUtils.backupData();
        }
    }

    @OnClick(R.id.backup_data)
    public void backupData() {
        if (!PermissionUtils.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestWriteExternal();
        } else {
            FileUtils.backupData();
            UIUtils.showSnackbar(parent, getString(R.string.data_backed_up));
        }
    }

    @OnClick(R.id.export_data)
    public void exportData() {
        if (!PermissionUtils.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestWriteExternal();
        } else {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("vnd.android.cursor.dir/email");
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(FileUtils.createZipArchive()));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.rnp_data));
                startActivity(Intent.createChooser(emailIntent , getString(R.string.export_with)));
            } catch (Exception e) {
                UIUtils.showSnackbar(parent, getString(R.string.archive_failed));
            }
        }
    }
}
