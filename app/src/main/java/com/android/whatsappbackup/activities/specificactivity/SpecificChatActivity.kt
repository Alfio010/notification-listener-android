package com.android.whatsappbackup.activities.specificactivity

import android.os.Bundle
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.activities.NotificationListViewerBaseActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.AuthUtils
import com.android.whatsappbackup.utils.DBUtils.searchChat

class SpecificChatActivity : NotificationListViewerBaseActivity() {
    private lateinit var pkgName: String
    private lateinit var chatTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        pkgName = intent.extras!!.getString("pkgName", MyApplication.defaultSwValue)
        chatTitle = intent.extras!!.getString("title", "")

        AuthUtils.askAuth(this)

        super.onCreate(savedInstanceState)
        title = chatTitle
    }

    override fun getNotifications(): List<Notifications> {
        return searchChat(pkgName, chatTitle)
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return getNotifications().filter { it.text.contains(filter) }
    }
}