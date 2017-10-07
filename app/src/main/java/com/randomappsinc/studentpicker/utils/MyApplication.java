package com.randomappsinc.studentpicker.utils;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;

public final class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new IoniconsModule())
               .with(new FontAwesomeModule());
        context = getApplicationContext();
        FileUtils.createExternalDirectory();
    }

    public static Context getAppContext() {
        return context;
    }
}
