package com.randomappsinc.studentpicker.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.database.DataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NameUtils {

    // For the choose multiple names at once case. We're just generating indices
    public static List<Integer> getRandomNumsInRange(int numNumbers, int capIndex) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i <= capIndex; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        if (numNumbers > capIndex) {
            return list;
        }

        List<Integer> chosenNumbers = new ArrayList<>();
        for (int i = 0; i < numNumbers; i++) {
            chosenNumbers.add(list.get(i));
        }
        return chosenNumbers;
    }

    public static List<List<Integer>> getRandomGroups(int namesPerGroup, int numberOfGroups, int capIndex) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i <= capIndex; i++) {
            list.add(i);
        }

        Collections.shuffle(list);
        int chosenCounter = 0;
        List<List<Integer>> listOfGroups = new ArrayList<>();
        for (int i = 0; i < numberOfGroups; i++) {
            List<Integer> listOfNamesPerGroup = new ArrayList<>();
            int j = 0;
            while (capIndex != -1 && j < namesPerGroup) {
                listOfNamesPerGroup.add(list.get(chosenCounter++));
                capIndex--;
                j++;
            }
            listOfGroups.add(listOfNamesPerGroup);
            if (capIndex == -1) break;
        }
        return listOfGroups;
    }

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
}
