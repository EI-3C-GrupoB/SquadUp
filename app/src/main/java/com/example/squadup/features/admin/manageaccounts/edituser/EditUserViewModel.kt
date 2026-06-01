package com.example.squadup.features.admin.manageaccounts.edituser

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.squadup.features.admin.manageaccounts.AccountRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EditUserViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userId: String = savedStateHandle.get<String>("userId") ?: ""

    private val _uiState = MutableStateFlow(EditUserUiState())
    val uiState: StateFlow<EditUserUiState> = _uiState.asStateFlow()

    private val staticUsers = mapOf(
        "1" to EditUserUiState("1", "James D.", "james@squadup.com", "JD", AccountRole.Player),
        "2" to EditUserUiState("2", "Sarah K.", "sarah.k@club.org", "SK", AccountRole.Organizer),
        "3" to EditUserUiState("3", "Marcus L.", "ml@proleague.com", "ML", AccountRole.Player),
        "4" to EditUserUiState("4", "Ana R.", "ana.r@admin.com", "AR", AccountRole.Admin),
        "5" to EditUserUiState("5", "Tom C.", "tom.c@club.org", "TC", AccountRole.Organizer),
        "6" to EditUserUiState("6", "Ben W.", "ben.w@league.com", "BW", AccountRole.Player),
        "7" to EditUserUiState("7", "Lisa M.", "lisa.m@squad.com", "LM", AccountRole.Player),
        "8" to EditUserUiState("8", "Pedro F.", "pedro.f@admin.com", "PF", AccountRole.Admin),
        "9" to EditUserUiState("9", "Kim J.", "kim.j@teams.com", "KJ", AccountRole.Player)
    )

    init {
        _uiState.value = staticUsers[userId] ?: EditUserUiState(userId = userId)
    }

    fun onRoleChange(role: AccountRole) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
    }

    fun onToggleSuspend() {
        _uiState.value = _uiState.value.copy(isSuspended = !_uiState.value.isSuspended)
    }
}
