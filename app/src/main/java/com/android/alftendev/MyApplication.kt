package com.android.alftendev

import android.app.Application
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.android.alftendev.models.Notifications
import com.android.alftendev.models.PackageName
import com.android.alftendev.utils.AUTO_DELETE_NOTI_WORKER
import com.android.alftendev.utils.CleanupDbWorker
import com.android.alftendev.utils.DatabaseFactory
import com.android.alftendev.utils.MyActivityLifecycleCallbacks
import com.android.alftendev.utils.MySharedPref.deleteNotiDaysInterval
import com.android.alftendev.utils.computables.PackageSettingsCache.initCache
import io.objectbox.Box
import io.objectbox.BoxStore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
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
        executor.execute { initCache() }

        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks())

        val deleteNotiDaysInterval = deleteNotiDaysInterval().toLong()

        if (deleteNotiDaysInterval > 0) {
            val cleanupRequest = PeriodicWorkRequestBuilder<CleanupDbWorker>(
                deleteNotiDaysInterval,
                TimeUnit.DAYS
            ).build()

            WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                AUTO_DELETE_NOTI_WORKER,
                ExistingPeriodicWorkPolicy.KEEP,
                cleanupRequest
            )
        }
    }
}
