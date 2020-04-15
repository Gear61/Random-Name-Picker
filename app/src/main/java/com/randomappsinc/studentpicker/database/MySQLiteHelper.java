package com.randomappsinc.studentpicker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.init.MyApplication;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.utils.JSONUtils;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Core fields
    private static final String DATABASE_NAME = "studentpicker.db";
    private static final int DATABASE_VERSION = 4;

    // V2 - Introduce concept of amount
    private static final String ADD_COUNT_COLUMN = "ALTER TABLE " + DatabaseTables.LEGACY_TABLE_NAME +
            " ADD COLUMN " + DatabaseColumns.NAME_COUNT + " INTEGER;";

    // V3 - Migrate to lists to names schema
    private static final String CREATE_LISTS_TABLE_OLD = "CREATE TABLE IF NOT EXISTS "
            + DatabaseTables.LISTS + "("
            + DatabaseColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DatabaseColumns.LIST_NAME + " TEXT);";

    private static final String CREATE_NAMES_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + DatabaseTables.NAMES +
            "(" + DatabaseColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + DatabaseColumns.LIST_ID + " INTEGER, " +
            DatabaseColumns.NAME + " TEXT, " + DatabaseColumns.NAME_COUNT + " INTEGER);";

    // V4 - Migrate name choosing state
    private static final String CREATE_LISTS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + DatabaseTables.LISTS + "("
            + DatabaseColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DatabaseColumns.LIST_NAME + " TEXT, "
            + DatabaseColumns.PRESENTATION_MODE + " BOOLEAN NOT NULL DEFAULT 0, "
            + DatabaseColumns.WITH_REPLACEMENT + " BOOLEAN NOT NULL DEFAULT 0, "
            + DatabaseColumns.AUTOMATIC_TTS + " BOOLEAN NOT NULL DEFAULT 0, "
            + DatabaseColumns.SHOW_AS_LIST + " BOOLEAN NOT NULL DEFAULT 0, "
            + DatabaseColumns.NUM_NAMES_CHOSEN + " INTEGER DEFAULT 1, "
            + DatabaseColumns.NAMES_HISTORY + " TEXT);";

    private static final String ADD_PRESENTATION = "ALTER TABLE " + DatabaseTables.LISTS
            + " ADD COLUMN " + DatabaseColumns.PRESENTATION_MODE + " BOOLEAN NOT NULL DEFAULT 0;";

    private static final String ADD_WITH_REPLACEMENT = "ALTER TABLE " + DatabaseTables.LISTS
            + " ADD COLUMN " + DatabaseColumns.WITH_REPLACEMENT + " BOOLEAN NOT NULL DEFAULT 0;";

    private static final String ADD_AUTOMATIC_TTS = "ALTER TABLE " + DatabaseTables.LISTS
            + " ADD COLUMN " + DatabaseColumns.AUTOMATIC_TTS + " BOOLEAN NOT NULL DEFAULT 0;";

    private static final String ADD_SHOW_AS_LIST = "ALTER TABLE " + DatabaseTables.LISTS
            + " ADD COLUMN " + DatabaseColumns.SHOW_AS_LIST + " BOOLEAN NOT NULL DEFAULT 0;";

    private static final String ADD_NUM_NAMES_CHOSEN = "ALTER TABLE " + DatabaseTables.LISTS
            + " ADD COLUMN " + DatabaseColumns.NUM_NAMES_CHOSEN + " INTEGER DEFAULT 1;";

    private static final String ADD_NAMES_HISTORY = "ALTER TABLE " + DatabaseTables.LISTS
            + " ADD COLUMN " + DatabaseColumns.NAMES_HISTORY + " TEXT;";

    private static final String CREATE_NAMES_IN_LIST_TABLE_QUERY =
            "CREATE TABLE IF NOT EXISTS " + DatabaseTables.NAMES_IN_LIST +  "("
                    + DatabaseColumns.LIST_ID + " INTEGER, "
                    + DatabaseColumns.NAME + " TEXT, "
                    + DatabaseColumns.NAME_COUNT + " INTEGER);";

    // Struct for migration
    private static class NameInfoPod {
        final String listName;
        public final String name;
        public final int amount;

        NameInfoPod(String listName, String name, int amount) {
            this.listName = listName;
            this.name = name;
            this.amount = amount;
        }
    }

    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_LISTS_TABLE_QUERY);
        database.execSQL(CREATE_NAMES_TABLE_QUERY);
        database.execSQL(CREATE_NAMES_IN_LIST_TABLE_QUERY);
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

            database.execSQL(ADD_COUNT_COLUMN);
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
            database.execSQL(CREATE_LISTS_TABLE_OLD);
            database.execSQL(CREATE_NAMES_TABLE_QUERY);

            PreferencesManager preferencesManager = new PreferencesManager(MyApplication.getAppContext());
            Set<String> listNames = preferencesManager.getNameLists();

            Map<String, Integer> listNamesToIdsMap = new HashMap<>();
            for (String listName : listNames) {
                ContentValues values = new ContentValues();
                values.put(DatabaseColumns.LIST_NAME, listName);
                int result = (int) database.insert(DatabaseTables.LISTS, null, values);
                listNamesToIdsMap.put(listName, result);
            }

            List<NameInfoPod> namesToMigrate = new ArrayList<>();
            Cursor nameInfoCursor = database.rawQuery(
                    "SELECT * FROM " + DatabaseTables.LEGACY_TABLE_NAME, null);
            if (nameInfoCursor.moveToFirst()){
                do {
                    String listName = nameInfoCursor.getString(
                            nameInfoCursor.getColumnIndex(DatabaseColumns.LIST_NAME));
                    String name = nameInfoCursor
                            .getString(nameInfoCursor.getColumnIndex(DatabaseColumns.PERSON_NAME_LEGACY));
                    int amount = nameInfoCursor.getInt(nameInfoCursor.getColumnIndex(DatabaseColumns.NAME_COUNT));
                    namesToMigrate.add(new NameInfoPod(listName, name, amount));
                } while (nameInfoCursor.moveToNext());
            }
            nameInfoCursor.close();

            for (NameInfoPod nameInfoPod : namesToMigrate) {
                ContentValues values = new ContentValues();
                values.put(DatabaseColumns.LIST_ID, listNamesToIdsMap.get(nameInfoPod.listName));
                values.put(DatabaseColumns.NAME, nameInfoPod.name);
                values.put(DatabaseColumns.NAME_COUNT, nameInfoPod.amount);
                database.insert(DatabaseTables.NAMES, null, values);
            }
            oldVersion++;
        }

        // Store settings in DB
        if (oldVersion == 3) {
            if (doesColumnNotExistInListsTable(database, DatabaseColumns.PRESENTATION_MODE)) {
                database.execSQL(ADD_PRESENTATION);
            }
            if (doesColumnNotExistInListsTable(database, DatabaseColumns.WITH_REPLACEMENT)) {
                database.execSQL(ADD_WITH_REPLACEMENT);
            }
            if (doesColumnNotExistInListsTable(database, DatabaseColumns.AUTOMATIC_TTS)) {
                database.execSQL(ADD_AUTOMATIC_TTS);
            }
            if (doesColumnNotExistInListsTable(database, DatabaseColumns.SHOW_AS_LIST)) {
                database.execSQL(ADD_SHOW_AS_LIST);
            }
            if (doesColumnNotExistInListsTable(database, DatabaseColumns.NUM_NAMES_CHOSEN)) {
                database.execSQL(ADD_NUM_NAMES_CHOSEN);
            }
            if (doesColumnNotExistInListsTable(database, DatabaseColumns.NAMES_HISTORY)) {
                database.execSQL(ADD_NAMES_HISTORY);
            }
            database.execSQL(CREATE_NAMES_IN_LIST_TABLE_QUERY);

            List<ListDO> listsToMigrate = new ArrayList<>();
            Cursor listInfoCursor = database.rawQuery(
                    "SELECT * FROM " + DatabaseTables.LISTS, null);
            if (listInfoCursor.moveToFirst()){
                do {
                    String listName = listInfoCursor.getString(
                            listInfoCursor.getColumnIndex(DatabaseColumns.LIST_NAME));
                    int listId = listInfoCursor
                            .getInt(listInfoCursor.getColumnIndex(DatabaseColumns.ID));
                    listsToMigrate.add(new ListDO(listId, listName));
                } while (listInfoCursor.moveToNext());
            }
            listInfoCursor.close();

            PreferencesManager preferencesManager = new PreferencesManager(MyApplication.getAppContext());
            for (ListDO listDO : listsToMigrate) {
                ListInfo listInfo = preferencesManager.getNameListState(listDO.getName());

                if (listInfo != null) {
                    Map<String, Integer> nameToAmount = listInfo.getNameAmounts();
                    for (String name : nameToAmount.keySet()) {
                        ContentValues values = new ContentValues();
                        values.put(DatabaseColumns.LIST_ID, listDO.getId());
                        values.put(DatabaseColumns.NAME, name);
                        values.put(DatabaseColumns.NAME_COUNT, nameToAmount.get(name));
                        database.insert(DatabaseTables.NAMES_IN_LIST, null, values);
                    }
                }

                ChoosingSettings choosingSettings = preferencesManager.getChoosingSettings(listDO.getName());

                if (choosingSettings != null) {
                    ContentValues newValues = new ContentValues();
                    newValues.put(
                            DatabaseColumns.PRESENTATION_MODE,
                            choosingSettings.isPresentationModeEnabled() ? 1 : 0);
                    newValues.put(
                            DatabaseColumns.WITH_REPLACEMENT,
                            choosingSettings.getWithReplacement() ? 1 : 0);
                    newValues.put(
                            DatabaseColumns.AUTOMATIC_TTS,
                            choosingSettings.getAutomaticTts() ? 1 : 0);
                    newValues.put(
                            DatabaseColumns.SHOW_AS_LIST,
                            choosingSettings.getShowAsList() ? 1 : 0);
                    newValues.put(
                            DatabaseColumns.NUM_NAMES_CHOSEN,
                            choosingSettings.getNumNamesToChoose());

                    if (listInfo != null) {
                        newValues.put(
                                DatabaseColumns.NAMES_HISTORY,
                                JSONUtils.namesArrayToJsonString(listInfo.getNameHistory()));
                    }

                    String[] whereArgs = new String[]{String.valueOf(listDO.getId())};
                    String whereStatement = DatabaseColumns.ID + " = ?";
                    database.update(DatabaseTables.LISTS, newValues, whereStatement, whereArgs);
                }
            }
        }
    }

    // This method will check if column exists in your table
    private boolean doesColumnNotExistInListsTable(SQLiteDatabase database, String fieldName) {
        boolean doesColumnNotExist = true;
        Cursor cursor = database.rawQuery("PRAGMA table_info(" + DatabaseTables.LISTS + ")",null);
        cursor.moveToFirst();
        do {
            String currentColumn = cursor.getString(1);
            if (currentColumn.equals(fieldName)) {
                doesColumnNotExist = false;
            }
        } while (cursor.moveToNext());
        cursor.close();
        return doesColumnNotExist;
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
