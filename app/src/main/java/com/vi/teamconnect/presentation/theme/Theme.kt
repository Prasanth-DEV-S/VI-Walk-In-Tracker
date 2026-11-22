package com.vi.teamconnect.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

private val PremiumWhiteGoldColorScheme = lightColorScheme(
    primary = PrimaryGold,
    onPrimary = DarkCharcoal,
    primaryContainer = SoftGoldHighlight,
    onPrimaryContainer = DarkCharcoal,
    secondary = DeepGold,
    onSecondary = PrimaryBackground,
    secondaryContainer = LightGold,
    onSecondaryContainer = DarkCharcoal,
    tertiary = DeepGold,
    onTertiary = PrimaryBackground,
    background = SecondaryBackground,
    onBackground = DarkCharcoal,
    surface = PrimaryBackground,
    onSurface = DarkCharcoal,
    surfaceVariant = SoftGoldHighlight,
    onSurfaceVariant = DarkCharcoal,
    outline = LightGoldBorder,
    outlineVariant = LightGoldBorder
)

@Composable
fun ViTeamConnectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = PremiumWhiteGoldColorScheme
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
