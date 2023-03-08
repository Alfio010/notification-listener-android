package com.android.whatsappbackup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.Utils.uiDefaultSettings
import com.android.whatsappbackup.adapters.NotificationsAdapter

abstract class NotificationListViewerBaseActivity : AppCompatActivity() {
    open lateinit var recycleView: RecyclerView
    open lateinit var etSearch: EditText
    private lateinit var adapter: NotificationsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    abstract fun getNotifications(): List<Notifications>
    abstract fun getNotificationsBySearch(filter: String): List<Notifications>

    open fun refreshList(notifications: List<Notifications>) {
        adapter = NotificationsAdapter(notifications, this)
        recycleView.layoutManager = linearLayoutManager
        recycleView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiDefaultSettings(this)

        setContentView(R.layout.activity_notification_list)

        etSearch = findViewById(R.id.etSearch)
        recycleView = findViewById(R.id.lvAll)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) = Unit
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                refreshList(getNotificationsBySearch(s.toString()))
            }
        })
        refreshList(getNotifications())
    }
}
