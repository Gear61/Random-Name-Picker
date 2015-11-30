package com.randomappsinc.studentpicker.Misc;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.randomappsinc.studentpicker.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alexanderchiou on 7/20/15.
 */
public class Utils {
    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        // Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        // If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

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

    public static void showSnackbar(View parent, String message) {
        Context context = Application.get().getApplicationContext();
        Snackbar snackbar = Snackbar.make(parent, message, Snackbar.LENGTH_LONG);
        View rootView = snackbar.getView();
        snackbar.getView().setBackgroundColor(context.getResources().getColor(R.color.app_teal));
        TextView tv = (TextView) rootView.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static String getFileName(String filePath) {
        String[] pieces = filePath.split("/");
        String fileName = pieces[pieces.length - 1];
        return fileName.replace(".txt", "");
    }

    public static String convertStreamToString(InputStream is) throws Exception {
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
}