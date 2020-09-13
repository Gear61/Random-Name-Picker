package com.randomappsinc.studentpicker.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    private const val EXACT_TIME_FORMAT = "EEEE, MMMM d, yyyy - h:mm:ss a"

    fun getLastBackupTime(unixTime: Long): String {
        val date = Date(unixTime)
        val simpleDateFormat = SimpleDateFormat(EXACT_TIME_FORMAT, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getDefault()
        return simpleDateFormat.format(date)
    }
}