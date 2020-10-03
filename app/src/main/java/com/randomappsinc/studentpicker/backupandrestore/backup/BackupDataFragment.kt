package com.randomappsinc.studentpicker.backupandrestore.backup

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.randomappsinc.studentpicker.R
import com.randomappsinc.studentpicker.databinding.BackupDataBinding
import com.randomappsinc.studentpicker.utils.PermissionUtils
import com.randomappsinc.studentpicker.utils.PreferencesManager
import com.randomappsinc.studentpicker.utils.TimeUtils
import com.randomappsinc.studentpicker.utils.UIUtils

private const val WRITE_EXTERNAL_STORAGE_CODE = 1
private const val WRITE_BACKUP_FILE_CODE = 350

class BackupDataFragment : Fragment() {

    private var _binding: BackupDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var preferencesManager: PreferencesManager

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = BackupDataBinding.inflate(inflater, container, false)
        preferencesManager = PreferencesManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpBackupButtonOnClickListener()
        setBackupSubtitle()
        changeBackupFolderOnClickListener()
        exportDataOnClickListener()
        BackupDataManager.setListener(backupDataListener)
    }

    fun setBackupSubtitle() {
        if (BackupDataManager.getBackupUri(context) == null) {
            binding.backupSubtitle.setText(R.string.backup_data_explanation)
        } else {
            val lastBackupUnixTime: Long = preferencesManager.lastBackupTime
            val lastBackupTime: String = TimeUtils.getLastBackupTime(lastBackupUnixTime)
            binding.backupSubtitle.text = getString(R.string.backup_subtitle_with_backup, lastBackupTime)
        }
    }

    private fun setUpBackupButtonOnClickListener() {
        binding.backupData.setOnClickListener {
            if (PermissionUtils.isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE, context)) {
                backupData()
            } else {
                PermissionUtils.requestPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE_CODE)
            }
        }
    }

    private fun backupData() {
        val backupUri: String? = BackupDataManager.getBackupUri(context)
        if (backupUri == null) {
            chooseBackupLocation()
        } else {
            BackupDataManager.backupData(requireContext(), true)
        }
    }

    private fun chooseBackupLocation() {
        UIUtils.showLongToast(R.string.choose_backup_folder, context)
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, BackupDataManager.BACKUP_FILE_NAME)
        }
        startActivityForResult(intent, WRITE_BACKUP_FILE_CODE)
    }

    private fun changeBackupFolderOnClickListener() {
        binding.changeBackupFolder.setOnClickListener {
            if (PermissionUtils.isPermissionGranted(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, context)) {
                chooseBackupLocation()
            } else {
                PermissionUtils.requestPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE_CODE)
            }
        }
    }

   private fun exportDataOnClickListener() {
        binding.exportData.setOnClickListener {
            val backupUri: Uri? = BackupDataManager.getBackupUriForExporting(context)
            if (backupUri == null) {
                UIUtils.showLongToast(R.string.cannot_export_nothing, context)
                return@setOnClickListener
            }
            val sharingIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/*"
                putExtra(Intent.EXTRA_STREAM, backupUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(sharingIntent, getString(R.string.export_data_with)))
        }
    }

    private val backupDataListener: BackupDataManager.Listener = object : BackupDataManager.Listener {
        override fun onBackupComplete() {
            UIUtils.showShortToast(R.string.backup_successful, context)
            setBackupSubtitle()
        }

        override fun onBackupFailed() {
            UIUtils.showLongToast(R.string.backup_failed, context)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == WRITE_BACKUP_FILE_CODE && resultCode == Activity.RESULT_OK && resultData != null) {
            val context = requireContext()
            val uri = resultData.data
            if (uri == null) {
                backupDataListener.onBackupFailed()
            } else {
                // Persist ability to read/write to this file
                val takeFlags = (resultData.flags
                        and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION))
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)

                BackupDataManager.setBackupUri(requireContext(), uri.toString())
                BackupDataManager.backupData(requireContext(), true)
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String?>,
            grantResults: IntArray) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_CODE &&
                grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            backupData()
        } else {
            UIUtils.showLongToast(R.string.backup_permission_denied, requireContext())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}