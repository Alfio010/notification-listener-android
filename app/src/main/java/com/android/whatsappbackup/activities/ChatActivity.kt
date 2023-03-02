package com.android.whatsappbackup.activities

import com.android.whatsappbackup.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.whatsappNotification

class ChatActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): List<Notifications> {
        return whatsappNotification().onEach {
            it.conversationTitle = it.conversationTitle?.substringBeforeLast("(")
        }.distinctBy { it.conversationTitle }
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return getNotifications().filter { it.title.contains(filter) }
    }
}
