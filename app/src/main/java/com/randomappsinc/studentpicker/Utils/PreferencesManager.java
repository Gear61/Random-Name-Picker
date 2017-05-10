package com.randomappsinc.studentpicker.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.randomappsinc.studentpicker.Models.ChoosingSettings;
import com.randomappsinc.studentpicker.R;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by alexanderchiou on 7/14/15.
 */
public class PreferencesManager {
    private SharedPreferences prefs;

    private static final String PREFS_KEY = "com.randomappsinc.studentpicker";
    private static final String STUDENT_LISTS_KEY = "com.randomappsinc.studentpicker.studentLists";
    private static final String FIRST_TIME_KEY = "firstTime";
    private static final String NUM_APP_OPENS = "numAppOpens";
    private static final String PRESENTATION_TEXT_SIZE_KEY = "presentationTextSize";
    private static final String PRESENTATION_TEXT_COLOR_KEY = "presentationTextColor";
    private static final String SHAKE_IS_NEW = "shakeIsNew";

    private static PreferencesManager instance;

    public static PreferencesManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized PreferencesManager getSync() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }

    private PreferencesManager() {
        Context context = MyApplication.getAppContext();
        prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
    }

    public Set<String> getNameLists() {
        return prefs.getStringSet(STUDENT_LISTS_KEY, new HashSet<String>());
    }

    private void setNameLists(Set<String> studentLists) {
        prefs.edit().remove(STUDENT_LISTS_KEY).apply();
        prefs.edit().putStringSet(STUDENT_LISTS_KEY, studentLists).apply();
    }

    public void addNameList(String newList) {
        Set<String> currentLists = getNameLists();
        currentLists.add(newList);
        setNameLists(currentLists);
    }

    public void removeNameList(String deletedList) {
        Set<String> currentLists = getNameLists();
        currentLists.remove(deletedList);
        setNameLists(currentLists);
        removeNamesListCache(deletedList);
    }

    public void renameList(String oldName, String newName) {
        removeNameList(oldName);
        addNameList(newName);
        moveNamesListCache(oldName, newName);
    }

    public boolean doesListExist(String listName) {
        return getNameLists().contains(listName);
    }

    public boolean getFirstTimeUser()
    {
        return prefs.getBoolean(FIRST_TIME_KEY, true);
    }

    public void setFirstTimeUser(boolean firstTimeUser) {
        prefs.edit().putBoolean(FIRST_TIME_KEY, firstTimeUser).apply();
    }

    public void cacheNameChoosingList(String listName, List<String> names, List<String> alreadyChosenNames,
                                      ChoosingSettings settings) {
        String cache = JSONUtils.serializeChoosingState(names, alreadyChosenNames, settings);
        prefs.edit().putString(listName, cache).apply();
    }

    public List<String> getCachedNameList(String listName) {
        return JSONUtils.extractNames(prefs.getString(listName, ""));
    }

    public List<String> getAlreadyChosenNames(String listName) {
        return JSONUtils.extractAlreadyChosenNames(prefs.getString(listName, ""));
    }

    public ChoosingSettings getChoosingSetings(String listName) {
        return JSONUtils.extractChoosingSettings(prefs.getString(listName, ""));
    }

    public void moveNamesListCache(String oldListName, String newListName) {
        String cache = prefs.getString(oldListName, "");
        removeNamesListCache(oldListName);
        prefs.edit().putString(newListName, cache).apply();
    }

    public void removeNamesListCache(String listName) {
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

    public int getPresentationTextColor() {
        return prefs.getInt(PRESENTATION_TEXT_COLOR_KEY,
                MyApplication.getAppContext().getResources().getColor(R.color.dark_gray));
    }

    public void setPresentationTextColor(int newColor) {
        prefs.edit().putInt(PRESENTATION_TEXT_COLOR_KEY, newColor).apply();
    }

    public boolean shouldShowShake() {
        boolean shouldShowShake = prefs.getBoolean(SHAKE_IS_NEW, true);
        prefs.edit().putBoolean(SHAKE_IS_NEW, false).apply();
        return shouldShowShake;
    }
}
