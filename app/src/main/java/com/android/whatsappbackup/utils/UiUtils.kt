package com.android.whatsappbackup.utils

import android.content.Context
import android.content.res.Configuration
import android.util.TypedValue
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.whatsappbackup.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object UiUtils {
    fun uiDefaultSettings(activity: AppCompatActivity, isIconAllowed: Boolean) {
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        val typedValue = TypedValue()
        activity.theme.resolveAttribute(
            com.google.android.material.R.attr.colorPrimary,
            typedValue,
            true
        )
        val color = ContextCompat.getColor(activity, typedValue.resourceId)
        activity.window.statusBarColor = color
        activity.window.navigationBarColor = color
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

    fun dateFormatter(date: Date): String {
        val formatter = SimpleDateFormat("dd/M/yyyy HH:mm:ss", Locale.getDefault())
        formatter.timeZone = TimeZone.getDefault()
        return formatter.format(date)
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

    fun changeTheme(context: Context) {
        when (MySharedPref.getThemeOptions()) {
            1 -> if (isDarkThemeOn(context)) {
                context.setTheme(R.style.Theme_dark)
            } else {
                context.setTheme(R.style.Theme_light)
            }

            2 -> context.setTheme(R.style.Theme_dark)
            3 -> context.setTheme(R.style.Theme_light)
        }
    }
}