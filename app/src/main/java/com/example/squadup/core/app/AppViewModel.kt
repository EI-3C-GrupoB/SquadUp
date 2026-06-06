package com.example.squadup.core.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.getCurrentLanguage
import com.example.squadup.core.utils.setAppLanguage
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppRepository()

    private val _uiState = MutableStateFlow(
        AppUiState(selectedLanguage = getCurrentLanguage(application))
    )
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            appRepository.sessionStatus.collect { status ->
                when (status) {
                    is SessionStatus.Authenticated -> {
                        _uiState.value = _uiState.value.copy(
                            isLoggedIn = true,
                            isInitializing = true
                        )
                        loadCurrentUser()
                    }
                    is SessionStatus.NotAuthenticated -> _uiState.value = AppUiState(
                        isInitializing = false,
                        isLoggedIn = false,
                        selectedLanguage = _uiState.value.selectedLanguage,
                        isDarkMode = _uiState.value.isDarkMode
                    )
                    else -> {}
                }
            }
        }
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            appRepository.loadCurrentUser()
                .onSuccess { user ->
                    _uiState.value = _uiState.value.copy(
                        isInitializing = false,
                        isLoggedIn = true,
                        userId = user.id,
                        displayName = user.displayName,
                        username = user.username,
                        photoUrl = user.photoUrl,
                        isAdmin = user.isAdmin
                    )
                    loadNotificationsCount()
                    setupNotificationsRealtime()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isInitializing = false
                    )
                }
        }
    }

    private fun setupNotificationsRealtime() {
        val userId = _uiState.value.userId ?: return
        viewModelScope.launch {
            try {
                val client = com.example.squadup.core.SupabaseClientProvider.client
                val channel = client.channel("notifications_count")
                val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                    table = "notificacao"
                }
                
                launch {
                    changes.collect {
                        loadNotificationsCount()
                    }
                }
                
                channel.subscribe()
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Error setup realtime", e)
            }
        }
    }

    fun loadNotificationsCount() {
        val userId = _uiState.value.userId ?: return
        viewModelScope.launch {
            try {
                val response = com.example.squadup.core.SupabaseClientProvider.client
                    .from("notificacao")
                    .select {
                        filter {
                            eq("user_id", userId)
                            eq("is_lida", false)
                        }
                    }
                val count = response.decodeList<com.example.squadup.features.notifications.NotificationsRow>().size
                _uiState.value = _uiState.value.copy(notificationsCount = count)
            } catch (e: Exception) {
                android.util.Log.e("AppViewModel", "Error loading count", e)
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            appRepository.logout().onSuccess {
                _uiState.value = AppUiState(
                    isInitializing = false,
                    isLoggedIn = false,
                    selectedLanguage = _uiState.value.selectedLanguage,
                    isDarkMode = _uiState.value.isDarkMode
                )
                onSuccess()
            }
        }
    }

    fun onLanguageChange(language: AppLanguage) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
        setAppLanguage(getApplication(), language)
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkMode = isDarkMode)
    }

    fun onAdminViewChange(isAdminView: Boolean) {
        _uiState.value = _uiState.value.copy(isAdminView = isAdminView)
    }

    fun onPhotoUrlChange(url: String) {
        _uiState.value = _uiState.value.copy(photoUrl = url)
    }
}
