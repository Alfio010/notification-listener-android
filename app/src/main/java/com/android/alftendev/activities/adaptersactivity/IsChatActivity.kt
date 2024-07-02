package com.android.alftendev.activities.adaptersactivity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.android.alftendev.MyApplication
import com.android.alftendev.R
import com.android.alftendev.adapters.IsChatAdapter
import com.android.alftendev.models.PackageName
import com.android.alftendev.utils.AuthUtils
import com.android.alftendev.utils.DBUtils
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.UiUtils
import com.android.alftendev.utils.UiUtils.uiDefaultSettings

class IsChatActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchSettings: EditText
    private lateinit var adapter: IsChatAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private fun refreshList(packagesName: List<PackageName>) {
        runOnUiThread {
            adapter = IsChatAdapter(packagesName)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = adapter
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(UiUtils.themeValueToTheme(this, MySharedPref.getThemeOptions()))
        super.onCreate(savedInstanceState)

        AuthUtils.askAuth(this)

        runOnUiThread {
            setContentView(R.layout.activity_is_chat)
            uiDefaultSettings(this, true)
        }

        adapter = IsChatAdapter(DBUtils.allPackageNameFromTable())

        etSearchSettings = findViewById(R.id.etIsCh)
        recyclerView = findViewById(R.id.rvIsChat)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        MyApplication.executor.submit { refreshList(DBUtils.allPackageNameFromTable()) }

        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
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