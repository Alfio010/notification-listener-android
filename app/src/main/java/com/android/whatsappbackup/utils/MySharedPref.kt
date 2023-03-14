package com.android.whatsappbackup.utils

import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.MyApplication.Companion.application
import com.google.android.material.color.DynamicColors

object MySharedPref {
    private const val autoBlacklistEnabled = "isAutoBlacklistOn"
    private const val notificationEnabled = "isNotificationEnabled"
    private const val dynamicColorsEnabled = "isDynamicColorsEnabled"

    fun setAutoBlacklist(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(autoBlacklistEnabled, value).apply()
    }

    fun getAutoBlacklistOn(): Boolean {
        return MyApplication.sharedPref.getBoolean(autoBlacklistEnabled, true)
    }

    fun setNotificationEnabled(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(notificationEnabled, value).apply()
    }

    fun getNotificationEnabled(): Boolean {
        return MyApplication.sharedPref.getBoolean(notificationEnabled, true)
    }

    fun setDynamicColors(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(dynamicColorsEnabled, value).apply()
        if (value) {
            DynamicColors.applyToActivitiesIfAvailable(application)
        }
    }

    fun getDynamicColors(): Boolean {
        return MyApplication.sharedPref.getBoolean(dynamicColorsEnabled, true)
    }
}
