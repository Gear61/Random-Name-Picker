package com.randomappsinc.studentpicker.Database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class DataSource {
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    // Constructor
    public DataSource() {
        dbHelper = new MySQLiteHelper();
    }

    // Open connection to database
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Terminate connection to database
    public void close() {
        dbHelper.close();
    }

    public List<String> getAllNamesInList(String listName) {
        List<String> names = new ArrayList<>();
        open();
        String[] columns = {MySQLiteHelper.COLUMN_PERSON_NAME};
        String selection = MySQLiteHelper.COLUMN_LIST_NAME + " = ?";
        String[] selectionArgs = {listName};
        Cursor cursor = database.query(MySQLiteHelper.PERSON_NAMES_TABLE_NAME, columns, selection,
                selectionArgs, null, null, null);
        while (cursor.moveToNext()) {
            names.add(cursor.getString(0));
        }
        cursor.close();
        close();
        return names;
    }

    public void addName(String name, String listName) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LIST_NAME, listName);
        values.put(MySQLiteHelper.COLUMN_PERSON_NAME, name);
        database.insert(MySQLiteHelper.PERSON_NAMES_TABLE_NAME, null, values);
        close();
    }

    public void removeName(String name, String listName) {
        open();
        long numInstances = getNumNamesInList(name, listName);
        String whereArgs[] = {name, listName};
        database.delete(MySQLiteHelper.PERSON_NAMES_TABLE_NAME, MySQLiteHelper.COLUMN_PERSON_NAME + " = ? AND " +
                MySQLiteHelper.COLUMN_LIST_NAME + " = ?", whereArgs);
        close();
        for (int i = 0; i < numInstances - 1; i++) {
            addName(name, listName);
        }
    }

    public void deleteList(String listName) {
        open();
        String whereArgs[] = {listName};
        database.delete(MySQLiteHelper.PERSON_NAMES_TABLE_NAME, MySQLiteHelper.COLUMN_LIST_NAME + " = ?", whereArgs);
        close();
    }

    public void renameList(String oldListName, String newListName) {
        open();
        ContentValues newValues = new ContentValues();
        newValues.put(MySQLiteHelper.COLUMN_LIST_NAME, newListName);
        String[] whereArgs = new String[]{oldListName};
        String whereStatement = MySQLiteHelper.COLUMN_LIST_NAME + "=?";
        database.update(MySQLiteHelper.PERSON_NAMES_TABLE_NAME, newValues, whereStatement, whereArgs);
        close();
    }

    public List<String> getMatchingNames(String prefix) {
        List<String> matchingNames = new ArrayList<>();
        open();
        Cursor cursor = database.rawQuery("SELECT DISTINCT " + MySQLiteHelper.COLUMN_PERSON_NAME +
                " FROM " + MySQLiteHelper.PERSON_NAMES_TABLE_NAME + " WHERE " +
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
        Cursor cursor = database.query(true, MySQLiteHelper.PERSON_NAMES_TABLE_NAME, columns, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            if (!cursor.getString(0).equals(currentList)) {
                names.add(cursor.getString(0));
            }
        }
        cursor.close();
        close();
        return names.toArray(new String[names.size()]);
    }

    public List<String> importNamesIntoList(String receivingList, List<String> givingLists) {
        List<String> newNames = new ArrayList<>();
        for (String listName : givingLists) {
            List<String> namesToImport = getAllNamesInList(listName);
            for (String name : namesToImport) {
                addName(name, receivingList);
                newNames.add(name);
            }
        }
        return newNames;
    }

    public long getNumNamesInList(String name, String listName) {
        return DatabaseUtils.queryNumEntries(database, MySQLiteHelper.PERSON_NAMES_TABLE_NAME,
                MySQLiteHelper.COLUMN_LIST_NAME + " = ? AND " + MySQLiteHelper.COLUMN_PERSON_NAME + " = ?",
                new String[] {listName, name});
    }

    public void renamePerson(String oldName, String newName, String listName) {
        removeName(oldName, listName);
        addName(newName, listName);
    }
}
