package com.example.squadup.features.notifications

enum class NotificationType {
    MATCH,
    EVENT,
    UPDATE,
    TEAM
}

data class NotificationItem(
    val id: String,
    val title: String,
    val description: String,
    val timeLabel: String,
    val type: NotificationType,
    val distanceLabel: String? = null,
    val imageUrl: String? = null,
    val primaryAction: String? = null,
    val secondaryAction: String? = null
)

data class NotificationsUiState(
    val todayNotifications: List<NotificationItem> = emptyList(),
    val earlierNotifications: List<NotificationItem> = emptyList()
) {
    val newNotificationsCount: Int
        get() = todayNotifications.size
}