package com.android.alftendev.services

import android.app.Notification
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.android.alftendev.MyApplication
import com.android.alftendev.services.utils.NotiUtils.isAutoBlacklistedNotification
import com.android.alftendev.services.utils.NotiUtils.shouldDropByDefaultBlacklist
import com.android.alftendev.services.utils.NotiUtils.shouldDropByPackage
import com.android.alftendev.utils.CustomLog
import com.android.alftendev.utils.DBUtils.createNotification
import com.android.alftendev.utils.DBUtils.createNotificationDeleted
import com.android.alftendev.utils.DBUtils.createPackageName
import com.android.alftendev.utils.DBUtils.getPackageName
import com.android.alftendev.utils.DBUtils.lastNotification
import com.android.alftendev.utils.DBUtils.searchDeletedNot
import com.android.alftendev.utils.DBUtils.searchOneNot
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.NotificationsUtils.sendNotification
import java.util.concurrent.Executors

class NotificationListenerServiceImpl : NotificationListenerService() {
    private val notiExecutor = Executors.newSingleThreadExecutor()

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
            val lastNotifications = lastNotification(safeSbn.packageName)

            val notificationExtras = safeSbn.notification.extras ?: return@execute

            val notificationData = getNotificationFromExtras(notificationExtras)

            if (searchOneNot(
                    safeSbn.packageName,
                    notificationData.title,
                    safeSbn.notification.`when`,
                    notificationData.text
                ) != null
            ) {
                return@execute
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

            val save = {
                if (notificationData.title.isNotEmpty() || notificationData.text.isNotEmpty()) {
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
                }
            }

            if (lastNotifications != null) {
                if (lastNotifications.title != notificationData.title || lastNotifications.text != notificationData.text) {
                    save()
                }
            } else {
                save()
            }
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

    private data class NotificationData(
        val title: String,
        val text: String,
        val bigText: String,
        val conversationTitle: String,
        val infoText: String,
        val peopleList: String,
        val titleBig: String
    )

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
