package com.android.whatsappbackup.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.home.AllNotificationsActivity
import com.android.whatsappbackup.activities.home.ChatsActivity
import com.android.whatsappbackup.activities.home.DeletedNotificationsActivity
import com.android.whatsappbackup.activities.home.GroupChatActivity
import com.android.whatsappbackup.activities.home.SearchActivity
import com.android.whatsappbackup.activities.home.SettingsActivity
import com.android.whatsappbackup.utils.Utils.askNotificationServicePermission
import com.android.whatsappbackup.utils.Utils.checkPostNotificationPermission
import com.android.whatsappbackup.utils.Utils.uiDefaultSettings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runOnUiThread {
            setContentView(R.layout.activity_main)
            uiDefaultSettings(this)
        }

        val bChats = findViewById<CardView>(R.id.bChats)
        val bAllNotifications = findViewById<CardView>(R.id.bHome)
        val bDeletedNotifications = findViewById<CardView>(R.id.bDeletedNotifications)
        val bAdvancedSearchActivity = findViewById<CardView>(R.id.bAdvancedSearchActivity)
        val bGroupChats = findViewById<CardView>(R.id.bGroupChats)
        val fbSettings = findViewById<CardView>(R.id.fbSettings)

        askNotificationServicePermission(this)

        checkPostNotificationPermission(this)

        bChats.setOnClickListener {
            val intent = Intent(this, ChatsActivity::class.java)
            startActivity(intent)
        }

        bAllNotifications.setOnClickListener {
            val intent = Intent(this, AllNotificationsActivity::class.java)
            startActivity(intent)
        }

        bDeletedNotifications.setOnClickListener {
            val intent = Intent(this, DeletedNotificationsActivity::class.java)
            startActivity(intent)
        }

        bAdvancedSearchActivity.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        bGroupChats.setOnClickListener {
            val intent = Intent(this, GroupChatActivity::class.java)
            startActivity(intent)
        }

        fbSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}
