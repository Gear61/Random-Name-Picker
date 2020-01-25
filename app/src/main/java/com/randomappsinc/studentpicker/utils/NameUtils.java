package com.randomappsinc.studentpicker.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;

import com.randomappsinc.studentpicker.R;

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

    public static void copyNamesToClipboard(
            String names,
            @Nullable View parent,
            int numNames,
            boolean historyMode,
            Context context) {
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

        if (parent == null) {
            UIUtils.showLongToast(messageId, context);
        } else {
            UIUtils.showSnackbar(parent, context.getString(messageId));
        }
    }

    // Given 0 (1st element in array), returns "1. ", scaling linearly with the input
    public static String getPrefix(int index) {
        return (index + 1) + ". ";
    }
}
