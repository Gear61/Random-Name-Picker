package com.randomappsinc.studentpicker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.randomappsinc.studentpicker.init.MyApplication;
import com.randomappsinc.studentpicker.utils.PreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // LEGACY fields (everything in 1 table)
    private static final String LEGACY_TABLE_NAME = "Students";
    private static final String COLUMN_PERSON_NAME_LEGACY = "student_name";

    // NEW table names
    static final String LISTS_TABLE_NAME = "Lists";
    static final String NAMES_TABLE_NAME = "Names";
    static final String NAMES_IN_LIST_TABLE_NAME = "NamesInList";

    // COLUMNS
    static final String COLUMN_ID = "id";
    static final String COLUMN_LIST_ID = "list_id";
    static final String COLUMN_LIST_NAME = "list_name";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_NAME_COUNT = "name_count";

    // Choosing settings columns
    static final String COLUMN_PRESENTATION_MODE = "presentation_mode";
    static final String COLUMN_WITH_REPLACEMENT = "with_replacement";
    static final String COLUMN_AUTOMATIC_TTS = "automatic_tts";
    static final String COLUMN_SHOW_AS_LIST = "show_as_list";
    static final String COLUMN_NUM_NAMES_CHOSEN = "num_names_chosen";
    static final String COLUMN_NAMES_HISTORY = "names_history";

    // Some random things fed to a super's method
    private static final String DATABASE_NAME = "studentpicker.db";
    private static final int DATABASE_VERSION = 4;

    // V2 - Introduce concept of amount
    private static final String ADD_COUNT_COLUMN = "ALTER TABLE " + LEGACY_TABLE_NAME +
            " ADD COLUMN " + COLUMN_NAME_COUNT + " INTEGER;";

    // V3 - Migrate to lists to names schema
    private static final String CREATE_LISTS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + LISTS_TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_LIST_NAME + " TEXT, "
            + COLUMN_PRESENTATION_MODE + " BOOLEAN NOT NULL DEFAULT 0 "
            + COLUMN_WITH_REPLACEMENT + " BOOLEAN NOT NULL DEFAULT 0 "
            + COLUMN_AUTOMATIC_TTS + " BOOLEAN NOT NULL DEFAULT 0 "
            + COLUMN_SHOW_AS_LIST + " BOOLEAN NOT NULL DEFAULT 0 "
            + COLUMN_NUM_NAMES_CHOSEN + " INTEGER "
            + COLUMN_NAMES_HISTORY + " TEXT);";

    private static final String CREATE_NAMES_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + NAMES_TABLE_NAME +
            "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_LIST_ID + " INTEGER, " +
            COLUMN_NAME + " TEXT, " + COLUMN_NAME_COUNT + " INTEGER);";

    // V4 - Migrate name choosing state
    private static final String ADD_PRESENTATION = "ALTER TABLE " + LISTS_TABLE_NAME
            + " ADD COLUMN " + COLUMN_PRESENTATION_MODE + " BOOLEAN NOT NULL DEFAULT 0;";

    private static final String ADD_WITH_REPLACEMENT = "ALTER TABLE " + LISTS_TABLE_NAME
            + " ADD COLUMN " + COLUMN_WITH_REPLACEMENT + " BOOLEAN NOT NULL DEFAULT 0;";

    private static final String ADD_AUTOMATIC_TTS = "ALTER TABLE " + LISTS_TABLE_NAME
            + " ADD COLUMN " + COLUMN_AUTOMATIC_TTS + " BOOLEAN NOT NULL DEFAULT 0;";

    private static final String ADD_SHOW_AS_LIST = "ALTER TABLE " + LISTS_TABLE_NAME
            + " ADD COLUMN " + COLUMN_SHOW_AS_LIST + " BOOLEAN NOT NULL DEFAULT 0;";

    private static final String ADD_NUM_NAMES_CHOSEN = "ALTER TABLE " + LISTS_TABLE_NAME
            + " ADD COLUMN " + COLUMN_NUM_NAMES_CHOSEN + " BOOLEAN NOT NULL DEFAULT 0;";

    private static final String ADD_NAMES_HISTORY = "ALTER TABLE " + LISTS_TABLE_NAME
            + " ADD COLUMN " + COLUMN_NAMES_HISTORY + " TEXT;";

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
            database.execSQL("DELETE FROM " + LEGACY_TABLE_NAME);

            for (String listName : oldData.keySet()) {
                Map<String, Integer> listData = oldData.get(listName);
                for (String name : listData.keySet()) {
                    ContentValues values = new ContentValues();
                    values.put(MySQLiteHelper.COLUMN_LIST_NAME, listName);
                    values.put(MySQLiteHelper.COLUMN_PERSON_NAME_LEGACY, name);
                    values.put(MySQLiteHelper.COLUMN_NAME_COUNT, listData.get(name));
                    database.insert(MySQLiteHelper.LEGACY_TABLE_NAME, null, values);
                }
            }
            oldVersion++;
        }

        // Convert to list + names schemas
        if (oldVersion == 2) {
            database.execSQL(CREATE_LISTS_TABLE_QUERY);
            database.execSQL(CREATE_NAMES_TABLE_QUERY);

            PreferencesManager preferencesManager = new PreferencesManager(MyApplication.getAppContext());
            Set<String> listNames = preferencesManager.getNameLists();

            Map<String, Integer> listNamesToIdsMap = new HashMap<>();
            for (String listName : listNames) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_LIST_NAME, listName);
                int result = (int) database.insert(LISTS_TABLE_NAME, null, values);
                listNamesToIdsMap.put(listName, result);
            }

            List<NameInfoPod> namesToMigrate = new ArrayList<>();
            Cursor nameInfoCursor = database.rawQuery(
                    "SELECT * FROM " + LEGACY_TABLE_NAME, null);
            if (nameInfoCursor.moveToFirst()){
                do {
                    String listName = nameInfoCursor.getString(
                            nameInfoCursor.getColumnIndex(COLUMN_LIST_NAME));
                    String name = nameInfoCursor
                            .getString(nameInfoCursor.getColumnIndex(COLUMN_PERSON_NAME_LEGACY));
                    int amount = nameInfoCursor.getInt(nameInfoCursor.getColumnIndex(COLUMN_NAME_COUNT));
                    namesToMigrate.add(new NameInfoPod(listName, name, amount));
                } while (nameInfoCursor.moveToNext());
            }
            nameInfoCursor.close();

            for (NameInfoPod nameInfoPod : namesToMigrate) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_LIST_ID, listNamesToIdsMap.get(nameInfoPod.listName));
                values.put(COLUMN_NAME, nameInfoPod.name);
                values.put(COLUMN_NAME_COUNT, nameInfoPod.amount);
                database.insert(NAMES_TABLE_NAME, null, values);
            }
            oldVersion++;
        }

        // Store settings in DB
        if (oldVersion == 3) {
            database.execSQL(ADD_PRESENTATION);
            database.execSQL(ADD_WITH_REPLACEMENT);
            database.execSQL(ADD_AUTOMATIC_TTS);
            database.execSQL(ADD_SHOW_AS_LIST);
            database.execSQL(ADD_NUM_NAMES_CHOSEN);
            database.execSQL(ADD_NAMES_HISTORY);
        }
    }

    // V1 -> V2 upgrade, get list names
    private List<String> getNonEmptyLists(SQLiteDatabase database) {
        List<String> lists = new ArrayList<>();
        String[] columns = {MySQLiteHelper.COLUMN_LIST_NAME};
        Cursor cursor = database.query(
                true,
                MySQLiteHelper.LEGACY_TABLE_NAME,
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
        Cursor cursor = database.rawQuery("SELECT " + MySQLiteHelper.COLUMN_PERSON_NAME_LEGACY +
                ", COUNT() FROM " + MySQLiteHelper.LEGACY_TABLE_NAME + " WHERE " +
                MySQLiteHelper.COLUMN_LIST_NAME + " = ? " +
                "GROUP BY " + MySQLiteHelper.COLUMN_PERSON_NAME_LEGACY, new String[] {listName});
        while (cursor.moveToNext()) {
            names.put(cursor.getString(0), cursor.getInt(1));
        }
        cursor.close();
        return names;
    }
}
