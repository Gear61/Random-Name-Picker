package com.randomappsinc.studentpicker.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PermissionUtils {

    public static boolean isPermissionGranted(String permission, Context context) {
        if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
                return true;
            } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()) {
                return true;
            }
        } else if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                return true;
            }
        }

        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity, String permission, int requestCode) {
        ActivityCompat.requestPermissions(activity, new String[] {permission}, requestCode);
    }

    public static void requestPermission(Fragment fragment, String permission, int requestCode) {
        fragment.requestPermissions(new String[]{permission}, requestCode);
    }
}
