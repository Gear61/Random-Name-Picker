package com.randomappsinc.studentpicker.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class MySQLiteHelper extends SQLiteOpenHelper
{
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

    static final String DATABASE_DROP = "DROP TABLE " + PERSON_NAMES_TABLE_NAME;

    public MySQLiteHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database)
    {
        database.execSQL(STUDENTS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.w(MySQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL(DATABASE_DROP);
        onCreate(db);
    }
}
