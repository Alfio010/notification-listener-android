package com.android.alftendev.composes.utils

import com.android.alftendev.models.Notifications
import java.util.Date

sealed interface ChatItem {
    val id: String

    data class MessageItem(val notification: Notifications) : ChatItem {
        override val id: String = notification.entityId.toString()
    }

    data class DateSeparator(val date: Date) : ChatItem {
        override val id: String = date.toString()
    }
}