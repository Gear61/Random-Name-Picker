package com.randomappsinc.studentpicker.utils;

import android.text.TextUtils;

public class StringUtil {

    public static String capitalizeWords(String input) {
        String[] words = input.split("\\s+");
        StringBuilder capitalizedVersion = new StringBuilder();
        for (String word : words) {
            if (TextUtils.isEmpty(word)) {
                continue;
            }

            String first = word.substring(0, 1);
            String restOfWord = word.substring(1);
            if (capitalizedVersion.length() > 0) {
                capitalizedVersion.append(" ");
            }
            capitalizedVersion
                    .append(first.toUpperCase())
                    .append(restOfWord);
        }
        return capitalizedVersion.toString();
    }
}
