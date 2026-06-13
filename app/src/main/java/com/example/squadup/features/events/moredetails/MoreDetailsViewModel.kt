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

            repository.observeEventDetails(
                eventId = eventId,
                currentUserId = currentUserId
            )
                .collect { details ->
                    val previousState = _uiState.value

                    _uiState.value = details
                        .copy(
                            isJoining = previousState.isJoining,
                            joiningRegistrationType = previousState.joiningRegistrationType,
                            isTeamPickerVisible = previousState.isTeamPickerVisible,
                            isLoadingAvailableTeams = previousState.isLoadingAvailableTeams,
                            availableTeams = previousState.availableTeams
                        )
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

    fun openTeamPicker(currentUserId: Int?) {
        val currentState = _uiState.value
        val eventId = currentState.eventId

        if (eventId == null) {
            _uiState.value = currentState.copy(errorMessage = "Evento inválido.")
            return
        }

        if (!currentState.canParticipateWithTeam) {
            _uiState.value = currentState.copy(errorMessage = "Não podes participar com equipa neste evento.")
            return
        }

        if (!currentState.userEventRegistrationStatus.isNullOrBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Já tens uma inscrição ou pedido activo neste evento.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isTeamPickerVisible = true,
                isLoadingAvailableTeams = true,
                availableTeams = emptyList(),
                errorMessage = null
            )

            repository.getAvailableTeamsForEvent(
                eventId = eventId,
                currentUserId = currentUserId
            ).onSuccess { teams ->
                _uiState.value = _uiState.value.copy(
                    isLoadingAvailableTeams = false,
                    availableTeams = teams
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoadingAvailableTeams = false,
                    errorMessage = exception.message ?: "Não foi possível carregar as tuas equipas."
                )
            }
        }
    }

    fun closeTeamPicker() {
        _uiState.value = _uiState.value.copy(
            isTeamPickerVisible = false,
            isLoadingAvailableTeams = false
        )
    }

    fun joinWithTeam(
        teamId: Int,
        currentUserId: Int?
    ) {
        val currentState = _uiState.value
        val eventId = currentState.eventId

        if (eventId == null) {
            _uiState.value = currentState.copy(errorMessage = "Evento inválido.")
            return
        }

        if (!currentState.canParticipateWithTeam) {
            _uiState.value = currentState.copy(errorMessage = "Não podes participar com equipa neste evento.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isJoining = true,
                joiningRegistrationType = "pedido_evento_equipa",
                errorMessage = null
            )

            repository.joinEventWithTeam(
                eventId = eventId,
                currentUserId = currentUserId,
                teamId = teamId
            ).onSuccess {
                _uiState.value = _uiState.value.copy(
                    isJoining = false,
                    joiningRegistrationType = null,
                    isTeamPickerVisible = false,
                    availableTeams = emptyList(),
                    errorMessage = null,
                    userEventRegistrationStatus = "pendente",
                    userEventRegistrationType = "pedido_evento_equipa"
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isJoining = false,
                    joiningRegistrationType = null,
                    errorMessage = exception.message ?: "Não foi possível pedir participação com equipa."
                )
            }
        }
    }

    fun verifyAccessCode(enteredCode: String) {
        val expected = _uiState.value.accessCode
        if (!expected.isNullOrBlank() && enteredCode.trim().uppercase() == expected.trim().uppercase()) {
            _uiState.value = _uiState.value.copy(codeVerified = true)
        } else {
            _uiState.value = _uiState.value.copy(errorMessage = "Código inválido. Tenta novamente.")
        }
    }

    override fun onCleared() {
        eventDetailsJob?.cancel()
        super.onCleared()
    }

    fun joinIndividually(currentUserId: Int?) {
        val currentState = _uiState.value
        val eventId = currentState.eventId

        if (eventId == null) {
            _uiState.value = currentState.copy(
                errorMessage = "Evento inválido."
            )
            return
        }

        if (!currentState.canParticipateIndividually) {
            _uiState.value = currentState.copy(
                errorMessage = "Não podes participar individualmente neste evento."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isJoining = true,
                joiningRegistrationType = "evento_individual",
                errorMessage = null
            )

            repository.joinEventIndividually(
                eventId = eventId,
                currentUserId = currentUserId
            ).onSuccess {
                _uiState.value = _uiState.value.copy(
                    isJoining = false,
                    joiningRegistrationType = null,
                    errorMessage = null,
                    userEventRegistrationStatus = "pendente",
                    userEventRegistrationType = "evento_individual"
                )
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isJoining = false,
                    joiningRegistrationType = null,
                    errorMessage = exception.message ?: "Não foi possível participar no evento."
                )
            }
        }
    }
}
