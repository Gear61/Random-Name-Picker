package com.randomappsinc.studentpicker.common

import android.content.Context
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog

class ProgressDialog(context: Context, @StringRes dialogTextResId: Int) {

    private val materialDialog: MaterialDialog = MaterialDialog.Builder(context)
        .content(dialogTextResId)
        .progress(true, 0)
        .cancelable(false)
        .build()

    fun show() {
        materialDialog.show()
    }

    fun dismiss() {
        materialDialog.dismiss()
    }
}