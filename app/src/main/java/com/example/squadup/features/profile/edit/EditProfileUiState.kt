package com.example.squadup.features.profile.edit

import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.SelectedLocation

data class EditProfileUiState(
    val name: String = "",
    val username: String = "",
    val location: SelectedLocation? = null,
    val showLocationPicker: Boolean = false,
    val photoUrl: String? = null,
    val selectedPlayStyle: PlayStyle = PlayStyle.HIGH,
    val notificationRadius: Int = 25,
    val selectedSports: List<SportType> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: Int? = null
)
