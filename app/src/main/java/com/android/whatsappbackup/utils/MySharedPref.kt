package com.android.whatsappbackup.utils

import com.android.whatsappbackup.MyApplication

object MySharedPref {
    const val AUTO_BLACKLIST_ENABLED_STRING = "isAutoBlacklistOn"
    const val NOTIFICATION_ENABLED_STRING = "isNotificationEnabled"
    const val AUTH_ENABLED_STRING = "isAuthEnabled"
    private const val GRAPH_HAVE_TO_ASK_STRING = "graphHaveToAsk"

    fun setAutoBlacklist(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(AUTO_BLACKLIST_ENABLED_STRING, value).apply()
    }

    fun getAutoBlacklistOn(): Boolean {
        return MyApplication.sharedPref.getBoolean(AUTO_BLACKLIST_ENABLED_STRING, true)
    }

    fun setNotificationEnabled(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(NOTIFICATION_ENABLED_STRING, value).apply()
    }

    fun getNotificationEnabled(): Boolean {
        return MyApplication.sharedPref.getBoolean(NOTIFICATION_ENABLED_STRING, true)
    }

    fun setAuthState(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(AUTH_ENABLED_STRING, value).apply()
    }

    fun getAuthState(): Boolean {
        return MyApplication.sharedPref.getBoolean(AUTH_ENABLED_STRING, false)
    }

    fun setGraphHaveToAsk(value: Boolean) {
        MyApplication.sharedPref.edit().putBoolean(GRAPH_HAVE_TO_ASK_STRING, value).apply()
    }

    fun getGraphHaveToAsk(): Boolean {
        return MyApplication.sharedPref.getBoolean(GRAPH_HAVE_TO_ASK_STRING, true)
    }
}
