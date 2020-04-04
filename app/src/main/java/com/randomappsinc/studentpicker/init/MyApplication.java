package com.randomappsinc.studentpicker.init;

import android.app.Application;
import android.content.Context;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Iconify.with(new IoniconsModule())
                .with(new FontAwesomeModule());
    }

    @Deprecated
    public static Context getAppContext() {
        return context;
    }
}
