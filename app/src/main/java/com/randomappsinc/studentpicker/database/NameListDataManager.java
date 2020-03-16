package com.randomappsinc.studentpicker.database;

import android.content.Context;

import java.util.HashSet;
import java.util.Set;

/** Singleton to help maintain the state of the name choosing page */
public class NameListDataManager {

    public interface Listener {
        void onNameAdded(String name, int amount, int listId);

        void onNameDeleted(String name, int amount, int listId);

        void onNameChanged(String oldName, String newName, int amount, int listId);
    }

    private static NameListDataManager instance;

    public static NameListDataManager get() {
        if (instance == null) {
            instance = getSync();
        }
        return instance;
    }

    private static synchronized NameListDataManager getSync() {
        if (instance == null) {
            instance = new NameListDataManager();
        }
        return instance;
    }

    private Set<Listener> listeners = new HashSet<>();

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }

    public void addName(Context context, String name, int amount, int listId) {
        DataSource dataSource = new DataSource(context);
        dataSource.addNames(name, amount, listId);
        for (Listener listener : listeners) {
            listener.onNameAdded(name, amount, listId);
        }
    }

    public void deleteName(Context context, String name, int amount, int listId) {
        DataSource dataSource = new DataSource(context);
        dataSource.removeNames(name, amount, listId);
        for (Listener listener : listeners) {
            listener.onNameDeleted(name, amount, listId);
        }
    }

    public void changeName(Context context, String oldName, String newName, int amount, int listId) {
        DataSource dataSource = new DataSource(context);
        dataSource.renamePeople(oldName, newName, listId, amount);
        for (Listener listener : listeners) {
            listener.onNameChanged(oldName, newName, amount, listId);
        }
    }
}
