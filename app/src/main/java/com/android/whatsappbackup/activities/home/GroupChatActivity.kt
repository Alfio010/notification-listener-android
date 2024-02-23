package com.android.whatsappbackup.activities.home

import com.android.whatsappbackup.activities.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.isChatNotifications

class GroupChatActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): List<Notifications> {
        return isChatNotifications().onEach {
            if (it.conversationTitle.isNullOrBlank()) {
                it.conversationTitle = it.title
            } else {
                it.conversationTitle = it.conversationTitle?.substringBeforeLast("(")
            }
        }.distinctBy { it.conversationTitle }
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return getNotifications().filter { it.title.contains(filter) }
    }
}
