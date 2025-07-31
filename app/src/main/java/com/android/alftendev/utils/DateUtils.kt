package com.android.alftendev.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    fun dateFormatter(date: Date): String {
        val formatter = SimpleDateFormat("dd/M/yyyy HH:mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(date)
    }

    fun dateFormatterOnlyDayMonthYear(date: Date): String {
        val formatter = SimpleDateFormat("dd/M/yyyy", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(date)
    }

    fun areDatesEqual(date1: Date?, date2: Date): Boolean {
        if (date1 == null) {
            return false
        }

        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }
}
