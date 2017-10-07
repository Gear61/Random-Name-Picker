package com.randomappsinc.studentpicker.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.randomappsinc.studentpicker.models.ListInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    // Constructor
    public DataSource() {
        dbHelper = new MySQLiteHelper();
    }

    // Open connection to database
    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Terminate connection to database
    private void close() {
        dbHelper.close();
    }

    public ListInfo getListInfo(String listName) {
        Map<String, Integer> nameAmounts = new HashMap<>();
        List<String> names = new ArrayList<>();
        int amount = 0;
        open();
        String[] columns = {MySQLiteHelper.COLUMN_PERSON_NAME, MySQLiteHelper.COLUMN_NAME_COUNT};
        String selection = MySQLiteHelper.COLUMN_LIST_NAME + " = ?";
        String[] selectionArgs = {listName};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, columns, selection,
                selectionArgs, null, null, MySQLiteHelper.COLUMN_PERSON_NAME + " ASC");
        while (cursor.moveToNext()) {
            nameAmounts.put(cursor.getString(0), cursor.getInt(1));
            names.add(cursor.getString(0));
            amount += cursor.getInt(1);
        }
        cursor.close();
        close();
        return new ListInfo(nameAmounts, names, amount, new ArrayList<String>());
    }

    public void addNames(String name, String listName, int amount) {
        int currentAmount = getAmount(listName, name);

        open();
        if (currentAmount == 0) {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_LIST_NAME, listName);
            values.put(MySQLiteHelper.COLUMN_PERSON_NAME, name);
            values.put(MySQLiteHelper.COLUMN_NAME_COUNT, amount);
            database.insert(MySQLiteHelper.TABLE_NAME, null, values);
        } else {
            ContentValues newValues = new ContentValues();
            newValues.put(MySQLiteHelper.COLUMN_NAME_COUNT, currentAmount + amount);
            String[] whereArgs = new String[]{listName, name};
            String whereStatement = MySQLiteHelper.COLUMN_LIST_NAME + " = ? AND " + MySQLiteHelper.COLUMN_PERSON_NAME + " = ?";
            database.update(MySQLiteHelper.TABLE_NAME, newValues, whereStatement, whereArgs);
        }
        close();
    }

    public void removeNames(String name, String listName, int amount) {
        int currentAmount = getAmount(listName, name);

        open();
        if (currentAmount <= amount) {
            String whereArgs[] = {listName, name};
            database.delete(MySQLiteHelper.TABLE_NAME,
                    MySQLiteHelper.COLUMN_LIST_NAME + " = ? AND " + MySQLiteHelper.COLUMN_PERSON_NAME + " = ?",
                    whereArgs);
        } else {
            ContentValues newValues = new ContentValues();
            newValues.put(MySQLiteHelper.COLUMN_NAME_COUNT, currentAmount - amount);
            String[] whereArgs = new String[]{listName, name};
            String whereStatement = MySQLiteHelper.COLUMN_LIST_NAME + " = ? AND " + MySQLiteHelper.COLUMN_PERSON_NAME + " = ?";
            database.update(MySQLiteHelper.TABLE_NAME, newValues, whereStatement, whereArgs);
        }
        close();
    }

    private int getAmount(String listName, String name) {
        open();
        String[] columns = {MySQLiteHelper.COLUMN_NAME_COUNT};
        String selection = MySQLiteHelper.COLUMN_LIST_NAME + " = ? AND " + MySQLiteHelper.COLUMN_PERSON_NAME + " = ?";
        String[] selectionArgs = {listName, name};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, columns, selection,
                selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        close();
        return 0;
    }

    public void deleteList(String listName) {
        open();
        String whereArgs[] = {listName};
        database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_LIST_NAME + " = ?", whereArgs);
        close();
    }

    public void renameList(String oldListName, String newListName) {
        open();
        ContentValues newValues = new ContentValues();
        newValues.put(MySQLiteHelper.COLUMN_LIST_NAME, newListName);
        String[] whereArgs = new String[]{oldListName};
        String whereStatement = MySQLiteHelper.COLUMN_LIST_NAME + " = ?";
        database.update(MySQLiteHelper.TABLE_NAME, newValues, whereStatement, whereArgs);
        close();
    }

    public List<String> getMatchingNames(String prefix) {
        List<String> matchingNames = new ArrayList<>();
        open();
        Cursor cursor = database.rawQuery("SELECT DISTINCT " + MySQLiteHelper.COLUMN_PERSON_NAME +
                " FROM " + MySQLiteHelper.TABLE_NAME + " WHERE " +
                MySQLiteHelper.COLUMN_PERSON_NAME + " like ? COLLATE NOCASE " +
                "ORDER BY " + MySQLiteHelper.COLUMN_PERSON_NAME + " ASC", new String[] {prefix + "%"});
        while (cursor.moveToNext()) {
            matchingNames.add(cursor.getString(0));
        }
        cursor.close();
        close();
        return matchingNames;
    }

    public String[] getAllNameLists(String currentList) {
        List<String> names = new ArrayList<>();
        open();
        String[] columns = {MySQLiteHelper.COLUMN_LIST_NAME};
        Cursor cursor = database.query(true, MySQLiteHelper.TABLE_NAME, columns, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (!cursor.getString(0).equals(currentList)) {
                names.add(cursor.getString(0));
            }
        }
        cursor.close();
        close();
        return names.toArray(new String[names.size()]);
    }

    public Map<String, Integer> importNamesIntoList(String receivingList, List<String> givingLists) {
        Map<String, Integer> nameAmounts = new HashMap<>();
        for (String listName : givingLists) {
            Map<String, Integer> namesToImport = getListInfo(listName).getNameAmounts();
            for (String name : namesToImport.keySet()) {
                addNames(name, receivingList, namesToImport.get(name));
                if (nameAmounts.containsKey(name)) {
                    int currentAmount = nameAmounts.get(name);
                    nameAmounts.put(name, currentAmount + namesToImport.get(name));
                } else {
                    nameAmounts.put(name, namesToImport.get(name));
                }
            }
        }
        return nameAmounts;
    }

    public void renamePeople(String oldName, String newName, String listName, int amount) {
        removeNames(oldName, listName, amount);
        addNames(newName, listName, amount);
    }
}
