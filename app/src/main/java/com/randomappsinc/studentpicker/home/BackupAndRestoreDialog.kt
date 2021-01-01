package com.randomappsinc.studentpicker.home

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.randomappsinc.studentpicker.R

class BackupAndRestoreDialog(context: Context, onPositiveClick: () -> Unit) {

    val dialog: MaterialDialog = MaterialDialog.Builder(context)
            .title(R.string.never_lost_your_data)
            .content(R.string.explore_backup_and_restore_feature)
            .positiveText(R.string.lets_try)
            .negativeText(R.string.maybe_later)
            .onPositive { _, _ ->
                onPositiveClick.invoke()
            }
            .show()
}