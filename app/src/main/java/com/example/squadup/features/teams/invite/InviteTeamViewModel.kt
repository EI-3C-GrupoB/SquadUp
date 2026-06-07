package com.example.squadup.features.teams.invite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InviteTeamViewModel : ViewModel() {

    private val repository = InviteTeamRepository()

    private val _uiState = MutableStateFlow(InviteTeamUiState())
    val uiState: StateFlow<InviteTeamUiState> = _uiState.asStateFlow()

    fun loadInviteState(teamId: String) {
        if (teamId.isBlank()) return

        viewModelScope.launch {
            repository.getInviteState(teamId).onSuccess { inviteState ->
                _uiState.value = inviteState
            }
        }
    }

    fun onUsernameOrEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(usernameOrEmail = value)
    }

    fun onInviteContact(contactId: String) {
        val teamId = _uiState.value.selectedTeamId
        if (teamId.isBlank()) return

        viewModelScope.launch {
            repository.inviteUser(teamId, contactId).onSuccess {
                val updatedContacts = _uiState.value.suggestedContacts.map { contact ->
                    if (contact.id == contactId) {
                        contact.copy(status = InviteStatus.SENT)
                    } else {
                        contact
                    }
                }

                _uiState.value = _uiState.value.copy(
                    suggestedContacts = updatedContacts
                )
            }
        }
    }

    fun onSendInvite() {
        val teamId = _uiState.value.selectedTeamId
        val value = _uiState.value.usernameOrEmail

        if (teamId.isBlank()) return

        viewModelScope.launch {
            repository.inviteByUsernameOrEmail(teamId, value).onSuccess {
                _uiState.value = _uiState.value.copy(usernameOrEmail = "")
            }
        }
    }
}
