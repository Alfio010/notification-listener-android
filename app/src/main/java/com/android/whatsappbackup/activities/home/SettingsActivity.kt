package com.android.whatsappbackup.activities.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.android.whatsappbackup.BuildConfig
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.adaptersactivity.BlackListActivity
import com.android.whatsappbackup.activities.adaptersactivity.IsChatActivity
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.MySharedPref
import com.android.whatsappbackup.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {
    companion object {
        private lateinit var myActivity: AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        myActivity = this
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        Utils.uiDefaultSettings(this, true)

        onBackPressedDispatcher.addCallback {
            finishAndRemoveTask()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val blackListAuto =
                findPreference<SwitchPreferenceCompat>(MySharedPref.autoBlacklistEnabled)

            if (blackListAuto != null) {
                blackListAuto.isChecked = MySharedPref.getAutoBlacklistOn()
                blackListAuto.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        MySharedPref.setAutoBlacklist(newValue as Boolean)
                        true
                    }
            }

            val isAuthEnabled =
                findPreference<SwitchPreferenceCompat>(MySharedPref.authEnabled)

            if (isAuthEnabled != null) {
                isAuthEnabled.isChecked = MySharedPref.getAuthState()
                isAuthEnabled.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        if (Utils.isBiometricAuthAvailable(requireContext(), myActivity)) {
                            MySharedPref.setAuthState(newValue as Boolean)
                        } else {
                            isAuthEnabled.isChecked = false
                            MySharedPref.setAuthState(false)
                        }
                        true
                    }
            }

            val openBlacklist = findPreference<Preference>("open_blacklist")

            if (openBlacklist != null) {
                openBlacklist.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val intent = Intent(requireContext(), BlackListActivity::class.java)
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
                    builder.setOnCancelListener { it.dismiss() }
                    builder.create()
                    builder.show()
                    true
                }
            }

            val isChat = findPreference<Preference>("is_chat")

            if (isChat != null) {
                isChat.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val intent = Intent(requireContext(), IsChatActivity::class.java)
                    startActivity(intent)
                    true
                }
            }

            val isNotificationEnabled =
                findPreference<SwitchPreferenceCompat>(MySharedPref.notificationEnabled)

            if (isNotificationEnabled != null) {
                var notificationEnabled = Utils.isNotificationPostPermissionEnabled(requireContext())
                MySharedPref.setNotificationEnabled(notificationEnabled)

                isNotificationEnabled.isChecked = notificationEnabled
                isNotificationEnabled.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        if (newValue as Boolean) {
                            Utils.checkPostNotificationPermission(requireContext())
                        }

                        notificationEnabled = Utils.isNotificationPostPermissionEnabled(requireContext())
                        MySharedPref.setNotificationEnabled(notificationEnabled)
                        isNotificationEnabled.isChecked = notificationEnabled
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

            val github = findPreference<Preference>("github")

            if (github != null) {
                github.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    Utils.openLink(
                        requireContext(),
                        "https://github.com/Alfio010/notification-listener-android"
                    )
                    true
                }
            }

            val version = findPreference<Preference>("version")
            if (version != null) {
                version.title = "${version.title} ${BuildConfig.VERSION_NAME}"
            }
        }
    }
}