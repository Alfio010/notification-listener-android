package com.android.alftendev

import android.app.Application
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.android.alftendev.models.Notifications
import com.android.alftendev.models.PackageName
import com.android.alftendev.utils.DatabaseFactory
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import io.objectbox.Box
import io.objectbox.BoxStore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

class MyApplication : Application() {
    companion object {
        lateinit var database: BoxStore
        lateinit var notifications: Box<Notifications>
        lateinit var packageNames: Box<PackageName>

        lateinit var sharedPref: SharedPreferences
        lateinit var sharedPrefName: String

        lateinit var defaultSwValue: String

        lateinit var pm: PackageManager

        lateinit var executor: ExecutorService

        lateinit var application: MyApplication

        val authSuccess = AtomicBoolean(false)
    }

    override fun onCreate() {
        super.onCreate()

        application = MyApplication()

        pm = applicationContext.packageManager
        executor = Executors.newCachedThreadPool()

        database = DatabaseFactory.createDatabase(this)
        notifications = database.boxFor(Notifications::class.java)
        packageNames = database.boxFor(PackageName::class.java)
        defaultSwValue = getString(R.string.defaultSwitchValue)

        sharedPrefName = "NotInfo"
        sharedPref = getSharedPreferences(sharedPrefName, MODE_PRIVATE)
    }
}
