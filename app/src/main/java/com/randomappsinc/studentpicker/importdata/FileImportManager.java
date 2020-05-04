package com.randomappsinc.studentpicker.importdata;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.OpenableColumns;

import androidx.annotation.StringRes;

import com.randomappsinc.studentpicker.R;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.ListDO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

public class FileImportManager {

    public interface Listener {
        void onFileParsingFailure(@StringRes int fileParsingError);

        void onFileParsingSuccess(String listNameText, String namesListText);

        void onFileSaved();
    }

    private Context context;
    private Listener listener;
    private Uri fileUri;
    private Handler backgroundHandler;

    FileImportManager(Context context, Listener listener, Uri fileUri, int fileType) {
        this.context = context;
        this.listener = listener;
        this.fileUri = fileUri;

        HandlerThread handlerThread = new HandlerThread("File parsing thread");
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());

        importFile(fileType);
    }

    private void importFile(int fileType) {
        switch (fileType) {
            case FileImportType.TEXT:
                extractNameListFromText();
                break;
            case FileImportType.CSV:
                extractNameListFromCsv();
                break;
        }
    }

    private void extractNameListFromText() {
        backgroundHandler.post(() -> {
            Cursor cursor = context.getContentResolver().query(
                    fileUri,
                    null,
                    null,
                    null,
                    null,
                    null);

            String listNameText = "";
            if (cursor != null && cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                listNameText = displayName.replace(".txt", "");
                cursor.close();
            }
            StringBuilder namesText = new StringBuilder();
            InputStream inputStream = null;
            BufferedReader reader = null;
            try {
                inputStream = context.getContentResolver().openInputStream(fileUri);
                if (inputStream == null) {
                    throw new IOException("Unable to find .txt file!");
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (namesText.length() > 0) {
                        namesText.append("\n");
                    }
                    namesText.append(line);
                }
            } catch (IOException exception) {
                listener.onFileParsingFailure(R.string.load_file_fail);
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ignored) {}
            }

            listener.onFileParsingSuccess(listNameText, namesText.toString());
        });
    }

    private void extractNameListFromCsv() {
        backgroundHandler.post(() -> {
            Cursor cursor = context.getContentResolver().query(
                    fileUri,
                    null,
                    null,
                    null,
                    null,
                    null);

            String listNameText = "";
            if (cursor != null && cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                listNameText = displayName.replace(".csv", "");
                cursor.close();
            }

            StringBuilder namesText = new StringBuilder();
            InputStream inputStream = null;
            BufferedReader reader = null;
            try {
                inputStream = context.getContentResolver().openInputStream(fileUri);
                if (inputStream == null) {
                    throw new IOException("Unable to find .csv file!");
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                CsvReader csvReader = new CsvReader();
                CsvParser csvParser = csvReader.parse(reader);

                CsvRow row;
                while ((row = csvParser.nextRow()) != null) {
                    String firstName = "";
                    try {
                        firstName = row.getField(0);
                    } catch (IndexOutOfBoundsException ignored) {}
                    String lastName = "";
                    try {
                        lastName = row.getField(1);
                    } catch (IndexOutOfBoundsException ignored) {}

                    if (namesText.length() > 0) {
                        namesText.append("\n");
                    }
                    namesText.append(firstName);
                    namesText.append(" ");
                    namesText.append(lastName);
                }
            } catch (IOException | ArrayIndexOutOfBoundsException exception) {
                listener.onFileParsingFailure(R.string.csv_read_failed);
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException ignored) {}
            }

            listener.onFileParsingSuccess(listNameText, namesText.toString());
        });
    }

    void saveNameList(Context context, String newListName, String[] allNames) {
        backgroundHandler.post(() -> {
            DataSource dataSource = new DataSource(context);
            ListDO newList = dataSource.addNameList(newListName);

            for (String name : allNames) {
                String cleanName = name.trim();
                if (!cleanName.isEmpty()) {
                    dataSource.addNames(cleanName, 1, newList.getId());
                }
            }

            listener.onFileSaved();
        });
    }
}
