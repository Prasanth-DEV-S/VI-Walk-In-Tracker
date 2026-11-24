package com.vi.teamconnect.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val ViBrandColorScheme = lightColorScheme(
    primary = ViRedPrimary,
    onPrimary = PrimaryBackground,
    primaryContainer = ViLightRed,
    onPrimaryContainer = ViDarkRed,
    secondary = ViRedPrimary,
    onSecondary = PrimaryBackground,
    secondaryContainer = ViLightYellow,
    onSecondaryContainer = DarkText,
    tertiary = ViYellowAccent,
    onTertiary = DarkText,
    background = SecondaryBackground,
    onBackground = DarkText,
    surface = PrimaryBackground,
    onSurface = DarkText,
    surfaceVariant = ViLightYellow,
    onSurfaceVariant = DarkText,
    outline = BorderColor,
    outlineVariant = BorderColor
)

@Composable
fun ViTeamConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = ViBrandColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = PrimaryBackground.toArgb()
            val controller = androidx.core.view.WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
