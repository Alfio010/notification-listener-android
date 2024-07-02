package com.android.alftendev.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.alftendev.R
import com.android.alftendev.activities.MainActivity
import com.android.alftendev.activities.home.DeletedNotificationsActivity
import com.android.alftendev.utils.MySharedPref.NOTIFICATION_ENABLED_STRING
import com.android.alftendev.utils.PermissionUtils.isNotificationPostPermissionEnabled

private const val CHANNEL_ID = "MY_SUPP_NOT"

object NotificationsUtils {
    fun sendNotification(context: Context, title: String, text: String) {
        val sharedPref = context.getSharedPreferences("NotInfo", MODE_PRIVATE)

        if (
            !sharedPref.getBoolean(NOTIFICATION_ENABLED_STRING, true) ||
            !isNotificationPostPermissionEnabled(context)
        ) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.notification_name)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = title
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val intentMain = Intent(context, MainActivity::class.java).apply {
            flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION
        }

        val intentNotification = Intent(
            context,
            DeletedNotificationsActivity::class.java
        ).setAction(Intent.ACTION_MAIN)

        val pendingIntent: PendingIntent =
            PendingIntent.getActivities(
                context, 0,
                arrayOf(intentMain, intentNotification), PendingIntent.FLAG_IMMUTABLE
            )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText("$text...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text).setBigContentTitle(title)
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(1234, builder.build())
            } catch (_: SecurityException) {
            }
        }
    }
}
