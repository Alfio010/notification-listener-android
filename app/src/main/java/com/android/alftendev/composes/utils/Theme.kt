package com.android.alftendev.composes.utils

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.core.view.WindowCompat
import com.android.alftendev.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val lightColorScheme = lightColorScheme(
        primary = colorResource(id = R.color.blue_dark),
        onPrimary = Color.White,
        tertiary = colorResource(id = R.color.text_color_light_mode),
        primaryContainer = colorResource(id = R.color.top_bar_background),
        secondary = colorResource(id = R.color.blue),
        onSecondary = colorResource(id = R.color.cyan_light),
        secondaryContainer = colorResource(id = R.color.blue_white),
        background = colorResource(id = R.color.background),
        onBackground = colorResource(id = R.color.background_light_cardview)
    )

    val darkColorScheme = darkColorScheme(
        primary = colorResource(id = R.color.blue_dark),
        onPrimary = Color.White,
        tertiary = Color.White,
        primaryContainer = colorResource(id = R.color.top_bar_dark_background),
        secondary = colorResource(id = R.color.blue),
        onSecondary = colorResource(id = R.color.cyan_light),
        secondaryContainer = colorResource(id = R.color.blue_white),
        background = colorResource(id = R.color.background_dark),
        onBackground = colorResource(id = R.color.background_dark_cardview)
    )

    val colorScheme = if (darkTheme) {
        darkColorScheme
    } else {
        lightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    rememberSystemUiController().setSystemBarsColor(
        color = colorScheme.background,
        darkIcons = false
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}