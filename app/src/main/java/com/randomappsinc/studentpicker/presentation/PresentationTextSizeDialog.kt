package com.randomappsinc.studentpicker.presentation

import android.content.Context
import com.afollestad.materialdialogs.MaterialDialog
import com.randomappsinc.studentpicker.R

class PresentationTextSizeDialog(context: Context, listener: Listener) {

    interface Listener {
        fun onChooseTextSize(newTextSize: Int)
    }

    private var dialog: MaterialDialog
    private var setTextSizeViewHolder: SetTextSizeViewHolder? = null

    init {
        dialog = MaterialDialog.Builder(context)
                .title(R.string.set_text_size_title)
                .customView(R.layout.set_text_size, true)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .onPositive { _, _ ->
                    val newTextSize: Int = setTextSizeViewHolder!!.textSizeSlider.progress + 1
                    listener.onChooseTextSize(newTextSize)
                }
                .onNegative { _, _ -> setTextSizeViewHolder!!.revertSetting() }
                .build()

        setTextSizeViewHolder = SetTextSizeViewHolder(dialog.customView)
    }

    fun show() {
        dialog.show()
    }
}