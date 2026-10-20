package com.android.alftendev.utils

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.android.alftendev.R
import com.android.alftendev.utils.DBUtils.deleteNotificationsOlderThan
import com.android.alftendev.utils.MySharedPref.deleteNotiDaysInterval
import com.android.alftendev.utils.MySharedPref.deleteNotiOlderThanDays
import com.android.alftendev.utils.MySharedPref.isAutoDeleteNotiEnabled
import java.util.Date
import java.util.concurrent.TimeUnit

class CleanupDbWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    companion object {
        val LOGGER = CustomLog("delete-noti-worker")

        fun initializeCleanupDbWorker(context: Context, forceUpdate: Boolean = false) {
            if (!isAutoDeleteNotiEnabled()) {
                return
            }

            val deleteNotiDaysInterval = deleteNotiDaysInterval().toLong()

            if (deleteNotiDaysInterval > 0) {
                val cleanupRequest = PeriodicWorkRequestBuilder<CleanupDbWorker>(
                    deleteNotiDaysInterval,
                    TimeUnit.DAYS
                ).build()

                val policy = if (forceUpdate) {
                    ExistingPeriodicWorkPolicy.UPDATE
                } else {
                    ExistingPeriodicWorkPolicy.KEEP
                }

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    AUTO_DELETE_NOTI_WORKER,
                    policy,
                    cleanupRequest
                )
            }
        }

        fun cancelCleanupDbWorker(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(AUTO_DELETE_NOTI_WORKER)
        }

        fun getWorkerStatusAsync(context: Context, onResult: (String) -> Unit) {
            val future = WorkManager.getInstance(context)
                .getWorkInfosForUniqueWork(AUTO_DELETE_NOTI_WORKER)

            future.addListener({
                try {
                    val list = future.get()
                    val result = if (list.isNullOrEmpty()) {
                        context.getString(R.string.non_programmato)
                    } else {
                        LOGGER.log("current number of $AUTO_DELETE_NOTI_WORKER: ${list.size}")
                        when (list.first().state) {
                            WorkInfo.State.RUNNING -> context.getString(R.string.in_esecuzione)
                            WorkInfo.State.ENQUEUED -> context.getString(R.string.attivo)
                            WorkInfo.State.CANCELLED -> context.getString(R.string.disattivato)
                            else -> context.getString(R.string.non_attivo)
                        }
                    }
                    onResult(context.getString(R.string.state_x, result))
                } catch (e: Exception) {
                    LOGGER.log("error: ${e.stackTraceToString()}")
                    onResult("Error")
                }
            }, ContextCompat.getMainExecutor(context))
        }
    }

    override suspend fun doWork(): Result {
        return try {
            val daysOld = deleteNotiOlderThanDays()
            if (daysOld > 0) {
                val cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60L * 60L * 1000L)
                LOGGER.log(
                    "deleting notification older than $daysOld days ($cutoffTime) (${
                        Date(
                            cutoffTime
                        )
                    }"
                )
                deleteNotificationsOlderThan(Date(cutoffTime))
            }

            Result.success()
        } catch (e: Exception) {
            LOGGER.log("error while deleting noti: $e")
            Result.retry()
        }
    }
}