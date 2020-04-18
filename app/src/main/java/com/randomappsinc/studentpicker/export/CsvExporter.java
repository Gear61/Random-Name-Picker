package com.randomappsinc.studentpicker.export;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.core.content.FileProvider;

import com.randomappsinc.studentpicker.common.Constants;
import com.randomappsinc.studentpicker.database.DataSource;
import com.randomappsinc.studentpicker.models.NameDO;
import com.randomappsinc.studentpicker.utils.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;

public class CsvExporter {

    public interface Listener {
        void onCsvFileCreated(Uri fileUri);

        void onCsvExportFailed();
    }

    private Listener listener;
    private Handler backgroundHandler;

    public CsvExporter(Listener listener) {
        this.listener = listener;
        HandlerThread handlerThread = new HandlerThread("CSV Exporter");
        handlerThread.start();
        this.backgroundHandler = new Handler(handlerThread.getLooper());
    }

    public void turnListIntoCsv(int listId, Context context) {
        backgroundHandler.post(() -> {
            DataSource dataSource = new DataSource(context);
            File csvFile = FileUtils.createCsvFileForList(context, dataSource.getListName(listId));
            if (csvFile == null) {
                listener.onCsvExportFailed();
                return;
            }

            List<NameDO> names = dataSource.getNamesInList(listId);

            try {
                CsvWriter csvWriter = new CsvWriter();
                FileWriter fileWriter = new FileWriter(csvFile, true);
                CsvAppender csvAppender = csvWriter.append(fileWriter);
                for (NameDO nameDO : names) {
                    String[] namePieces = nameDO.getName().split("\\s+");

                    for (int i = 0; i < nameDO.getAmount(); i++) {
                        csvAppender.appendLine(namePieces);
                    }
                }
                csvAppender.flush();
            } catch (IOException exception) {
                listener.onCsvExportFailed();
                return;
            }

            Uri fileUri = FileProvider.getUriForFile(context, Constants.FILE_PROVIDER_AUTHORITY, csvFile);
            listener.onCsvFileCreated(fileUri);
        });
    }
}
