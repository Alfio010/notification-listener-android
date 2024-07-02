package com.android.alftendev.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.alftendev.MyApplication
import com.android.alftendev.R
import com.android.alftendev.adapters.NotificationsAdapter
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.AuthUtils.askAuth
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.UiUtils
import com.android.alftendev.utils.UiUtils.uiDefaultSettings

abstract class NotificationListViewerBaseActivity : AppCompatActivity() {
    open lateinit var recycleView: RecyclerView
    open lateinit var etSearch: EditText
    private lateinit var adapter: NotificationsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    abstract fun getNotifications(): List<Notifications>
    abstract fun getNotificationsBySearch(filter: String): List<Notifications>

    open fun refreshList(notifications: List<Notifications>) {
        runOnUiThread {
            adapter = NotificationsAdapter(notifications, this)
            recycleView.layoutManager = linearLayoutManager
            recycleView.adapter = adapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(UiUtils.themeValueToTheme(this, MySharedPref.getThemeOptions()))
        super.onCreate(savedInstanceState)

        askAuth(this)

        runOnUiThread {
            uiDefaultSettings(this, true)
            setContentView(R.layout.activity_notification_list)
        }

        etSearch = findViewById(R.id.etSearch)
        recycleView = findViewById(R.id.lvAll)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) = Unit
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                MyApplication.executor.submit { refreshList(getNotificationsBySearch(s.toString())) }
            }
        })

        MyApplication.executor.submit { refreshList(getNotifications()) }

        onBackPressedDispatcher.addCallback {
            finishAndRemoveTask()
        }
    }
}
