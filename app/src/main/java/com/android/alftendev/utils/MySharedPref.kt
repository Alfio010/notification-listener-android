package com.android.alftendev.utils

import android.annotation.SuppressLint
import com.android.alftendev.MyApplication

@SuppressLint("ApplySharedPref")
object MySharedPref {
    const val AUTO_BLACKLIST_ENABLED_STRING = "isAutoBlacklistOn"
    const val NOTIFICATION_ENABLED_STRING = "isNotificationEnabled"
    const val AUTH_ENABLED_STRING = "isAuthEnabled"
    const val THEME_OPTIONS_ENABLED = "isAutoTheme"
    const val RECORD_NOTIFICATIONS_ENABLED = "isRecordNotificationEnabled"
    const val RECORD_NOTIFICATIONS_ALREADY_ASKED = "isRecordNotificationAlreadyAsked"
    const val BLOCK_SCREENSHOT = "areScreenshotBlocked"
    const val IS_BLACKLIST_ENABLED = "isBlacklistEnabled"
    const val IS_AUTO_DELETE_NOTI_ENABLED = "isDeleteNotiDaysIntervalEnabled"
    const val DELETE_NOTI_DAYS_INTERVAL = "deleteNotiDaysInterval"
    const val DELETE_NOTI_OLDER_THAN_DAYS = "deleteNotiOlderThanDays"

    fun getAutoBlacklistOn(): Boolean {
        return MyApplication.sharedPref.getBoolean(AUTO_BLACKLIST_ENABLED_STRING, true)
    }

    fun getAuthState(): Boolean {
        return MyApplication.sharedPref.getBoolean(AUTH_ENABLED_STRING, false)
    }

    fun getThemeOptions(): Int {
        return MyApplication.sharedPref.getInt(THEME_OPTIONS_ENABLED, 1)
    }

    fun getIsRecordNotificationsEnabled(): Boolean {
        return MyApplication.sharedPref.getBoolean(RECORD_NOTIFICATIONS_ENABLED, false)
    }

    fun getIsRecordNotificationsAlreadyAsked(): Boolean {
        return MyApplication.sharedPref.getBoolean(RECORD_NOTIFICATIONS_ALREADY_ASKED, false)
    }

    fun getAreScreenshotBlocked(): Boolean {
        return MyApplication.sharedPref.getBoolean(BLOCK_SCREENSHOT, false)
    }

    fun isDefaultModeBlacklistEnabled(): Boolean {
        return MyApplication.sharedPref.getBoolean(IS_BLACKLIST_ENABLED, true)
    }

    fun isAutoDeleteNotiEnabled(): Boolean {
        return MyApplication.sharedPref.getBoolean(IS_AUTO_DELETE_NOTI_ENABLED, false)
    }

    fun deleteNotiDaysInterval(): Int {
        return MyApplication.sharedPref.getInt(DELETE_NOTI_DAYS_INTERVAL, 0)
    }

    fun deleteNotiOlderThanDays(): Int {
        return MyApplication.sharedPref.getInt(DELETE_NOTI_OLDER_THAN_DAYS, 0)
    }
}
