package com.android.alftendev.utils

import android.content.Context
import com.android.alftendev.models.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.config.ValidateOnOpenModePages
import java.io.File

object DatabaseFactory {
    fun createDatabase(context: Context): BoxStore {
        val store = File(context.filesDir, "objectbox")

        val builder = MyObjectBox
            .builder()
            .androidContext(context)
            .baseDirectory(store)
            .validateOnOpen(ValidateOnOpenModePages.WithLeaves)
            .validateOnOpenPageLimit(20)
            .maxReaders(200)

        return builder.build()
    }
}
