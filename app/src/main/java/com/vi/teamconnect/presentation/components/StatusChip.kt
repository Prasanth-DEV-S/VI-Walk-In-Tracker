package com.vi.teamconnect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.presentation.theme.BorderColor
import com.vi.teamconnect.presentation.theme.DarkText
import com.vi.teamconnect.presentation.theme.StatusActiveGreen
import com.vi.teamconnect.presentation.theme.StatusPendingYellow
import com.vi.teamconnect.presentation.theme.StatusRejectedRed
import com.vi.teamconnect.presentation.theme.StatusSuspendedDarkRed
import com.vi.teamconnect.presentation.theme.ViLightRed
import com.vi.teamconnect.presentation.theme.ViLightYellow
import com.vi.teamconnect.presentation.theme.ViRedPrimary

@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (text.trim().lowercase()) {
        "active" -> Triple(StatusActiveGreen.copy(alpha = 0.1f), StatusActiveGreen, StatusActiveGreen.copy(alpha = 0.3f))
        "yes" -> Triple(ViLightYellow, Color(0xFFB78100), StatusPendingYellow)
        "no" -> Triple(Color(0xFFF3F4F6), Color(0xFF6B7280), BorderColor)
        "pending_approval", "pending" -> Triple(ViLightYellow, Color(0xFFB78100), StatusPendingYellow)
        "rejected" -> Triple(ViLightRed, StatusRejectedRed, StatusRejectedRed.copy(alpha = 0.3f))
        "suspended" -> Triple(ViLightRed, StatusSuspendedDarkRed, StatusSuspendedDarkRed.copy(alpha = 0.3f))
        "prepaid", "pre" -> Triple(ViLightRed, ViRedPrimary, ViRedPrimary.copy(alpha = 0.3f))
        "postpaid", "pp" -> Triple(Color(0xFFF0F0F0), DarkText, BorderColor)
        else -> Triple(ViLightYellow, DarkText, BorderColor)
    }

    val label = when (text.uppercase()) {
        "PENDING_APPROVAL" -> "Pending Approval"
        "ACTIVE" -> "Active"
        "REJECTED" -> "Rejected"
        "SUSPENDED" -> "Suspended"
        else -> text
    }

    Text(
        text = label,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .background(bgColor, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
