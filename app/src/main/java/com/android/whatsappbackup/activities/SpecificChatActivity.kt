package com.android.whatsappbackup.activities

import android.os.Bundle
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.searchChat

class SpecificChatActivity : NotificationListViewerBaseActivity() {
    private lateinit var pkgName: String
    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        pkgName = intent.extras!!.getString("pkgName", MyApplication.defaultSwValue)
        title = intent.extras!!.getString("title", "")
        super.onCreate(savedInstanceState)
    }

    override fun getNotifications(): List<Notifications> {
        return searchChat(pkgName, title)
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return getNotifications().filter { it.text.contains(filter) }
    }
}