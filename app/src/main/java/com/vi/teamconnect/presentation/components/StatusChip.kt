package com.vi.teamconnect.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vi.teamconnect.presentation.theme.StatusActiveGreen
import com.vi.teamconnect.presentation.theme.StatusPendingOrange
import com.vi.teamconnect.presentation.theme.StatusPpPurple
import com.vi.teamconnect.presentation.theme.StatusPreBlue
import com.vi.teamconnect.presentation.theme.StatusRejectedRed

@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when (text.trim().lowercase()) {
        "active", "yes" -> StatusActiveGreen.copy(alpha = 0.15f) to StatusActiveGreen
        "pending_approval", "pending" -> StatusPendingOrange.copy(alpha = 0.15f) to StatusPendingOrange
        "rejected", "no", "suspended" -> StatusRejectedRed.copy(alpha = 0.15f) to StatusRejectedRed
        "pre" -> StatusPreBlue.copy(alpha = 0.15f) to StatusPreBlue
        "pp" -> StatusPpPurple.copy(alpha = 0.15f) to StatusPpPurple
        else -> Color.Gray.copy(alpha = 0.15f) to Color.DarkGray
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
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
            .background(bgColor, shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
