package com.android.whatsappbackup.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.android.whatsappbackup.BuildConfig
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.R
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.MySharedPref
import com.android.whatsappbackup.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class MySettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Utils.uiDefaultSettings(this)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val blackListAuto = findPreference<SwitchPreferenceCompat>("isAutoBlacklistOn")

            if (blackListAuto != null) {
                blackListAuto.isChecked = MySharedPref.getAutoBlacklistOn()
                blackListAuto.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        MySharedPref.setAutoBlacklist(newValue as Boolean)
                        true
                    }
            }

            val openBlacklist = findPreference<Preference>("open_blacklist")

            if (openBlacklist != null) {
                openBlacklist.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val intent = Intent(requireContext(), BlackListActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    startActivity(intent)
                    true
                }
            }

            val resetBlacklist = findPreference<Preference>("reset_blacklist")

            if (resetBlacklist != null) {
                resetBlacklist.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val builder = MaterialAlertDialogBuilder(requireContext())
                    builder.setMessage(getString(R.string.blacklist_reset_confirm))
                    builder.setPositiveButton(
                        getString(R.string.yes)
                    ) { _, _ ->

                        MyApplication.packageNames.all.forEach {
                            it.isBlackList = false
                            MyApplication.packageNames.put(it)
                        }

                    }
                    builder.setNegativeButton(getString(R.string.no)) { _, _ -> }
                    builder.create()
                    builder.show()
                    true
                }
            }

            val isNotificationEnabled = findPreference<SwitchPreferenceCompat>("isAutoBlacklistOn")

            if (isNotificationEnabled != null) {
                isNotificationEnabled.isChecked = MySharedPref.getNotificationEnabled()
                isNotificationEnabled.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        MySharedPref.setNotificationEnabled(newValue as Boolean)
                        true
                    }
            }

            val notificationCount = findPreference<Preference>("notification_count")
            if (notificationCount != null) {
                notificationCount.title =
                    "${notificationCount.title} ${DBUtils.countNotifications()}"
            }

            val rateApp = findPreference<Preference>("rate_app")

            if (rateApp != null) {
                rateApp.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    Utils.openPlayStore(
                        requireContext(),
                        BuildConfig.APPLICATION_ID
                    )
                    true
                }
            }

            val version = findPreference<Preference>("version")
            if (version != null) {
                version.title = "${version.title} ${BuildConfig.VERSION_NAME}"
            }

            val isDynamicColorsEnabled =
                findPreference<SwitchPreferenceCompat>("isDynamicColorsEnabled")

            if (isDynamicColorsEnabled != null) {
                isDynamicColorsEnabled.isChecked = MySharedPref.getDynamicColors()
                isDynamicColorsEnabled.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        MySharedPref.setDynamicColors(newValue as Boolean)
                        true
                    }
            }
        }
    }
}