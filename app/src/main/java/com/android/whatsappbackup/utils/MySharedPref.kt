package com.android.whatsappbackup.utils

import android.annotation.SuppressLint
import com.android.whatsappbackup.MyApplication

@SuppressLint("ApplySharedPref")
object MySharedPref {
    const val AUTO_BLACKLIST_ENABLED_STRING = "isAutoBlacklistOn"
    const val NOTIFICATION_ENABLED_STRING = "isNotificationEnabled"
    const val AUTH_ENABLED_STRING = "isAuthEnabled"
    const val THEME_OPTIONS_ENABLED = "isAutoTheme"

    fun setAutoBlacklist(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(AUTO_BLACKLIST_ENABLED_STRING, value).commit()
    }

    fun getAutoBlacklistOn(): Boolean {
        return MyApplication.sharedPref.getBoolean(AUTO_BLACKLIST_ENABLED_STRING, true)
    }

    fun setNotificationEnabled(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(NOTIFICATION_ENABLED_STRING, value).commit()
    }

    fun getNotificationEnabled(): Boolean {
        return MyApplication.sharedPref.getBoolean(NOTIFICATION_ENABLED_STRING, true)
    }

    fun setAuthState(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(AUTH_ENABLED_STRING, value).commit()
    }

    fun getAuthState(): Boolean {
        return MyApplication.sharedPref.getBoolean(AUTH_ENABLED_STRING, false)
    }

    fun setThemeOptions(value: Int) {
        MyApplication.sharedPref.edit().putInt(THEME_OPTIONS_ENABLED, value).commit()
    }

    fun getThemeOptions(): Int {
        return MyApplication.sharedPref.getInt(THEME_OPTIONS_ENABLED, 1)
    }
}
