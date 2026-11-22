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
import com.vi.teamconnect.presentation.theme.DarkCharcoal
import com.vi.teamconnect.presentation.theme.DeepGold
import com.vi.teamconnect.presentation.theme.LightGold
import com.vi.teamconnect.presentation.theme.LightGoldBorder
import com.vi.teamconnect.presentation.theme.PrimaryGold
import com.vi.teamconnect.presentation.theme.StatusActiveGreen
import com.vi.teamconnect.presentation.theme.StatusPendingAmber
import com.vi.teamconnect.presentation.theme.StatusRejectedRed

@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, borderColor) = when (text.trim().lowercase()) {
        "active" -> Triple(StatusActiveGreen.copy(alpha = 0.1f), StatusActiveGreen, StatusActiveGreen.copy(alpha = 0.3f))
        "yes" -> Triple(LightGold.copy(alpha = 0.3f), DeepGold, LightGoldBorder)
        "no" -> Triple(Color(0xFFF3F4F6), Color(0xFF6B7280), Color(0xFFE5E7EB))
        "pending_approval", "pending" -> Triple(StatusPendingAmber.copy(alpha = 0.1f), StatusPendingAmber, StatusPendingAmber.copy(alpha = 0.3f))
        "rejected", "suspended" -> Triple(StatusRejectedRed.copy(alpha = 0.1f), StatusRejectedRed, StatusRejectedRed.copy(alpha = 0.3f))
        "prepaid", "pre" -> Triple(LightGold.copy(alpha = 0.25f), DeepGold, LightGoldBorder)
        "postpaid", "pp" -> Triple(DarkCharcoal.copy(alpha = 0.08f), DarkCharcoal, LightGoldBorder)
        else -> Triple(LightGold.copy(alpha = 0.15f), DarkCharcoal, LightGoldBorder)
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
