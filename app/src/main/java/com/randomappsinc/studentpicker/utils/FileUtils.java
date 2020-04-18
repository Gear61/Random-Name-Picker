package com.randomappsinc.studentpicker.utils;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.IOException;

public class FileUtils {

    @Nullable
    public static File createCsvFileForList(Context context, String listName) {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(storageDir, listName + ".csv");
        // Delete file if it already exists to prevent ourselves from appending to old content
        if (file.delete()) {
            try {
                return file.createNewFile() ? file : null;
            } catch (IOException exception) {
                return null;
            }
        }
        return file;
    }

    @Nullable
    public static File createTxtFileForList(Context context, String listName) {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        File file = new File(storageDir, listName + ".txt");
        // Delete file if it already exists to prevent ourselves from appending to old content
        if (file.delete()) {
            try {
                return file.createNewFile() ? file : null;
            } catch (IOException exception) {
                return null;
            }
        }
        return file;
    }
}
