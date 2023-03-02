package com.android.whatsappbackup.utils

import android.content.Context
import com.android.whatsappbackup.models.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.model.ValidateOnOpenMode
import java.io.File

object DatabaseFactory {
    fun createDatabase(context: Context): BoxStore {
        val store = File(context.filesDir, "objectbox")

        val builder = MyObjectBox
            .builder()
            .androidContext(context)
            .baseDirectory(store)
            .validateOnOpen(ValidateOnOpenMode.WithLeaves)
            .validateOnOpenPageLimit(20)
            .maxReaders(200)

        return builder.build()
    }
}
