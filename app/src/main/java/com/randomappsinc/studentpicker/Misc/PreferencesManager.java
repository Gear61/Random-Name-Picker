package com.randomappsinc.studentpicker.Misc;

/**
 * Created by alexanderchiou on 7/19/15.
 */

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by alexanderchiou on 7/14/15.
 */
public class PreferencesManager
{
    private Context context;
    private SharedPreferences prefs;

    private static final String PREFS_KEY = "com.randomappsinc.studentpicker";
    private static final String STUDENT_LISTS_KEY = "com.randomappsinc.studentpicker.studentLists";
    private static PreferencesManager instance;

    public static PreferencesManager get()
    {
        if (instance == null)
        {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized PreferencesManager getSync()
    {
        if (instance == null)
        {
            instance = new PreferencesManager();
        }
        return instance;
    }

    private PreferencesManager()
    {
        this.context = Application.get().getApplicationContext();
        prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE);
    }

    public Set<String> getStudentLists()
    {
        return prefs.getStringSet(STUDENT_LISTS_KEY, new HashSet<String>());
    }

    public void setStudentsList(Set<String> studentLists)
    {
        prefs.edit().putStringSet(STUDENT_LISTS_KEY, studentLists).commit();
    }
}

