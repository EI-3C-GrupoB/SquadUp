package com.example.squadup.features.teams.invite

data class InviteTeamUiState(
    val inviteCode: String = "",
    val selectedTeamId: String = "",
    val usernameOrEmail: String = "",
    val suggestedContacts: List<SuggestedContactItem> = emptyList()
)

data class SuggestedContactItem(
    val id: String,
    val name: String,
    val username: String,
    val subtitle: String,
    val initials: String,
    val status: InviteStatus = InviteStatus.INVITE
)

enum class InviteStatus {
    INVITE,
    SENT,
    MEMBER
}
