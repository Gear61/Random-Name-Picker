package com.randomappsinc.studentpicker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.randomappsinc.studentpicker.theme.ThemeMode;

public class PreferencesManager {

    private final SharedPreferences prefs;

    private static final String PREFS_KEY = "com.randomappsinc.studentpicker";
    private static final String NUM_APP_OPENS = "numAppOpens";
    private static final String PRESENTATION_TEXT_SIZE_KEY = "presentationTextSize";
    private static final String PRESENTATION_TEXT_COLOR_KEY = "presentationTextColor";
    private static final String SHAKE_ENABLED = "shakeEnabled";
    private static final String BACKUP_URI = "backupUri";
    private static final String LAST_BACKUP_TIME = "lastBackupTime";
    private static final String THEME_MODE = "themeMode";
    private static final String SHOULD_SHOW_ADS_KEY = "shouldShowAds";
    private static final String HAS_SEEN_PREMIUM_TOOLTIP = "hasSeenPremiumTooltip";
    private static final String HAS_SEEN_BACKUP_AND_RESTORE = "hasSeenBackupAndRestore";

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
    }

    public void increaseNumAppOpens() {
        int numAppOpens = prefs.getInt(NUM_APP_OPENS, 0);
        prefs.edit().putInt(NUM_APP_OPENS, numAppOpens + 1).apply();
    }

    public int getNumAppOpens() {
        return prefs.getInt(NUM_APP_OPENS, 0);
    }

    public int getPresentationTextSize() {
        return prefs.getInt(PRESENTATION_TEXT_SIZE_KEY, 6);
    }

    public void setPresentationTextSize(int newSize) {
        prefs.edit().putInt(PRESENTATION_TEXT_SIZE_KEY, newSize).apply();
    }

    public int getPresentationTextColor(int defaultColor) {
        return prefs.getInt(PRESENTATION_TEXT_COLOR_KEY, defaultColor);
    }

    public void setPresentationTextColor(int newColor) {
        prefs.edit().putInt(PRESENTATION_TEXT_COLOR_KEY, newColor).apply();
    }

    public void setShakeEnabled(boolean shakeEnabled) {
        prefs.edit().putBoolean(SHAKE_ENABLED, shakeEnabled).apply();
    }

    public boolean isShakeEnabled() {
        return prefs.getBoolean(SHAKE_ENABLED, false);
    }

    @Nullable
    public String getBackupUri() {
        return prefs.getString(BACKUP_URI, null);
    }

    public void setBackupUri(@Nullable String uriString) {
        prefs.edit().putString(BACKUP_URI, uriString).apply();
    }

    public long getLastBackupTime() {
        return prefs.getLong(LAST_BACKUP_TIME, 0);
    }

    public void updateLastBackupTime() {
        prefs.edit().putLong(LAST_BACKUP_TIME, System.currentTimeMillis()).apply();
    }

    public void setThemeMode(@ThemeMode int themeMode) {
        prefs.edit().putInt(THEME_MODE, themeMode).apply();
    }

    public int getThemeMode() {
        return prefs.getInt(THEME_MODE, ThemeMode.FOLLOW_SYSTEM);
    }

    public void onPremiumAcquired() {
        prefs.edit().putBoolean(SHOULD_SHOW_ADS_KEY, false).apply();
    }

    public boolean isOnFreeVersion() {
        return prefs.getBoolean(SHOULD_SHOW_ADS_KEY, true);
    }

    public boolean hasSeenPremiumTooltip() {
        boolean hasSeenTooltip = prefs.getBoolean(HAS_SEEN_PREMIUM_TOOLTIP, false);
        prefs.edit().putBoolean(HAS_SEEN_PREMIUM_TOOLTIP, true).apply();
        return hasSeenTooltip;
    }

    public boolean hasSeenBackupAndRestore() {
        boolean hasSeenBackupAndRestore = prefs.getBoolean(HAS_SEEN_BACKUP_AND_RESTORE, false);
        prefs.edit().putBoolean(HAS_SEEN_BACKUP_AND_RESTORE, true).apply();
        return hasSeenBackupAndRestore;
    }
}
