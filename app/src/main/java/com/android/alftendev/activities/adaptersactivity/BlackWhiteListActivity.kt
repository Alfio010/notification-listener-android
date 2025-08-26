package com.android.alftendev.activities.adaptersactivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
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
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.UiUtils
import com.android.alftendev.utils.UiUtils.uiDefaultSettings

class BlackWhiteListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchSettings: EditText
    private lateinit var adapter: BlackWhitelistAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var blacklistMode: Boolean = true

    private fun refreshList(packagesName: List<PackageName>, blacklistMode: Boolean) {
        runOnUiThread {
            adapter = BlackWhitelistAdapter(packagesName, blacklistMode)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = adapter
        }
    }

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
            BlackWhitelistAdapter(DBUtils.allPackageNameFromTable(), true)
        } else {
            BlackWhitelistAdapter(DBUtils.allPackageNameFromTable(), false)
        }

        etSearchSettings = findViewById(R.id.etSearchSettings)
        recyclerView = findViewById(R.id.lvSettings)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        MyApplication.executor.submit {
            refreshList(
                DBUtils.allPackageNameFromTable(),
                blacklistMode
            )
        }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                MyApplication.executor.submit {
                    refreshList(
                        DBUtils.allPackageNameFromTable(),
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
            refreshList(DBUtils.allPackageNameFromTable(), blacklistMode)
        }
    }
}