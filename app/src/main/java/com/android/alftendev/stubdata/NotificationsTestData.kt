package com.android.alftendev.stubdata

import com.android.alftendev.models.Notifications
import java.util.Date

val singleNotification = Notifications(
    entityId = 1,
    title = "Titolo nuovo messaggio",
    time = Date(),
    text = "Ciao! Come va?",
    conversationTitle = "Mario Rossi",
    isDeleted = true
)

val testNotifications = listOf(
    Notifications(
        entityId = 1,
        title = "Nuovo messaggio",
        time = Date(),
        text = "Ciao! Come va?",
        conversationTitle = "Mario Rossi",
        isDeleted = false
    ),
    Notifications(
        entityId = 2,
        title = "Aggiornamento",
        time = Date(System.currentTimeMillis() - 3600_000), // 1h fa
        text = "La tua app è stata aggiornata.",
        conversationTitle = null,
        isDeleted = false
    ),
    Notifications(
        entityId = 3,
        title = "Messaggio eliminato",
        time = Date(System.currentTimeMillis() - 7200_000), // 2h fa
        text = "",
        conversationTitle = "Gruppo amici",
        isDeleted = true
    )
)
