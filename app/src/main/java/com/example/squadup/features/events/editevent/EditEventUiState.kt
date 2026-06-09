package com.example.squadup.features.events.editevent

data class EditEventUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null,

    val eventId: String = "",
    val title: String = "",
    val description: String = "",
    val address: String = "",
    val startDate: String = "",
    val startTime: String = "",
    val endDate: String = "",
    val endTime: String = "",
    val entryFee: String = "",
    val maxTeams: String = "",
    val participationLimit: String = "",
    val isPublic: Boolean = true,
) {
    val canSave: Boolean get() = title.isNotBlank() && !isSaving
}
