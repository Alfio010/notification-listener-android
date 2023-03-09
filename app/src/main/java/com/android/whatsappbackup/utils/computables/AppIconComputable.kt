package com.android.whatsappbackup.utils.computables

import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.android.whatsappbackup.MyApplication.Companion.pm
import org.apache.commons.lang3.concurrent.Computable
import org.apache.commons.lang3.concurrent.Memoizer

internal object AppIconComputable : Computable<String, Drawable?> {
    override fun compute(packageName: String): Drawable? {
        return try {
            pm.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
}

object AppIcon : Memoizer<String, Drawable?>(AppIconComputable, true)