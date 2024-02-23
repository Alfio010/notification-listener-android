package com.android.whatsappbackup.activities.home

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.adaptersactivity.ListSearchActivity
import com.android.whatsappbackup.utils.AuthUtils.askAuth
import com.android.whatsappbackup.utils.DBUtils.allPackageName
import com.android.whatsappbackup.utils.DBUtils.nameToPackageName
import com.android.whatsappbackup.utils.UiUtils.uiDefaultSettings
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askAuth(this)

        runOnUiThread {
            setContentView(R.layout.activity_search)
            uiDefaultSettings(this, true)
        }

        val bSearch = findViewById<MaterialButton>(R.id.bSearch)
        val etAdvancedSearch = findViewById<EditText>(R.id.etAdvancedSearch)
        val swSearch = findViewById<MaterialSwitch>(R.id.swSearch)

        val spinnerSearch: Spinner = findViewById(R.id.spinnerSearch)

        runOnUiThread {
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                mutableListOf<String>()
            ).also { adapter ->
                adapter.add(MyApplication.defaultSwValue)
                adapter.addAll(allPackageName().onEach {
                    if (it.name.isNullOrBlank()) {
                        it.name = it.pkg
                    }
                }.map { it.name })

                adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice)
                spinnerSearch.adapter = adapter
            }
        }

        bSearch.setOnClickListener {
            val text = etAdvancedSearch.text.toString()
            val pkgName = nameToPackageName(spinnerSearch.selectedItem.toString())
            val isDeleted = swSearch.isChecked
            val intent = Intent(this, ListSearchActivity::class.java).setAction(Intent.ACTION_MAIN)

            intent.putExtra("text", text)
            intent.putExtra("pkgName", pkgName)
            intent.putExtra("isDeleted", isDeleted)

            startActivity(intent)
        }

        onBackPressedDispatcher.addCallback {
            finishAndRemoveTask()
        }
    }
}