package com.android.whatsappbackup.activities.home

import android.annotation.SuppressLint
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
import com.android.whatsappbackup.MyApplication.Companion.sharedPrefName
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.adaptersactivity.BlackListActivity
import com.android.whatsappbackup.activities.adaptersactivity.IsChatActivity
import com.android.whatsappbackup.utils.AuthUtils.askAuth
import com.android.whatsappbackup.utils.AuthUtils.isBiometricAuthAvailable
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.MySharedPref
import com.android.whatsappbackup.utils.MySharedPref.AUTH_ENABLED_STRING
import com.android.whatsappbackup.utils.MySharedPref.AUTO_BLACKLIST_ENABLED_STRING
import com.android.whatsappbackup.utils.MySharedPref.NOTIFICATION_ENABLED_STRING
import com.android.whatsappbackup.utils.MySharedPref.THEME_OPTIONS_ENABLED
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
        @SuppressLint("ApplySharedPref")
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val blackListAuto =
                findPreference<SwitchPreferenceCompat>(AUTO_BLACKLIST_ENABLED_STRING)

            if (blackListAuto != null) {
                blackListAuto.isChecked = MySharedPref.getAutoBlacklistOn()
                blackListAuto.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        val sharedPref =
                            requireContext().getSharedPreferences(sharedPrefName, MODE_PRIVATE)
                        sharedPref.edit()
                            .putBoolean(AUTO_BLACKLIST_ENABLED_STRING, newValue as Boolean).commit()
                        true
                    }
            }

            val isAuthEnabled =
                findPreference<SwitchPreferenceCompat>(AUTH_ENABLED_STRING)

            if (isAuthEnabled != null) {
                isAuthEnabled.isChecked = MySharedPref.getAuthState()
                isAuthEnabled.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        val sharedPref =
                            requireContext().getSharedPreferences(sharedPrefName, MODE_PRIVATE)
                        if (isBiometricAuthAvailable(requireContext(), myActivity)) {
                            sharedPref.edit().putBoolean(AUTH_ENABLED_STRING, newValue as Boolean)
                                .commit()
                        } else {
                            isAuthEnabled.isChecked = false
                            sharedPref.edit().putBoolean(AUTH_ENABLED_STRING, false).commit()
                        }
                        true
                    }
            }

            val themeOptions = findPreference<ListPreference>(THEME_OPTIONS_ENABLED)

            if (themeOptions != null) {
                themeOptions.summary = UiUtils.themeValueToString(
                    this.requireContext(),
                    MySharedPref.getThemeOptions()
                )
                themeOptions.value = UiUtils.themeValueToString(
                    this.requireContext(),
                    MySharedPref.getThemeOptions()
                )
                themeOptions.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        val sharedPref =
                            requireContext().getSharedPreferences(sharedPrefName, MODE_PRIVATE)

                        sharedPref.edit().putInt(
                            THEME_OPTIONS_ENABLED,
                            UiUtils.themeStringToValue(
                                this.requireContext(),
                                newValue.toString()
                            )
                        )
                            .commit()

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

            val isNotificationPermissionEnabled = findPreference<Preference>("is_notification_permission_enabled")

            if (isNotificationPermissionEnabled != null) {
                if (isNotificationPostPermissionEnabled(requireContext())) {
                    isNotificationPermissionEnabled.summary =
                        getString(R.string.enabled)
                } else {
                    isNotificationPermissionEnabled.summary =
                        getString(R.string.disabled)
                }

                /* TODO fix
                isNotificationPermissionEnabled.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(
                            myActivity,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            1
                        )
                    }

                    true
                }
                */
            }

            val isNotificationEnabled =
                findPreference<SwitchPreferenceCompat>(NOTIFICATION_ENABLED_STRING)

            if (isNotificationEnabled != null) {
                val notificationEnabled = requireContext().getSharedPreferences(sharedPrefName, MODE_PRIVATE)
                    .getBoolean(NOTIFICATION_ENABLED_STRING, true)

                isNotificationEnabled.isChecked = notificationEnabled
                isNotificationEnabled.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        if (newValue as Boolean) {
                            checkPostNotificationPermission(requireContext())
                        }

                        val sharedPref =
                            requireContext().getSharedPreferences(sharedPrefName, MODE_PRIVATE)
                        sharedPref.edit()
                            .putBoolean(NOTIFICATION_ENABLED_STRING, newValue as Boolean).commit()

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

            val appLicense = findPreference<Preference>("app_license")

            if (appLicense != null) {
                appLicense.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    Utils.openLink(
                        requireContext(),
                        "https://www.gnu.org/licenses/gpl-3.0.html"
                    )
                    true
                }
            }

            val licenses = findPreference<Preference>("licenses")

            if (licenses != null) {
                licenses.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    startActivity(
                        Intent(
                            this.requireContext(),
                            LicensesActivity::class.java
                        ).setAction(Intent.ACTION_MAIN)
                    )
                    true
                }
            }

            val licenseMPAndroidChart = findPreference<Preference>("licenseMPAndroidChart")

            if (licenseMPAndroidChart != null) {
                licenseMPAndroidChart.onPreferenceClickListener =
                    Preference.OnPreferenceClickListener {
                        Utils.openLink(
                            requireContext(),
                            "http://www.apache.org/licenses/LICENSE-2.0"
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