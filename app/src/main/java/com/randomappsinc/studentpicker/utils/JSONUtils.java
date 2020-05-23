package com.randomappsinc.studentpicker.utils;

import android.text.TextUtils;

import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.models.ListInfo;
import com.randomappsinc.studentpicker.models.NameDO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONUtils {

    private static final String NAMES_KEY = "names";
    private static final String ALREADY_CHOSEN_NAMES_KEY = "alreadyChosenNames";
    private static final String SETTINGS_KEY = "settings";
    private static final String PRESENTATION_MODE_KEY = "presentationMode";
    private static final String WITH_REPLACEMENT_KEY = "withReplacement";
    private static final String AUTOMATIC_TTS_KEY = "automaticTts";
    private static final String SHOW_AS_LIST_KEY = "showAsList";
    private static final String NUM_NAMES_TO_CHOOSE_KEY = "numNamesToChoose";

    @Deprecated
    static ListInfo extractChoosingState(String cachedList) {
        if (cachedList.isEmpty()) {
            return null;
        }

        Map<String, NameDO> nameMap = new HashMap<>();
        List<String> names = new ArrayList<>();
        int numNames = 0;
        List<String> alreadyChosenNames = new ArrayList<>();
        try {
            JSONObject nameListJson = new JSONObject(cachedList);
            JSONArray namesArray = nameListJson.getJSONArray(NAMES_KEY);
            for (int i = 0; i < namesArray.length(); i++) {
                String name = namesArray.getString(i);
                if (nameMap.containsKey(name)) {
                    int currentAmount = nameMap.get(name).getAmount();
                    nameMap.get(name).setAmount(currentAmount + 1);
                } else {
                    nameMap.put(name, new NameDO(-1, name, 1, null));
                    names.add(name);
                }
                numNames++;
            }
            Collections.sort(names);

            JSONArray alreadyChosenNamesArray = nameListJson.getJSONArray(ALREADY_CHOSEN_NAMES_KEY);
            for (int i = 0; i < alreadyChosenNamesArray.length(); i++) {
                alreadyChosenNames.add(alreadyChosenNamesArray.getString(i));
            }
        }
        catch (JSONException ignored) {}

        return new ListInfo(nameMap, names, numNames, alreadyChosenNames);
    }

    @Deprecated
    static ChoosingSettings extractChoosingSettings(String cachedList) {
        ChoosingSettings settings = new ChoosingSettings();
        try {
            JSONObject nameListJson = new JSONObject(cachedList);
            JSONObject settingsJson = nameListJson.getJSONObject(SETTINGS_KEY);
            settings.setWithReplacement(settingsJson.getBoolean(WITH_REPLACEMENT_KEY));
            settings.setNumNamesToChoose(settingsJson.getInt(NUM_NAMES_TO_CHOOSE_KEY));
            settings.setPresentationMode(settingsJson.getBoolean(PRESENTATION_MODE_KEY));
            settings.setAutomaticTts(settingsJson.getBoolean(AUTOMATIC_TTS_KEY));
            settings.setShowAsList(settingsJson.getBoolean(SHOW_AS_LIST_KEY));
        } catch (JSONException ignored) {}
        return settings;
    }

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
