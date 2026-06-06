package com.example.squadup.features.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel : ViewModel() {

    private val repository = NotificationsRepository()
    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            repository.getNotificationsRealtime().collect { notifications ->
                _uiState.value = notifications
            }
        }
    }

    fun respondToJoinRequest(notification: NotificationItem, accept: Boolean) {
        val conviteId = notification.referenceId ?: run {
            android.util.Log.e("NotificationsVM", "No referenceId for notification: ${notification.id}")
            return
        }
        android.util.Log.d("NotificationsVM", "Responding to join request for convite: $conviteId, accept: $accept")
        viewModelScope.launch {
            repository.respondToJoinRequest(notification.id, conviteId, accept)
                .onSuccess {
                    android.util.Log.d("NotificationsVM", "Response successful")
                    // Realtime will handle the list update
                }
                .onFailure { error ->
                    android.util.Log.e("NotificationsVM", "Response failed: ${error.message}", error)
                }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            repository.deleteNotification(notificationId)
        }
    }
}
