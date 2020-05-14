package com.randomappsinc.studentpicker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.common.PremiumFeature;
import com.randomappsinc.studentpicker.models.ListInfo;

import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {

    private SharedPreferences prefs;

    private static final String PREFS_KEY = "com.randomappsinc.studentpicker";
    private static final String STUDENT_LISTS_KEY = "com.randomappsinc.studentpicker.studentLists";
    private static final String NUM_APP_OPENS = "numAppOpens";
    private static final String PRESENTATION_TEXT_SIZE_KEY = "presentationTextSize";
    private static final String PRESENTATION_TEXT_COLOR_KEY = "presentationTextColor";
    private static final String SHAKE_ENABLED = "shakeEnabled";
    private static final String SHOULD_SHOW_ADS_KEY = "shouldShowAds";
    private static final String HAS_SEEN_PREMIUM_TOOLTIP = "hasSeenPremiumTooltip";
    private static final String HAS_SEEN_PREMIUM_FEATURE_UNLOCK = "hasSeenPremiumFeatureUnlock";
    private static final String PREMIUM_FEATURE_PREFIX = "premium_feature_";

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

    public void setIsOnFreeVersion(boolean shouldShowAds) {
        prefs.edit().putBoolean(SHOULD_SHOW_ADS_KEY, shouldShowAds).apply();
    }

    public boolean isOnFreeVersion() {
        return prefs.getBoolean(SHOULD_SHOW_ADS_KEY, true);
    }

    public boolean hasSeenPremiumTooltip() {
        boolean hasSeenTooltip = prefs.getBoolean(HAS_SEEN_PREMIUM_TOOLTIP, false);
        prefs.edit().putBoolean(HAS_SEEN_PREMIUM_TOOLTIP, true).apply();
        return hasSeenTooltip;
    }

    public boolean hasSeenPremiumFeatureUnlock() {
        return prefs.getBoolean(HAS_SEEN_PREMIUM_FEATURE_UNLOCK, false);
    }

    public void onPremiumFeatureUnlockSeen() {
        prefs.edit().putBoolean(HAS_SEEN_PREMIUM_TOOLTIP, true).apply();
    }

    public boolean hasUnlockedFeature(@PremiumFeature String feature) {
        return !isOnFreeVersion() || prefs.getBoolean(PREMIUM_FEATURE_PREFIX + feature, false);
    }

    public void unlockedFeature(@PremiumFeature String feature) {
        prefs.edit().putBoolean(PREMIUM_FEATURE_PREFIX + feature, true).apply();
    }

    /** API dead zone - DO NOT THE USE THE APIS BELOW **/
    @Deprecated
    public Set<String> getNameLists() {
        return prefs.getStringSet(STUDENT_LISTS_KEY, new HashSet<>());
    }

    @Deprecated
    @Nullable
    public ListInfo getNameListState(String listName) {
        return JSONUtils.extractChoosingState(prefs.getString(listName, ""));
    }

    @Deprecated
    @Nullable
    public ChoosingSettings getChoosingSettings(String listName) {
        return JSONUtils.extractChoosingSettings(prefs.getString(listName, ""));
    }
}
