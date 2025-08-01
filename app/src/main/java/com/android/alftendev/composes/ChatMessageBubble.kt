package com.android.alftendev.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.alftendev.R
import com.android.alftendev.models.Notifications
import com.android.alftendev.stubdata.singleNotification
import com.android.alftendev.utils.DateUtils.dateFormatter

@Composable
fun ChatMessageBubble(
    message: Notifications,
    canOpen: Boolean,
    isDialogOpen: Boolean,
    onDialogChange: (Boolean) -> Unit
) {
    val displayText = message.text
    val timestamp = dateFormatter(message.time)

    if (isDialogOpen && !canOpen) {
        CustomAlertDialog(
            context = LocalContext.current,
            notification = message,
            onDismiss = { onDialogChange(false) }
        )
    }

    SelectionContainer {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .background(
                        androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(12.dp)
                    .widthIn(max = 280.dp)
                    .clickable { onDialogChange(true) }
            ) {
                Column {
                    Text(
                        text = displayText,
                        fontSize = 16.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (message.isDeleted) {
                            Text(
                                text = LocalContext.current.getString(R.string.deleted),
                                fontSize = 16.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = timestamp,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatBubblePreview() {
    ChatMessageBubble(
        singleNotification,
        canOpen = false,
        isDialogOpen = true,
        onDialogChange = {}
    )
}
