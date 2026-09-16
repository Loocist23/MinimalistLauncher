package com.devaz.minimallauncher.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devaz.minimallauncher.ui.getAccentColor
import com.devaz.minimallauncher.viewmodel.SettingsViewModel

@Composable
fun MinimalLauncherTheme(
    content: @Composable () -> Unit
) {
    val settingsViewModel: SettingsViewModel = viewModel()
    val themeMode by settingsViewModel.themeMode.observeAsState("system")
    val accentColorKey by settingsViewModel.accentColorKey.observeAsState("purple")

    val darkTheme = when (themeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val accent = getAccentColor(accentColorKey)
    val accentDark = accent.copy(alpha = 0.8f)

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = accentDark,
            secondary = accentDark.copy(alpha = 0.7f),
            tertiary = accentDark.copy(alpha = 0.6f)
        )
    } else {
        lightColorScheme(
            primary = accent,
            secondary = accent.copy(alpha = 0.8f),
            tertiary = accent.copy(alpha = 0.7f)
        )
    }

    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
