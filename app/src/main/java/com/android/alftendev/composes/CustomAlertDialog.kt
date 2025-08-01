package com.android.alftendev.composes

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.android.alftendev.R
import com.android.alftendev.models.Notifications
import com.android.alftendev.stubdata.singleNotification
import com.android.alftendev.utils.Utils
import com.android.alftendev.utils.computables.AppIcon
import com.google.accompanist.drawablepainter.rememberDrawablePainter

@Composable
fun CustomAlertDialog(
    context: Context,
    notification: Notifications,
    onDismiss: () -> Unit
) {
    val openDialog = remember { mutableStateOf(true) }
    val icon = AppIcon.compute(notification.packageName.target.pkg)

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
                onDismiss()
            },
            title = {
                SelectionContainer {
                    Text(text = notification.title)
                }
            },
            icon = {
                icon?.let {
                    Image(
                        painter = rememberDrawablePainter(it),
                        contentDescription = notification.packageName.target.pkg
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = context.getString(R.string.app_name)
                )
            },
            text = {
                SelectionContainer {
                    Column {
                        Text(text = "${LocalContext.current.getString(R.string.text)} ${notification.text}")
                        Spacer(modifier = Modifier.height(8.dp))

                        if (!notification.titleBig.isNullOrBlank()) {
                            Text(text = "${LocalContext.current.getString(R.string.titleBig)} ${notification.titleBig}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (!notification.conversationTitle.isNullOrBlank()) {
                            Text(text = "${LocalContext.current.getString(R.string.conversationTitle)} ${notification.conversationTitle}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (!notification.bigText.isNullOrBlank()) {
                            Text(text = "${LocalContext.current.getString(R.string.textBig)} ${notification.bigText!!.trim()}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (!notification.infoText.isNullOrBlank()) {
                            Text(text = "${LocalContext.current.getString(R.string.infoText)} ${notification.infoText}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        if (!notification.peopleList.isNullOrBlank()) {
                            Text(text = "${LocalContext.current.getString(R.string.peopleList)} ${notification.peopleList}")
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Text(text = "${LocalContext.current.getString(R.string.isDeleted)} ${notification.isDeleted}")
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    Utils.openApp(notification.packageName.target.pkg, context)
                    openDialog.value = false
                }) {
                    Text(text = context.getString(R.string.open_app))
                }
            },
            dismissButton = {
                Button(onClick = {
                    openDialog.value = false
                }) {
                    Text(text = context.getString(R.string.back))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DialogPreview() {
    CustomAlertDialog(
        LocalContext.current,
        singleNotification,
    ) {}
}
