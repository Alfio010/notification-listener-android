package com.android.alftendev.activities.home

import com.android.alftendev.activities.NotificationListViewerBaseActivity
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.DBUtils.notificationWithoutChat
import com.android.alftendev.utils.DBUtils.notificationWithoutChatSearch

class AllNotificationsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): List<Notifications> {
        return notificationWithoutChat()
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return notificationWithoutChatSearch(filter)
    }
}
