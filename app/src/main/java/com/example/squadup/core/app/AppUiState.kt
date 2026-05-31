package com.example.squadup.core.app

import com.example.squadup.core.utils.AppLanguage

data class AppUiState(
    val selectedLanguage: AppLanguage = AppLanguage.EN,
    val isDarkMode: Boolean = false,
    val isAdmin: Boolean = true,
    val isAdminView: Boolean = false
)