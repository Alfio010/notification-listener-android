package com.android.whatsappbackup.activities.home

import com.android.whatsappbackup.activities.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.isChatNotificationSearch
import com.android.whatsappbackup.utils.DBUtils.isChatNotifications

class ChatsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): List<Notifications> {
        return isChatNotifications()
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return isChatNotificationSearch(filter)
    }
}
