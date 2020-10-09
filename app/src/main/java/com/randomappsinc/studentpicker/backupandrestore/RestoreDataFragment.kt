package com.randomappsinc.studentpicker.backupandrestore

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.randomappsinc.studentpicker.R
import com.randomappsinc.studentpicker.databinding.RestoreDataBinding
import com.randomappsinc.studentpicker.utils.PermissionUtils
import com.randomappsinc.studentpicker.utils.UIUtils

private const val READ_EXTERNAL_STORAGE_CODE = 2
private const val READ_BACKUP_REQUEST_CODE = 9001

class RestoreDataFragment : Fragment() {

    private var _binding: RestoreDataBinding? = null
    private val binding: RestoreDataBinding get() = _binding!!

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
}