package com.android.alftendev.utils

import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.alftendev.R

object UiUtils {
    fun uiDefaultSettings(activity: AppCompatActivity, isIconAllowed: Boolean) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        activity.supportActionBar?.setDisplayShowHomeEnabled(true)
        if (isIconAllowed) {
            activity.supportActionBar?.setIcon(R.mipmap.ic_launcher_foreground)
        }
    }

    fun showToast(text: String, context: AppCompatActivity) {
        context.runOnUiThread { Toast.makeText(context, text, Toast.LENGTH_LONG).show() }
    }

    fun isDarkThemeOn(context: Context): Boolean {
        return context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    fun themeStringToValue(context: Context, value: String): Int {
        when (value) {
            context.getString(R.string.system) -> return 1
            context.getString(R.string.dark) -> return 2
            context.getString(R.string.light) -> return 3
        }

        return 1
    }

    fun themeValueToString(context: Context, value: Int): String {
        when (value) {
            1 -> return context.getString(R.string.system)
            2 -> return context.getString(R.string.dark)
            3 -> return context.getString(R.string.light)
        }

        return context.getString(R.string.system)
    }

    fun themeValueToTheme(context: Context, value: Int): Int {
        when (value) {
            1 -> return if (isDarkThemeOn(context)) {
                R.style.Theme_dark
            } else {
                R.style.Theme_light
            }

            2 -> return R.style.Theme_dark
            3 -> return R.style.Theme_light
        }

        return R.style.Theme_light
    }

    fun setTheme(context: Context) {
        context.setTheme(themeValueToTheme(context, MySharedPref.getThemeOptions()))
    }

    fun showLoadingDialog(context: Context): AlertDialog {
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.layout_progress_loading, null)
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(false)
            .create()
        dialog.show()
        return dialog
    }
}