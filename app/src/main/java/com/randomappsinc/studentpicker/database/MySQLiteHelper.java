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

    private static final String DATABASE_NAME = "studentpicker.db";
    private static final int DATABASE_VERSION = 9;

    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(TableCreationScripts.CREATE_LISTS_TABLE_QUERY);
        database.execSQL(TableCreationScripts.CREATE_NAMES_TABLE_QUERY);
        database.execSQL(TableCreationScripts.CREATE_NAMES_IN_LIST_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            List<String> nonEmptyLists = getNonEmptyLists(database);

            // List name -> (NameDO -> Count)
            Map<String, Map<String, Integer>> oldData = new HashMap<>();
            for (String listName : nonEmptyLists) {
                oldData.put(listName, getNameCountsLegacy(database, listName));
            }

            safelyAddColumnToTable(
                    database, DatabaseColumns.NAME, "INTEGER", DatabaseTables.LEGACY_TABLE_NAME);
            database.execSQL("DELETE FROM " + DatabaseTables.LEGACY_TABLE_NAME);

            for (String listName : oldData.keySet()) {
                Map<String, Integer> listData = oldData.get(listName);
                for (String name : listData.keySet()) {
                    ContentValues values = new ContentValues();
                    values.put(DatabaseColumns.LIST_NAME, listName);
                    values.put(DatabaseColumns.PERSON_NAME_LEGACY, name);
                    values.put(DatabaseColumns.NAME_COUNT, listData.get(name));
                    database.insert(DatabaseTables.LEGACY_TABLE_NAME, null, values);
                }
            }
            oldVersion++;
        }

        // Convert to list + names schemas
        if (oldVersion == 2) {
            database.execSQL(TableCreationScripts.CREATE_LISTS_TABLE_QUERY);
            database.execSQL(TableCreationScripts.CREATE_NAMES_TABLE_QUERY);
            oldVersion++;
        }

        // Store settings in DB
        if (oldVersion == 3) {
            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.PRESENTATION_MODE,
                    "BOOLEAN NOT NULL DEFAULT 0",
                    DatabaseTables.LISTS);

            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.WITH_REPLACEMENT,
                    "BOOLEAN NOT NULL DEFAULT 0",
                    DatabaseTables.LISTS);

            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.AUTOMATIC_TTS,
                    "BOOLEAN NOT NULL DEFAULT 0",
                    DatabaseTables.LISTS);

            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.SHOW_AS_LIST,
                    "BOOLEAN NOT NULL DEFAULT 0",
                    DatabaseTables.LISTS);

            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.NUM_NAMES_CHOSEN,
                    "INTEGER DEFAULT 1",
                    DatabaseTables.LISTS);

            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.NAMES_HISTORY,
                    "TEXT",
                    DatabaseTables.LISTS);

            database.execSQL(TableCreationScripts.CREATE_NAMES_IN_LIST_TABLE_QUERY);
            oldVersion++;
        }

        // Add choosing message customization
        if (oldVersion == 4) {
            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.CHOOSING_MESSAGE,
                    "TEXT",
                    DatabaseTables.LISTS);
            oldVersion++;
        }

        // Add speech language customization
        if (oldVersion == 5) {
            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.SPEECH_LANGUAGE,
                    "INTEGER DEFAULT -1",
                    DatabaseTables.LISTS);
            oldVersion++;
        }

        // Add speech language customization
        if (oldVersion == 6) {
            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.PREVENT_DUPLICATES,
                    "BOOLEAN NOT NULL DEFAULT 0",
                    DatabaseTables.LISTS);
            oldVersion++;
        }

        // Add photo attachment
        if (oldVersion == 7) {
            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.PHOTO_URI,
                    "TEXT",
                    DatabaseTables.NAMES);
            oldVersion++;
        }

        // Store grouping settings in DB
        if (oldVersion == 8) {
            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.NAMES_PER_GROUP,
                    "INTEGER DEFAULT 2",
                    DatabaseTables.NAMES);

            safelyAddColumnToTable(
                    database,
                    DatabaseColumns.NUMBER_OF_GROUPS,
                    "INTEGER DEFAULT 1",
                    DatabaseTables.NAMES);
        }
    }

    // Checks to see if the given column exists in the given table and adds it if it doesn't exist
    private void safelyAddColumnToTable(
            SQLiteDatabase database, String fieldName, String fieldType, String tableName) {
        boolean doesColumnNotExist = true;
        Cursor cursor = database.rawQuery("PRAGMA table_info(" + tableName + ")",null);
        cursor.moveToFirst();
        do {
            String currentColumn = cursor.getString(1);
            if (currentColumn.equals(fieldName)) {
                doesColumnNotExist = false;
            }
        } while (cursor.moveToNext());
        cursor.close();

        if (doesColumnNotExist) {
            String addColumnQuery = "ALTER TABLE " + tableName +
                    " ADD COLUMN " + fieldName +
                    " " + fieldType + ";";
            database.execSQL(addColumnQuery);
        }
    }

    // V1 -> V2 upgrade, get list names
    private List<String> getNonEmptyLists(SQLiteDatabase database) {
        List<String> lists = new ArrayList<>();
        String[] columns = {DatabaseColumns.LIST_NAME};
        Cursor cursor = database.query(
                true,
                DatabaseTables.LEGACY_TABLE_NAME,
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
        Cursor cursor = database.rawQuery("SELECT " + DatabaseColumns.PERSON_NAME_LEGACY +
                ", COUNT() FROM " + DatabaseTables.LEGACY_TABLE_NAME + " WHERE " +
                DatabaseColumns.LIST_NAME + " = ? " +
                "GROUP BY " + DatabaseColumns.PERSON_NAME_LEGACY, new String[] {listName});
        while (cursor.moveToNext()) {
            names.put(cursor.getString(0), cursor.getInt(1));
        }
        cursor.close();
        return names;
    }
}
