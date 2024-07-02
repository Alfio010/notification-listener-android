package com.android.alftendev.activities.home

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.alftendev.R
import com.android.alftendev.utils.MySharedPref
import com.android.alftendev.utils.UiUtils
import com.android.alftendev.utils.UiUtils.uiDefaultSettings

class LicensesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(UiUtils.themeValueToTheme(this, MySharedPref.getThemeOptions()))
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        runOnUiThread {
            setContentView(R.layout.activity_license)
            uiDefaultSettings(this, true)
        }

        val webView = findViewById<WebView>(R.id.license_webview)

        webView.webChromeClient = WebChromeClient()
        webView.settings.setSupportZoom(true)
        webView.loadUrl("file:///android_asset/open_source_licenses.html")
    }
}
