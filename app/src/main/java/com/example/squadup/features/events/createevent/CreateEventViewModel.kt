package com.example.squadup.features.events.createevent

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.RecurrenceType
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.SelectedLocation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CreateEventViewModel : ViewModel() {

    private val repository = CreateEventRepository()

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    init {
        loadUserContext()
    }

    fun onCoverImageSelected(uri: Uri?) {
        _uiState.value = _uiState.value.copy(
            coverImageUri = uri
        )
    }

    private fun loadUserContext() {
        viewModelScope.launch {
            repository.getUserContext()
                .onSuccess { context ->
                    _uiState.value = _uiState.value.copy(
                        userRole = context.userRole,
                        userTeams = context.userTeams,
                        formatOptions = context.formatOptions,
                        format = context.formatOptions.firstOrNull() ?: _uiState.value.format
                    )
                }
        }
    }

    fun onNextStep() {
        val next = when (_uiState.value.currentStep) {
            CreateEventStep.BASIC_INFO -> CreateEventStep.FORMAT_PLAYERS
            CreateEventStep.FORMAT_PLAYERS -> CreateEventStep.LOCATION_TIME
            CreateEventStep.LOCATION_TIME -> CreateEventStep.REVIEW
            CreateEventStep.REVIEW -> CreateEventStep.REVIEW
        }

        _uiState.value = _uiState.value.copy(currentStep = next)
    }

    fun onPreviousStep() {
        val previous = when (_uiState.value.currentStep) {
            CreateEventStep.BASIC_INFO -> null
            CreateEventStep.FORMAT_PLAYERS -> CreateEventStep.BASIC_INFO
            CreateEventStep.LOCATION_TIME -> CreateEventStep.FORMAT_PLAYERS
            CreateEventStep.REVIEW -> CreateEventStep.LOCATION_TIME
        }

        if (previous != null) {
            _uiState.value = _uiState.value.copy(currentStep = previous)
        }
    }

    fun onGoToStep(step: CreateEventStep) {
        _uiState.value = _uiState.value.copy(currentStep = step)
    }

    fun onEventNameChange(value: String) {
        _uiState.value = _uiState.value.copy(eventName = value)
    }

    fun onPrivacyChange(isPublic: Boolean) {
        _uiState.value = _uiState.value.copy(
            isPublic = isPublic,
            isPublicEvent = isPublic
        )
    }

    fun onSportSelect(sport: SportType) {
        val currentSport = _uiState.value.selectedSport
        val newSport = if (currentSport == sport) null else sport

        _uiState.value = _uiState.value.copy(
            selectedSport = newSport,
            format = formatsFor(newSport).firstOrNull().orEmpty()
        )
    }

    fun formatsFor(sport: SportType?): List<String> {
        return _uiState.value.formatOptions.ifEmpty {
            EventFormat.entries.map { it.name.replace("_", " ") }
        }
    }

    fun onEventFormatChange(eventFormat: EventFormat) {
        _uiState.value = _uiState.value.copy(eventFormat = eventFormat)
    }

    fun onFormatChange(format: String) {
        _uiState.value = _uiState.value.copy(format = format)
    }

    fun onMaxTeamsChange(delta: Int) {
        val newMaxTeams = (_uiState.value.maxTeams + delta).coerceIn(2, 64)
        _uiState.value = _uiState.value.copy(maxTeams = newMaxTeams)
    }

    fun onGeneralRulesChange(value: String) {
        if (value.length <= 2000) {
            _uiState.value = _uiState.value.copy(generalRules = value)
        }
    }

    fun onPublicEventToggle(value: Boolean) {
        _uiState.value = _uiState.value.copy(
            isPublicEvent = value,
            isPublic = value
        )
    }

    fun onEntryFeeChange(value: String) {
        _uiState.value = _uiState.value.copy(entryFee = value)
    }

    fun onAllowTeamsToggle(value: Boolean) {
        if (!value && !_uiState.value.allowFreeAgents) return
        _uiState.value = _uiState.value.copy(allowTeams = value)
    }

    fun onAllowFreeAgentsToggle(value: Boolean) {
        if (!value && !_uiState.value.allowTeams) return
        _uiState.value = _uiState.value.copy(allowFreeAgents = value)
    }

    fun onVenueChange(value: String) {
        _uiState.value = _uiState.value.copy(venue = value)
    }

    fun onLocationSelected(location: SelectedLocation) {
        _uiState.value = _uiState.value.copy(
            latitude = location.lat,
            longitude = location.lng,
            venue = location.address
        )
    }

    fun onEventDateChange(value: String) {
        _uiState.value = _uiState.value.copy(eventDate = value)
    }

    fun onStartTimeChange(value: String) {
        _uiState.value = _uiState.value.copy(startTime = value)
    }

    fun onEndTimeChange(value: String) {
        _uiState.value = _uiState.value.copy(endTime = value)
    }

    fun onRegistrationStartDateChange(value: String) {
        _uiState.value = _uiState.value.copy(registrationStartDate = value)
    }

    fun onRegistrationStartTimeChange(value: String) {
        _uiState.value = _uiState.value.copy(registrationStartTime = value)
    }

    fun onRegistrationEndDateChange(value: String) {
        _uiState.value = _uiState.value.copy(registrationEndDate = value)
    }

    fun onRegistrationEndTimeChange(value: String) {
        _uiState.value = _uiState.value.copy(registrationEndTime = value)
    }

    fun onRecurringToggle(value: Boolean) {
        _uiState.value = _uiState.value.copy(
            isRecurring = value,
            showRecurrenceDialog = value
        )
    }

    fun onShowRecurrenceDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showRecurrenceDialog = show)
    }

    fun onRecurrenceTypeChange(type: RecurrenceType) {
        _uiState.value = _uiState.value.copy(recurrenceType = type)
    }

    fun onRecurringDayToggle(day: Int) {
        val currentDays = _uiState.value.recurringDays

        _uiState.value = _uiState.value.copy(
            recurringDays = if (day in currentDays) {
                currentDays - day
            } else {
                currentDays + day
            }
        )
    }

    fun onTeamNotifyToggle(teamId: String) {
        val currentTeams = _uiState.value.teamsToNotify

        _uiState.value = _uiState.value.copy(
            teamsToNotify = if (teamId in currentTeams) {
                currentTeams - teamId
            } else {
                currentTeams + teamId
            }
        )
    }

    fun createEvent(
        context: Context,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            repository.createEvent(
                state = _uiState.value,
                context = context
            ).onSuccess {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = null
                )
                onSuccess()
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = exception.message ?: "Não foi possível criar o evento."
                )
            }
        }
    }
}
