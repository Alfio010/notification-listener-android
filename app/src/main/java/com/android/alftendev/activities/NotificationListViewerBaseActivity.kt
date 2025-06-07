package com.android.alftendev.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.alftendev.MyApplication
import com.android.alftendev.MyApplication.Companion.authSuccess
import com.android.alftendev.MyApplication.Companion.sharedPrefName
import com.android.alftendev.R
import com.android.alftendev.activities.home.AllNotificationsActivity
import com.android.alftendev.activities.home.ChatsActivity
import com.android.alftendev.activities.home.DeletedNotificationsActivity
import com.android.alftendev.activities.home.GroupChatActivity
import com.android.alftendev.activities.home.PieGraphActivity
import com.android.alftendev.activities.home.SearchActivity
import com.android.alftendev.activities.home.SettingsActivity
import com.android.alftendev.adapters.NotificationsAdapter
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.AuthUtils.askAuth
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.MySharedPref.RECORD_NOTIFICATIONS_ENABLED
import com.android.alftendev.utils.MySharedPref.getIsRecordNotificationsEnabled
import com.android.alftendev.utils.PermissionUtils.askNotificationServicePermission
import com.android.alftendev.utils.PermissionUtils.isNotificationServiceEnabled
import com.android.alftendev.utils.UiUtils
import com.android.alftendev.utils.UiUtils.uiDefaultSettings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView

abstract class NotificationListViewerBaseActivity : AppCompatActivity() {
    open lateinit var recycleView: RecyclerView
    open lateinit var etSearch: EditText
    private lateinit var adapter: NotificationsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

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
            uiDefaultSettings(this, false)
            setContentView(R.layout.activity_notification_list)
        }

        drawerLayout = findViewById(R.id.notificationsDrawerLayout)

        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        actionBarToggle.syncState()

        navView = findViewById(R.id.notificationsNavView)

        etSearch = findViewById(R.id.etSearch)
        recycleView = findViewById(R.id.lvAll)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        if (javaClass.simpleName == "AllNotificationsActivity") {
            if (!getIsRecordNotificationsEnabled()) {
                runOnUiThread {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle(getString(R.string.ask_not_permission_title))
                    builder.setMessage(getString(R.string.ask_not_permission))
                    builder.setPositiveButton(
                        getString(R.string.yes)
                    ) { _, _ ->
                        val sharedPref = this.getSharedPreferences(sharedPrefName, MODE_PRIVATE)
                        sharedPref.edit()
                            .putBoolean(RECORD_NOTIFICATIONS_ENABLED, true).commit()
                        askNotificationServicePermission(this)
                    }
                    builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
                    builder.setOnCancelListener { it.dismiss() }
                    builder.create()
                    builder.show()
                }
            } else if (!isNotificationServiceEnabled(this)) {
                runOnUiThread {
                    UiUtils.showToast(getString(R.string.explain_not_permission), this)
                }
            }
        }

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

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navAllNotification -> {
                    if (javaClass.simpleName == "AllNotificationsActivity") {
                        return@setNavigationItemSelectedListener true
                    }
                    val navIntent = Intent(
                        this,
                        AllNotificationsActivity::class.java
                    ).setAction(Intent.ACTION_MAIN)
                    startActivity(navIntent)
                    true
                }

                R.id.navChats -> {
                    if (javaClass.simpleName == "ChatsActivity") {
                        return@setNavigationItemSelectedListener true
                    }
                    val navIntent =
                        Intent(this, ChatsActivity::class.java).setAction(Intent.ACTION_MAIN)
                    startActivity(navIntent)
                    if (javaClass.simpleName != "AllNotificationsActivity") {
                        finishAndRemoveTask()
                    }
                    true
                }

                R.id.navGroupChat -> {
                    if (javaClass.simpleName == "GroupChatActivity") {
                        return@setNavigationItemSelectedListener true
                    }
                    val navIntent =
                        Intent(this, GroupChatActivity::class.java).setAction(Intent.ACTION_MAIN)
                    startActivity(navIntent)
                    if (javaClass.simpleName != "AllNotificationsActivity") {
                        finishAndRemoveTask()
                    }
                    true
                }

                R.id.navDeletedNotification -> {
                    if (javaClass.simpleName == "DeletedNotificationsActivity") {
                        return@setNavigationItemSelectedListener true
                    }
                    val navIntent = Intent(
                        this,
                        DeletedNotificationsActivity::class.java
                    ).setAction(Intent.ACTION_MAIN)
                    startActivity(navIntent)
                    if (javaClass.simpleName != "AllNotificationsActivity") {
                        finishAndRemoveTask()
                    }
                    true
                }

                R.id.navGraph -> {
                    if (javaClass.simpleName == "PieGraphActivity") {
                        return@setNavigationItemSelectedListener true
                    }
                    val navIntent =
                        Intent(this, PieGraphActivity::class.java).setAction(Intent.ACTION_MAIN)
                    startActivity(navIntent)
                    if (javaClass.simpleName != "AllNotificationsActivity") {
                        finishAndRemoveTask()
                    }
                    true
                }

                R.id.navAdvancedSearch -> {
                    if (javaClass.simpleName == "SearchActivity") {
                        return@setNavigationItemSelectedListener true
                    }
                    val navIntent =
                        Intent(this, SearchActivity::class.java).setAction(Intent.ACTION_MAIN)
                    startActivity(navIntent)
                    if (javaClass.simpleName != "AllNotificationsActivity") {
                        finishAndRemoveTask()
                    }
                    true
                }

                R.id.navSettings -> {
                    if (javaClass.simpleName == "SettingsActivity") {
                        return@setNavigationItemSelectedListener true
                    }
                    val navIntent =
                        Intent(this, SettingsActivity::class.java).setAction(Intent.ACTION_MAIN)
                    startActivity(navIntent)
                    if (javaClass.simpleName != "AllNotificationsActivity") {
                        finishAndRemoveTask()
                    }
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.executor.submit { refreshList(getNotifications()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (javaClass.simpleName == "AllNotificationsActivity") {
            authSuccess.set(false)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(navView)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }
}
