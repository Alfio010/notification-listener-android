package com.android.whatsappbackup.activities.home

import com.android.whatsappbackup.activities.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.notificationWithoutChat
import com.android.whatsappbackup.utils.DBUtils.notificationWithoutChatSearch

class AllNotificationsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): List<Notifications> {
        return notificationWithoutChat()
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return notificationWithoutChatSearch(filter)
    }
}
