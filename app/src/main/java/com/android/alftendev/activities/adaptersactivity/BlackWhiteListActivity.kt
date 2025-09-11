package com.android.alftendev.activities.adaptersactivity

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.alftendev.MyApplication
import com.android.alftendev.R
import com.android.alftendev.adapters.BlackWhitelistAdapter
import com.android.alftendev.models.PackageName
import com.android.alftendev.utils.AuthUtils
import com.android.alftendev.utils.DBUtils
import com.android.alftendev.utils.DBUtils.getInstalledPackageNamesFromList
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.UiUtils
import com.android.alftendev.utils.UiUtils.uiDefaultSettings
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch

class BlackWhiteListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchSettings: EditText
    private lateinit var adapter: BlackWhitelistAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var blacklistMode: Boolean = true
    private var onlyInstalledApps: Boolean = false

    private fun refreshList(packagesName: List<PackageName>, blacklistMode: Boolean) {
        val packageNameFiltered = if (onlyInstalledApps) {
            getInstalledPackageNamesFromList(packagesName)
        } else {
            packagesName
        }

        runOnUiThread {
            adapter = BlackWhitelistAdapter(packageNameFiltered, blacklistMode)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = adapter
        }
    }

    private fun selectOrDeselectAll(installedSwitchChecked: Boolean, select: Boolean) {
        runOnUiThread {
            val allPackages = if (installedSwitchChecked) {
                getInstalledPackageNamesFromList(DBUtils.allPackageName())
            } else {
                DBUtils.allPackageName().toList()
            }

            if (blacklistMode) {
                allPackages.forEach { it.isBlackList = select }
                MyApplication.packageNames.put(allPackages)
            } else {
                allPackages.forEach { it.isWhiteList = select }
                MyApplication.packageNames.put(allPackages)
            }

            refreshList(
                DBUtils.allPackageName(),
                blacklistMode
            )
        }
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(UiUtils.themeValueToTheme(this, MySharedPref.getThemeOptions()))
        super.onCreate(savedInstanceState)

        AuthUtils.askAuth(this)

        runOnUiThread {
            setContentView(R.layout.blacklist_activity)
            uiDefaultSettings(this, true)
        }

        blacklistMode = intent.extras!!.getBoolean("blacklistMode", true)

        adapter = if (blacklistMode) {
            setTitle(getString(R.string.blacklist))
            BlackWhitelistAdapter(DBUtils.allPackageName(), true)
        } else {
            setTitle(getString(R.string.whitelist))
            BlackWhitelistAdapter(DBUtils.allPackageName(), false)
        }

        val installedSwitch = findViewById<MaterialSwitch>(R.id.swShowOnlyInstalledApps)
        val settingButton = findViewById<ImageButton>(R.id.bBlackWhitelistSettings)
        etSearchSettings = findViewById(R.id.etSearchSettings)
        recyclerView = findViewById(R.id.lvSettings)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        MyApplication.executor.submit {
            refreshList(
                DBUtils.allPackageName(),
                blacklistMode
            )
        }

        installedSwitch.setOnCheckedChangeListener { _, isChecked ->
            onlyInstalledApps = isChecked
            runOnUiThread {
                refreshList(
                    DBUtils.allPackageName(),
                    blacklistMode
                )
            }
        }

        settingButton.setOnClickListener {
            val customAlertDialogView = LayoutInflater.from(this)
                .inflate(R.layout.custom_blacklist_settings_dialog, null, false)

            customAlertDialogView.findViewById<MaterialButton>(R.id.bToggleAllBlacklisted)
                .setOnClickListener {
                    selectOrDeselectAll(onlyInstalledApps, true)
                }

            customAlertDialogView.findViewById<MaterialButton>(R.id.bDeToggleAllBlacklisted)
                .setOnClickListener {
                    selectOrDeselectAll(onlyInstalledApps, false)
                }

            val builder = MaterialAlertDialogBuilder(this)
            if (blacklistMode) {
                builder.setTitle("${getString(R.string.blacklist)} ${getString(R.string.settings)}")
            } else {
                builder.setTitle("${getString(R.string.whitelist)} ${getString(R.string.settings)}")

            }
            builder.setView(customAlertDialogView)
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setNeutralButton(R.string.back) { _, _ -> }
            builder.setOnCancelListener { it.dismiss() }
            builder.create()
            builder.show()
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                MyApplication.executor.submit {
                    refreshList(
                        DBUtils.allPackageName(),
                        blacklistMode
                    )
                }
            }
        })

        etSearchSettings.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) = Unit
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                MyApplication.executor.submit {
                    refreshList(
                        DBUtils.packageNameSearch(s.toString()),
                        blacklistMode
                    )
                }
            }
        })

        onBackPressedDispatcher.addCallback {
            finishAndRemoveTask()
        }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.executor.submit {
            refreshList(DBUtils.allPackageName(), blacklistMode)
        }
    }
}