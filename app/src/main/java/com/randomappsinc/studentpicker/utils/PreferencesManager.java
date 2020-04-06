package com.randomappsinc.studentpicker.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.models.ListInfo;

import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {

    private SharedPreferences prefs;

    private static final String PREFS_KEY = "com.randomappsinc.studentpicker";
    private static final String STUDENT_LISTS_KEY = "com.randomappsinc.studentpicker.studentLists";
    private static final String FIRST_TIME_KEY = "firstTime";
    private static final String NUM_APP_OPENS = "numAppOpens";
    private static final String PRESENTATION_TEXT_SIZE_KEY = "presentationTextSize";
    private static final String PRESENTATION_TEXT_COLOR_KEY = "presentationTextColor";
    private static final String SHAKE_ENABLED = "shakeEnabled";
    private static final String SHOULD_SHOW_ADS_KEY = "shouldShowAds";

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
    }

    public Set<String> getNameLists() {
        return prefs.getStringSet(STUDENT_LISTS_KEY, new HashSet<>());
    }

    public void removeNameList(String deletedList) {
        removeNamesListCache(deletedList);
    }

    public void renameList(String oldName, String newName) {
        moveNamesListCache(oldName, newName);
    }

    public void setNameListState(String listName, ListInfo currentState, ChoosingSettings settings) {
        String cache = JSONUtils.serializeChoosingState(currentState, settings);
        prefs.edit().putString(listName, cache).apply();
    }

    public ListInfo getNameListState(String listName) {
        return JSONUtils.extractChoosingState(prefs.getString(listName, ""));
    }

    public ChoosingSettings getChoosingSettings(String listName) {
        return JSONUtils.extractChoosingSettings(prefs.getString(listName, ""));
    }

    private void moveNamesListCache(String oldListName, String newListName) {
        String cache = prefs.getString(oldListName, "");
        removeNamesListCache(oldListName);
        prefs.edit().putString(newListName, cache).apply();
    }

    private void removeNamesListCache(String listName) {
        prefs.edit().remove(listName).apply();
    }

    public int rememberAppOpen() {
        int numAppOpens = prefs.getInt(NUM_APP_OPENS, 0);
        numAppOpens++;
        prefs.edit().putInt(NUM_APP_OPENS, numAppOpens).apply();
        return numAppOpens;
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

    public void setShouldShowAds(boolean shouldShowAds) {
        prefs.edit().putBoolean(SHOULD_SHOW_ADS_KEY, shouldShowAds).apply();
    }

    public boolean shouldShowAds() {
        return prefs.getBoolean(SHOULD_SHOW_ADS_KEY, true);
    }
}
