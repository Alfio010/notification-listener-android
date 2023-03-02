package com.android.whatsappbackup.activities

import com.android.whatsappbackup.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.deletedNotification
import com.android.whatsappbackup.utils.DBUtils.deletedNotificationSearch

class DeletedNotificationsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): List<Notifications> {
        return deletedNotification()
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return deletedNotificationSearch(filter)
    }
}
