package com.randomappsinc.studentpicker.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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

    public List<ListDO> getNameLists() {
        return new ArrayList<>();
    }

    public ListDO addNameList(String newListName) {
        return new ListDO();
    }

    public ListInfo getListInfo(int listId) {
        Map<String, Integer> nameAmounts = new HashMap<>();
        List<String> names = new ArrayList<>();
        int amount = 0;
        open();
        String[] columns = {MySQLiteHelper.COLUMN_PERSON_NAME, MySQLiteHelper.COLUMN_NAME_COUNT};
        String selection = MySQLiteHelper.COLUMN_LIST_NAME + " = ?";
        String[] selectionArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, columns, selection,
                selectionArgs, null, null, MySQLiteHelper.COLUMN_PERSON_NAME + " ASC");
        while (cursor.moveToNext()) {
            nameAmounts.put(cursor.getString(0), cursor.getInt(1));
            names.add(cursor.getString(0));
            amount += cursor.getInt(1);
        }
        cursor.close();
        close();
        return new ListInfo(null, names, amount, new ArrayList<>());
    }

    public void addNames(int nameId, int listId, int amount) {
        int currentAmount = getAmount(nameId);

        open();
        if (currentAmount == 0) {
            /* ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_LIST_NAME, listName);
            values.put(MySQLiteHelper.COLUMN_PERSON_NAME, name);
            values.put(MySQLiteHelper.COLUMN_NAME_COUNT, amount);
            database.insert(MySQLiteHelper.TABLE_NAME, null, values); */
        } else {
            /* ContentValues newValues = new ContentValues();
            newValues.put(MySQLiteHelper.COLUMN_NAME_COUNT, currentAmount + amount);
            String[] whereArgs = new String[]{listName, name};
            String whereStatement = MySQLiteHelper.COLUMN_LIST_NAME
                    + " = ? AND "
                    + MySQLiteHelper.COLUMN_PERSON_NAME + " = ?";
            database.update(MySQLiteHelper.TABLE_NAME, newValues, whereStatement, whereArgs); */
        }
        close();
    }

    void removeNames(int nameId, int amount) {
        int currentAmount = getAmount(nameId);

        open();
        if (currentAmount <= amount) {
            /* String[] whereArgs = {listName, name};
            database.delete(MySQLiteHelper.TABLE_NAME,
                    MySQLiteHelper.COLUMN_LIST_NAME
                            + " = ? AND "
                            + MySQLiteHelper.COLUMN_PERSON_NAME + " = ?",
                    whereArgs); */
        } else {
            /* ContentValues newValues = new ContentValues();
            newValues.put(MySQLiteHelper.COLUMN_NAME_COUNT, currentAmount - amount);
            String[] whereArgs = new String[]{listName, name};
            String whereStatement = MySQLiteHelper.COLUMN_LIST_NAME
                    + " = ? AND "
                    + MySQLiteHelper.COLUMN_PERSON_NAME + " = ?";
            database.update(MySQLiteHelper.TABLE_NAME, newValues, whereStatement, whereArgs); */
        }
        close();
    }

    private int getAmount(int nameId) {
        open();
        String[] columns = {MySQLiteHelper.COLUMN_NAME_COUNT};
        String selection = MySQLiteHelper.COLUMN_LIST_NAME
                + " = ? AND "
                + MySQLiteHelper.COLUMN_PERSON_NAME + " = ?";
        String[] selectionArgs = {String.valueOf(nameId)};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME, columns, selection,
                selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        close();
        return 0;
    }

    public void deleteList(int listId) {
        open();
        String[] whereArgs = {String.valueOf(listId)};
        database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_LIST_NAME + " = ?", whereArgs);
        close();
    }

    public void renameList(ListDO listDO) {
        open();
        /* ContentValues newValues = new ContentValues();
        newValues.put(MySQLiteHelper.COLUMN_LIST_NAME, newListName);
        String[] whereArgs = new String[]{oldListName};
        String whereStatement = MySQLiteHelper.COLUMN_LIST_NAME + " = ?";
        database.update(MySQLiteHelper.TABLE_NAME, newValues, whereStatement, whereArgs); */
        close();
    }

    public List<String> getMatchingNames(String prefix) {
        List<String> matchingNames = new ArrayList<>();
        open();
        Cursor cursor = database.rawQuery("SELECT DISTINCT " + MySQLiteHelper.COLUMN_PERSON_NAME +
                " FROM " + MySQLiteHelper.TABLE_NAME + " WHERE " +
                MySQLiteHelper.COLUMN_PERSON_NAME + " like ? COLLATE NOCASE " +
                "ORDER BY " + MySQLiteHelper.COLUMN_PERSON_NAME + " ASC", new String[] {prefix + "%"});
        while (cursor.moveToNext()) {
            matchingNames.add(cursor.getString(0));
        }
        cursor.close();
        close();
        return matchingNames;
    }

    public String[] getAllNameListsMinusCurrent(String currentList) {
        List<String> names = new ArrayList<>();
        open();
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
            if (!cursor.getString(0).equals(currentList)) {
                names.add(cursor.getString(0));
            }
        }
        cursor.close();
        close();
        return names.toArray(new String[names.size()]);
    }

    Map<String, Integer> importNamesIntoList(int receivingListId, List<Integer> givingLists) {
        Map<String, Integer> nameAmounts = new HashMap<>();
        for (Integer listId : givingLists) {
            Map<String, NameDO> namesToImport = getListInfo(listId).getNameInformation();
            for (String name : namesToImport.keySet()) {
                // TODO: Bring this back!!!
                // addNames(name, receivingListId, namesToImport.get(name).getAmount());
                if (nameAmounts.containsKey(name)) {
                    int currentAmount = nameAmounts.get(name);
                    nameAmounts.put(name, currentAmount + namesToImport.get(name).getAmount());
                } else {
                    nameAmounts.put(name, namesToImport.get(name).getAmount());
                }
            }
        }
        return nameAmounts;
    }

    void renamePeople(String oldName, String newName, int listId, int amount) {
        /* removeNames(oldName, listId, amount);
        addNames(newName, listId, amount); */
    }
}
