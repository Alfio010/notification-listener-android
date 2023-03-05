package com.android.whatsappbackup.utils

import android.content.Context
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.MyApplication.Companion.notifications
import com.android.whatsappbackup.MyApplication.Companion.packagenames
import com.android.whatsappbackup.MyApplication.Companion.pkgWhatsApp
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.models.Notifications_
import com.android.whatsappbackup.models.PackageName
import com.android.whatsappbackup.models.PackageName_
import io.objectbox.query.QueryBuilder
import java.util.*

object DBUtils {
    fun searchDeletedNot(pkgName: String, date: Long, title: String, text: String): Notifications? {
        return notifications
            .query()
            .equal(Notifications_.packageName, pkgName, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .equal(Notifications_.time, date)
            .equal(Notifications_.title, title, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .notEqual(Notifications_.text, text, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
            .findFirst()
    }

    fun searchOneNot(pkgName: String, title: String, date: Long, text: String): Notifications? {
        return notifications
            .query()
            .equal(Notifications_.packageName, pkgName, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .equal(Notifications_.title, title, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .equal(Notifications_.time, date)
            .equal(Notifications_.text, text, QueryBuilder.StringOrder.CASE_SENSITIVE)
            .build()
            .findFirst()
    }

    fun deletedNotification(): List<Notifications> {
        return notifications
            .query()
            .equal(Notifications_.isDeleted, true)
            .orderDesc(Notifications_.time)
            .build()
            .find()
    }

    fun perAppAllNotifications(pkgName: String): List<Notifications> {
        return notifications
            .query()
            .equal(
                Notifications_.packageName,
                pkgName,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .orderDesc(Notifications_.time)
            .build()
            .find()
    }

    fun searchChat(pkgName: String, title: String): List<Notifications> {
        return notifications
            .query()
            .equal(
                Notifications_.packageName,
                pkgName,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .startsWith(
                Notifications_.title,
                title,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .orderDesc(Notifications_.time)
            .build()
            .find()
    }

    fun allNotification(): List<Notifications> {
        return notifications
            .query()
            .notEqual(
                Notifications_.packageName,
                pkgWhatsApp,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .orderDesc(Notifications_.time)
            .build()
            .find()
    }

    fun whatsappNotification(): MutableList<Notifications> {
        return notifications
            .query()
            .equal(
                Notifications_.packageName,
                pkgWhatsApp,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .orderDesc(Notifications_.time)
            .build()
            .find()
    }

    fun allNotificationSearch(string: String): List<Notifications> {
        return notifications
            .query()
            .notEqual(
                Notifications_.packageName,
                pkgWhatsApp,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .contains(Notifications_.title, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .or()
            .contains(Notifications_.text, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(Notifications_.time)
            .build()
            .find()
    }

    fun perPackageNotificationSearch(string: String, pkgName: String): List<Notifications> {
        return notifications
            .query()
            .equal(
                Notifications_.packageName,
                pkgName,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .contains(Notifications_.title, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .or()
            .contains(Notifications_.text, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(Notifications_.time)
            .build()
            .find()
    }

    fun advancedNotificationSearch(
        string: String,
        pkgName: String,
        isDeleted: Boolean
    ): List<Notifications> {
        if (pkgName == MyApplication.defaultSwValue) {
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
            .equal(
                Notifications_.packageName,
                pkgName,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .equal(Notifications_.isDeleted, isDeleted)
            .contains(Notifications_.title, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .or()
            .contains(Notifications_.text, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(Notifications_.time)
            .build()
            .find()
    }

    fun advancedNotificationSearchWithoutText(
        pkgName: String,
        isDeleted: Boolean
    ): List<Notifications> {
        if (pkgName == MyApplication.defaultSwValue) {
            return notifications
                .query()
                .equal(Notifications_.isDeleted, isDeleted)
                .orderDesc(Notifications_.time)
                .build()
                .find()
        }
        return notifications
            .query()
            .equal(
                Notifications_.packageName,
                pkgName,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .equal(Notifications_.isDeleted, isDeleted)
            .orderDesc(Notifications_.time)
            .build()
            .find()
    }

    fun allPackageName(): List<PackageName> {
        return packagenames
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
            .equal(
                Notifications_.packageName,
                pkgName,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .orderDesc(Notifications_.entityId)
            .build()
            .findFirst()
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
    ): Notifications {
        return Notifications(
            0,
            pkgName,
            title,
            Date(date),
            text,
            bigText,
            conversationTitle,
            infoText,
            peopleList,
            titleBig
        )
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
    ): Notifications {
        return Notifications(
            0,
            pkgName,
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
    }

    fun countNotifications(): Long {
        return notifications
            .query()
            .build()
            .count()
    }

    fun createPackageName(
        pkgName: String,
        context: Context
    ): PackageName {
        return PackageName(
            0,
            SomeUtils.getAppName(pkgName, context),
            pkgName
        )
    }

    fun createBlackListPackageName(
        pkgName: String,
        context: Context
    ): PackageName {
        return PackageName(
            0,
            SomeUtils.getAppName(pkgName, context),
            pkgName,
            true
        )
    }

    fun packageNameExists(pkgName: String): PackageName? {
        return packagenames
            .query()
            .equal(
                PackageName_.pkg,
                pkgName,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .build()
            .findFirst()
    }

    fun allPackageNameFromTable(): List<PackageName> {
        return packagenames
            .query()
            .orderDesc(PackageName_.entityId)
            .build()
            .find()
    }

    fun nameToPackageName(name: String): String {
        val packageName = packagenames
            .query()
            .equal(
                PackageName_.name,
                name,
                QueryBuilder.StringOrder.CASE_SENSITIVE
            )
            .build()
            .findFirst()

        return packageName?.pkg
            ?: (packagenames
                .query()
                .equal(
                    PackageName_.pkg,
                    name,
                    QueryBuilder.StringOrder.CASE_SENSITIVE
                )
                .build()
                .findFirst()?.pkg ?: "")
    }

    fun packageNameSearch(string: String): List<PackageName> {
        return packagenames
            .query()
            .contains(PackageName_.name, string, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(PackageName_.entityId)
            .build()
            .find()
    }
}

