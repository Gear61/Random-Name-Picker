package com.randomappsinc.studentpicker.common

import android.content.Context
import androidx.annotation.StringRes
import com.afollestad.materialdialogs.MaterialDialog

class ProgressDialog(context: Context, @StringRes title: Int) {

    private val materialDialog: MaterialDialog = MaterialDialog.Builder(context)
        .title(title)
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