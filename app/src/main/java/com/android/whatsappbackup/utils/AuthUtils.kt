package com.android.whatsappbackup.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.MainActivity

object AuthUtils {
    fun isBiometricAuthAvailable(context: Context, activity: AppCompatActivity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val biometricManager = BiometricManager.from(context)
            when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
                BiometricManager.BIOMETRIC_SUCCESS ->
                    return true

                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                    UiUtils.showToast(context.getString(R.string.biometricUnavailable), activity)
                    return false
                }

                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                    UiUtils.showToast(
                        context.getString(R.string.biometricCurrentlyUnavailable),
                        activity
                    )
                    return false
                }

                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    UiUtils.showToast(context.getString(R.string.authNoEnrolled), activity)
                    return false
                }

                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                    UiUtils.showToast(context.getString(R.string.biometricError) + 1, activity)
                    return false
                }

                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                    UiUtils.showToast(context.getString(R.string.biometricError) + 2, activity)
                    return false
                }

                BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                    UiUtils.showToast(context.getString(R.string.biometricError) + 3, activity)
                    return false
                }

                else -> {
                    UiUtils.showToast(context.getString(R.string.biometricError) + 4, activity)
                    return false
                }
            }
        }
        UiUtils.showToast(context.getString(R.string.biometricUnsupported), activity)
        return false
    }

    fun haveToAskAuth(): Boolean {
        return MySharedPref.getAuthState() && !MyApplication.authSuccess.get()
    }

    fun askAuth(activity: Activity) {
        if (haveToAskAuth()) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }
    }
}