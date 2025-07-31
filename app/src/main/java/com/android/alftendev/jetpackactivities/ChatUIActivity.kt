package com.android.alftendev.jetpackactivities

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.android.alftendev.MyApplication
import com.android.alftendev.R
import com.android.alftendev.composes.ChatScreen
import com.android.alftendev.composes.SearchAndFilterContainer
import com.android.alftendev.composes.utils.MyTheme
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.AuthUtils
import com.android.alftendev.utils.DBUtils
import com.android.alftendev.utils.computables.AppIcon
import com.google.accompanist.drawablepainter.rememberDrawablePainter

class ChatUIActivity : ComponentActivity() {
    private lateinit var pkgName: String
    private lateinit var chatTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AuthUtils.askAuth(this)

        pkgName = intent.extras!!.getString("pkgName", MyApplication.defaultSwValue)
        chatTitle = intent.extras!!.getString("title", "")

        val messages = DBUtils.searchChat(pkgName, chatTitle)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        setContent {
            MyTheme {
                val rememberedMessages = remember(pkgName, chatTitle) { messages }
                MyApp(rememberedMessages, chatTitle, pkgName)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp(messages: List<Notifications>, chatTitle: String, pkgName: String) {
    val icon = AppIcon.compute(pkgName)

    val painterIcon = if (icon != null) {
        rememberDrawablePainter(icon)
    } else {
        painterResource(id = R.mipmap.ic_launcher)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = chatTitle,
                        modifier = Modifier.padding(vertical = 2.dp),
                        color = colorScheme.tertiary
                    )
                },
                navigationIcon = {
                    Image(
                        painter = painterIcon,
                        contentDescription = pkgName,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorScheme.primaryContainer,
                    titleContentColor = colorScheme.tertiary,
                ),
            )
        },
        containerColor = colorScheme.background
    ) { innerPadding ->
        SearchAndFilterContainer(
            LocalContext.current,
            allNotifications = messages,
            getPackageName = { message -> message.packageName.target.pkg },
            isDeleted = { message -> message.isDeleted },
            notificationText = { message -> message.text },
            modifier = Modifier.padding(innerPadding)
        ) { filteredMessages ->
            ChatScreen(
                messages = filteredMessages,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}