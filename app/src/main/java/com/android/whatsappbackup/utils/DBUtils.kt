package com.android.whatsappbackup.utils

import android.content.Context
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.MyApplication.Companion.notifications
import com.android.whatsappbackup.MyApplication.Companion.packageNames
import com.android.whatsappbackup.R
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.models.Notifications_
import com.android.whatsappbackup.models.PackageName
import com.android.whatsappbackup.models.PackageName_
import io.objectbox.query.LazyList
import io.objectbox.query.QueryBuilder
import java.util.Date

object DBUtils {
    fun searchDeletedNot(pkgName: String, date: Long, title: String, text: String): Notifications? {
        return notifications
            .query()
            .equal(Notifications_.time, date)
            .equal(Notifications_.title, title, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .notEqual(Notifications_.text, text, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
            .find()
            .firstOrNull { it.packageName.target.pkg == pkgName }
    }

    fun searchOneNot(pkgName: String, title: String, date: Long, text: String): Notifications? {
        return notifications
            .query()
            .equal(Notifications_.title, title, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .equal(Notifications_.time, date)
            .equal(Notifications_.text, text, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
            .find()
            .firstOrNull { it.packageName.target.pkg == pkgName }
    }

    fun deletedNotification(): LazyList<Notifications> {
        return notifications
            .query()
            .equal(Notifications_.isDeleted, true)
            .orderDesc(Notifications_.time)
            .build()
            .findLazy()
    }

    fun isChatNotifications(): List<Notifications> {
        return notifications
            .query()
            .orderDesc(Notifications_.time)
            .build()
            .findLazy()
            .filter { it.packageName.target.isChat }
    }

    fun searchChat(pkgName: String, title: String): List<Notifications> {
        return notifications
            .query()
            .startsWith(
                Notifications_.title,
                title,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .orderDesc(Notifications_.time)
            .build()
            .findLazy()
            .filter { it.packageName.target.pkg == pkgName }
    }

    fun notificationWithoutChat(): List<Notifications> {
        return notifications
            .query()
            .orderDesc(Notifications_.time)
            .build()
            .findLazy()
            .filter { !it.packageName.target.isChat }
    }

    fun notificationWithoutChatSearch(string: String): List<Notifications> {
        return notifications
            .query()
            .contains(Notifications_.title, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .or()
            .contains(Notifications_.text, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(Notifications_.time)
            .build()
            .find()
            .filter { !it.packageName.target.isChat }
    }

    fun isChatNotificationSearch(string: String): List<Notifications> {
        return notifications
            .query()
            .contains(Notifications_.title, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .or()
            .contains(Notifications_.text, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(Notifications_.time)
            .build()
            .find()
            .filter { it.packageName.target.isChat }
    }

    fun advancedNotificationSearch(
        string: String,
        pkgName: String,
        isDeleted: Boolean
    ): List<Notifications> {
        if (pkgName == MyApplication.defaultSwValue || pkgName.isBlank()) {
            return notifications
                .query()
                .equal(Notifications_.isDeleted, isDeleted)
                .contains(Notifications_.title, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .or()
                .contains(Notifications_.text, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
                .orderDesc(Notifications_.time)
                .build()
                .find()
        }

        return notifications
            .query()
            .equal(Notifications_.isDeleted, isDeleted)
            .contains(Notifications_.title, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .or()
            .contains(Notifications_.text, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(Notifications_.time)
            .build()
            .find()
            .filter { it.packageName.target.pkg == pkgName }
    }

    fun advancedNotificationSearchWithoutText(
        pkgName: String,
        isDeleted: Boolean
    ): List<Notifications> {
        if (pkgName == MyApplication.defaultSwValue || pkgName.isBlank()) {
            return notifications
                .query()
                .equal(Notifications_.isDeleted, isDeleted)
                .orderDesc(Notifications_.time)
                .build()
                .find()
        }

        return notifications
            .query()
            .equal(Notifications_.isDeleted, isDeleted)
            .orderDesc(Notifications_.time)
            .build()
            .find()
            .filter { it.packageName.target.pkg == pkgName }
    }

    fun allPackageName(): List<PackageName> {
        return packageNames
            .query()
            .build()
            .find()
    }

    fun deletedNotificationSearch(string: String): List<Notifications> {
        return notifications
            .query()
            .equal(Notifications_.isDeleted, true)
            .contains(Notifications_.title, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .or()
            .contains(Notifications_.text, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(Notifications_.time)
            .build()
            .find()
    }

    fun lastNotification(pkgName: String): Notifications? {
        return notifications
            .query()
            .orderDesc(Notifications_.entityId)
            .build()
            .find().firstOrNull { it.packageName.target.pkg == pkgName }
    }

    fun createNotification(
        pkgName: String,
        title: String,
        date: Long,
        text: String,
        bigText: String,
        conversationTitle: String,
        infoText: String,
        peopleList: String,
        titleBig: String
    ): Notifications? {
        val packageName = packageNameExists(pkgName) ?: return null

        val notifications = Notifications(
            0,
            title,
            Date(date),
            text,
            bigText,
            conversationTitle,
            infoText,
            peopleList,
            titleBig
        )

        notifications.packageName.target = packageName

        return notifications
    }

    fun createNotificationDeleted(
        pkgName: String,
        title: String,
        date: Long,
        text: String,
        bigText: String,
        conversationTitle: String,
        infoText: String,
        peopleList: String,
        titleBig: String
    ): Notifications? {
        val packageName = packageNameExists(pkgName) ?: return null

        val notification = Notifications(
            0,
            title,
            Date(date),
            text,
            bigText,
            conversationTitle,
            infoText,
            peopleList,
            titleBig,
            true
        )

        notification.packageName.target = packageName

        return notification
    }

    fun countNotifications(): Long {
        return notifications
            .query()
            .build()
            .count()
    }

    fun createPackageName(
        pkgName: String
    ): PackageName {
        return PackageName(
            0,
            Utils.getAppName(pkgName),
            pkgName
        )
    }

    fun createBlackListPackageName(
        pkgName: String
    ): PackageName {
        return PackageName(
            0,
            Utils.getAppName(pkgName),
            pkgName,
            true
        )
    }

    fun packageNameExists(pkgName: String): PackageName? {
        return packageNames
            .query()
            .equal(
                PackageName_.pkg,
                pkgName,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .build()
            .findFirst()
    }

    fun allPackageNameFromTable(): LazyList<PackageName> {
        return packageNames
            .query()
            .orderDesc(PackageName_.entityId)
            .build()
            .findLazy()
    }

    fun nameToPackageName(name: String): String {
        val packageName = packageNames
            .query()
            .equal(
                PackageName_.name,
                name,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .build()
            .findFirst()

        return packageName?.pkg
            ?: (packageNames
                .query()
                .equal(
                    PackageName_.pkg,
                    name,
                    QueryBuilder.StringOrder.CASE_SENSITIVE
                )
                .build()
                .findFirst()?.pkg ?: "")
    }

    fun packageNameSearch(string: String): LazyList<PackageName> {
        return packageNames
            .query()
            .contains(PackageName_.name, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(PackageName_.entityId)
            .build()
            .findLazy()
    }

    fun getPercentNotifications(context: Context): MutableMap<String, Float> {
        val countedPackageName: MutableMap<String, Int> = mutableMapOf()

        notifications
            .query()
            .build()
            .find()
            .forEach { notification ->
                val pkgName =
                    notification.packageName.target.name ?: notification.packageName.target.pkg

                if (pkgName.isNotBlank()) {
                    if (countedPackageName.containsKey(pkgName)) {
                        countedPackageName[pkgName] = countedPackageName[pkgName]!! + 1
                    } else {
                        countedPackageName[pkgName] = 1
                    }
                }
            }

        val totalNotifications = notifications
            .query()
            .build()
            .count()
            .toFloat()

        val percentageMap: MutableMap<String, Float> = mutableMapOf()

        countedPackageName.forEach {
            percentageMap[it.key] = (it.value.toFloat() / totalNotifications) * 100f
        }

        var others = 0f

        countedPackageName.forEach {
            if (percentageMap[it.key]!! < 5f) {
                others += percentageMap[it.key]!!
            }
        }

        return percentageMap.toSortedMap(compareBy<String?> { percentageMap[it] }.thenBy { it })
            .apply {
                if (others > 0f) {
                    put(context.getString(R.string.others), others)
                }
            }
    }
}

