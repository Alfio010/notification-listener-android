package com.android.alftendev.activities

import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import com.android.alftendev.MyApplication.Companion.authSuccess
import com.android.alftendev.MyApplication.Companion.executor
import com.android.alftendev.R
import com.android.alftendev.activities.home.AllNotificationsActivity
import com.android.alftendev.utils.AuthUtils.haveToAskAuth
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.PermissionUtils.askNotificationServicePermission
import com.android.alftendev.utils.PermissionUtils.isNotificationServiceEnabled
import com.android.alftendev.utils.UiUtils
import com.android.alftendev.utils.UiUtils.showToast
import com.android.alftendev.utils.UiUtils.uiDefaultSettings
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    private lateinit var bReAuth: MaterialButton
    private lateinit var bNotiPermission: MaterialButton
    private lateinit var bContinue: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(UiUtils.themeValueToTheme(this, MySharedPref.getThemeOptions()))
        super.onCreate(savedInstanceState)

        askNotificationServicePermission(this)

        runOnUiThread {
            setContentView(R.layout.activity_main)
            uiDefaultSettings(this, false)
        }

        if (!haveToAskAuth()) {
            val intent = Intent(this, AllNotificationsActivity::class.java)
            startActivity(intent)
            finish()
        }

        bReAuth = findViewById(R.id.bReAuth)
        bNotiPermission = findViewById(R.id.bNotiServicePermission)
        bContinue = findViewById(R.id.bContinue)

        if (!isNotificationServiceEnabled(this)) {
            runOnUiThread {
                bNotiPermission.visibility = View.VISIBLE
                bNotiPermission.text = getString(R.string.ask_not_permission)
                bNotiPermission.setOnClickListener {
                    askNotificationServicePermission(this)
                }
            }
        }

        bContinue.setOnClickListener {
            val intent = Intent(this, AllNotificationsActivity::class.java)
            startActivity(intent)
            finish()
        }

        initializeBiometricAuth()
    }

    override fun onResume() {
        super.onResume()
        if (isNotificationServiceEnabled(this)) {
            runOnUiThread {
                bReAuth.visibility = View.GONE
                bReAuth.text = getString(R.string.reauth)
            }
            initializeBiometricAuth()
        }
    }

    private fun initializeBiometricAuth() {
        if (haveToAskAuth()) {
            val biometricPrompt = BiometricPrompt(
                this, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(
                        errorCode: Int,
                        errString: CharSequence
                    ) {
                        super.onAuthenticationError(errorCode, errString)
                        runOnUiThread {
                            showToast(getString(R.string.authErr), this@MainActivity)
                            bReAuth.visibility = View.VISIBLE
                        }
                    }

                    override fun onAuthenticationSucceeded(
                        result: BiometricPrompt.AuthenticationResult
                    ) {
                        super.onAuthenticationSucceeded(result)
                        authSuccess.set(true)
                        runOnUiThread {
                            showToast(getString(R.string.authSuccess), this@MainActivity)
                        }
                        val intent = Intent(this@MainActivity, AllNotificationsActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        runOnUiThread {
                            showToast(getString(R.string.authFail), this@MainActivity)
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
