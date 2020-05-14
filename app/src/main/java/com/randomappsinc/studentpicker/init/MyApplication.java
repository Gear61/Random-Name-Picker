package com.randomappsinc.studentpicker.init;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.ads.MobileAds;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.joanzapata.iconify.fonts.IoniconsModule;
import com.randomappsinc.studentpicker.theme.ThemeManager;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

public class MyApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Iconify.with(new IoniconsModule())
                .with(new FontAwesomeModule());
        MobileAds.initialize(this);
        PreferencesManager preferencesManager = new PreferencesManager(this);
        ThemeManager.applyTheme(preferencesManager.getThemeMode());
    }

    @Deprecated
    public static Context getAppContext() {
        return context;
    }
}
