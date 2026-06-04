package com.example.squadup.features.notifications

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        NotificationsUiState(
            todayNotifications = listOf(
                NotificationItem(
                    id = "1",
                    title = "Match Reminder",
                    description = "Your game The Mavericks vs Red Eagles starts at 6:30 PM. Don’t forget your jersey!",
                    timeLabel = "2h ago",
                    type = NotificationType.MATCH,
                    primaryAction = "Get Directions",
                    secondaryAction = "Details"
                ),
                NotificationItem(
                    id = "2",
                    title = "New Event Nearby",
                    description = "Open Game: Sunday Morning Scrimmage at Viana do Castelo. Join 12 others!",
                    timeLabel = "Today",
                    type = NotificationType.EVENT,
                    distanceLabel = "0.8 miles away"
                )
            ),
            earlierNotifications = listOf(
                NotificationItem(
                    id = "3",
                    title = "Waiting List Update",
                    description = "A spot opened up for the Summer League. Claim it now!",
                    timeLabel = "Yesterday",
                    type = NotificationType.EVENT
                ),
                NotificationItem(
                    id = "4",
                    title = "System Update",
                    description = "SquadUp v2.4 is live. Improved team chat and match stats are now available.",
                    timeLabel = "2 days ago",
                    type = NotificationType.UPDATE
                ),
                NotificationItem(
                    id = "5",
                    title = "New Team Members",
                    description = "Kobbie Mainoo and 2 others joined your squad ‘Midnight Hoops’.",
                    timeLabel = "Oct 12",
                    type = NotificationType.TEAM
                )
            )
        )
    )

    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()
}