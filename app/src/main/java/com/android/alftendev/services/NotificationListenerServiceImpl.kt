package com.android.alftendev.services

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.android.alftendev.MyApplication
import com.android.alftendev.utils.CustomLog
import com.android.alftendev.utils.DBUtils
import com.android.alftendev.utils.DBUtils.createBlackListPackageName
import com.android.alftendev.utils.DBUtils.createNotification
import com.android.alftendev.utils.DBUtils.createNotificationDeleted
import com.android.alftendev.utils.DBUtils.createPackageName
import com.android.alftendev.utils.DBUtils.lastNotification
import com.android.alftendev.utils.DBUtils.packageNameExists
import com.android.alftendev.utils.DBUtils.searchDeletedNot
import com.android.alftendev.utils.DBUtils.searchOneNot
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.NotificationsUtils.sendNotification
import com.android.alftendev.utils.Utils.isBlacklistedNotification
import com.android.alftendev.utils.Utils.isDiscordAndBlank

class NotificationListenerServiceImpl : NotificationListenerService() {
    companion object {
        val LOGGER = CustomLog("not-listener")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        if (isBlacklistedNotification(sbn)) {
            return
        }

        if (DBUtils.allPackageNameFromTable().any { it.pkg == sbn.packageName && it.isBlackList }) {
            return
        }

        if (MySharedPref.getAutoBlacklistOn() &&
            sbn.notification.category == Notification.CATEGORY_SYSTEM
        ) {
            createBlackListPackageName(sbn.packageName)
            return
        }

        val lastNotifications = lastNotification(sbn.packageName)

        val notificationExtras = sbn.notification.extras ?: return

        val title =
            notificationExtras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()

        val conversationTitle =
            notificationExtras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
                .orEmpty()

        val text = notificationExtras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()

        val bigText =
            notificationExtras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()

        val infoText =
            notificationExtras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString().orEmpty()

        val titleBig =
            notificationExtras.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString().orEmpty()

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

        if (searchOneNot(
                sbn.packageName,
                title,
                sbn.notification.`when`,
                text
            ) != null
        ) {
            return
        }

        if (isDiscordAndBlank(sbn.packageName, text)) {
            return
        }

        val entityDeleted = searchDeletedNot(sbn.packageName, sbn.notification.`when`, title, text)

        if (entityDeleted != null) {
            entityDeleted.isDeleted = true
            MyApplication.notifications.put(entityDeleted)
            sendNotification(this, entityDeleted.title, entityDeleted.text)
            return
        }

        if (packageNameExists(sbn.packageName) == null) {
            MyApplication.packageNames.put(createPackageName(sbn.packageName))
        }

        val save = {
            if (title.isNotEmpty() || text.isNotEmpty()) {
                val notification = createNotification(
                    sbn.packageName,
                    title,
                    sbn.notification.`when`,
                    text,
                    bigText,
                    conversationTitle,
                    infoText,
                    peopleList.toString(),
                    titleBig
                )

                if (notification != null) {
                    LOGGER.log("NOTIFICATION: $notification")

                    MyApplication.notifications.put(notification)
                }
            }
        }

        if (lastNotifications != null) {
            if (lastNotifications.title != title || lastNotifications.text != text) {
                save()
            }
        } else {
            save()
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        if (isBlacklistedNotification(sbn)) {
            return
        }

        if (sbn != null) {
            if (DBUtils.allPackageNameFromTable()
                    .any { it.pkg == sbn.packageName && it.isBlackList }
            ) {
                return
            }

            if (MySharedPref.getAutoBlacklistOn() &&
                sbn.notification.category == Notification.CATEGORY_SYSTEM
            ) {
                createBlackListPackageName(sbn.packageName)
                return
            }
        }

        val notificationExtras = sbn?.notification?.extras ?: return

        val title =
            notificationExtras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()

        val conversationTitle =
            notificationExtras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString()
                .orEmpty()

        val text = notificationExtras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()

        val bigText =
            notificationExtras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString().orEmpty()

        val infoText =
            notificationExtras.getCharSequence(Notification.EXTRA_INFO_TEXT)?.toString().orEmpty()

        val titleBig =
            notificationExtras.getCharSequence(Notification.EXTRA_TITLE_BIG)?.toString().orEmpty()

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

        val entity =
            searchOneNot(sbn.packageName, title, sbn.notification.`when`, text)

        if (entity != null) {
            return
        }

        if (isDiscordAndBlank(sbn.packageName, text)) {
            return
        }

        if (title.isNotEmpty() || text.isNotEmpty()) {
            val deletedNotification =
                sbn.let {
                    createNotificationDeleted(
                        it.packageName,
                        title,
                        it.notification.`when`,
                        text,
                        bigText,
                        conversationTitle,
                        infoText,
                        peopleList.toString(),
                        titleBig
                    )
                }

            if (deletedNotification != null) {
                MyApplication.notifications.put(deletedNotification)
                sendNotification(this, deletedNotification.title, deletedNotification.text)
            }
        }
    }
}
