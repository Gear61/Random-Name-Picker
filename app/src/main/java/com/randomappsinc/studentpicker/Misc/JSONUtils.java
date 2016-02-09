package com.randomappsinc.studentpicker.Misc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexanderchiou on 1/31/16.
 */
public class JSONUtils {
    public static final String NAMES_KEY = "names";
    public static final String ALREADY_CHOSEN_NAMES_KEY = "alreadyChosenNames";

    // Given a list of names, converts it to a JSON and stringifies it
    public static String serializeNameList(List<String> names, List<String> alreadyChosenNames) {
        try {
            JSONObject nameListJson = new JSONObject();

            // Store current names in list
            JSONArray namesArray = new JSONArray();
            for (String name : names) {
                namesArray.put(name);
            }
            nameListJson.put(NAMES_KEY, namesArray);

            // Store already chosen names
            JSONArray alreadyChosenNamesArray = new JSONArray();
            for (String alreadyChosenName : alreadyChosenNames) {
                alreadyChosenNamesArray.put(alreadyChosenName);
            }
            nameListJson.put(ALREADY_CHOSEN_NAMES_KEY, alreadyChosenNamesArray);

            return nameListJson.toString();
        }
        catch (JSONException e) {
            return "";
        }
    }

    // Given a serialized JSON "cache" string of a name list, extracts the names
    public static List<String> extractNames(String cachedList) {
        List<String> names = new ArrayList<>();
        try {
            JSONObject nameListJson = new JSONObject(cachedList);
            JSONArray namesArray = nameListJson.getJSONArray(NAMES_KEY);
            for (int i = 0; i < namesArray.length(); i++) {
                names.add(namesArray.getString(i));
            }
        }
        catch (JSONException ignored) {}
        return names;
    }

    // Given a serialized JSON "cache" string of a name list, extracts the names
    public static List<String> extractAlreadyChosenNames(String cachedList) {
        List<String> alreadyChosenNames = new ArrayList<>();
        try {
            JSONObject nameListJson = new JSONObject(cachedList);
            JSONArray alreadyChosenNamesArray = nameListJson.getJSONArray(ALREADY_CHOSEN_NAMES_KEY);
            for (int i = 0; i < alreadyChosenNamesArray.length(); i++) {
                alreadyChosenNames.add(alreadyChosenNamesArray.getString(i));
            }
        }
        catch (JSONException ignored) {}
        return alreadyChosenNames;
    }
}
