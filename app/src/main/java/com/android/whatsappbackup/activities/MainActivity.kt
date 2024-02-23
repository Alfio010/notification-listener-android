package com.android.whatsappbackup.activities

import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
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
import com.android.whatsappbackup.utils.AuthUtils.haveToAskAuth
import com.android.whatsappbackup.utils.MySharedPref
import com.android.whatsappbackup.utils.PermissionUtils.askNotificationServicePermission
import com.android.whatsappbackup.utils.PermissionUtils.checkPostNotificationPermission
import com.android.whatsappbackup.utils.PermissionUtils.isNotificationServiceEnabled
import com.android.whatsappbackup.utils.UiUtils.showToast
import com.android.whatsappbackup.utils.UiUtils.uiDefaultSettings
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private lateinit var cardViewGrid: GridLayout
    private lateinit var bReAuth: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationServicePermission(this)

        runOnUiThread {
            setContentView(R.layout.activity_main)
            uiDefaultSettings(this, false)
        }

        cardViewGrid = findViewById(R.id.cardViewGrid)
        val bChats = findViewById<CardView>(R.id.bChats)
        val bAllNotifications = findViewById<CardView>(R.id.bHome)
        val bDeletedNotifications = findViewById<CardView>(R.id.bDeletedNotifications)
        val bAdvancedSearchActivity = findViewById<CardView>(R.id.bAdvancedSearchActivity)
        val bGroupChats = findViewById<CardView>(R.id.bGroupChats)
        val bGraph = findViewById<CardView>(R.id.bGraph)
        bReAuth = findViewById(R.id.bReAuth)

        if (isNotificationServiceEnabled(this)) {
            checkPostNotificationPermission(this)
        }

        bChats.setOnClickListener {
            val intent = Intent(this, ChatsActivity::class.java).setAction(Intent.ACTION_MAIN)
            startActivity(intent)
        }

        bAllNotifications.setOnClickListener {
            val intent =
                Intent(this, AllNotificationsActivity::class.java).setAction(Intent.ACTION_MAIN)
            startActivity(intent)
        }

        bDeletedNotifications.setOnClickListener {
            val intent =
                Intent(this, DeletedNotificationsActivity::class.java).setAction(Intent.ACTION_MAIN)
            startActivity(intent)
        }

        bAdvancedSearchActivity.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java).setAction(Intent.ACTION_MAIN)
            startActivity(intent)
        }

        bGroupChats.setOnClickListener {
            val intent = Intent(this, GroupChatActivity::class.java).setAction(Intent.ACTION_MAIN)
            startActivity(intent)
        }

        bGraph.setOnClickListener {
            val intent = Intent(this, PieGraphActivity::class.java).setAction(Intent.ACTION_MAIN)
            startActivity(intent)
        }

        if (!isNotificationServiceEnabled(this)) {
            runOnUiThread {
                cardViewGrid.visibility = View.GONE
                bReAuth.visibility = View.VISIBLE
                bReAuth.text = getString(R.string.ask_not_permission)
                bReAuth.setOnClickListener {
                    askNotificationServicePermission(this)
                }
            }
        }

        initializeBiometricAuth()
    }

    override fun onResume() {
        super.onResume()
        if (isNotificationServiceEnabled(this)) {
            runOnUiThread {
                cardViewGrid.visibility = View.VISIBLE
                bReAuth.visibility = View.GONE
                bReAuth.text = getString(R.string.reauth)
            }
            checkPostNotificationPermission(this)
            initializeBiometricAuth()
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
                    return true
                }
                val intent =
                    Intent(this, SettingsActivity::class.java).setAction(Intent.ACTION_MAIN)
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        authSuccess.set(false)
    }

    private fun initializeBiometricAuth() {
        if (haveToAskAuth()) {
            val biometricPrompt = BiometricPrompt(this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        runOnUiThread {
                            showToast(getString(R.string.authErr), this@MainActivity)
                            cardViewGrid.visibility = View.GONE
                            bReAuth.visibility = View.VISIBLE
                        }
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        runOnUiThread {
                            showToast(getString(R.string.authSuccess), this@MainActivity)
                            cardViewGrid.visibility = View.VISIBLE
                            bReAuth.visibility = View.GONE
                        }
                        authSuccess.set(true)
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        runOnUiThread {
                            showToast(getString(R.string.authFail), this@MainActivity)
                            cardViewGrid.visibility = View.GONE
                            bReAuth.visibility = View.VISIBLE
                        }
                    }
                })

            val promptInfo = PromptInfo.Builder()
                .setTitle(getString(R.string.biometric_title))
                .setConfirmationRequired(false).apply {
                    val km =
                        this@MainActivity.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
                    if (km.isDeviceSecure) {
                        @Suppress("DEPRECATION")
                        setDeviceCredentialAllowed(true)
                    } else {
                        setNegativeButtonText(getString(R.string.cancel))
                    }
                }.build()

            bReAuth.setOnClickListener {
                runOnUiThread {
                    biometricPrompt.authenticate(promptInfo)
                }
            }

            runOnUiThread {
                biometricPrompt.authenticate(promptInfo)
            }
        }
    }
}
