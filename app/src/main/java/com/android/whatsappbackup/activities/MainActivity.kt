package com.android.whatsappbackup.activities

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.cardview.widget.CardView
import com.android.whatsappbackup.MyApplication.Companion.authSuccess
import com.android.whatsappbackup.MyApplication.Companion.executor
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.home.AllNotificationsActivity
import com.android.whatsappbackup.activities.home.ChatsActivity
import com.android.whatsappbackup.activities.home.DeletedNotificationsActivity
import com.android.whatsappbackup.activities.home.GroupChatActivity
import com.android.whatsappbackup.activities.home.PieGraphActivity
import com.android.whatsappbackup.activities.home.SearchActivity
import com.android.whatsappbackup.activities.home.SettingsActivity
import com.android.whatsappbackup.utils.MySharedPref
import com.android.whatsappbackup.utils.Utils
import com.android.whatsappbackup.utils.Utils.askNotificationServicePermission
import com.android.whatsappbackup.utils.Utils.checkPostNotificationPermission
import com.android.whatsappbackup.utils.Utils.uiDefaultSettings

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        runOnUiThread {
            setContentView(R.layout.activity_main)
            uiDefaultSettings(this, false)
        }

        val bChats = findViewById<CardView>(R.id.bChats)
        val bAllNotifications = findViewById<CardView>(R.id.bHome)
        val bDeletedNotifications = findViewById<CardView>(R.id.bDeletedNotifications)
        val bAdvancedSearchActivity = findViewById<CardView>(R.id.bAdvancedSearchActivity)
        val bGroupChats = findViewById<CardView>(R.id.bGroupChats)
        val bGraph = findViewById<CardView>(R.id.bGraph)

        val buttonList: List<CardView> by lazy {
            listOf(
                bChats, bAllNotifications, bDeletedNotifications, bAdvancedSearchActivity,
                bGroupChats, bChats
            )
        }

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

        bGraph.setOnClickListener {
            val intent = Intent(this, PieGraphActivity::class.java)
            startActivity(intent)
        }

        if (MySharedPref.getAuthState() && !authSuccess.get()) {
            val biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        Utils.showToast(getString(R.string.authErr), this@MainActivity)
                        buttonList.forEach { it.isClickable = false }
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        Utils.showToast(getString(R.string.authSuccess), this@MainActivity)
                        buttonList.forEach { it.isClickable = true }
                        authSuccess.set(true)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Utils.showToast(getString(R.string.authFail), this@MainActivity)
                        buttonList.forEach { it.isClickable = false }
                    }
                })

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app")
                .setSubtitle("Log in using your biometric credential")
                .setConfirmationRequired(false).apply {
                    val km =
                        this@MainActivity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    if (km.isDeviceSecure) {
                        @Suppress("DEPRECATION")
                        setDeviceCredentialAllowed(true)
                    } else {
                        setNegativeButtonText("Cancel")
                    }
                }.build()

            runOnUiThread {
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settigs_menu -> {
                if (MySharedPref.getAuthState() && !authSuccess.get()) {
                    return false
                }
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStop() {
        super.onStop()
        authSuccess.set(false)
    }
}
