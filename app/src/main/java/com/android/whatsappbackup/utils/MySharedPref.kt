package com.android.whatsappbackup.utils

import com.android.whatsappbackup.MyApplication

object MySharedPref {
    private const val autoBlacklistEnabled = "isAutoBlacklistOn"
    private const val notificationEnabled = "isNotificationEnabled"

    fun setAutoBlacklist(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(autoBlacklistEnabled, value).apply()
    }

    fun isAutoBlacklistOn(): Boolean {
        return MyApplication.sharedPref.getBoolean(autoBlacklistEnabled, true)
    }

    fun setNotificationEnabled(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(notificationEnabled, value).apply()
    }

    fun isNotificationEnabled(): Boolean {
        return MyApplication.sharedPref.getBoolean(notificationEnabled, true)
    }
}
