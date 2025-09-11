package com.android.alftendev.utils.computables

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.android.alftendev.MyApplication.Companion.pm

object AppListCache {
    private var cachedApps: List<ApplicationInfo>? = null

    @SuppressLint("QueryPermissionsNeeded")
    fun getInstalledApps(): List<ApplicationInfo> {
        if (cachedApps == null) {
            cachedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
        }
        return cachedApps!!
    }
}