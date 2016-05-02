package com.randomappsinc.studentpicker.Utils;

import android.Manifest;
import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

import java.io.File;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public final class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule());
        context = getApplicationContext();
        createExternalDirectory();
    }

    public static void createExternalDirectory() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Create external storage directory for our app if it doesn't exist
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                File ourDirectory = new File(android.os.Environment.getExternalStorageDirectory(), "RandomNamePicker");
                if (!ourDirectory.exists()) {
                    ourDirectory.mkdirs();
                }
            }
        }
    }

    public static Context getAppContext() {
        return context;
    }
}
