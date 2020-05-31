package com.randomappsinc.studentpicker.extentions

import android.view.View

fun View.maybeVisible(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}