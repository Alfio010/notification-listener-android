package com.android.alftendev.composes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.alftendev.composes.utils.ChatItem
import com.android.alftendev.models.Notifications
import com.android.alftendev.stubdata.testNotifications
import com.android.alftendev.utils.DateUtils.areDatesEqual
import com.android.alftendev.utils.DateUtils.dateFormatterOnlyDayMonthYear

@Composable
fun ChatScreen(messages: List<Notifications>, modifier: Modifier = Modifier) {
    val chatItems = remember(messages) {
        val items = mutableListOf<ChatItem>()
        messages.forEachIndexed { index, message ->
            val prevMessage = messages.getOrNull(index - 1)
            val dateChanged =
                prevMessage == null || !areDatesEqual(prevMessage.time, message.time)

            if (dateChanged) {
                items.add(ChatItem.DateSeparator(message.time))
            }
            items.add(ChatItem.MessageItem(message))
        }
        items
    }

    val openDialogs = remember { mutableStateMapOf<Long, Boolean>() }
    val listState =
        rememberLazyListState(initialFirstVisibleItemIndex = chatItems.lastIndex.coerceAtLeast(0))

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(
            items = chatItems,
            key = { it.id }
        ) { item ->
            when (item) {
                is ChatItem.DateSeparator -> {
                    CenteredDate(dateFormatterOnlyDayMonthYear(item.date))
                }

                is ChatItem.MessageItem -> {
                    val message = item.notification
                    ChatMessageBubble(
                        message = message,
                        canOpen = listState.isScrollInProgress && openDialogs.all { !it.value },
                        isDialogOpen = openDialogs[message.entityId] == true,
                        onDialogChange = { isOpen -> openDialogs[message.entityId] = isOpen }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(
        messages = testNotifications,
        modifier = Modifier.fillMaxSize()
    )
}
