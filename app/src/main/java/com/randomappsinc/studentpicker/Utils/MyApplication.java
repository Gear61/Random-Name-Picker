package com.randomappsinc.studentpicker.Utils;

import android.app.Application;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public final class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Iconify.with(new FontAwesomeModule());
        instance = this;
    }

    public static MyApplication instance;

    public static MyApplication get() {
        return instance;
    }
}
