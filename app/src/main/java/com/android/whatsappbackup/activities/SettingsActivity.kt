package com.android.whatsappbackup.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.RatingBar
import androidx.appcompat.app.AppCompatActivity
import com.android.whatsappbackup.BuildConfig
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.R
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.MySharedPref
import com.android.whatsappbackup.utils.Utils
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        Utils.uiDefaultSettings(this)

        val bResetBlackList = findViewById<MaterialButton>(R.id.bResetBlackList)
        val bBlacklistSettings = findViewById<MaterialButton>(R.id.bBlacklistSettings)
        val swBlacklistAuto = findViewById<MaterialSwitch>(R.id.swBlacklistAuto)
        val swEnableNotification = findViewById<MaterialSwitch>(R.id.swEnableNotification)
        val tvCountNotification = findViewById<MaterialTextView>(R.id.tvCountNotification)
        val tvVersion = findViewById<MaterialTextView>(R.id.tvVersion)
        val settingsRatingBar = findViewById<RatingBar>(R.id.settingsRatingBar)

        tvCountNotification.text =
            "${getString(R.string.notification_count)} ${DBUtils.countNotifications()}"
        tvVersion.text = "${getString(R.string.version)} ${BuildConfig.VERSION_NAME}"

        settingsRatingBar.setOnRatingBarChangeListener { _, _, _ ->
            Utils.openPlayStore(
                this,
                packageName
            )
        }

        swBlacklistAuto.isChecked = MySharedPref.isAutoBlacklistOn()
        swEnableNotification.isChecked = MySharedPref.isNotificationEnabled()

        bResetBlackList.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setMessage(getString(R.string.blacklist_reset_confirm))
            builder.setPositiveButton(
                getString(R.string.yes)
            ) { _, _ ->

                MyApplication.packagenames.all.forEach {
                    it.isBlackList = false
                    MyApplication.packagenames.put(it)
                }

            }
            builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
            builder.create()
            builder.show()
        }

        bBlacklistSettings.setOnClickListener {
            val intent = Intent(this, BlackListActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
        }

        swBlacklistAuto.setOnClickListener {
            MySharedPref.setAutoBlacklist(swBlacklistAuto.isChecked)
        }

        swEnableNotification.setOnClickListener {
            MySharedPref.setNotificationEnabled(swEnableNotification.isChecked)
        }
    }
}