package com.android.whatsappbackup.utils

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.app.NotificationManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
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
import com.android.whatsappbackup.MyApplication.Companion.pm
import com.android.whatsappbackup.R
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun isNotificationServiceEnabled(context: Context): Boolean {
        return Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners")
            ?.split(":", ",")
            ?.any { it.startsWith(context.packageName + "/") } == true
    }

    private fun showToast(text: String, context: AppCompatActivity) {
        context.runOnUiThread { Toast.makeText(context, text, Toast.LENGTH_LONG).show() }
    }

    fun askNotificationServicePermission(context: Context) {
        if (!isNotificationServiceEnabled(context)) {
            showToast(context.getString(R.string.ask_not_permission), context as AppCompatActivity)

            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }

            context.startActivity(intent)
        }
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

        if (sbn.packageName == "com.whatsapp" && sbn.key!!.contains("null")) {
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

    fun uiDefaultSettings(activity: AppCompatActivity, isIconAllowed: Boolean) {
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
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        if (isIconAllowed) {
            activity.supportActionBar?.setIcon(R.mipmap.ic_launcher_foreground)
        }
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

    fun openApp(pkgName: String, context: Context) {
        val launchIntent = pm.getLaunchIntentForPackage(pkgName)
        if (launchIntent != null) {
            context.startActivity(launchIntent)
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

    private fun isNotificationPostPermissionEnabled(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(
                context,
                POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.areNotificationsEnabled()
        }
    }

    fun checkPostNotificationPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !isNotificationPostPermissionEnabled(
                context
            )
        ) {
            ActivityCompat.requestPermissions(context as Activity, arrayOf(POST_NOTIFICATIONS), 1)
        }
    }

    fun openLink(context: Context, uri: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }

    fun isDarkThemeOn(context: Context): Boolean {
        return context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == UI_MODE_NIGHT_YES
    }
}
