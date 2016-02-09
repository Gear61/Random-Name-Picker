package com.randomappsinc.studentpicker.Misc;

/**
 * Created by alexanderchiou on 7/19/15.
 */

import android.content.Context;
import android.content.SharedPreferences;

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
        Context context = MyApplication.get().getApplicationContext();
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

    public void removeNameList(String newList) {
        Set<String> currentLists = getNameLists();
        currentLists.remove(newList);
        setNameLists(currentLists);
    }

    public void renameList(String oldName, String newName) {
        removeNameList(oldName);
        addNameList(newName);
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

    public void cacheNameChoosingList(String listName, List<String> names, List<String> alreadyChosenNames) {
        prefs.edit().putString(listName, JSONUtils.serializeNameList(names, alreadyChosenNames)).apply();
    }

    public List<String> getCachedNameList(String listName) {
        return JSONUtils.extractNames(prefs.getString(listName, ""));
    }

    public List<String> getAlreadyChosenNames(String listName) {
        return JSONUtils.extractAlreadyChosenNames(prefs.getString(listName, ""));
    }

    public void moveNamesListCache(String oldListName, String newListName) {
        String cache = prefs.getString(oldListName, "");
        removeNamesListCache(oldListName);
        prefs.edit().putString(newListName, cache).apply();
    }

    public void removeNamesListCache(String listName) {
        prefs.edit().remove(listName).apply();
    }
}
