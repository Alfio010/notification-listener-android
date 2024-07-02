package com.android.alftendev.activities.home

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.android.alftendev.MyApplication
import com.android.alftendev.R
import com.android.alftendev.activities.adaptersactivity.ListSearchActivity
import com.android.alftendev.utils.AuthUtils.askAuth
import com.android.alftendev.utils.DBUtils.allPackageName
import com.android.alftendev.utils.DBUtils.nameToPackageName
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.UiUtils
import com.android.alftendev.utils.UiUtils.uiDefaultSettings
import com.google.android.material.button.MaterialButton
import com.google.android.material.materialswitch.MaterialSwitch

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(UiUtils.themeValueToTheme(this, MySharedPref.getThemeOptions()))
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