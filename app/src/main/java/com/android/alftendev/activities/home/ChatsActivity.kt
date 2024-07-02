package com.android.alftendev.activities.home

import com.android.alftendev.activities.NotificationListViewerBaseActivity
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.DBUtils.isChatNotificationSearch
import com.android.alftendev.utils.DBUtils.isChatNotifications

class ChatsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): List<Notifications> {
        return isChatNotifications()
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return isChatNotificationSearch(filter)
    }
}
