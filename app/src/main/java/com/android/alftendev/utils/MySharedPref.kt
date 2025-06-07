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
}
