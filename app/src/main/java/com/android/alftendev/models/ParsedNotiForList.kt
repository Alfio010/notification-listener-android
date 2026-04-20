package com.android.alftendev.models

import android.content.Context
import android.graphics.drawable.Icon
import com.android.alftendev.R
import com.android.alftendev.utils.Utils.getIconFromDrawable
import com.android.alftendev.utils.computables.AppIcon
import com.android.alftendev.widget.NotificationWidgetAdapter.Companion.MAX_LENGTH


data class ParsedNotiForList(
    var title: String,
    var text: String,
    var icon: Icon
)

fun getParsedNoti(
    title: String,
    text: String,
    packageName: String,
    context: Context
): ParsedNotiForList {
    val parsedTitle = if (title.trim() != "null") {
        title
    } else {
        context.getString(R.string.null_value)
    }

    val parsedText = if (text.length > MAX_LENGTH) {
        text.subSequence(0, MAX_LENGTH).toString() + "..."
    } else if (text.trim() == "null") {
        context.getString(R.string.null_value)
    } else {
        text
    }

    val icon = AppIcon.compute(packageName)

    val parsedIcon = if (icon != null) {
        getIconFromDrawable(icon)
    } else {
        Icon.createWithResource(context, R.drawable.baseline_android_24)
    }

    return ParsedNotiForList(parsedTitle, parsedText, parsedIcon)
}
