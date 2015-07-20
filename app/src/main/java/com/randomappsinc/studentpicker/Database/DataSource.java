package com.randomappsinc.studentpicker.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexanderchiou on 7/19/15.
 */
public class DataSource
{
    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    // Constructor
    public DataSource(Context context)
    {
        dbHelper = new MySQLiteHelper(context);
    }

    // Open connection to database
    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    // Terminate connection to database
    public void close()
    {
        dbHelper.close();
    }

    public List<String> getAllStudents(String listName)
    {
        List<String> students = new ArrayList<>();
        open();
        String[] columns = {MySQLiteHelper.COLUMN_STUDENT_NAME};
        String selection = MySQLiteHelper.COLUMN_LIST_NAME + " = ?";
        String[] selectionArgs = {listName};
        Cursor cursor = database.query(MySQLiteHelper.STUDENTS_TABLE_NAME, columns, selection,
                selectionArgs, null, null, null);
        while (cursor.moveToNext())
        {
            students.add(cursor.getString(0));
        }
        cursor.close();
        close();
        return students;
    }

    public void addStudent (String studentName, String listName)
    {
        open();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_LIST_NAME, listName);
        values.put(MySQLiteHelper.COLUMN_STUDENT_NAME, studentName);
        database.insert(MySQLiteHelper.STUDENTS_TABLE_NAME, null, values);
        close();
    }

    public void removeStudent (String studentName, String listName)
    {
        open();
        String whereArgs[] = {studentName, listName};
        database.delete(MySQLiteHelper.STUDENTS_TABLE_NAME, MySQLiteHelper.COLUMN_STUDENT_NAME + " = ? AND " +
                        MySQLiteHelper.COLUMN_LIST_NAME + " = ?", whereArgs);
        close();
    }

    public void deleteList(String listName)
    {
        open();
        String whereArgs[] = {listName};
        database.delete(MySQLiteHelper.STUDENTS_TABLE_NAME, MySQLiteHelper.COLUMN_LIST_NAME + " = ?", whereArgs);
        close();
    }
}
