package com.randomappsinc.studentpicker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Table name
    public static final String TABLE_NAME = "Students";

    // COLUMNS
    public static final String COLUMN_LIST_NAME = "list_name";
    public static final String COLUMN_PERSON_NAME = "student_name";
    public static final String COLUMN_NAME_COUNT = "name_count";

    // Some random things fed to a super's method
    private static final String DATABASE_NAME = "studentpicker.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statements
    private static final String STUDENTS_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + COLUMN_LIST_NAME
            + " TEXT, " + COLUMN_PERSON_NAME + " TEXT, " + COLUMN_NAME_COUNT + " INTEGER);";

    // Updates
    // V2
    private static final String ADD_COUNT_COLUMN = "ALTER TABLE " + TABLE_NAME +
            " ADD COLUMN " + COLUMN_NAME_COUNT + " INTEGER;";

    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(STUDENTS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            List<String> nonEmptyLists = getNonEmptyLists(database);

            // List name -> (Name -> Count)
            Map<String, Map<String, Integer>> oldData = new HashMap<>();
            for (String listName : nonEmptyLists) {
                oldData.put(listName, getNameCountsLegacy(database, listName));
            }

            database.execSQL(ADD_COUNT_COLUMN);
            database.execSQL("DELETE FROM " + TABLE_NAME);

            for (String listName : oldData.keySet()) {
                Map<String, Integer> listData = oldData.get(listName);
                for (String name : listData.keySet()) {
                    ContentValues values = new ContentValues();
                    values.put(MySQLiteHelper.COLUMN_LIST_NAME, listName);
                    values.put(MySQLiteHelper.COLUMN_PERSON_NAME, name);
                    values.put(MySQLiteHelper.COLUMN_NAME_COUNT, listData.get(name));
                    database.insert(MySQLiteHelper.TABLE_NAME, null, values);
                }
            }
        }
    }

    // V1 -> V2 upgrade, get list names
    private List<String> getNonEmptyLists(SQLiteDatabase database) {
        List<String> lists = new ArrayList<>();
        String[] columns = {MySQLiteHelper.COLUMN_LIST_NAME};
        Cursor cursor = database.query(
                true,
                MySQLiteHelper.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                null,
                null);
        while (cursor.moveToNext()) {
            lists.add(cursor.getString(0));
        }
        cursor.close();
        return lists;
    }

    // V1 -> V2 upgrade, save legacy data
    private Map<String, Integer> getNameCountsLegacy(SQLiteDatabase database, String listName) {
        Map<String, Integer> names = new HashMap<>();
        Cursor cursor = database.rawQuery("SELECT " + MySQLiteHelper.COLUMN_PERSON_NAME +
                ", COUNT() FROM " + MySQLiteHelper.TABLE_NAME + " WHERE " +
                MySQLiteHelper.COLUMN_LIST_NAME + " = ? " +
                "GROUP BY " + MySQLiteHelper.COLUMN_PERSON_NAME, new String[] {listName});
        while (cursor.moveToNext()) {
            names.put(cursor.getString(0), cursor.getInt(1));
        }
        cursor.close();
        return names;
    }
}
