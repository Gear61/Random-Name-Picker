package com.randomappsinc.studentpicker.Database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.randomappsinc.studentpicker.Utils.MyApplication;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    // Table name
    public static final String PERSON_NAMES_TABLE_NAME = "Students";

    // COLUMNS
    public static final String COLUMN_LIST_NAME = "list_name";
    public static final String COLUMN_PERSON_NAME = "student_name";

    // Some random things fed to a super's method
    private static final String DATABASE_NAME = "studentpicker.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statements
    static final String STUDENTS_CREATE = "CREATE TABLE IF NOT EXISTS " + PERSON_NAMES_TABLE_NAME + "(" + COLUMN_LIST_NAME
            + " TEXT, " + COLUMN_PERSON_NAME + " TEXT);";

    public MySQLiteHelper() {
        super(MyApplication.getAppContext(), DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(STUDENTS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
