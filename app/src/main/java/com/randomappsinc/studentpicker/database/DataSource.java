package com.randomappsinc.studentpicker.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.common.Language;
import com.randomappsinc.studentpicker.grouping.GroupMakingSettings;
import com.randomappsinc.studentpicker.models.ListDO;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.models.NameDO;
import com.randomappsinc.studentpicker.utils.JSONUtils;

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

        // We botched the database upgrade for group settings, so we need this safety for a while
        dbHelper.addGroupColumns();
    }

    // Open connection to database
    private void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    // Terminate connection to database
    private void close() {
        dbHelper.close();
    }

    public String getListName(int listId) {
        open();
        String[] columns = {DatabaseColumns.LIST_NAME};
        String selection = DatabaseColumns.ID + " = ?";
        String[] whereArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(DatabaseTables.LISTS, columns, selection,
                whereArgs, null, null, null);
        if (cursor.moveToNext()) {
            return cursor.getString(0);
        }
        cursor.close();
        close();
        return "";
    }

    public long getNumLists() {
        open();
        long numLists = DatabaseUtils.queryNumEntries(
                database, DatabaseTables.LISTS, null, null);
        close();
        return numLists;
    }

    public List<ListDO> getNameLists(String searchTerm) {
        List<ListDO> lists = new ArrayList<>();
        open();
        Cursor cursor;
        if (TextUtils.isEmpty(searchTerm)) {
            String[] columns = {DatabaseColumns.ID, DatabaseColumns.LIST_NAME};
            cursor = database.query(DatabaseTables.LISTS, columns, null,
                    null, null, null, null);
        } else {
            cursor = database.rawQuery("SELECT * FROM " + DatabaseTables.LISTS + " WHERE " +
                    DatabaseColumns.LIST_NAME + " like ? COLLATE NOCASE",
                    new String[] {searchTerm + "%"});
        }
        while (cursor.moveToNext()) {
            lists.add(new ListDO(cursor.getInt(0), cursor.getString(1)));
        }
        cursor.close();
        close();
        return lists;
    }

    public ListDO addNameList(String newListName) {
        open();
        ContentValues values = new ContentValues();
        values.put(DatabaseColumns.LIST_NAME, newListName);
        int result = (int) database.insert(DatabaseTables.LISTS, null, values);
        close();
        return new ListDO(result, newListName);
    }

    public void deleteList(int listId) {
        open();
        String[] whereArgs = {String.valueOf(listId)};

        // Delete the list
        database.delete(
                DatabaseTables.LISTS,
                DatabaseColumns.ID + " = ?",
                whereArgs);

        // Delete the names in the list
        database.delete(
                DatabaseTables.NAMES,
                DatabaseColumns.LIST_ID + " = ?",
                whereArgs);

        // Delete the name list choosing state
        database.delete(
                DatabaseTables.NAMES_IN_LIST,
                DatabaseColumns.LIST_ID + " = ?",
                whereArgs);

        close();
    }

    public void renameList(int listId, String newName) {
        open();
        ContentValues newValues = new ContentValues();
        newValues.put(DatabaseColumns.LIST_NAME, newName);
        String[] whereArgs = new String[] {String.valueOf(listId)};
        String whereStatement = DatabaseColumns.ID + " = ?";
        database.update(DatabaseTables.LISTS, newValues, whereStatement, whereArgs);
        close();
    }

    public List<ListDO> getAllNameList() {
        Map<Integer, ListDO> allNamesMap = new HashMap<>();
        open();
        String query = "SELECT Lists.id, Lists.list_name, Names.name, Names.name_count " +
                "FROM Lists " +
                "LEFT JOIN Names ON Lists.id = Names.list_id " +
                "ORDER BY Lists.id";
        Cursor cursor = database.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int listId = cursor.getInt(0);
            String listName = cursor.getString(1);
            NameDO nameDO = new NameDO();
            String name = cursor.getString(2);
            nameDO.setName(name);
            int nameAmount = cursor.getInt(3);
            nameDO.setAmount(nameAmount);
            if (allNamesMap.get(listId) == null) {
                ListDO listDO = new ListDO(listId, listName);
                List<NameDO> nameDOs = new ArrayList<>();
                if (name != null) {
                    nameDOs.add(nameDO);
                }
                listDO.setNamesInList(nameDOs);
                allNamesMap.put(listId, listDO);
            } else {
                ListDO listDO = allNamesMap.get(listId);
                listDO.getNamesInList().add(nameDO);
            }
        }
        cursor.close();
        close();
        return new ArrayList<>(allNamesMap.values());
    }

    public List<NameDO> getNamesInList(int listId) {
        List<NameDO> nameDOs = new ArrayList<>();
        open();
        String[] columns = {
                DatabaseColumns.ID,
                DatabaseColumns.NAME,
                DatabaseColumns.NAME_COUNT,
                DatabaseColumns.PHOTO_URI
        };
        String selection = DatabaseColumns.LIST_ID + " = ?";
        String[] selectionArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(DatabaseTables.NAMES, columns, selection,
                selectionArgs, null, null, DatabaseColumns.NAME + " ASC");
        while (cursor.moveToNext()) {
            int nameId = cursor.getInt(0);
            String name = cursor.getString(1);
            int nameAmount = cursor.getInt(2);
            String photoUri = cursor.getString(3);
            nameDOs.add(new NameDO(nameId, name, nameAmount, photoUri));
        }
        cursor.close();
        close();
        return nameDOs;
    }

    public ListInfo getListInfo(int listId) {
        Map<String, NameDO> nameMap = new HashMap<>();
        List<String> names = new ArrayList<>();
        int totalAmountOfNames = 0;
        open();
        String[] columns = {
                DatabaseColumns.ID,
                DatabaseColumns.NAME,
                DatabaseColumns.NAME_COUNT,
                DatabaseColumns.PHOTO_URI
        };
        String selection = DatabaseColumns.LIST_ID + " = ?";
        String[] selectionArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(DatabaseTables.NAMES, columns, selection,
                selectionArgs, null, null, DatabaseColumns.NAME + " ASC");
        while (cursor.moveToNext()) {
            int nameId = cursor.getInt(0);
            String name = cursor.getString(1);
            int nameAmount = cursor.getInt(2);
            String photoUri = cursor.getString(3);

            nameMap.put(name, new NameDO(nameId, name, nameAmount, photoUri));
            names.add(name);
            totalAmountOfNames += nameAmount;
        }
        cursor.close();
        close();
        return new ListInfo(nameMap, names, totalAmountOfNames, new ArrayList<>());
    }

    public ListInfo getChoosingStateListInfo(int listId) {
        Map<String, NameDO> nameMap = new HashMap<>();
        List<String> names = new ArrayList<>();
        int totalAmountOfNames = 0;
        open();
        String[] selectionArgs = {String.valueOf(listId)};
        String query = "SELECT NamesInList.name, NamesInList.name_count, Names.photo_uri " +
                "FROM NamesInList " +
                "LEFT JOIN Names ON NamesInList.list_id = Names.list_id " +
                "AND NamesInList.name = Names.name " +
                "WHERE NamesInList.list_id = ? " +
                "ORDER BY NamesInList.name ASC";
        Cursor cursor = database.rawQuery(query, selectionArgs);
        while (cursor.moveToNext()) {
            NameDO nameDO = new NameDO();
            String name = cursor.getString(0);
            nameDO.setName(name);
            int nameAmount = cursor.getInt(1);
            nameDO.setAmount(nameAmount);
            nameDO.setPhotoUri(cursor.getString(2));
            nameMap.put(name, nameDO);
            names.add(name);
            totalAmountOfNames += nameAmount;
        }
        cursor.close();

        String[] historyColumns = {DatabaseColumns.NAMES_HISTORY};
        String historySelection = DatabaseColumns.ID + " = ?";
        Cursor historyCursor = database.query(
                DatabaseTables.LISTS,
                historyColumns,
                historySelection,
                selectionArgs,
                null,
                null,
                null);
        List<String> namesHistory = new ArrayList<>();
        if (historyCursor.moveToFirst()) {
            String namesHistoryJsonBlurb = historyCursor.getString(0);
            namesHistory.addAll(JSONUtils.extractNamesHistory(namesHistoryJsonBlurb));
        }
        historyCursor.close();

        close();
        return new ListInfo(nameMap, names, totalAmountOfNames, namesHistory);
    }

    public void addNames(String name, int amount, int listId) {
        addNamesInternal(name, amount, listId, DatabaseTables.NAMES);
        addNamesInternal(name, amount, listId, DatabaseTables.NAMES_IN_LIST);
    }

    private void addNamesInternal(String name, int amount, int listId, String tableName) {
        int currentAmount = getAmountOfName(name, listId, tableName);

        open();
        if (currentAmount == 0) {
            ContentValues values = new ContentValues();
            values.put(DatabaseColumns.LIST_ID, listId);
            values.put(DatabaseColumns.NAME, name);
            values.put(DatabaseColumns.NAME_COUNT, amount);
            database.insert(tableName, null, values);
        } else {
            ContentValues newValues = new ContentValues();
            newValues.put(DatabaseColumns.NAME_COUNT, currentAmount + amount);
            String[] whereArgs = new String[]{name, String.valueOf(listId)};
            String whereStatement = DatabaseColumns.NAME + " = ? AND "
                    + DatabaseColumns.LIST_ID + " = ?";
            database.update(tableName, newValues, whereStatement, whereArgs);
        }
        close();
    }

    public void removeNames(String name, int amount, int listId) {
        removeNamesInternal(name, amount, listId, DatabaseTables.NAMES);
        removeNamesInternal(name, amount, listId, DatabaseTables.NAMES_IN_LIST);
    }

    private void removeNamesInternal(String name, int amount, int listId, String tableName) {
        int currentAmount = getAmountOfName(name, listId, tableName);

        open();
        if (currentAmount <= amount) {
            String[] whereArgs = {name, String.valueOf(listId)};
            database.delete(tableName,
                    DatabaseColumns.NAME
                            + " = ? AND "
                            + DatabaseColumns.LIST_ID + " = ?",
                    whereArgs);
        } else {
            ContentValues newValues = new ContentValues();
            newValues.put(DatabaseColumns.NAME_COUNT, currentAmount - amount);
            String[] whereArgs = new String[]{name, String.valueOf(listId)};
            String whereStatement = DatabaseColumns.NAME
                    + " = ? AND "
                    + DatabaseColumns.LIST_ID + " = ?";
            database.update(tableName, newValues, whereStatement, whereArgs);
        }
        close();
    }

    private int getAmountOfName(String name, int listId, String tableName) {
        open();
        String[] columns = {DatabaseColumns.NAME_COUNT};
        String selection = DatabaseColumns.NAME
                + " = ? AND "
                + DatabaseColumns.LIST_ID + " = ?";
        String[] selectionArgs = {name, String.valueOf(listId)};
        Cursor cursor = database.query(tableName, columns, selection,
                selectionArgs, null, null, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        cursor.close();
        close();
        return 0;
    }

    public List<String> getMatchingNames(String prefix) {
        List<String> matchingNames = new ArrayList<>();
        open();
        Cursor cursor = database.rawQuery("SELECT DISTINCT " + DatabaseColumns.NAME +
                " FROM " + DatabaseTables.NAMES + " WHERE " +
                DatabaseColumns.NAME + " like ? COLLATE NOCASE " +
                "ORDER BY " + DatabaseColumns.NAME + " ASC", new String[] {prefix + "%"});
        while (cursor.moveToNext()) {
            matchingNames.add(cursor.getString(0));
        }
        cursor.close();
        close();
        return matchingNames;
    }

    public void renamePeople(String oldName, String newName, int listId, int amount) {
        removeNames(oldName, amount, listId);
        addNames(newName, amount, listId);
    }

    public ChoosingSettings getChoosingSettings(int listId) {
        open();
        String[] columns = {
                DatabaseColumns.PRESENTATION_MODE,
                DatabaseColumns.WITH_REPLACEMENT,
                DatabaseColumns.AUTOMATIC_TTS,
                DatabaseColumns.SHOW_AS_LIST,
                DatabaseColumns.NUM_NAMES_CHOSEN,
                DatabaseColumns.SPEECH_LANGUAGE,
                DatabaseColumns.PREVENT_DUPLICATES
        };
        String selection = DatabaseColumns.ID + " = ?";
        String[] selectionArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(DatabaseTables.LISTS, columns, selection,
                selectionArgs, null, null, null);
        ChoosingSettings choosingSettings = new ChoosingSettings();
        if (cursor.moveToFirst()) {
            choosingSettings.setPresentationMode(cursor.getInt(0) != 0);
            choosingSettings.setWithReplacement(cursor.getInt(1) != 0);
            choosingSettings.setAutomaticTts(cursor.getInt(2) != 0);
            choosingSettings.setShowAsList(cursor.getInt(3) != 0);
            choosingSettings.setNumNamesToChoose(cursor.getInt(4));
            choosingSettings.setSpeechLanguage(cursor.getInt(5));
            choosingSettings.setPreventDuplicates(cursor.getInt(6) != 0);
        }
        cursor.close();
        close();

        return choosingSettings;
    }

    public GroupMakingSettings getGroupMakingSettings(int listId, int listSize) {
        open();
        String[] columns = {
                DatabaseColumns.NAMES_PER_GROUP,
                DatabaseColumns.NUMBER_OF_GROUPS
        };
        String selection = DatabaseColumns.ID + " = ?";
        String[] selectionArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(DatabaseTables.LISTS, columns, selection,
                selectionArgs, null, null, null);
        GroupMakingSettings groupMakingSettings = new GroupMakingSettings(listSize);
        if (cursor.moveToFirst()) {
            groupMakingSettings.setNumOfNamesPerGroup(cursor.getInt(0));
            groupMakingSettings.setNumOfGroups(cursor.getInt(1));
        }
        cursor.close();
        close();

        return groupMakingSettings;
    }

    public void saveNameListState(int listId, ListInfo listInfo, ChoosingSettings choosingSettings) {
        open();

        // Persist names in list
        String[] whereArgsToClearNamesState = {String.valueOf(listId)};
        database.delete(
                DatabaseTables.NAMES_IN_LIST,
                DatabaseColumns.LIST_ID + " = ?",
                whereArgsToClearNamesState);

        Map<String, NameDO> nameAmounts = listInfo.getNameMap();
        for (String name : nameAmounts.keySet()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseColumns.LIST_ID, listId);
            values.put(DatabaseColumns.NAME, name);
            values.put(DatabaseColumns.NAME_COUNT, nameAmounts.get(name).getAmount());
            database.insert(DatabaseTables.NAMES_IN_LIST, null, values);
        }

        // Persist choosing settings
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
                DatabaseColumns.PREVENT_DUPLICATES,
                choosingSettings.getPreventDuplicates() ? 1 : 0);
        newValues.put(
                DatabaseColumns.NUM_NAMES_CHOSEN,
                choosingSettings.getNumNamesToChoose());
        newValues.put(
                DatabaseColumns.NAMES_HISTORY,
                JSONUtils.namesArrayToJsonString(listInfo.getNameHistory()));
        String[] whereArgs = new String[]{String.valueOf(listId)};
        String whereStatement = DatabaseColumns.ID + " = ?";
        database.update(DatabaseTables.LISTS, newValues, whereStatement, whereArgs);

        close();
    }

    public void saveGroupMakingSettingState(int listId, GroupMakingSettings groupMakingSettings) {
        open();
        ContentValues newValues = new ContentValues();
        newValues.put(
                DatabaseColumns.NAMES_PER_GROUP,
                groupMakingSettings.getNumOfNamesPerGroup());
        newValues.put(
                DatabaseColumns.NUMBER_OF_GROUPS,
                groupMakingSettings.getNumOfGroups());
        String[] whereArgs = new String[]{String.valueOf(listId)};
        String whereStatement = DatabaseColumns.ID + " = ?";
        database.update(DatabaseTables.LISTS, newValues, whereStatement, whereArgs);
    }

    public List<ListDO> getNonEmptyOtherLists(int listIdToExclude) {
        open();
        String query = "SELECT DISTINCT "
                + "a." + DatabaseColumns.ID + ", " + "a." + DatabaseColumns.LIST_NAME
                + " FROM " + DatabaseTables.LISTS + " a"
                + " INNER JOIN " + DatabaseTables.NAMES + " b"
                + " ON a." + DatabaseColumns.ID
                + " = b." + DatabaseColumns.LIST_ID
                + " WHERE a." + DatabaseColumns.ID + " != ?";
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(listIdToExclude)});
        List<ListDO> nameLists = new ArrayList<>();
        while (cursor.moveToNext()) {
            nameLists.add(new ListDO(cursor.getInt(0), cursor.getString(1)));
        }
        cursor.close();
        close();

        return nameLists;
    }

    public void importOtherLists(int receivingListId, List<ListDO> listsToImportFrom) {
        open();
        String[] columns = {
                DatabaseColumns.NAME,
                DatabaseColumns.NAME_COUNT
        };

        StringBuilder whereQuery = new StringBuilder();
        for (int i = 0; i < listsToImportFrom.size(); i++) {
            if (whereQuery.length() > 0) {
                whereQuery.append(" OR ");
            }
            whereQuery
                    .append(DatabaseColumns.LIST_ID)
                    .append(" = ?");
        }

        String[] selectionArgs = new String[listsToImportFrom.size()];
        for (int i = 0; i < listsToImportFrom.size(); i++) {
            selectionArgs[i] = String.valueOf(listsToImportFrom.get(i).getId());
        }

        Cursor cursor = database.query(DatabaseTables.NAMES, columns, whereQuery.toString(),
                selectionArgs, null, null, null);
        Map<String, Integer> nameToAmount = new HashMap<>();
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            int nameAmount = cursor.getInt(1);

            if (nameToAmount.containsKey(name)) {
                int currentAmount = nameToAmount.get(name);
                int newAmount = currentAmount + nameAmount;
                nameToAmount.put(name, newAmount);
            } else {
                nameToAmount.put(name, nameAmount);
            }
        }
        cursor.close();
        close();

        for (String name : nameToAmount.keySet()) {
            addNames(name, nameToAmount.get(name), receivingListId);
        }
    }

    @Nullable
    public String getChoosingMessage(int listId) {
        open();
        String[] columns = {
                DatabaseColumns.CHOOSING_MESSAGE,
        };
        String selection = DatabaseColumns.ID + " = ?";
        String[] selectionArgs = {String.valueOf(listId)};
        Cursor cursor = database.query(DatabaseTables.LISTS, columns, selection,
                selectionArgs, null, null, null);
        String choosingMessage = null;
        if (cursor.moveToFirst()) {
            choosingMessage = cursor.getString(0);
        }
        cursor.close();
        close();

        return choosingMessage;
    }

    public void updateChoosingMessage(int listId, String newMessage) {
        open();

        ContentValues newValues = new ContentValues();
        newValues.put(DatabaseColumns.CHOOSING_MESSAGE, newMessage);
        String[] whereArgs = new String[]{String.valueOf(listId)};
        String whereStatement = DatabaseColumns.ID + " = ?";
        database.update(DatabaseTables.LISTS, newValues, whereStatement, whereArgs);

        close();
    }

    public void updateSpeechLanguage(int listId, @Language int speechLanguage) {
        open();

        ContentValues newValues = new ContentValues();
        newValues.put(DatabaseColumns.SPEECH_LANGUAGE, speechLanguage);
        String[] whereArgs = new String[]{String.valueOf(listId)};
        String whereStatement = DatabaseColumns.ID + " = ?";
        database.update(DatabaseTables.LISTS, newValues, whereStatement, whereArgs);

        close();
    }

    public void updateNamePhoto(int nameId, String photoUri) {
        open();

        ContentValues newValues = new ContentValues();
        newValues.put(DatabaseColumns.PHOTO_URI, photoUri);
        String[] whereArgs = new String[]{String.valueOf(nameId)};
        String whereStatement = DatabaseColumns.ID + " = ?";
        database.update(DatabaseTables.NAMES, newValues, whereStatement, whereArgs);

        close();
    }
}
