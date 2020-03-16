package com.randomappsinc.studentpicker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.models.NameDO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    public DataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    // Open connection to database
    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Terminate connection to database
    private void close() {
        dbHelper.close();
    }

    public String getListName(int listId) {
        open();
        String[] columns = {MySQLiteHelper.COLUMN_LIST_NAME};
        String selection = MySQLiteHelper.COLUMN_ID + " = ?";
        String[] whereArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(MySQLiteHelper.LISTS_TABLE_NAME, columns, selection,
                whereArgs, null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getString(0);
        }
        cursor.close();
        close();
        return "";
    }

    public List<ListDO> getNameLists() {
        List<ListDO> lists = new ArrayList<>();
        open();
        String[] columns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_LIST_NAME};
        Cursor cursor = database.query(MySQLiteHelper.LISTS_TABLE_NAME, columns, null,
                null, null, null, null);
        while (cursor.moveToNext()) {
            lists.add(new ListDO(cursor.getInt(0), cursor.getString(1)));
        }
        cursor.close();
        close();
        return lists;
    }

    public ListDO addNameList(String newListName) {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LIST_NAME, newListName);
        int result = (int) database.insert(MySQLiteHelper.LISTS_TABLE_NAME, null, values);
        close();
        return new ListDO(result, newListName);
    }

    public void deleteList(int listId) {
        open();
        String[] whereArgs = {String.valueOf(listId)};

        // Delete the list
        database.delete(
                MySQLiteHelper.LISTS_TABLE_NAME,
                MySQLiteHelper.COLUMN_ID + " = ?",
                whereArgs);

        // Delete the names in the list
        database.delete(
                MySQLiteHelper.NAMES_TABLE_NAME,
                MySQLiteHelper.COLUMN_LIST_ID + " = ?",
                whereArgs);

        close();
    }

    public void renameList(ListDO updatedList) {
        open();
        ContentValues newValues = new ContentValues();
        newValues.put(MySQLiteHelper.COLUMN_LIST_NAME, updatedList.getName());
        String[] whereArgs = new String[] {String.valueOf(updatedList.getId())};
        String whereStatement = MySQLiteHelper.COLUMN_ID + " = ?";
        database.update(MySQLiteHelper.LISTS_TABLE_NAME, newValues, whereStatement, whereArgs);
        close();
    }

    public ListInfo getListInfo(int listId) {
        Map<String, NameDO> nameAmounts = new HashMap<>();
        List<String> names = new ArrayList<>();
        int amount = 0;
        open();
        String[] columns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_NAME_COUNT};
        String selection = MySQLiteHelper.COLUMN_LIST_ID + " = ?";
        String[] selectionArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(MySQLiteHelper.NAMES_TABLE_NAME, columns, selection,
                selectionArgs, null, null, MySQLiteHelper.COLUMN_NAME + " ASC");
        while (cursor.moveToNext()) {
            int nameId = cursor.getInt(0);
            String name = cursor.getString(1);
            int nameAmount = cursor.getInt(2);

            nameAmounts.put(cursor.getString(1), new NameDO(nameId, name, nameAmount));
            names.add(name);
            amount += nameAmount;
        }
        cursor.close();
        close();
        return new ListInfo(nameAmounts, names, amount, new ArrayList<>());
    }

    public void addNameIntoNewList(String name, int listId) {
        int currentAmount = getAmountOfName(name, listId);

        open();
        if (currentAmount == 0) {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_LIST_ID, listId);
            values.put(MySQLiteHelper.NAMES_TABLE_NAME, name);
            values.put(MySQLiteHelper.COLUMN_NAME_COUNT, 1);
            database.insert(MySQLiteHelper.NAMES_TABLE_NAME, null, values);
        } else {
            ContentValues newValues = new ContentValues();
            newValues.put(MySQLiteHelper.COLUMN_NAME_COUNT, currentAmount + 1);
            String[] whereArgs = new String[] {String.valueOf(listId), name};
            String whereStatement = MySQLiteHelper.COLUMN_LIST_ID
                    + " = ? AND "
                    + MySQLiteHelper.COLUMN_NAME + " = ?";
            database.update(MySQLiteHelper.NAMES_TABLE_NAME, newValues, whereStatement, whereArgs);
        }
        close();
    }

    public void addNames(String name, int listId, int amount) {
        int currentAmount = getAmountOfName(name, listId);

        open();
        if (currentAmount == 0) {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_LIST_ID, listId);
            values.put(MySQLiteHelper.COLUMN_NAME, name);
            values.put(MySQLiteHelper.COLUMN_NAME_COUNT, amount);
            database.insert(MySQLiteHelper.NAMES_TABLE_NAME, null, values);
        } else {
            ContentValues newValues = new ContentValues();
            newValues.put(MySQLiteHelper.COLUMN_NAME_COUNT, currentAmount + amount);
            String[] whereArgs = new String[]{name, String.valueOf(listId)};
            String whereStatement = MySQLiteHelper.COLUMN_NAME + " = ? AND "
                    + MySQLiteHelper.COLUMN_LIST_ID + " = ?";
            database.update(MySQLiteHelper.NAMES_TABLE_NAME, newValues, whereStatement, whereArgs);
        }
        close();
    }

    void removeNames(String name, int amount, int listId) {
        int currentAmount = getAmountOfName(name, listId);

        open();
        if (currentAmount <= amount) {
            String[] whereArgs = {name, String.valueOf(listId)};
            database.delete(MySQLiteHelper.NAMES_TABLE_NAME,
                    MySQLiteHelper.COLUMN_NAME
                            + " = ? AND "
                            + MySQLiteHelper.COLUMN_LIST_ID + " = ?",
                    whereArgs);
        } else {
            ContentValues newValues = new ContentValues();
            newValues.put(MySQLiteHelper.COLUMN_NAME_COUNT, currentAmount - amount);
            String[] whereArgs = new String[]{name, String.valueOf(listId)};
            String whereStatement = MySQLiteHelper.COLUMN_NAME
                    + " = ? AND "
                    + MySQLiteHelper.COLUMN_LIST_ID + " = ?";
            database.update(MySQLiteHelper.NAMES_TABLE_NAME, newValues, whereStatement, whereArgs);
        }
        close();
    }

    private int getAmount(String name, int listId) {
        open();
        String[] columns = {MySQLiteHelper.COLUMN_NAME_COUNT};
        String selection = MySQLiteHelper.COLUMN_NAME + " = ? AND "
                + MySQLiteHelper.COLUMN_LIST_ID + " = ?";
        String[] selectionArgs = {name, String.valueOf(listId)};
        Cursor cursor = database.query(MySQLiteHelper.NAMES_TABLE_NAME, columns, selection,
                selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        close();
        return 0;
    }

    private int getAmountOfName(String name, int listId) {
        open();
        String[] columns = {MySQLiteHelper.COLUMN_NAME_COUNT};
        String selection = MySQLiteHelper.COLUMN_NAME
                + " = ? AND "
                + MySQLiteHelper.COLUMN_LIST_ID + " = ?";
        String[] selectionArgs = {name, String.valueOf(listId)};
        Cursor cursor = database.query(MySQLiteHelper.NAMES_TABLE_NAME, columns, selection,
                selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        close();
        return 0;
    }

    public List<String> getMatchingNames(String prefix) {
        List<String> matchingNames = new ArrayList<>();
        open();
        Cursor cursor = database.rawQuery("SELECT DISTINCT " + MySQLiteHelper.COLUMN_PERSON_NAME_LEGACY +
                " FROM " + MySQLiteHelper.LEGACY_TABLE_NAME + " WHERE " +
                MySQLiteHelper.COLUMN_PERSON_NAME_LEGACY + " like ? COLLATE NOCASE " +
                "ORDER BY " + MySQLiteHelper.COLUMN_PERSON_NAME_LEGACY + " ASC", new String[] {prefix + "%"});
        while (cursor.moveToNext()) {
            matchingNames.add(cursor.getString(0));
        }
        cursor.close();
        close();
        return matchingNames;
    }

    void renamePeople(String oldName, String newName, int listId, int amount) {
        removeNames(oldName, listId, amount);
        addNames(newName, listId, amount);
    }
}
