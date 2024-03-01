package com.android.whatsappbackup.activities.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.android.whatsappbackup.BuildConfig
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.adaptersactivity.BlackListActivity
import com.android.whatsappbackup.activities.adaptersactivity.IsChatActivity
import com.android.whatsappbackup.utils.AuthUtils.askAuth
import com.android.whatsappbackup.utils.AuthUtils.isBiometricAuthAvailable
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.MySharedPref
import com.android.whatsappbackup.utils.PermissionUtils.checkPostNotificationPermission
import com.android.whatsappbackup.utils.PermissionUtils.isNotificationPostPermissionEnabled
import com.android.whatsappbackup.utils.UiUtils
import com.android.whatsappbackup.utils.UiUtils.setTheme
import com.android.whatsappbackup.utils.UiUtils.uiDefaultSettings
import com.android.whatsappbackup.utils.Utils
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {
    companion object {
        private lateinit var myActivity: AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(UiUtils.themeValueToTheme(this.application, MySharedPref.getThemeOptions()))
        super.onCreate(savedInstanceState)

        askAuth(this)

        runOnUiThread { setContentView(R.layout.settings_activity) }

        myActivity = this
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        uiDefaultSettings(this, true)

        onBackPressedDispatcher.addCallback {
            finishAndRemoveTask()
        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val blackListAuto =
                findPreference<SwitchPreferenceCompat>(MySharedPref.AUTO_BLACKLIST_ENABLED_STRING)

            if (blackListAuto != null) {
                blackListAuto.isChecked = MySharedPref.getAutoBlacklistOn()
                blackListAuto.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        MySharedPref.setAutoBlacklist(newValue as Boolean)
                        true
                    }
            }

            val isAuthEnabled =
                findPreference<SwitchPreferenceCompat>(MySharedPref.AUTH_ENABLED_STRING)

            if (isAuthEnabled != null) {
                isAuthEnabled.isChecked = MySharedPref.getAuthState()
                isAuthEnabled.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        if (isBiometricAuthAvailable(requireContext(), myActivity)) {
                            MySharedPref.setAuthState(newValue as Boolean)
                        } else {
                            isAuthEnabled.isChecked = false
                            MySharedPref.setAuthState(false)
                        }
                        true
                    }
            }

            val themeOptions = findPreference<ListPreference>(MySharedPref.THEME_OPTIONS_ENABLED)

            if (themeOptions != null) {
                themeOptions.summary = UiUtils.themeValueToString(this.requireContext(),MySharedPref.getThemeOptions())
                    themeOptions.value = UiUtils.themeValueToString(
                    this.requireContext(),
                    MySharedPref.getThemeOptions()
                )
                themeOptions.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        MySharedPref.setThemeOptions(
                            UiUtils.themeStringToValue(
                                this.requireContext(),
                                newValue.toString()
                            )
                        )
                        setTheme(myActivity)
                        myActivity.recreate()
                        UiUtils.showToast(getString(R.string.theme_changed), myActivity)
                        true
                    }
            }

            val openBlacklist = findPreference<Preference>("open_blacklist")

            if (openBlacklist != null) {
                openBlacklist.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    val intent = Intent(
                        requireContext(),
                        BlackListActivity::class.java
                    ).setAction(Intent.ACTION_MAIN)
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
                    val intent = Intent(
                        requireContext(),
                        IsChatActivity::class.java
                    ).setAction(Intent.ACTION_MAIN)
                    startActivity(intent)
                    true
                }
            }

            val isNotificationEnabled =
                findPreference<SwitchPreferenceCompat>(MySharedPref.NOTIFICATION_ENABLED_STRING)

            if (isNotificationEnabled != null) {
                var notificationEnabled =
                    isNotificationPostPermissionEnabled(requireContext())
                MySharedPref.setNotificationEnabled(notificationEnabled)

                isNotificationEnabled.isChecked = notificationEnabled
                isNotificationEnabled.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        if (newValue as Boolean) {
                            checkPostNotificationPermission(requireContext())
                        }

                        notificationEnabled =
                            isNotificationPostPermissionEnabled(requireContext())
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