package com.android.whatsappbackup.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.home.DeletedNotificationsActivity

const val icon = R.mipmap.ic_launcher
private const val channelID = "MY_SUPP_NOT"

object NotificationsUtils {
    fun sendNotification(context: Context, title: String, text: String) {
        if (MySharedPref.getNotificationEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.notification_name)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelID, name, importance).apply {
                    description = title
                }
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, DeletedNotificationsActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            val builder = NotificationCompat.Builder(context, channelID)
                .setSmallIcon(icon)
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
                if (Utils.isNotificationServiceEnabled(context)) {
                    try {
                        notify(1234, builder.build())
                    }catch (_: SecurityException) { }
                }
            }
        }
    }
}
