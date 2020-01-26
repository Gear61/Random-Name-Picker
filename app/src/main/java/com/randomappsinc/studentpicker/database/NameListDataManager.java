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

    public interface AdjustAmountListener {
        void onNamesRemoved(String name, int amountToDelete);

        void onNamesAdded(String name, int amountToAdd);
    }

    private static NameListDataManager instance;
    private AdjustAmountListener adjustAmountListener;

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

    public void registerAmountListener(AdjustAmountListener adjustAmountListener) {
        this.adjustAmountListener = adjustAmountListener;
    }

    public void addName(Context context, String name, int amount, String listName) {
        DataSource dataSource = new DataSource(context);
        dataSource.addNames(name, listName, amount);
        for (Listener listener : listeners) {
            listener.onNameAdded(name, amount, listName);
        }
    }

    public void deleteName(Context context, String name, int amount, String listName) {
        DataSource dataSource = new DataSource(context);
        dataSource.removeNames(name, listName, amount);
        for (Listener listener : listeners) {
            listener.onNameDeleted(name, amount, listName);
        }
    }

    public void setNameAmount(String name, int amountToHave, int currentAmount, Context context, String listName) {
        if (currentAmount > amountToHave) {
            int amountToDelete = currentAmount - amountToHave;
            deleteName(context, name, amountToDelete, listName);
            adjustAmountListener.onNamesRemoved(name, amountToDelete);
        } else {
            int amountToAdd = amountToHave - currentAmount;
            addName(context, name, amountToAdd, listName);
            adjustAmountListener.onNamesAdded(name, amountToAdd);
        }
    }

    public void changeName(Context context, String oldName, String newName, int amount, String listName) {
        DataSource dataSource = new DataSource(context);
        dataSource.renamePeople(oldName, newName, listName, amount);
        for (Listener listener : listeners) {
            listener.onNameChanged(oldName, newName, amount, listName);
        }
    }

    public ListInfo importNameLists(Context context, String receivingList, List<String> givingLists) {
        DataSource dataSource = new DataSource(context);
        Map<String, Integer> change = dataSource.importNamesIntoList(receivingList, givingLists);
        for (Listener listener : listeners) {
            listener.onNameListsImported(change, receivingList);
        }
        return dataSource.getListInfo(receivingList);
    }
}
