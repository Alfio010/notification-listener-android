package com.android.alftendev.activities.specificactivity

import android.os.Bundle
import com.android.alftendev.MyApplication
import com.android.alftendev.activities.NotificationListViewerBaseActivity
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.AuthUtils
import com.android.alftendev.utils.DBUtils.searchChat

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