package com.vi.teamconnect.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// White & Gold Premium Palette
val PrimaryBackground = Color(0xFFFFFFFF)
val SecondaryBackground = Color(0xFFFAF9F6)
val PrimaryGold = Color(0xFFD4AF37)
val LightGold = Color(0xFFF5E6B3)
val DeepGold = Color(0xFFB8860B)
val DarkCharcoal = Color(0xFF252525)
val SecondaryGrey = Color(0xFF777777)
val CardWhite = Color(0xFFFFFFFF)
val LightGoldBorder = Color(0xFFE8D9AE)
val SoftGoldHighlight = Color(0xFFFAF3DF)

// Status colors
val StatusActiveGreen = Color(0xFF2E7D32)
val StatusPendingAmber = Color(0xFFD97706)
val StatusRejectedRed = Color(0xFFDC2626)

// Premium Gradients
val PrimaryGoldGradient = Brush.horizontalGradient(
    colors = listOf(LightGold, PrimaryGold)
)

val GoldButtonGradient = Brush.horizontalGradient(
    colors = listOf(LightGold, PrimaryGold)
)

val DashboardHighlightGradient = Brush.verticalGradient(
    colors = listOf(PrimaryBackground, SoftGoldHighlight)
)

val PremiumCardAccentGradient = Brush.horizontalGradient(
    colors = listOf(PrimaryGold, DeepGold)
)
