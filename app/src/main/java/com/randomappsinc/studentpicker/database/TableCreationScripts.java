package com.randomappsinc.studentpicker.database;

class TableCreationScripts {

    static final String CREATE_NAMES_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS " + DatabaseTables.NAMES +
            "(" + DatabaseColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DatabaseColumns.LIST_ID + " INTEGER, "
            + DatabaseColumns.NAME + " TEXT, "
            + DatabaseColumns.NAME_COUNT + " INTEGER, "
            + DatabaseColumns.PHOTO_URI + " TEXT);";

    static final String CREATE_LISTS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + DatabaseTables.LISTS + "("
            + DatabaseColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DatabaseColumns.LIST_NAME + " TEXT, "
            + DatabaseColumns.PRESENTATION_MODE + " BOOLEAN NOT NULL DEFAULT 0, "
            + DatabaseColumns.WITH_REPLACEMENT + " BOOLEAN NOT NULL DEFAULT 0, "
            + DatabaseColumns.AUTOMATIC_TTS + " BOOLEAN NOT NULL DEFAULT 0, "
            + DatabaseColumns.SHOW_AS_LIST + " BOOLEAN NOT NULL DEFAULT 0, "
            + DatabaseColumns.NUM_NAMES_CHOSEN + " INTEGER DEFAULT 1, "
            + DatabaseColumns.NAMES_HISTORY + " TEXT, "
            + DatabaseColumns.CHOOSING_MESSAGE + " TEXT, "
            + DatabaseColumns.SPEECH_LANGUAGE + " INTEGER DEFAULT -1, "
            + DatabaseColumns.PREVENT_DUPLICATES + " BOOLEAN NOT NULL DEFAULT 0, "
            + DatabaseColumns.NAMES_PER_GROUP + " INTEGER DEFAULT 2, "
            + DatabaseColumns.NUMBER_OF_GROUPS + " INTEGER DEFAULT 0);";

    static final String CREATE_NAMES_IN_LIST_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS "
            + DatabaseTables.NAMES_IN_LIST +  "("
            + DatabaseColumns.LIST_ID + " INTEGER, "
            + DatabaseColumns.NAME + " TEXT, "
            + DatabaseColumns.NAME_COUNT + " INTEGER);";
}
