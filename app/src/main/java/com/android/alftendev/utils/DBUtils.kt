package com.android.alftendev.utils

import android.content.Context
import com.android.alftendev.MyApplication
import com.android.alftendev.MyApplication.Companion.notifications
import com.android.alftendev.MyApplication.Companion.packageNames
import com.android.alftendev.R
import com.android.alftendev.activities.home.PieGraphActivity.Companion.OTHERS_MAX_VALUE
import com.android.alftendev.models.NotificationStatsItem
import com.android.alftendev.models.Notifications
import com.android.alftendev.models.Notifications_
import com.android.alftendev.models.PackageName
import com.android.alftendev.models.PackageName_
import com.android.alftendev.utils.Utils.isAppInstalled
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

    fun deletedNotificationForWidget(): List<Notifications> {
        return notifications
            .query()
            .equal(Notifications_.isDeleted, true)
            .orderDesc(Notifications_.time)
            .build()
            .find(0, 50)
    }

    fun getChatNotifications(): List<Notifications> {
        return notifications
            .query()
            .orderDesc(Notifications_.time)
            .build()
            .findLazy()
            .filter { it.packageName.target.isChat }
    }

    fun getChatNotificationsForWidget(): List<Notifications> {
        return notifications
            .query()
            .orderDesc(Notifications_.time)
            .build()
            .find(0, 65)
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
            .order(Notifications_.time)
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
            .order(PackageName_.name)
            .build()
            .find()
    }

    fun allPackageNameLazy(): LazyList<PackageName> {
        return packageNames
            .query()
            .build()
            .findLazy()
    }

    fun allNotifications(): LazyList<Notifications> {
        return notifications
            .query()
            .build()
            .findLazy()
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

    fun lastNotificationWithLimit(): List<Notifications> {
        return notifications
            .query()
            .orderDesc(Notifications_.entityId)
            .build()
            .find(0, 50)
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
        val packageName = getPackageName(pkgName) ?: return null

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

    fun createNotificationFromJson(
        pkgName: String,
        title: String,
        date: Long,
        text: String,
        bigText: String?,
        conversationTitle: String?,
        infoText: String?,
        peopleList: String?,
        titleBig: String?,
        isDeleted: Boolean
    ) {
        val packageName = getPackageName(pkgName)

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
            isDeleted
        )

        notification.packageName.target = packageName

        if (packageName != null) {
            notifications.put(notification)
        }
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
        val packageName = getPackageName(pkgName) ?: return null

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
            Utils.getAppNameFromPackageName(pkgName),
            pkgName
        )
    }

    fun createPackageNameFromJson(
        pkgName: String,
        name: String?,
        isBlacklist: Boolean,
        isWhiteList: Boolean,
        isChat: Boolean
    ) {
        if (getPackageName(pkgName) == null) {
            packageNames.put(
                PackageName(
                    0,
                    name,
                    pkgName,
                    isBlacklist,
                    isWhiteList,
                    isChat
                )
            )
        }
    }

    fun createBlackListPackageName(
        pkgName: String
    ): PackageName {
        return PackageName(
            0,
            Utils.getAppNameFromPackageName(pkgName),
            pkgName,
            true
        )
    }

    fun getPackageName(pkgName: String): PackageName? {
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
            .order(PackageName_.name)
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
            if (percentageMap[it.key]!! < OTHERS_MAX_VALUE) {
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

    fun getSpecificNotificationsForGraph(appLabel: String): MutableList<NotificationStatsItem> {
        val notificationItemList: MutableList<NotificationStatsItem> = mutableListOf()

        val countedNotifications: MutableMap<String, Long> = mutableMapOf()

        notifications
            .query()
            .build()
            .find()
            .filter { it.packageName.target.name == appLabel || it.packageName.target.pkg == appLabel }
            .forEach { notification ->
                val title = if (notification.packageName.target.pkg.contains("com.whatsapp")) {
                    notification.conversationTitle?.substringBeforeLast("(")
                        ?: notification.title
                } else {
                    notification.title
                }

                if (title.isNotBlank()) {
                    if (countedNotifications.containsKey(title) || countedNotifications.any {
                            it.key.startsWith(
                                title
                            )
                        }) {
                        countedNotifications[title] = countedNotifications[title]!! + 1
                    } else {
                        countedNotifications[title] = 1
                    }
                }
            }

        var total = 0f

        countedNotifications.forEach { total += it.value }

        countedNotifications.toSortedMap(compareBy<String?> { countedNotifications[it] }.thenBy { it })
            .forEach {
                notificationItemList.add(
                    NotificationStatsItem(
                        it.key,
                        it.value,
                        ("%.2f".format((100.0 * it.value / total))).toDoubleOrNull()
                    )
                )
            }

        return notificationItemList
    }

    fun deleteNotificationById(id: Long) {
        notifications
            .query()
            .equal(Notifications_.entityId, id)
            .build()
            .remove()
    }

    fun getInstalledPackageNamesFromList(packageName: List<PackageName>): MutableList<PackageName> {
        return packageName.toMutableList().apply { removeAll {
            !isAppInstalled(it.pkg)
        }}
    }
}
