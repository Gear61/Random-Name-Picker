package com.randomappsinc.studentpicker.database;

// Holds database table names
class DatabaseTables {

    static final String LISTS = "Lists";
    static final String NAMES = "Names";

    // For persisting the choosing state (what names remain in the list_
    static final String NAMES_IN_LIST = "NamesInList";

    // LEGACY table (everything in 1 table)
    static final String LEGACY_TABLE_NAME = "Students";
}
