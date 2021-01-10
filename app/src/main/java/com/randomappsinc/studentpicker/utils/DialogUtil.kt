package com.randomappsinc.studentpicker.utils

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.randomappsinc.studentpicker.R

object DialogUtil {

    fun showBackupAndRestoreUpsell(context: Context, onPositiveClick: () -> Unit) {
        MaterialDialog.Builder(context)
                .title(R.string.backup_and_restore_dialog_title)
                .content(R.string.backup_and_restore_dialog_body)
                .positiveText(R.string.backup_and_restore_dialog_positive_button)
                .negativeText(R.string.backup_and_restore_dialog_negative_button)
                .onPositive { _, _ ->
                    onPositiveClick.invoke()
                }
                .cancelable(false)
                .show()
    }
}
