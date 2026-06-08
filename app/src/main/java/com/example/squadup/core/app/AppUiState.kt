package com.example.squadup.core.app

import com.example.squadup.core.enums.UserRole
import com.example.squadup.core.utils.AppLanguage

data class AppUiState(
    // estado do carregamento inicial
    val isInitializing: Boolean = true,

    // sessão do utilizador
    val isLoggedIn: Boolean = false,
    val userId: Int? = null,
    val displayName: String = "",
    val username: String = "",
    val photoUrl: String? = null,
    val isAdmin: Boolean = false,
    val userRole: UserRole = UserRole.PLAYER,

    // preferências globais
    val selectedLanguage: AppLanguage = AppLanguage.EN,
    val isDarkMode: Boolean = false,

    // controlo de vista admin
    val isAdminView: Boolean = false,

    // notificações
    val notificationsCount: Int = 0
)
