package com.android.alftendev.activities.specificactivity

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.alftendev.R
import com.android.alftendev.adapters.SpecificNotificationAdapter
import com.android.alftendev.utils.AuthUtils
import com.android.alftendev.utils.DBUtils
import com.android.alftendev.utils.DBUtils.nameToPackageName
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.UiUtils

class SpecificGraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(UiUtils.themeValueToTheme(this, MySharedPref.getThemeOptions()))

        AuthUtils.askAuth(this)

        super.onCreate(savedInstanceState)
        runOnUiThread {
            setContentView(R.layout.activity_specific_graph)
            UiUtils.uiDefaultSettings(this, true)
        }

        val appLabel = intent.extras!!.getString("appLabel", String())

        val adapter = SpecificNotificationAdapter(
            DBUtils.getSpecificNotificationsForGraph(appLabel),
            nameToPackageName(appLabel)
        )
        val rvSpecificNotificationStat = findViewById<RecyclerView>(R.id.rvSpecificNotificationStat)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        rvSpecificNotificationStat.layoutManager = linearLayoutManager
        rvSpecificNotificationStat.adapter = adapter

        onBackPressedDispatcher.addCallback {
            finishAndRemoveTask()
        }
    }
}