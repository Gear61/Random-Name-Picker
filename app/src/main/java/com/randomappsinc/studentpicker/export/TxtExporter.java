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

public class TxtExporter {

    public interface Listener {
        void onTxtFileCreated(Uri fileUri);

        void onTxtExportFailed();
    }

    private Listener listener;
    private Handler backgroundHandler;

    public TxtExporter(Listener listener) {
        this.listener = listener;
        HandlerThread handlerThread = new HandlerThread("TXT Exporter");
        handlerThread.start();
        this.backgroundHandler = new Handler(handlerThread.getLooper());
    }

    public void turnListIntoTxt(int listId, Context context) {
        backgroundHandler.post(() -> {
            DataSource dataSource = new DataSource(context);
            File txtFile = FileUtils.createTxtFileForList(context, dataSource.getListName(listId));
            if (txtFile == null) {
                listener.onTxtExportFailed();
                return;
            }

            List<NameDO> names = dataSource.getNamesInList(listId);

            try {
                FileWriter fileWriter = new FileWriter(txtFile, true);
                for (NameDO nameDO : names) {
                    for (int i = 0; i < nameDO.getAmount(); i++) {
                        fileWriter.append(nameDO.getName()).append(System.lineSeparator());
                    }
                }
                fileWriter.flush();
            } catch (IOException exception) {
                listener.onTxtExportFailed();
                return;
            }

            Uri fileUri = FileProvider.getUriForFile(context, Constants.FILE_PROVIDER_AUTHORITY, txtFile);
            listener.onTxtFileCreated(fileUri);
        });
    }
}
