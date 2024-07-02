package com.android.alftendev.activities.adaptersactivity

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import com.android.alftendev.MyApplication.Companion.defaultSwValue
import com.android.alftendev.R
import com.android.alftendev.activities.NotificationListViewerBaseActivity
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.AuthUtils
import com.android.alftendev.utils.DBUtils.advancedNotificationSearch
import com.android.alftendev.utils.DBUtils.advancedNotificationSearchWithoutText
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.UiUtils
import kotlin.properties.Delegates

class ListSearchActivity : NotificationListViewerBaseActivity() {
    private lateinit var pkgName: String
    private lateinit var text: String
    private var isDeleted by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        pkgName = intent.extras!!.getString("pkgName", defaultSwValue)
        isDeleted = intent.extras!!.getBoolean("isDeleted", false)
        text = intent.extras!!.getString("text", "")

        AuthUtils.askAuth(this)

        setTheme(UiUtils.themeValueToTheme(this, MySharedPref.getThemeOptions()))
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
