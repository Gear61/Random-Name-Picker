package com.randomappsinc.studentpicker.theme;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    public static void applyTheme(@ThemeMode int themeMode) {
        switch (themeMode) {
            case ThemeMode.LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case ThemeMode.DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case ThemeMode.FOLLOW_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
