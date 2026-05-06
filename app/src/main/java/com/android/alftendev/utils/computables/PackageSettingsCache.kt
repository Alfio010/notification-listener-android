package com.android.alftendev.utils.computables

import com.android.alftendev.MyApplication.Companion.packageNames
import com.android.alftendev.models.PackageSettings
import java.util.concurrent.ConcurrentHashMap

object PackageSettingsCache {
    val packagesCache = ConcurrentHashMap<String, PackageSettings>()

    fun initCache() {
        val allPackages = packageNames.all

        packagesCache.clear()
        for (pkg in allPackages) {
            packagesCache[pkg.pkg] = PackageSettings(pkg.isBlackList, pkg.isWhiteList)
        }
    }

    fun updatePackage(pkg: String, isBlackList: Boolean, isWhiteList: Boolean) {
        val existing = packagesCache[pkg]

        if (existing != null) {
            packagesCache[pkg] = existing.copy(
                isBlackList = isBlackList,
                isWhiteList = isWhiteList
            )
        } else {
            packagesCache[pkg] = PackageSettings(isBlackList, isWhiteList)
        }
    }
}
