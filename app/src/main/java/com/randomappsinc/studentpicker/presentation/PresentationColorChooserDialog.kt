package com.randomappsinc.studentpicker.presentation

import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.afollestad.materialdialogs.color.ColorChooserDialog
import com.afollestad.materialdialogs.color.ColorChooserDialog.ColorCallback
import com.randomappsinc.studentpicker.R
import com.randomappsinc.studentpicker.utils.PreferencesManager

class PresentationColorChooserDialog(
        private val fragmentActivity: FragmentActivity,
        private val listener: Listener,
        private val preferencesManager: PreferencesManager
) : ColorCallback {

    interface Listener {
        fun onChooseTextColor(selectedColor: Int)
    }

    private var dialog: ColorChooserDialog
    private val textNormalColor = ContextCompat.getColor(fragmentActivity, R.color.text_normal)

    init {
        dialog = ColorChooserDialog.Builder(fragmentActivity, R.string.set_text_color_title)
                .dynamicButtonColor(false)
                .preselect(preferencesManager.getPresentationTextColor(textNormalColor))
                .build()
    }

    override fun onColorSelection(dialog: ColorChooserDialog, selectedColor: Int) {
        preferencesManager.setPresentationTextColor(selectedColor)
        listener.onChooseTextColor(selectedColor)
    }

    override fun onColorChooserDismissed(dialog: ColorChooserDialog) {}

    fun show() {
        dialog.show(fragmentActivity)
    }
}