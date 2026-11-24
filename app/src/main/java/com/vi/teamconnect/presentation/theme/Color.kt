package com.vi.teamconnect.presentation.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// VI Brand Primary Palette
val PrimaryBackground = Color(0xFFFFFFFF)       // #FFFFFF - Primary Background (White)
val SecondaryBackground = Color(0xFFF8F8F8)     // #F8F8F8 - Light Background

val ViRedPrimary = Color(0xFFE31E36)            // #E31E36 - VI Red (Primary Brand Color)
val ViDarkRed = Color(0xFFC8102E)               // #C8102E - Dark Red (Pressed / Emphasis)
val ViYellowAccent = Color(0xFFFFD500)          // #FFD500 - VI Yellow (Highlight & Accent)
val ViLightYellow = Color(0xFFFFF8D9)           // #FFF8D9 - Light Yellow (Subtle Highlight Background)
val ViLightRed = Color(0xFFFFF0F1)              // #FFF0F1 - Light Red (Error / Selection)

// Supporting Typography & UI Elements
val DarkText = Color(0xFF252525)                // #252525 - Dark Primary Text
val SecondaryText = Color(0xFF777777)           // #777777 - Secondary Supporting Text
val BorderColor = Color(0xFFEAEAEA)             // #EAEAEA - Border Color

// Status Colors
val StatusActiveGreen = Color(0xFF198754)       // #198754 - Success Green
val StatusPendingYellow = Color(0xFFFFD500)      // #FFD500 - Pending Yellow
val StatusRejectedRed = Color(0xFFD32F2F)        // #D32F2F - Error Red
val StatusSuspendedDarkRed = Color(0xFFC8102E)   // #C8102E - Suspended Dark Red

// Aliases for legacy component compatibility
val DarkCharcoal = DarkText
val SecondaryGrey = SecondaryText
val LightGoldBorder = BorderColor
val SoftGoldHighlight = ViLightYellow
val DeepGold = ViRedPrimary
val PrimaryGold = ViRedPrimary
val LightGold = ViLightYellow

// VI Brand Gradients
val PrimaryRedGradient = Brush.horizontalGradient(
    colors = listOf(ViRedPrimary, ViDarkRed)
)

val PrimaryYellowGradient = Brush.horizontalGradient(
    colors = listOf(ViYellowAccent, Color(0xFFFFC107))
)

val GoldButtonGradient = PrimaryRedGradient
val GoldButtonYellowGradient = PrimaryYellowGradient

val DashboardHighlightGradient = Brush.verticalGradient(
    colors = listOf(PrimaryBackground, ViLightYellow)
)
