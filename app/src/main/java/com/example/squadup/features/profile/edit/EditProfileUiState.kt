package com.example.squadup.features.profile.edit

import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.SportType

data class EditProfileUiState(
    val username: String = "",
    val selectedPlayStyle: PlayStyle = PlayStyle.HIGH,
    val selectedSports: List<SportType> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: Int? = null
)
