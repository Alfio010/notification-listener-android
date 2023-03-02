package com.android.whatsappbackup.activities

import com.android.whatsappbackup.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.allNotification
import com.android.whatsappbackup.utils.DBUtils.allNotificationSearch

class AllNotificationsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): List<Notifications> {
        return allNotification()
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return allNotificationSearch(filter)
    }
}
