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
import com.android.whatsappbackup.utils.SomeUtils.isBlacklistedNotification
import com.android.whatsappbackup.utils.SomeUtils.isDiscordAndBlank

class NotificationListenerServiceImpl : NotificationListenerService() {
    companion object {
        val LOGGER = CustomLog("not-listener")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        val key = sbn.key.toString()

        if (isBlacklistedNotification(sbn)) {
            return
        }

        if (DBUtils.allPackageNameFromTable().any { it.pkg == sbn.packageName && it.isBlackList }) {
            return
        }

        if (MySharedPref.isAutoBlacklistOn() && SomeLists.blackListedNotificationKeys.any {
                sbn.packageName.contains(
                    it
                )
            }) {
            createBlackListPackageName(sbn.packageName, this)
            return
        }

        var title = ""
        var text = ""
        var bigText = ""
        var conversationTitle = ""
        var infoText = ""
        var peopleList = ""
        var titleBig = ""

        val lastNotifications = lastNotification(sbn.packageName)

        LOGGER.doLog("key: $key")

        val notificationExtras = sbn.notification.extras ?: return

        notificationExtras.getString(Notification.EXTRA_TITLE)?.let {
            LOGGER.doLog("extra-title: $it")
            title = it
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationExtras.getString(Notification.EXTRA_CONVERSATION_TITLE)?.let {
                LOGGER.doLog("conversation-title: $it")
                conversationTitle = it
            }
        }

        notificationExtras.getString(Notification.EXTRA_TEXT)?.let {
            LOGGER.doLog("extra-text: $it")
            text = it
        }

        notificationExtras.getString(Notification.EXTRA_BIG_TEXT)?.let {
            LOGGER.doLog("big-text: $it")
            bigText = it
        }

        notificationExtras.getString(Notification.EXTRA_INFO_TEXT)?.let {
            LOGGER.doLog("info-text: $it")
            infoText = it
        }

        notificationExtras.getString(Notification.EXTRA_TITLE_BIG)?.let {
            LOGGER.doLog("title big: $it")
            titleBig = it
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            notificationExtras.getString(Notification.EXTRA_PEOPLE_LIST)?.let {
                LOGGER.doLog("people list: $it")
                peopleList = it
            }
        }

        if (searchOneNot(sbn.packageName, title, sbn.notification.`when`, text) != null) {
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
                MyApplication.notifications.put(notification)

                if (packageNameExists(sbn.packageName) == null) {
                    MyApplication.packagenames.put(createPackageName(sbn.packageName, this))
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

            if (MySharedPref.isAutoBlacklistOn() &&
                SomeLists.blackListedNotificationKeys.any { sbn.packageName.contains(it) }
            ) {
                createBlackListPackageName(sbn.packageName, this)
                return
            }
        }

        var title = ""
        var text = ""
        var bigText = ""
        var conversationTitle = ""
        var infoText = ""
        var peopleList = ""
        var titleBig = ""

        val notificationExtras = sbn?.notification?.extras ?: return

        notificationExtras.getString(Notification.EXTRA_TITLE)?.let {
            LOGGER.doLog("extra-title: $it")
            title = it
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notificationExtras.getString(Notification.EXTRA_CONVERSATION_TITLE)?.let {
                LOGGER.doLog("conversation-title: $it")
                conversationTitle = it
            }
        }

        notificationExtras.getString(Notification.EXTRA_TEXT)?.let {
            LOGGER.doLog("extra-text: $it")
            text = it
        }

        notificationExtras.getString(Notification.EXTRA_BIG_TEXT)?.let {
            LOGGER.doLog("big-text: $it")
            bigText = it
        }

        notificationExtras.getString(Notification.EXTRA_INFO_TEXT)?.let {
            LOGGER.doLog("info-text: $it")
            infoText = it
        }

        notificationExtras.getString(Notification.EXTRA_TITLE_BIG)?.let {
            LOGGER.doLog("title big: $it")
            titleBig = it
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            notificationExtras.getString(Notification.EXTRA_PEOPLE_LIST)?.let {
                LOGGER.doLog("people list: $it")
                peopleList = it
            }
        }

        val entity = sbn.let { searchOneNot(it.packageName, title, it.notification.`when`, text) }

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

            MyApplication.notifications.put(deletedNotification)

            sendNotification(this, deletedNotification.title, deletedNotification.text)
        }
    }
}
