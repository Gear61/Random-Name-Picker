package com.randomappsinc.studentpicker.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.View;

import com.randomappsinc.studentpicker.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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

    public static String getFileName(String filePath) {
        String[] pieces = filePath.split("/");
        String fileName = pieces[pieces.length - 1];
        return fileName.replace(".txt", "");
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getNamesFromFile(String filePath) throws Exception {
        File file = new File(filePath);
        FileInputStream fileInputStream = new FileInputStream(file);
        String contents = convertStreamToString(fileInputStream);
        String[] allNames = contents.split("\\r?\\n");
        StringBuilder namesString = new StringBuilder();
        for (int i = 0; i < allNames.length; i++) {
            if (i != 0) {
                namesString.append("\n");
            }
            namesString.append(allNames[i]);
        }
        fileInputStream.close();
        return namesString.toString();
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

    public static String[] getNameOptions(String name, Context context) {
        String[] options = new String[3];
        options[0] = context.getString(R.string.rename_person, name);
        options[1] = context.getString(R.string.delete_name, name);
        options[2] = context.getString(R.string.duplicate, name);
        return options;
    }

    // Given 0 (1st element in array), returns "1. ", scaling linearly with the input
    public static String getPrefix(int index) {
        return String.valueOf(index + 1) + ". ";
    }
}
