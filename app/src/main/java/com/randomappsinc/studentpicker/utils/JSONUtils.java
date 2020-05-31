package com.randomappsinc.studentpicker.utils;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JSONUtils {

    public static String namesArrayToJsonString(List<String> chosenNames) {
        JSONArray alreadyChosenNamesArray = new JSONArray();
        for (String alreadyChosenName : chosenNames) {
            alreadyChosenNamesArray.put(alreadyChosenName);
        }
        return alreadyChosenNamesArray.toString();
    }

    public static List<String> extractNamesHistory(String namesArrayText) {
        if (TextUtils.isEmpty(namesArrayText)) {
            return Collections.emptyList();
        }

        List<String> alreadyChosenNames = new ArrayList<>();
        try {
            JSONArray namesArray = new JSONArray(namesArrayText);
            for (int i = 0; i < namesArray.length(); i++) {
                alreadyChosenNames.add(namesArray.getString(i));
            }
        }
        catch (JSONException ignored) {}

        return alreadyChosenNames;
    }
}
