package com.randomappsinc.studentpicker.backupandrestore.backup

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import com.randomappsinc.studentpicker.database.DataSource
import com.randomappsinc.studentpicker.utils.JSONUtils
import com.randomappsinc.studentpicker.utils.PreferencesManager
import java.io.FileOutputStream

object BackupDataManager {
    const val BACKUP_FILE_NAME = "random-name-picker-backup.txt"

    interface Listener {
        fun onBackupComplete()
        fun onBackupFailed()
    }

    private val backgroundHandler: Handler
    private val uiHandler = Handler(Looper.getMainLooper())
    private var listener: Listener? = null

    init {
        val handlerThread = HandlerThread("Backup Data")
        handlerThread.start()
        backgroundHandler = Handler(handlerThread.looper)
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    fun setBackupUri(context: Context?, backupUri: String) {
        PreferencesManager(context).backupUri = backupUri
    }

    fun getBackupUri(context: Context?): String? {
        return PreferencesManager(context).backupUri
    }

    fun getBackupUriForExporting(context: Context?): Uri? {
        val preferencesManager = PreferencesManager(context)
        val backupUri = preferencesManager.backupUri
        return if (backupUri != null) {
            Uri.parse(backupUri)
        } else null
    }

    fun backupData(context: Context, userTriggered: Boolean) {
        val dataSource = DataSource(context)
        val preferencesManager = PreferencesManager(context)
        val backupUri = getBackupUri(context)
        if (backupUri != null) {
            backgroundHandler.post {
                try {
                    val fileDescriptor =
                        context.contentResolver.openFileDescriptor(Uri.parse(backupUri), "w")
                    if (fileDescriptor == null) {
                        if (userTriggered) {
                            alertListenerOfBackupFail()
                        }
                        return@post
                    }
                    FileOutputStream(fileDescriptor.fileDescriptor).run {
                        write(JSONUtils.serializeNameList(dataSource.allNameList).toByteArray())
                        close()
                    }
                    fileDescriptor.close()
                    preferencesManager.updateLastBackupTime()
                    if (userTriggered) {
                        alertListenerOfBackupComplete()
                    }
                } catch (exception: Exception) {
                    if (userTriggered) {
                        alertListenerOfBackupFail()
                    }
                }
            }
        } else {
            if (userTriggered) {
                alertListenerOfBackupFail()
            }
        }
    }

    private fun alertListenerOfBackupComplete() {
        uiHandler.post { listener?.onBackupComplete() }
    }

    private fun alertListenerOfBackupFail() {
        uiHandler.post { listener?.onBackupFailed() }
    }
}