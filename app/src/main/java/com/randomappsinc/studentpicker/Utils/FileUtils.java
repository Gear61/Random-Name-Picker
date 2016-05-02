package com.randomappsinc.studentpicker.Utils;

import android.Manifest;
import android.os.Environment;

import com.randomappsinc.studentpicker.Database.DataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by alexanderchiou on 5/1/16.
 */
public class FileUtils {
    // Create external storage directory for our app if it doesn't exist
    public static void createExternalDirectory() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                File ourDirectory = new File(android.os.Environment.getExternalStorageDirectory(), "RandomNamePicker");
                if (!ourDirectory.exists()) {
                    ourDirectory.mkdirs();
                }
            }
        }
    }

    public static void backupData() {
        if (PermissionUtils.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Set<String> nameLists = PreferencesManager.get().getNameLists();
            for (String listName : nameLists) {
                createListBackup(listName);
            }
        }
    }

    public static void createListBackup(String listName) {
        DataSource dataSource = new DataSource();
        List<String> namesInList = dataSource.getAllNamesInList(listName);
        if (!namesInList.isEmpty()) {
            File listBackup = new File(Environment.getExternalStorageDirectory().getPath() + "/RandomNamePicker",
                    listName + ".txt");

            if (listBackup.exists()) {
                listBackup.delete();
            }

            try {
                FileWriter fw = new FileWriter(listBackup);
                fw.write(getNameListString(namesInList));
                fw.close();
            } catch (IOException ignored) {}
        }
    }

    public static String getNameListString(List<String> names) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            if (i != 0) {
                stringBuilder.append("\n");
            }
            stringBuilder.append(names.get(i));
        }
        return stringBuilder.toString();
    }

    public static File createZipArchive() throws Exception {
        backupData();

        File archive = new File(Environment.getExternalStorageDirectory().getPath() + "/RandomNamePicker",
                "Random Name Picker Data.zip");

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));
        DataSource dataSource = new DataSource();

        Set<String> nameLists = PreferencesManager.get().getNameLists();
        for (String listName : nameLists) {
            List<String> namesInList = dataSource.getAllNamesInList(listName);
            if (!namesInList.isEmpty()) {
                out.putNextEntry(new ZipEntry(listName + ".txt"));
                byte[] data = getNameListString(namesInList).getBytes();
                out.write(data, 0, data.length);
                out.closeEntry();
            }
        }
        out.close();

        return archive;
    }
}
