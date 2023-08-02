package com.android.whatsappbackup.activities.home

import com.android.whatsappbackup.activities.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.deletedNotification
import com.android.whatsappbackup.utils.DBUtils.deletedNotificationSearch
import io.objectbox.query.LazyList

class DeletedNotificationsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): LazyList<Notifications> {
        return deletedNotification()
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return deletedNotificationSearch(filter)
    }
}
