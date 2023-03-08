package com.android.whatsappbackup.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.whatsappbackup.R
import com.android.whatsappbackup.models.PackageName
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.Utils
import com.android.whatsappbackup.adapters.SettingsAdapter

class BlackListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var etSearchSettings: EditText
    private lateinit var adapter: SettingsAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager

    private fun refreshList(packagesName: List<PackageName>) {
        adapter = SettingsAdapter(packagesName)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blacklist_activity)
        Utils.uiDefaultSettings(this)

        etSearchSettings = findViewById(R.id.etSearchSettings)
        recyclerView = findViewById(R.id.lvSettings)
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        refreshList(DBUtils.allPackageNameFromTable())

        etSearchSettings.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) = Unit
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                refreshList(DBUtils.packageNameSearch(s.toString()))
            }
        })
    }

}