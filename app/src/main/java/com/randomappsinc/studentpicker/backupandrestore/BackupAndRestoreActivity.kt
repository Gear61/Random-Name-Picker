package com.randomappsinc.studentpicker.backupandrestore

import android.os.Bundle
import com.randomappsinc.studentpicker.R
import com.randomappsinc.studentpicker.common.Constants
import com.randomappsinc.studentpicker.common.StandardActivity
import com.randomappsinc.studentpicker.databinding.BackupAndRestoreBinding
import com.randomappsinc.studentpicker.utils.PreferencesManager

class BackupAndRestoreActivity : StandardActivity() {

    private lateinit var binding: BackupAndRestoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = BackupAndRestoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val preferencesManager = PreferencesManager(this)
        preferencesManager.onBackupAndRestoreSeen(true)

        val viewPager = binding.backupAndRestorePager
        viewPager.adapter = BackupAndRestoreTabsAdapter(
                supportFragmentManager,
                resources.getStringArray(R.array.backup_and_restore_tabs))
        binding.backupAndRestoreTabs.setupWithViewPager(viewPager)

        val goToRestore = intent.getBooleanExtra(Constants.GO_TO_RESTORE_IMMEDIATELY_KEY, false)
        if (goToRestore) {
            viewPager.currentItem = 1
        }
    }
}