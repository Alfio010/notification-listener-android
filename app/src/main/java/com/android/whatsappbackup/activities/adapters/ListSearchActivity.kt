package com.android.whatsappbackup.activities.adapters

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import com.android.whatsappbackup.MyApplication.Companion.defaultSwValue
import com.android.whatsappbackup.NotificationListViewerBaseActivity
import com.android.whatsappbackup.R
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.DBUtils.advancedNotificationSearch
import com.android.whatsappbackup.utils.DBUtils.advancedNotificationSearchWithoutText
import kotlin.properties.Delegates

class ListSearchActivity : NotificationListViewerBaseActivity() {
    private lateinit var pkgName: String
    private lateinit var text: String
    private var isDeleted by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        pkgName = intent.extras!!.getString("pkgName", defaultSwValue)
        isDeleted = intent.extras!!.getBoolean("isDeleted", false)
        text = intent.extras!!.getString("text", "")

        Log.d("aaa-test", "pkg $pkgName deleted $isDeleted text $text")

        super.onCreate(savedInstanceState)
        findViewById<LinearLayout>(R.id.llNotiList).visibility = View.GONE
    }

    override fun getNotifications(): List<Notifications> {
        return if (text.isBlank()) {
            advancedNotificationSearchWithoutText(pkgName, isDeleted)
        } else {
            advancedNotificationSearch(text, pkgName, isDeleted)
        }
    }

    override fun getNotificationsBySearch(filter: String): List<Notifications> {
        return getNotifications().filter { it.text.contains(filter) }
    }
}
