package com.android.whatsappbackup.activities

import com.android.whatsappbackup.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.allNotification
import com.android.whatsappbackup.utils.DBUtils.allNotificationSearch
import io.objectbox.query.LazyList

class AllNotificationsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): LazyList<Notifications> {
        return allNotification()
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return allNotificationSearch(filter)
    }
}
