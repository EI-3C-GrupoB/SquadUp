package com.example.squadup.features.events.createevent

import androidx.lifecycle.ViewModel
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CreateEventViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    init {
        loadUserContext()
    }

    private fun loadUserContext() {
        // Hardcoded — substituir com dados da sessão quando ligar à BD
        // Mudar role para null ou ORGANIZER para testar sem a secção de equipas
        _uiState.value = _uiState.value.copy(
            userRole = UserRole.PLAYER_ORGANIZER,
            userTeams = listOf(
                NotifyTeamItem("1", "The Mavericks", 12, SportType.SOCCER),
                NotifyTeamItem("2", "Midnight Hoop", 8, SportType.BASKETBALL)
            )
        )
    }

    // Navigation
    fun onNextStep() {
        val next = when (_uiState.value.currentStep) {
            CreateEventStep.BASIC_INFO     -> CreateEventStep.FORMAT_PLAYERS
            CreateEventStep.FORMAT_PLAYERS -> CreateEventStep.LOCATION_TIME
            CreateEventStep.LOCATION_TIME  -> CreateEventStep.REVIEW
            CreateEventStep.REVIEW         -> CreateEventStep.REVIEW
        }
        _uiState.value = _uiState.value.copy(currentStep = next)
    }

    fun onPreviousStep() {
        val prev = when (_uiState.value.currentStep) {
            CreateEventStep.BASIC_INFO     -> null
            CreateEventStep.FORMAT_PLAYERS -> CreateEventStep.BASIC_INFO
            CreateEventStep.LOCATION_TIME  -> CreateEventStep.FORMAT_PLAYERS
            CreateEventStep.REVIEW         -> CreateEventStep.LOCATION_TIME
        }
        if (prev != null) _uiState.value = _uiState.value.copy(currentStep = prev)
    }

    fun onGoToStep(step: CreateEventStep) {
        _uiState.value = _uiState.value.copy(currentStep = step)
    }

    // Step 1
    fun onEventNameChange(value: String) {
        _uiState.value = _uiState.value.copy(eventName = value)
    }

    fun onPrivacyChange(isPublic: Boolean) {
        _uiState.value = _uiState.value.copy(isPublic = isPublic)
    }

    fun onSportSelect(sport: SportType) {
        val current = _uiState.value.selectedSport
        // toggle: clicking the same sport deselects it
        _uiState.value = _uiState.value.copy(
            selectedSport = if (current == sport) null else sport,
            format = formatsFor(if (current == sport) null else sport).first()
        )
    }

    fun formatsFor(sport: SportType?): List<String> = when (sport) {
        SportType.SOCCER     -> listOf("11v11 Full Match", "7v7 Format", "5v5 Small Sided")
        SportType.BASKETBALL -> listOf("5v5 Full Court", "3v3 Half Court")
        SportType.PADDLE     -> listOf("Singles", "Doubles")
        SportType.VOLLEYBALL -> listOf("6v6 Standard", "4v4 Beach")
        SportType.FUTSAL     -> listOf("5v5 Futsal", "4v4 Indoor")
        null                 -> listOf("5v5 Full Court")
    }

    // Step 2
    fun onFormatChange(format: String) {
        _uiState.value = _uiState.value.copy(format = format)
    }

    fun onMaxTeamsChange(delta: Int) {
        val new = (_uiState.value.maxTeams + delta).coerceIn(2, 64)
        _uiState.value = _uiState.value.copy(maxTeams = new)
    }

    fun onGeneralRulesChange(value: String) {
        if (value.length <= 2000)
            _uiState.value = _uiState.value.copy(generalRules = value)
    }

    fun onPublicEventToggle(value: Boolean) {
        _uiState.value = _uiState.value.copy(isPublicEvent = value)
    }

    fun onEntryFeeChange(value: String) {
        _uiState.value = _uiState.value.copy(entryFee = value)
    }

    // Step 3
    fun onVenueChange(value: String) {
        _uiState.value = _uiState.value.copy(venue = value)
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
        val current = _uiState.value.recurringDays
        _uiState.value = _uiState.value.copy(
            recurringDays = if (day in current) current - day else current + day
        )
    }

    // Review
    fun onTeamNotifyToggle(teamId: String) {
        val current = _uiState.value.teamsToNotify
        _uiState.value = _uiState.value.copy(
            teamsToNotify = if (teamId in current) current - teamId else current + teamId
        )
    }
}
