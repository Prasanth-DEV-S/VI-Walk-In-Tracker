package com.vi.teamconnect.presentation.notifications

import androidx.lifecycle.ViewModel
import com.vi.teamconnect.domain.model.NotificationItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor() : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationItem>>(
        listOf(
            NotificationItem(
                notificationId = "1",
                title = "Welcome to VI Team Connect",
                message = "Your employee account has been created and verified.",
                timestamp = System.currentTimeMillis() - 3600000,
                type = "INFO"
            ),
            NotificationItem(
                notificationId = "2",
                title = "System Update",
                message = "Shared customer visit register is synced with Cloud Firestore.",
                timestamp = System.currentTimeMillis() - 86400000,
                type = "UPDATE"
            )
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()
}
