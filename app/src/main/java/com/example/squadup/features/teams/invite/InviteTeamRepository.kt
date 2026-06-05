package com.example.squadup.features.teams.invite

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class InviteTeamRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getInviteState(): Result<InviteTeamUiState> {
        return try {
            val currentUser = getCurrentUserRow()
            val team = currentUser?.let { getOwnedTeams(it.id).firstOrNull() }
            val contacts = supabaseClient
                .from("utilizador")
                .select()
                .decodeList<InviteTeamUserRow>()
                .filter { it.id != currentUser?.id }
                .take(5)
                .map {
                    SuggestedContactItem(
                        id = it.id.toString(),
                        name = it.name,
                        username = "@${it.username}",
                        subtitle = it.email.orEmpty(),
                        initials = it.name.initials()
                    )
                }

            Result.success(
                InviteTeamUiState(
                    inviteCode = team?.inviteCode.orEmpty(),
                    selectedTeamId = team?.id?.toString().orEmpty(),
                    suggestedContacts = contacts
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun inviteUser(teamId: String, userId: String): Result<Unit> {
        return try {
            val inviter = getCurrentUserRow()
            supabaseClient
                .from("convite")
                .insert(
                    InviteTeamInsertRow(
                        teamId = teamId.toIntOrNull() ?: return Result.success(Unit),
                        invitedUserId = userId.toIntOrNull() ?: return Result.success(Unit),
                        inviterUserId = inviter?.id
                    )
                )

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun inviteByUsernameOrEmail(teamId: String, value: String): Result<Unit> {
        return try {
            val normalizedValue = value.trim().removePrefix("@")
            if (normalizedValue.isBlank()) return Result.success(Unit)

            val user = supabaseClient
                .from("utilizador")
                .select()
                .decodeList<InviteTeamUserRow>()
                .firstOrNull {
                    it.username.equals(normalizedValue, ignoreCase = true) ||
                            it.email.equals(normalizedValue, ignoreCase = true)
                }
                ?: return Result.success(Unit)

            inviteUser(teamId, user.id.toString())
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserRow(): InviteTeamUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        return runCatching {
            supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<InviteTeamUserRow>()
        }.getOrNull()
    }

    private suspend fun getOwnedTeams(userId: Int): List<InviteTeamTeamRow> {
        return supabaseClient
            .from("equipa")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList()
    }

    private fun String.initials(): String {
        return split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }
}
