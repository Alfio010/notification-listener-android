package com.android.alftendev.utils

import android.util.Log
import com.android.alftendev.BuildConfig

@JvmInline
value class CustomLog(private val tag: String) {
    fun log(msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d("aaa-$tag", msg)
        }
    }
}