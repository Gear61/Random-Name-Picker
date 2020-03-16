package com.randomappsinc.studentpicker.utils;

import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.models.ListInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class JSONUtils {

    private static final String NAMES_KEY = "names";
    private static final String ALREADY_CHOSEN_NAMES_KEY = "alreadyChosenNames";
    private static final String SETTINGS_KEY = "settings";
    private static final String PRESENTATION_MODE_KEY = "presentationMode";
    private static final String WITH_REPLACEMENT_KEY = "withReplacement";
    private static final String AUTOMATIC_TTS_KEY = "automaticTts";
    private static final String SHOW_AS_LIST_KEY = "showAsList";
    private static final String NUM_NAMES_TO_CHOOSE_KEY = "numNamesToChoose";

    // Given a list of names, converts it to a JSON and stringifies it
    static String serializeChoosingState(ListInfo currentState, ChoosingSettings choosingSettings) {
        try {
            JSONObject nameListJson = new JSONObject();

            // Store current names in list
            JSONArray namesArray = new JSONArray();
            Map<String, Integer> nameAmounts = currentState.getNameAmounts();
            for (String name : currentState.getUniqueNames()) {
                int amount = nameAmounts.get(name);
                for (int i = 0; i < amount; i++) {
                    namesArray.put(name);
                }
            }
            nameListJson.put(NAMES_KEY, namesArray);

            // Store already chosen names
            JSONArray alreadyChosenNamesArray = new JSONArray();
            for (String alreadyChosenName : currentState.getNameHistory()) {
                alreadyChosenNamesArray.put(alreadyChosenName);
            }
            nameListJson.put(ALREADY_CHOSEN_NAMES_KEY, alreadyChosenNamesArray);

            JSONObject settings = new JSONObject();
            settings.put(PRESENTATION_MODE_KEY, choosingSettings.isPresentationModeEnabled());
            settings.put(WITH_REPLACEMENT_KEY, choosingSettings.getWithReplacement());
            settings.put(AUTOMATIC_TTS_KEY, choosingSettings.getAutomaticTts());
            settings.put(SHOW_AS_LIST_KEY, choosingSettings.getShowAsList());
            settings.put(NUM_NAMES_TO_CHOOSE_KEY, choosingSettings.getNumNamesToChoose());
            nameListJson.put(SETTINGS_KEY, settings);

            return nameListJson.toString();
        }
         catch (JSONException e) {
            return "";
        }
    }

    // Given a serialized JSON "cache" string of a name list, extracts the list state
    static ListInfo extractChoosingState(String cachedList) {
        if (cachedList.isEmpty()) {
            return null;
        }

        Map<String, Integer> nameAmounts = new HashMap<>();
        List<String> names = new ArrayList<>();
        int numNames = 0;
        List<String> alreadyChosenNames = new ArrayList<>();
        try {
            JSONObject nameListJson = new JSONObject(cachedList);
            JSONArray namesArray = nameListJson.getJSONArray(NAMES_KEY);
            for (int i = 0; i < namesArray.length(); i++) {
                String name = namesArray.getString(i);
                if (nameAmounts.containsKey(name)) {
                    int currentAmount = nameAmounts.get(name);
                    nameAmounts.put(name, currentAmount + 1);
                } else {
                    nameAmounts.put(name, 1);
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

        return new ListInfo(nameAmounts, names, numNames, alreadyChosenNames);
    }

    // Given a serialized JSON "cache" string of a name list, extracts the choosing settings
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
        } catch (JSONException ignored) {
        }
        return settings;
    }
}
