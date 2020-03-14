package com.randomappsinc.studentpicker.database;

import android.content.Context;

import com.randomappsinc.studentpicker.models.ListInfo;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Singleton to help maintain the state of the name choosing page */
public class NameListDataManager {

    public interface Listener {
        void onNameAdded(String name, int amount, String listName);

        void onNameDeleted(String name, int amount, String listName);

        void onNameChanged(String oldName, String newName, int amount, String listName);

        void onNameListsImported(Map<String, Integer> nameAmounts, String listName);
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

    public int addName(Context context, String name, int amount, String listName) {
        DataSource dataSource = new DataSource(context);
        // TODO: Bring this back!!!
        // dataSource.addNameIntoNewList(name, listName, amount);
        for (Listener listener : listeners) {
            listener.onNameAdded(name, amount, listName);
        }
        return 1;
    }

    public void deleteName(Context context, String name, int amount, String listName) {
        DataSource dataSource = new DataSource(context);
        // TODO: Bring this back!!!
        // dataSource.removeNames(name, listName, amount);
        for (Listener listener : listeners) {
            listener.onNameDeleted(name, amount, listName);
        }
    }

    public void changeName(Context context, String oldName, String newName, int amount, String listName) {
        DataSource dataSource = new DataSource(context);
        // TODO: Bring this back!!!
        // dataSource.renamePeople(oldName, newName, listName, amount);
        for (Listener listener : listeners) {
            listener.onNameChanged(oldName, newName, amount, listName);
        }
    }

    public ListInfo importNameLists(Context context, int receivingListId, List<Integer> givingLists) {
        DataSource dataSource = new DataSource(context);
        // TODO: Bring this back!!!
        Map<String, Integer> change = dataSource.importNamesIntoList(receivingListId, givingLists);
        for (Listener listener : listeners) {
            // listener.onNameListsImported(change, receivingListId);
        }
        // TODO: Use real ID
        return dataSource.getListInfo(1);
    }
}
