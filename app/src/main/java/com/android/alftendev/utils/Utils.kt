package com.android.alftendev.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toIcon
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import com.android.alftendev.MyApplication.Companion.pm
import com.android.alftendev.utils.computables.AppListCache
import java.util.Random


object Utils {
    fun openPlayStore(context: Context, pkg: String) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, "market://details?id=$pkg".toUri()))
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    "https://play.google.com/store/apps/details?id=$pkg".toUri()
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
        } catch (_: java.lang.Exception) {
            String()
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    @Suppress("Unused")
    fun getPackageNameFromAppName(appName: String): String? {
        val apps = AppListCache.getInstalledApps()

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

    @SuppressLint("QueryPermissionsNeeded")
    @Suppress("Unused")
    fun isAppInstalled(packageName: String): Boolean {
        return AppListCache.getInstalledApps().any { it.packageName == packageName }
    }

    fun openLink(context: Context, uri: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri.toUri()))
    }

    fun generateRandomColor(): Int {
        return Color.argb(
            255,
            Random().nextInt(256), Random().nextInt(256), Random().nextInt(256)
        )
    }

    fun getDominantColor(drawable: Drawable): Int {
        val bitmap = drawable.toBitmap()
        val palette = Palette.from(bitmap).generate()
        val swatches = palette.swatches.sortedByDescending { it.population }
        for (swatch in swatches) {
            if (!isWhiteOrNearWhite(swatch.rgb)) {
                return swatch.rgb
            }
        }
        return 0xFFFFFFFF.toInt()
    }

    fun isWhiteOrNearWhite(color: Int): Boolean {
        val red = (color shr 16) and 0xFF
        val green = (color shr 8) and 0xFF
        val blue = color and 0xFF
        return red > 240 && green > 240 && blue > 240
    }

    fun getIconFromDrawable(drawable: Drawable): Icon {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return drawable.toBitmap().toIcon()
        }

        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return Icon.createWithBitmap(bitmap)
    }
}
