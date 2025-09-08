package com.android.alftendev.services.utils

import com.android.alftendev.utils.DBUtils.getPackageName
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
}