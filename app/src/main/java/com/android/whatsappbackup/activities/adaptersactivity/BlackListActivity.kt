package com.android.whatsappbackup.activities.adaptersactivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.R
import com.android.whatsappbackup.adapters.BlacklistAdapter
import com.android.whatsappbackup.models.PackageName
import com.android.whatsappbackup.utils.AuthUtils
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.MySharedPref
import com.android.whatsappbackup.utils.UiUtils
import com.android.whatsappbackup.utils.UiUtils.uiDefaultSettings

class BlackListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchSettings: EditText
    private lateinit var adapter: BlacklistAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private fun refreshList(packagesName: List<PackageName>) {
        runOnUiThread {
            adapter = BlacklistAdapter(packagesName)
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

        adapter = BlacklistAdapter(DBUtils.allPackageNameFromTable())

        etSearchSettings = findViewById(R.id.etSearchSettings)
        recyclerView = findViewById(R.id.lvSettings)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        MyApplication.executor.submit { refreshList(DBUtils.allPackageNameFromTable()) }

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                MyApplication.executor.submit { refreshList(DBUtils.allPackageNameFromTable()) }
            }
        })

        etSearchSettings.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) = Unit
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                MyApplication.executor.submit { refreshList(DBUtils.packageNameSearch(s.toString())) }
            }
        })

        onBackPressedDispatcher.addCallback {
            finishAndRemoveTask()
        }
    }

}