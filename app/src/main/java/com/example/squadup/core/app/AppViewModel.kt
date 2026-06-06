package com.example.squadup.core.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.getCurrentLanguage
import com.example.squadup.core.utils.setAppLanguage
import io.github.jan.supabase.auth.status.SessionStatus
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
                    is SessionStatus.Authenticated -> loadCurrentUser()
                    is SessionStatus.NotAuthenticated -> _uiState.value = AppUiState(
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
                        isLoggedIn = true,
                        userId = user.id,
                        displayName = user.displayName,
                        username = user.username,
                        isAdmin = user.isAdmin
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoggedIn = false)
                }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            appRepository.logout().onSuccess {
                _uiState.value = AppUiState(
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
}
