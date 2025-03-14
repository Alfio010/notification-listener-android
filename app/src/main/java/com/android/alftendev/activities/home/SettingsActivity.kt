package com.android.alftendev.activities.home

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.android.alftendev.BuildConfig
import com.android.alftendev.MyApplication
import com.android.alftendev.MyApplication.Companion.sharedPrefName
import com.android.alftendev.R
import com.android.alftendev.activities.adaptersactivity.BlackListActivity
import com.android.alftendev.activities.adaptersactivity.IsChatActivity
import com.android.alftendev.utils.AuthUtils.askAuth
import com.android.alftendev.utils.AuthUtils.isBiometricAuthAvailable
import com.android.alftendev.utils.DBUtils
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.MySharedPref.AUTH_ENABLED_STRING
import com.android.alftendev.utils.MySharedPref.AUTO_BLACKLIST_ENABLED_STRING
import com.android.alftendev.utils.MySharedPref.NOTIFICATION_ENABLED_STRING
import com.android.alftendev.utils.MySharedPref.RECORD_NOTIFICATIONS_ENABLED
import com.android.alftendev.utils.MySharedPref.THEME_OPTIONS_ENABLED
import com.android.alftendev.utils.PermissionUtils.checkPostNotificationPermission
import com.android.alftendev.utils.PermissionUtils.isNotificationPostPermissionEnabled
import com.android.alftendev.utils.PermissionUtils.isNotificationServiceEnabled
import com.android.alftendev.utils.UiUtils
import com.android.alftendev.utils.UiUtils.setTheme
import com.android.alftendev.utils.UiUtils.uiDefaultSettings
import com.android.alftendev.utils.Utils
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

            val isNotificationRecordingServiceEnabled =
                findPreference<Preference>("is_record_notification_permission_enabled")

            if (isNotificationRecordingServiceEnabled != null) {
                if (isNotificationServiceEnabled(requireContext())) {
                    isNotificationRecordingServiceEnabled.summary =
                        getString(R.string.enabled)
                } else {
                    isNotificationRecordingServiceEnabled.summary =
                        getString(R.string.disabled)
                }
            }

            val isNotificationServiceEnabled =
                findPreference<SwitchPreferenceCompat>("isRecordNotificationEnabled")

            if (isNotificationServiceEnabled != null) {
                val notificationEnabled =
                    requireContext().getSharedPreferences(sharedPrefName, MODE_PRIVATE)
                        .getBoolean(RECORD_NOTIFICATIONS_ENABLED, true)

                isNotificationServiceEnabled.isChecked = notificationEnabled
                isNotificationServiceEnabled.onPreferenceChangeListener =
                    Preference.OnPreferenceChangeListener { _, newValue ->
                        val sharedPref =
                            requireContext().getSharedPreferences(sharedPrefName, MODE_PRIVATE)
                        sharedPref.edit()
                            .putBoolean(RECORD_NOTIFICATIONS_ENABLED, newValue as Boolean).commit()

                        true
                    }
            }

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

            val isNotificationPermissionEnabled =
                findPreference<Preference>("is_notification_permission_enabled")

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
                val notificationEnabled =
                    requireContext().getSharedPreferences(sharedPrefName, MODE_PRIVATE)
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
                            .putBoolean(NOTIFICATION_ENABLED_STRING, newValue).commit()

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
                            WebViewActivity()::class.java
                        ).setAction(Intent.ACTION_MAIN)
                            .putExtra("filePath", "file:///android_asset/open_source_licenses.html")
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

            val privacyPolicy = findPreference<Preference>("privacyPolicy")

            if (privacyPolicy != null) {
                privacyPolicy.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                    Utils.openLink(
                        requireContext(),
                        "https://alfio010.github.io/"
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