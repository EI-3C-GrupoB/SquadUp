package com.example.squadup.features.auth.register

import androidx.annotation.StringRes
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.UserRole
import com.example.squadup.core.ui.components.SelectedLocation

data class RegisterUiState(
    val fullName: String = "",
    val username: String = "",
    val email: String = "",
    val birthDate: String = "",
    val password: String = "",
    val accountType: UserRole = UserRole.PLAYER,
    val modalities: List<Modality> = emptyList(),
    val selectedModalities: Set<String> = emptySet(),
    val location: SelectedLocation? = null,
    val playStyle: PlayStyle = PlayStyle.LOW,
    val notificationRadius: Int = 25,
    val showLocationPicker: Boolean = false,
    val isLoading: Boolean = false,
    val isRegisterSuccessful: Boolean = false,
    @param:StringRes val errorMessage: Int? = null
)
