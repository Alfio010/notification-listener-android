package com.android.alftendev.activities.home

import com.android.alftendev.activities.NotificationListViewerBaseActivity
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.DBUtils.deletedNotification
import com.android.alftendev.utils.DBUtils.deletedNotificationSearch
import io.objectbox.query.LazyList

class DeletedNotificationsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): LazyList<Notifications> {
        return deletedNotification()
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return deletedNotificationSearch(filter)
    }
}
