package com.android.whatsappbackup

import android.app.Application
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.models.PackageName
import com.android.whatsappbackup.utils.DatabaseFactory
import com.google.android.material.color.DynamicColors
import io.objectbox.Box
import io.objectbox.BoxStore

class MyApplication : Application() {
    companion object {
        lateinit var database: BoxStore
        lateinit var notifications: Box<Notifications>
        lateinit var packageNames: Box<PackageName>

        lateinit var sharedPref: SharedPreferences

        lateinit var pkgWhatsApp: String
        lateinit var defaultSwValue: String

        lateinit var pm: PackageManager
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        pm = applicationContext.packageManager

        database = DatabaseFactory.createDatabase(this)
        notifications = database.boxFor(Notifications::class.java)
        packageNames = database.boxFor(PackageName::class.java)

        pkgWhatsApp = "com.whatsapp"
        defaultSwValue = getString(R.string.defaultSwitchValue)

        sharedPref = getSharedPreferences("NotInfo", MODE_PRIVATE)
    }
}
