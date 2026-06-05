package com.example.squadup.features.teams.invite

data class InviteTeamUiState(
    val inviteCode: String = "SQUAD-X92",
    val usernameOrEmail: String = "",
    val suggestedContacts: List<SuggestedContactItem> = emptyList()
)

data class SuggestedContactItem(
    val id: String,
    val name: String,
    val username: String,
    val subtitle: String,
    val initials: String,
    val status: InviteStatus = InviteStatus.NOT_SENT
)

enum class InviteStatus {
    NOT_SENT,
    SENT
}