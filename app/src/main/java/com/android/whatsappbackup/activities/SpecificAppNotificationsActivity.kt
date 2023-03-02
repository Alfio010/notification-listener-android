package com.android.whatsappbackup.activities

import com.android.whatsappbackup.MyApplication.Companion.pkgWhatsApp
import com.android.whatsappbackup.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.perAppAllNotifications
import com.android.whatsappbackup.utils.DBUtils.perPackageNotificationSearch

class SpecificAppNotificationsActivity : NotificationListViewerBaseActivity() {
    override fun getNotifications(): List<Notifications> {
        return perAppAllNotifications(pkgWhatsApp)
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return perPackageNotificationSearch(filter, pkgWhatsApp)
    }
}
