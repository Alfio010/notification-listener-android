package com.android.whatsappbackup.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.android.whatsappbackup.R
import com.android.whatsappbackup.models.PackageName
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.SomeUtils
import com.android.whatsappbackup.adapters.CustomSettingsAdapter

class BlackListActivity : AppCompatActivity() {
    private lateinit var lvSettings: ListView
    private lateinit var etSearchSettings: EditText
    private lateinit var adapter: CustomSettingsAdapter

    private fun refreshList(packagesName: List<PackageName>) {
        adapter = CustomSettingsAdapter(this, R.layout.custom_blacklist_layout, packagesName)
        lvSettings.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blacklist_activity)
        SomeUtils.uiDefaultSettings(this)

        etSearchSettings = findViewById(R.id.etSearchSettings)
        lvSettings = findViewById(R.id.lvSettings)

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