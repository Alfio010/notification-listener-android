package com.android.whatsappbackup

import android.app.Application
import android.content.SharedPreferences
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.models.PackageName
import com.android.whatsappbackup.utils.DatabaseFactory
import com.google.android.material.color.DynamicColors
import io.objectbox.Box
import io.objectbox.BoxStore
import kotlin.properties.Delegates

class MyApplication : Application() {
    companion object {
        lateinit var database: BoxStore
        lateinit var notifications: Box<Notifications>
        lateinit var packagenames: Box<PackageName>

        lateinit var sharedPref: SharedPreferences

        lateinit var pkgWhatsApp: String
        lateinit var defaultSwValue: String

        var maxLength by Delegates.notNull<Int>()
    }

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        database = DatabaseFactory.createDatabase(this)
        notifications = database.boxFor(Notifications::class.java)
        packagenames = database.boxFor(PackageName::class.java)

        pkgWhatsApp = "com.whatsapp"
        defaultSwValue = getString(R.string.defaultSwitchValue)

        maxLength = 35

        sharedPref = getSharedPreferences("NotInfo", MODE_PRIVATE)
    }
}
