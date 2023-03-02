package com.android.whatsappbackup

import android.app.Application
import android.content.SharedPreferences
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.models.PackageName
import com.android.whatsappbackup.utils.DatabaseFactory
import io.objectbox.Box
import io.objectbox.BoxStore

class MyApplication : Application() {
    companion object {
        lateinit var database: BoxStore
        lateinit var notifications: Box<Notifications>
        lateinit var packagenames: Box<PackageName>

        lateinit var sharedPref: SharedPreferences

        lateinit var pkgWhatsApp: String
        lateinit var defaultSwValue: String
    }

    override fun onCreate() {
        super.onCreate()
        database = DatabaseFactory.createDatabase(this)
        notifications = database.boxFor(Notifications::class.java)
        packagenames = database.boxFor(PackageName::class.java)

        pkgWhatsApp = "com.whatsapp"
        defaultSwValue = getString(R.string.defaultSwitchValue)

        sharedPref = getSharedPreferences("NotInfo", MODE_PRIVATE)
    }
}
