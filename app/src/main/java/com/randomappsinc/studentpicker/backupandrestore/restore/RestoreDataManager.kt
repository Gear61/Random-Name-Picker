package com.randomappsinc.studentpicker.backupandrestore.restore

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.randomappsinc.studentpicker.database.DataSource
import com.randomappsinc.studentpicker.models.ListDO
import com.randomappsinc.studentpicker.utils.JSONUtils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

object RestoreDataManager {

    interface Listener {

        fun onDataRestorationStarted()

        fun onRestoreCompleted()

        fun onDataRestorationFailed()

        fun onFileNotFound()
    }

    private val backgroundHandler: Handler
    private val uiHandler = Handler(Looper.getMainLooper())
    private var listener: Listener? = null

    init {
        val handlerThread = HandlerThread("Restore Data")
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun restoreData(uri: Uri, context: Context) {
        listener?.onDataRestorationStarted()

        backgroundHandler.post {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                if (inputStream == null) {
                    alertOfFileNotFound()
                    return@post
                }
                val reader = BufferedReader(InputStreamReader(inputStream))
                val stringBuilder = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
                inputStream.close()
                val nameListJson = stringBuilder.toString()
                val listDOs = JSONUtils.deserializeNameListsJson(nameListJson)
                saveNameList(context, listDOs)
            } catch (exception: IOException) {
                alertOfDataRestorationFailed()
            }
        }
    }

    private fun saveNameList(context: Context, listDOs: List<ListDO>) {
        if (listDOs.isEmpty()) {
            alertOfDataRestorationFailed()
            return
        }
        val dataSource = DataSource(context)
        for (listDO in listDOs) {
            val newList = dataSource.addNameList(listDO.name)
            for (nameDO in listDO.namesInList) {
                dataSource.addNames(nameDO.name, nameDO.amount, newList.id)
            }
        }

        listener?.onRestoreCompleted()
    }

    private fun alertOfFileNotFound() {
        uiHandler.post { listener?.onFileNotFound() }
    }

    private fun alertOfDataRestorationFailed() {
        uiHandler.post { listener?.onDataRestorationFailed() }
    }
}