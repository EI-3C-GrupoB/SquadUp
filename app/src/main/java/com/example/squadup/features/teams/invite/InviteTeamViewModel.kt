package com.example.squadup.features.teams.invite

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InviteTeamViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        InviteTeamUiState(
            suggestedContacts = listOf(
                SuggestedContactItem(
                    id = "1",
                    name = "Marcus Jensen",
                    username = "@marcus_lifts",
                    subtitle = "In your contacts",
                    initials = "MJ"
                ),
                SuggestedContactItem(
                    id = "2",
                    name = "Sarah Chen",
                    username = "@schen_pro",
                    subtitle = "Frequent squad mate",
                    initials = "SC",
                    status = InviteStatus.SENT
                ),
                SuggestedContactItem(
                    id = "3",
                    name = "David Okafor",
                    username = "@dave_hoops",
                    subtitle = "In your contacts",
                    initials = "DO"
                )
            )
        )
    )

    val uiState: StateFlow<InviteTeamUiState> = _uiState.asStateFlow()

    fun onUsernameOrEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(usernameOrEmail = value)
    }

    fun onInviteContact(contactId: String) {
        val updatedContacts = _uiState.value.suggestedContacts.map { contact ->
            if (contact.id == contactId) {
                contact.copy(status = InviteStatus.SENT)
            } else {
                contact
            }
        }

        _uiState.value = _uiState.value.copy(suggestedContacts = updatedContacts)
    }

    fun onSendInvite() {
        _uiState.value = _uiState.value.copy(usernameOrEmail = "")
    }
}