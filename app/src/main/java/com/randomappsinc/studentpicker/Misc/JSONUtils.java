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

    // Given a list of names, converts it to a JSON and stringifies it
    public static String serializeNameList(List<String> names) {
        try {
            JSONObject nameListJson = new JSONObject();
            JSONArray namesArray = new JSONArray();
            for (String name : names) {
                namesArray.put(name);
            }
            nameListJson.put(NAMES_KEY, namesArray);
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
}
