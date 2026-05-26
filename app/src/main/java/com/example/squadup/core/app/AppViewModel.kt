package com.example.squadup.core.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.squadup.core.utils.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.squadup.core.utils.getCurrentLanguage
import com.example.squadup.core.utils.setAppLanguage

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(
        AppUiState(
            selectedLanguage = getCurrentLanguage(application)
        )
    )
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun onLanguageChange(language: AppLanguage) {
        _uiState.value = _uiState.value.copy(selectedLanguage = language)
        setAppLanguage(getApplication(), language)
    }

    fun onDarkModeChange(isDarkMode: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkMode = isDarkMode)
    }
}