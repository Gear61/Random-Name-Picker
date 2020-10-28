package com.randomappsinc.studentpicker.backupandrestore.restore

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.randomappsinc.studentpicker.R
import com.randomappsinc.studentpicker.common.ProgressDialog
import com.randomappsinc.studentpicker.databinding.RestoreDataBinding
import com.randomappsinc.studentpicker.utils.PermissionUtils
import com.randomappsinc.studentpicker.utils.UIUtils

private const val READ_EXTERNAL_STORAGE_CODE = 2
private const val READ_BACKUP_REQUEST_CODE = 9001

class RestoreDataFragment : Fragment(), RestoreDataManager.Listener {

    private var _binding: RestoreDataBinding? = null
    private val binding: RestoreDataBinding get() = _binding!!
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = RestoreDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreDataOnClickListener()
        RestoreDataManager.setListener(this)
        progressDialog = ProgressDialog(requireContext(), R.string.restoring_your_data)
    }

    private fun restoreDataOnClickListener() {
        binding.restoreData.setOnClickListener {
            if (PermissionUtils.isPermissionGranted(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    requireContext())) {
                chooseBackupLocation()
            } else {
                PermissionUtils.requestPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE_CODE)
            }
        }
    }

    private fun chooseBackupLocation() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/*"
        }
        startActivityForResult(intent, READ_BACKUP_REQUEST_CODE)
    }

    override fun onDataRestorationStarted() {
        progressDialog.show()
    }

    override fun onRestoreCompleted() {
        progressDialog.dismiss()
        UIUtils.showShortToast(R.string.data_restoration_successful, context)
    }

    override fun onDataRestorationFailed() {
        progressDialog.dismiss()
        UIUtils.showLongToast(R.string.data_restoration_failed, context)
    }

    override fun onFileNotFound() {
        progressDialog.dismiss()
        UIUtils.showLongToast(R.string.backup_file_not_found, context)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == READ_BACKUP_REQUEST_CODE && resultCode == Activity.RESULT_OK && resultData != null) {
            val uri = resultData.data
            uri?.let {
                RestoreDataManager.restoreData(uri, requireContext())
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == READ_EXTERNAL_STORAGE_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            chooseBackupLocation()
        } else {
            UIUtils.showLongToast(R.string.restore_permission_denied, requireContext())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}