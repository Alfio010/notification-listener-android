package com.android.whatsappbackup.utils

import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.util.TypedValue
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.whatsappbackup.BuildConfig
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.MyApplication.Companion.pm
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun isNotificationServiceEnabled(context: Context): Boolean {
        return Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
            ?.split(":", ",")
            ?.any { it.startsWith(context.packageName + "/") } == true
    }

    fun showToast(text: String, context: AppCompatActivity) {
        context.runOnUiThread { Toast.makeText(context, text, Toast.LENGTH_LONG).show() }
    }

    fun dateFormatter(date: Date): String {
        val formatter = SimpleDateFormat("dd/M/yyyy HH:mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(date)
    }

    fun isBlacklistedNotification(sbn: StatusBarNotification?): Boolean {
        if (sbn == null) {
            return true
        }

        if (sbn.packageName == MyApplication.pkgWhatsApp && sbn.key!!.contains("null")) {
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

        if (sbn.key == "com.sec.android.app.samsungapps|121314|null|10091") {
            return true
        }

        return false
    }

    fun uiDefaultSettings(activity: AppCompatActivity) {
        activity.supportActionBar?.hide()
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val typedValue = TypedValue()
        activity.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary,
            typedValue,
            true
        )
        val color = ContextCompat.getColor(activity, typedValue.resourceId)
        activity.window.statusBarColor = color
        activity.window.navigationBarColor = color
    }

    fun isDiscordAndBlank(pkgName: String, text: String): Boolean {
        if (pkgName == "com.discord" && text.isBlank()) {
            return true
        }
        return false
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

    @Suppress("DEPRECATION")
    fun getAppName(pkgName: String): String {
        return try {
            pm.getApplicationLabel(pm.getApplicationInfo(pkgName, 0))
                .toString()
        } catch (e: java.lang.Exception) {
            String()
        }
    }

    fun checkPostNotificationPermission(context: Context, activity: AppCompatActivity) {
        if (ActivityCompat.checkSelfPermission(
                context,
                POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED &&
            Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU
        ) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(activity, arrayOf(POST_NOTIFICATIONS), 1)
            }
            return
        }
    }
}
