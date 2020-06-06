package com.randomappsinc.studentpicker.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.models.NameDO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameUtils {

    public static void copyNamesToClipboard(String names, int numNames, boolean historyMode, Context context) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Activity.CLIPBOARD_SERVICE);
        if (clipboard == null) {
            return;
        }
        ClipData clip = ClipData.newPlainText(context.getString(R.string.chosen_names), names);
        clipboard.setPrimaryClip(clip);

        int messageId;
        if (historyMode) {
            messageId = R.string.name_history_copied;
        } else {
            if (numNames > 1) {
                messageId = R.string.copy_confirmation_plural;
            } else {
                messageId = R.string.copy_confirmation_singular;
            }
        }

        UIUtils.showLongToast(messageId, context);
    }

    // Given 0 (1st element in array), returns "1. ", scaling linearly with the input
    public static String getPrefix(int index) {
        return (index + 1) + ". ";
    }

    public static String getChoosingMessage(Context context, int listId, int numNames) {
        DataSource dataSource = new DataSource(context);
        String choosingMessage = dataSource.getChoosingMessage(listId);
        if (TextUtils.isEmpty(choosingMessage)) {
            return numNames == 1
                    ? context.getString(R.string.name_chosen)
                    : context.getString(R.string.names_chosen);
        }
        return choosingMessage;
    }

    public static List<List<String>> createGroups(ListInfo listInfo, int namesPerGroup, int numGroups) {
        List<List<String>> groups = new ArrayList<>();
        if (numGroups == 0 || namesPerGroup == 0) {
            return groups;
        }

        // Get the "longform" list of all the names, expanding duplicates
        List<String> allNames = listInfo.getLongList();

        // Add as many names lists to fill as we can.
        // This will be minimum of the number of names or the # of groups desired.
        for (int i = 0; i < Math.min(allNames.size(), numGroups); i++) {
            groups.add(new ArrayList<>());
        }

        // Put names into lists until we have all the names we need or run out of names
        int totalNamesToAttemptToAdd = numGroups * namesPerGroup;
        int numNamesAdded = 0;
        Collections.shuffle(allNames);
        while (numNamesAdded < Math.min(allNames.size(), totalNamesToAttemptToAdd)) {
            // Go through every list and add a single name. This will prevent uneven groups.
            for (List<String> nameList : groups) {
                nameList.add(allNames.get(numNamesAdded));
                numNamesAdded++;

                if (numNamesAdded >= Math.min(allNames.size(), totalNamesToAttemptToAdd)) {
                    break;
                }
            }
        }

        return groups;
    }

    /** Use convertNameListToString() instead */
    @Deprecated
    public static String flattenListToString(List<String> names, ChoosingSettings settings) {
        StringBuilder namesText = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            if (namesText.length() > 0) {
                namesText.append("\n");
            }
            if (settings.getShowAsList()) {
                namesText.append(NameUtils.getPrefix(i));
            }
            namesText.append(names.get(i));
        }
        return namesText.toString();
    }

    public static String convertNameListToString(List<NameDO> names, ChoosingSettings settings) {
        StringBuilder namesText = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            if (namesText.length() > 0) {
                namesText.append("\n");
            }
            if (settings.getShowAsList()) {
                namesText.append(NameUtils.getPrefix(i));
            }
            namesText.append(names.get(i).getName());
        }
        return namesText.toString();
    }

    /** Gets the name display for names NON-CHOOSING scenarios */
    public static String getDisplayTextForName(NameDO nameDO) {
        String name = nameDO.getName();
        int amount = nameDO.getAmount();
        return amount == 1 ? name : name + " (" + amount + ")";
    }
}
