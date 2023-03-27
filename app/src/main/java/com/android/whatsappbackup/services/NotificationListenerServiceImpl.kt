package com.android.whatsappbackup.services

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.utils.CustomLog
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.DBUtils.createBlackListPackageName
import com.android.whatsappbackup.utils.DBUtils.createNotification
import com.android.whatsappbackup.utils.DBUtils.createNotificationDeleted
import com.android.whatsappbackup.utils.DBUtils.createPackageName
import com.android.whatsappbackup.utils.DBUtils.lastNotification
import com.android.whatsappbackup.utils.DBUtils.packageNameExists
import com.android.whatsappbackup.utils.DBUtils.searchDeletedNot
import com.android.whatsappbackup.utils.DBUtils.searchOneNot
import com.android.whatsappbackup.utils.MySharedPref
import com.android.whatsappbackup.utils.NotificationsUtils.sendNotification
import com.android.whatsappbackup.utils.SomeLists
import com.android.whatsappbackup.utils.Utils.isBlacklistedNotification
import com.android.whatsappbackup.utils.Utils.isDiscordAndBlank

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

        if (MySharedPref.getAutoBlacklistOn() && SomeLists.blackListedNotificationKeys.any {
                sbn.packageName.contains(
                    it
                )
            }) {
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
            notificationExtras.getCharSequence(Notification.EXTRA_PEOPLE_LIST)?.toString().orEmpty()
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
                    peopleList,
                    titleBig
                )

                if (notification != null) {
                    LOGGER.doLog("NOTIFICATION: $notification")

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
                SomeLists.blackListedNotificationKeys.any { sbn.packageName.contains(it) }
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
            notificationExtras.getCharSequence(Notification.EXTRA_PEOPLE_LIST)?.toString().orEmpty()
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
                        peopleList,
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
