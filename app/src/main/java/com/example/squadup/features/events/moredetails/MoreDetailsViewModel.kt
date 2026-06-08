package com.example.squadup.features.events.moredetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.EventParticipationType
import com.example.squadup.core.enums.UserRole
import com.example.squadup.core.permissions.EventPermissions
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MoreDetailsViewModel : ViewModel() {

    private val repository = MoreDetailsRepository()
    private var eventDetailsJob: Job? = null

    private val _uiState = MutableStateFlow(
        MoreDetailsUiState(
            isLoading = true
        )
    )

    val uiState: StateFlow<MoreDetailsUiState> = _uiState.asStateFlow()

    fun loadEvent(
        eventId: String,
        currentUserId: Int?,
        userRole: UserRole
    ) {
        if (eventId.isBlank()) {
            _uiState.value = MoreDetailsUiState(
                isLoading = false,
                errorMessage = "Evento inválido."
            )
            return
        }

        eventDetailsJob?.cancel()

        eventDetailsJob = viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            repository.observeEventDetails(eventId)
                .collect { details ->
                    _uiState.value = details
                        .withPermissions(
                            currentUserId = currentUserId,
                            userRole = userRole
                        )
                        .copy(isLoading = false)
                }
        }
    }

    private fun MoreDetailsUiState.withPermissions(
        currentUserId: Int?,
        userRole: UserRole
    ): MoreDetailsUiState {
        val parsedParticipationType = EventParticipationType.fromDbValue(participationType)

        val canManage = EventPermissions.canManageEvent(
            currentUserId = currentUserId,
            eventCreatorId = creatorId,
            userRole = userRole
        )

        val canParticipateInEvent = EventPermissions.canParticipateInEvent(
            currentUserId = currentUserId,
            eventCreatorId = creatorId,
            userRole = userRole
        )

        val canParticipateIndividual = EventPermissions.canParticipateIndividually(
            currentUserId = currentUserId,
            eventCreatorId = creatorId,
            userRole = userRole,
            participationType = parsedParticipationType
        )

        val canParticipateTeam = EventPermissions.canParticipateWithTeam(
            currentUserId = currentUserId,
            eventCreatorId = creatorId,
            userRole = userRole,
            participationType = parsedParticipationType
        )

        return copy(
            canManageEvent = canManage,
            canParticipate = canParticipateInEvent,
            canParticipateIndividually = canParticipateIndividual,
            canParticipateWithTeam = canParticipateTeam
        )
    }

    override fun onCleared() {
        eventDetailsJob?.cancel()
        super.onCleared()
    }
}
