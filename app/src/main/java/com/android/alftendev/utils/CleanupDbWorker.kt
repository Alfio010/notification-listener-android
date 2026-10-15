package com.android.alftendev.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.android.alftendev.utils.DBUtils.deleteNotificationsOlderThan
import com.android.alftendev.utils.MySharedPref.deleteNotiOlderThanDays
import java.util.Date

class CleanupDbWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    companion object {
        val LOGGER = CustomLog("delete-noti-worker")
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