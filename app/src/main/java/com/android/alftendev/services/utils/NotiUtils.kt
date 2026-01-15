package com.android.alftendev.services.utils

import android.app.Notification
import android.service.notification.StatusBarNotification
import com.android.alftendev.BuildConfig
import com.android.alftendev.utils.DBUtils.createBlackListPackageName
import com.android.alftendev.utils.DBUtils.getPackageName
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.MySharedPref.isBlacklistEnabled

object NotiUtils {
    fun shouldDropByPackage(pkg: String): Boolean {
        val packageName = getPackageName(pkg)

        if (packageName != null) {
            if (isBlacklistEnabled()) {
                if (packageName.isBlackList) {
                    return true
                }
            } else {
                if (!packageName.isWhiteList) {
                    return true
                }
            }
        }

        return false
    }

    fun shouldDropByDefaultBlacklist(sbn: StatusBarNotification): Boolean {
        if (MySharedPref.getAutoBlacklistOn()) {
            if (sbn.isOngoing) {
                return true
            }

            if (sbn.notification.category == Notification.CATEGORY_SYSTEM) {
                createBlackListPackageName(sbn.packageName)
                return true
            }
        }

        return false
    }

    fun isAutoBlacklistedNotification(sbn: StatusBarNotification?): Boolean {
        if (sbn == null) {
            return true
        }

        if (sbn.packageName.startsWith("com.whatsapp") && sbn.key!!.contains("null")) {
            return true
        }

        if (sbn.packageName == "com.sec.android.app.clock.package") {
            return true
        }

        if (sbn.packageName == BuildConfig.APPLICATION_ID) {
            return true
        }

        if (sbn.key == "-1|android|27|null|1000") {
            return true
        }

        if (sbn.key == "charging_state") {
            return true
        }

        return sbn.key == "com.sec.android.app.samsungapps|121314|null|10091"
    }
}