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

class WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(UiUtils.themeValueToTheme(this, MySharedPref.getThemeOptions()))
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        runOnUiThread {
            setContentView(R.layout.activity_webview)
            uiDefaultSettings(this, true)
            supportActionBar?.hide()
        }

        val webView = findViewById<WebView>(R.id.license_webview)

        val filePath = intent.extras!!.getString("filePath")

        webView.webChromeClient = WebChromeClient()
        webView.settings.setSupportZoom(true)

        if (filePath != null) {
            webView.loadUrl(filePath)
        }
    }
}
