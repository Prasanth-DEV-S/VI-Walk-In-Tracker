package com.vi.teamconnect.domain.model

data class NotificationItem(
    val notificationId: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val type: String = "INFO", // REGISTRATION, APPROVAL, REJECTION, RECORD_UPDATE
    val recipientUid: String? = null
)
