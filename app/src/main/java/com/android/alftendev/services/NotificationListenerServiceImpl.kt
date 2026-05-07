package com.android.alftendev.services

import android.app.Notification
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.collection.LruCache
import com.android.alftendev.MyApplication
import com.android.alftendev.services.utils.NotiUtils.isAutoBlacklistedNotification
import com.android.alftendev.services.utils.NotiUtils.shouldDropByDefaultBlacklist
import com.android.alftendev.services.utils.NotiUtils.shouldDropByPackage
import com.android.alftendev.utils.CustomLog
import com.android.alftendev.utils.DBUtils.createNotification
import com.android.alftendev.utils.DBUtils.createNotificationDeleted
import com.android.alftendev.utils.DBUtils.createPackageName
import com.android.alftendev.utils.DBUtils.getPackageName
import com.android.alftendev.utils.DBUtils.searchDeletedNot
import com.android.alftendev.utils.DBUtils.searchOneNot
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.NotificationsUtils.sendNotification
import java.util.concurrent.Executors

class NotificationListenerServiceImpl : NotificationListenerService() {
    private data class LastNotiData(val title: String, val text: String, val date: Long)

    private data class NotificationData(
        val title: String,
        val text: String,
        val bigText: String,
        val conversationTitle: String,
        val infoText: String,
        val peopleList: String,
        val titleBig: String
    )

    private val notiExecutor = Executors.newSingleThreadExecutor()
    private val recentNotificationsCache = LruCache<String, LastNotiData>(30)

    companion object {
        val LOGGER = CustomLog("not-listener")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)

        if (shouldIgnoreNotification(sbn)) {
            return
        }

        val safeSbn = sbn!!

        notiExecutor.execute {
            val notificationExtras = safeSbn.notification.extras ?: return@execute

            val notificationData = getNotificationFromExtras(notificationExtras)

            if (notificationData.title.isEmpty() && notificationData.text.isEmpty()) {
                return@execute
            }

            val currentNotiData = LastNotiData(notificationData.title, notificationData.text, safeSbn.notification.`when`)

            val lastNotification = recentNotificationsCache[safeSbn.packageName]

            if (lastNotification == currentNotiData) {
                return@execute
            }

            if (lastNotification != null) {
                val isSameContent = currentNotiData.title == lastNotification.title && currentNotiData.text == lastNotification.text

                if (isSameContent && currentNotiData.date - lastNotification.date <= 30 * 1000) {
                    return@execute
                }
            }

            val entityDeleted =
                searchDeletedNot(
                    safeSbn.packageName,
                    safeSbn.notification.`when`,
                    notificationData.title,
                    notificationData.text
                )

            if (entityDeleted != null) {
                entityDeleted.isDeleted = true
                MyApplication.notifications.put(entityDeleted)
                sendNotification(this, entityDeleted.title, entityDeleted.text)
                return@execute
            }

            if (getPackageName(safeSbn.packageName) == null) {
                MyApplication.packageNames.put(createPackageName(safeSbn.packageName))
            }

            val notification = createNotification(
                safeSbn.packageName,
                notificationData.title,
                safeSbn.notification.`when`,
                notificationData.text,
                notificationData.bigText,
                notificationData.conversationTitle,
                notificationData.infoText,
                notificationData.peopleList,
                notificationData.titleBig
            )

            if (notification != null) {
                LOGGER.log("SAVED NOTIFICATION: $notification")

                MyApplication.notifications.put(notification)
            }

            recentNotificationsCache.put(
                safeSbn.packageName,
                currentNotiData
            )
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (shouldIgnoreNotification(sbn)) {
            return
        }

        val safeSbn = sbn!!

        notiExecutor.execute {
            val notificationExtras = safeSbn.notification?.extras ?: return@execute

            val notificationData = getNotificationFromExtras(notificationExtras)

            val entity =
                searchOneNot(
                    safeSbn.packageName,
                    notificationData.title,
                    safeSbn.notification.`when`,
                    notificationData.text
                )

            if (entity != null) {
                return@execute
            }

            if (notificationData.title.isNotEmpty() || notificationData.text.isNotEmpty()) {
                val deletedNotification =
                    safeSbn.let {
                        createNotificationDeleted(
                            it.packageName,
                            notificationData.title,
                            it.notification.`when`,
                            notificationData.text,
                            notificationData.bigText,
                            notificationData.conversationTitle,
                            notificationData.infoText,
                            notificationData.peopleList,
                            notificationData.titleBig
                        )
                    }

                if (deletedNotification != null) {
                    MyApplication.notifications.put(deletedNotification)
                    sendNotification(this, deletedNotification.title, deletedNotification.text)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notiExecutor.shutdown()
    }

    private fun shouldIgnoreNotification(sbn: StatusBarNotification?): Boolean {
        if (sbn == null) return true
        if (!MySharedPref.getIsRecordNotificationsEnabled()) return true
        if (isAutoBlacklistedNotification(sbn)) return true
        if (shouldDropByPackage(sbn.packageName)) return true
        if (shouldDropByDefaultBlacklist(sbn)) return true
        return false
    }

    private fun getNotificationFromExtras(notificationExtras: Bundle): NotificationData {
        val title =
            notificationExtras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()

        val conversationTitle =
            notificationExtras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)
                ?.toString()
                .orEmpty()

        val text =
            notificationExtras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()

        val bigText =
            notificationExtras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
                .orEmpty()

        val infoText =
            notificationExtras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString()
                .orEmpty()

        val titleBig =
            notificationExtras.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString()
                .orEmpty()

        val peopleList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                notificationExtras.getCharSequenceArrayList(Notification.EXTRA_PEOPLE_LIST)
                    ?.toString().orEmpty()
            } catch (e: ClassCastException) {
                LOGGER.log("error casting: ${e.stackTraceToString()}")
            }
        } else {
            String()
        }

        return NotificationData(
            title = title,
            text = text,
            bigText = bigText,
            conversationTitle = conversationTitle,
            infoText = infoText,
            peopleList = peopleList as String,
            titleBig = titleBig
        )
    }
}
