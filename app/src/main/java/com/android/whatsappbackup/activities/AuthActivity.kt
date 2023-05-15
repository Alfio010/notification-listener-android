package com.android.whatsappbackup.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.activities.adaptersactivity.ListSearchActivity
import com.android.whatsappbackup.utils.Utils

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)

        runOnUiThread {
            val baseLayout = CoordinatorLayout(this)

            baseLayout.addView(baseLayout)
            this.setContentView(baseLayout)
        }

        val biometricPrompt = BiometricPrompt(this, MyApplication.executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    Utils.showToast("auth err", this@AuthActivity)
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    Utils.showToast("auth succeeded", this@AuthActivity)

                    val intent = Intent(this@AuthActivity, MainActivity::class.java)
                    intent.putExtra("isAuthSucceeded", true)
                    intent.putExtra("openAuthScreen", false)
                    this@AuthActivity.startActivity(intent)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Utils.showToast("auth fail", this@AuthActivity)
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric login for my app")
            .setSubtitle("Log in using your biometric credential")
            .setNegativeButtonText("Use account password")
            .build()

        runOnUiThread {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}