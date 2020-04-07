package com.randomappsinc.studentpicker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
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

    public long getNumLists() {
        open();
        long numLists = DatabaseUtils.queryNumEntries(
                database, MySQLiteHelper.LISTS_TABLE_NAME, null, null);
        close();
        return numLists;
    }

    public List<ListDO> getNameLists(String searchTerm) {
        List<ListDO> lists = new ArrayList<>();
        open();
        Cursor cursor;
        if (TextUtils.isEmpty(searchTerm)) {
            String[] columns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_LIST_NAME};
            cursor = database.query(MySQLiteHelper.LISTS_TABLE_NAME, columns, null,
                    null, null, null, null);
        } else {
            cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.LISTS_TABLE_NAME + " WHERE " +
                    MySQLiteHelper.COLUMN_LIST_NAME + " like ? COLLATE NOCASE",
                    new String[] {searchTerm + "%"});
        }
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

    public List<NameDO> getNamesInList(int listId) {
        List<NameDO> nameDOs = new ArrayList<>();
        open();
        String[] columns = {
                MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_NAME_COUNT};
        String selection = MySQLiteHelper.COLUMN_LIST_ID + " = ?";
        String[] selectionArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(MySQLiteHelper.NAMES_TABLE_NAME, columns, selection,
                selectionArgs, null, null, MySQLiteHelper.COLUMN_NAME + " ASC");
        while (cursor.moveToNext()) {
            int nameId = cursor.getInt(0);
            String name = cursor.getString(1);
            int nameAmount = cursor.getInt(2);

            nameDOs.add(new NameDO(nameId, name, nameAmount));
        }
        cursor.close();
        close();
        return nameDOs;
    }

    public ListInfo getListInfo(int listId) {
        Map<String, Integer> nameAmounts = new HashMap<>();
        List<String> names = new ArrayList<>();
        int totalAmountOfNames = 0;
        open();
        String[] columns = {MySQLiteHelper.COLUMN_NAME, MySQLiteHelper.COLUMN_NAME_COUNT};
        String selection = MySQLiteHelper.COLUMN_LIST_ID + " = ?";
        String[] selectionArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(MySQLiteHelper.NAMES_TABLE_NAME, columns, selection,
                selectionArgs, null, null, MySQLiteHelper.COLUMN_NAME + " ASC");
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            int nameAmount = cursor.getInt(1);

            nameAmounts.put(name, nameAmount);
            names.add(name);
            totalAmountOfNames += nameAmount;
        }
        cursor.close();
        close();
        return new ListInfo(nameAmounts, names, totalAmountOfNames, new ArrayList<>());
    }

    public void addNames(String name, int amount, int listId) {
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
        Cursor cursor = database.rawQuery("SELECT DISTINCT " + MySQLiteHelper.COLUMN_NAME +
                " FROM " + MySQLiteHelper.NAMES_TABLE_NAME + " WHERE " +
                MySQLiteHelper.COLUMN_NAME + " like ? COLLATE NOCASE " +
                "ORDER BY " + MySQLiteHelper.COLUMN_NAME + " ASC", new String[] {prefix + "%"});
        while (cursor.moveToNext()) {
            matchingNames.add(cursor.getString(0));
        }
        cursor.close();
        close();
        return matchingNames;
    }

    void renamePeople(String oldName, String newName, int listId, int amount) {
        removeNames(oldName, amount, listId);
        addNames(newName, amount, listId);
    }

    public ChoosingSettings getChoosingSettings(int listId) {
        open();
        ChoosingSettings choosingSettings = new ChoosingSettings();

        open();
        String[] columns = {
                MySQLiteHelper.COLUMN_PRESENTATION_MODE,
                MySQLiteHelper.COLUMN_WITH_REPLACEMENT,
                MySQLiteHelper.COLUMN_AUTOMATIC_TTS,
                MySQLiteHelper.COLUMN_SHOW_AS_LIST,
                MySQLiteHelper.COLUMN_NUM_NAMES_CHOSEN
        };
        String selection = MySQLiteHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(MySQLiteHelper.LISTS_TABLE_NAME, columns, selection,
                selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            choosingSettings.setPresentationMode(cursor.getInt(0) != 0);
            choosingSettings.setWithReplacement(cursor.getInt(1) != 0);
            choosingSettings.setAutomaticTts(cursor.getInt(2) != 0);
            choosingSettings.setShowAsList(cursor.getInt(3) != 0);
            choosingSettings.setNumNamesToChoose(cursor.getInt(4));
        }
        cursor.close();
        close();

        return choosingSettings;
    }

    public void saveNameListState(int listId, ChoosingSettings choosingSettings) {
        open();



        ContentValues newValues = new ContentValues();
        newValues.put(
                MySQLiteHelper.COLUMN_PRESENTATION_MODE,
                choosingSettings.isPresentationModeEnabled() ? 1 : 0);
        newValues.put(
                MySQLiteHelper.COLUMN_WITH_REPLACEMENT,
                choosingSettings.getWithReplacement() ? 1 : 0);
        newValues.put(
                MySQLiteHelper.COLUMN_AUTOMATIC_TTS,
                choosingSettings.getAutomaticTts() ? 1 : 0);
        newValues.put(
                MySQLiteHelper.COLUMN_SHOW_AS_LIST,
                choosingSettings.getShowAsList() ? 1 : 0);
        newValues.put(
                MySQLiteHelper.COLUMN_NUM_NAMES_CHOSEN,
                choosingSettings.getNumNamesToChoose());
        String[] whereArgs = new String[]{String.valueOf(listId)};
        String whereStatement = MySQLiteHelper.COLUMN_ID + " = ?";
        database.update(MySQLiteHelper.LISTS_TABLE_NAME, newValues, whereStatement, whereArgs);

        close();
    }
}
