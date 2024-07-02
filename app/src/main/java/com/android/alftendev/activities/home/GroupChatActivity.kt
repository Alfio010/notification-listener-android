package com.android.alftendev.activities.home

import com.android.alftendev.activities.NotificationListViewerBaseActivity
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.DBUtils.isChatNotifications

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
