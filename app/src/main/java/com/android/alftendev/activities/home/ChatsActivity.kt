package com.android.alftendev.activities.home

import com.android.alftendev.activities.NotificationListViewerBaseActivity
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.DBUtils.getChatNotifications
import com.android.alftendev.utils.DBUtils.isChatNotificationSearch

class ChatsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): List<Notifications> {
        return getChatNotifications()
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return isChatNotificationSearch(filter)
    }
}
