package com.android.alftendev.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.service.notification.StatusBarNotification
import com.android.alftendev.BuildConfig
import com.android.alftendev.MyApplication.Companion.pm
import java.util.Random


object Utils {
    fun isBlacklistedNotification(sbn: StatusBarNotification?): Boolean {
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

    fun isDiscordAndBlank(pkgName: String, text: String): Boolean {
        return pkgName == "com.discord" && text.isBlank()
    }

    fun openPlayStore(context: Context, pkg: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")))
        } catch (e: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
                )
            )
        }
    }

    fun openApp(pkgName: String, context: Context) {
        val launchIntent = pm.getLaunchIntentForPackage(pkgName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
        }
    }

    fun getAppNameFromPackageName(pkgName: String): String {
        return try {
            pm.getApplicationLabel(pm.getApplicationInfo(pkgName, 0))
                .toString()
        } catch (e: java.lang.Exception) {
            String()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun getPackageNameFromAppName(appName: String): String? {
        val apps by lazy { pm.getInstalledApplications(PackageManager.GET_META_DATA) }

        for (appInfo in apps) {
            val label = pm.getApplicationLabel(appInfo).toString()
            if (label == appName) {
                return appInfo.packageName
            }
        }

        if (getAppNameFromPackageName(appName) != String()) {
            return appName
        }

        return null
    }

    fun openLink(context: Context, uri: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }

    fun generateRandomColor(): Int {
        return Color.argb(
            255,
            Random().nextInt(256), Random().nextInt(256), Random().nextInt(256)
        )
    }

    fun getDominantColor(bitmap: Bitmap): Int {
        val newBitmap = Bitmap.createScaledBitmap(bitmap, 1, 1, true)
        val color = newBitmap.getPixel(0, 0)
        newBitmap.recycle()
        return color
    }
}
