package com.android.whatsappbackup

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.SomeUtils.uiDefaultSettings
import com.android.whatsappbackup.utils.adapters.CustomAdapter

abstract class NotificationListViewerBaseActivity : AppCompatActivity() {
    open lateinit var list: ListView
    open lateinit var etSearch: EditText
    private lateinit var adapter: CustomAdapter

    abstract fun getNotifications(): List<Notifications>
    abstract fun getNotificationsBySearch(filter: String): List<Notifications>

    open fun refreshList(notifications: List<Notifications>) {
        adapter = CustomAdapter(this, R.layout.custom_notification_layout, notifications)
        list.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uiDefaultSettings(this)

        setContentView(R.layout.activity_notification_list)

        etSearch = findViewById(R.id.etSearch)
        list = findViewById(R.id.lvAll)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) = Unit
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                refreshList(getNotificationsBySearch(s.toString()))
            }
        })
        refreshList(getNotifications())
    }
}
