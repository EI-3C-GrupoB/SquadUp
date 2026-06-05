package com.example.squadup.features.teams.createteam

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlin.random.Random

class CreateTeamRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun createTeam(state: CreateTeamUiState): Result<Unit> {
        return try {
            val owner = getCurrentUserRow()
            supabaseClient
                .from("equipa")
                .insert(
                    CreateTeamInsertRow(
                        name = state.teamName.trim(),
                        inviteCode = generateInviteCode(state.teamName),
                        ownerId = owner?.id
                    )
                )

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserRow(): CreateTeamUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        return runCatching {
            supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<CreateTeamUserRow>()
        }.getOrNull()
    }

    private fun generateInviteCode(teamName: String): String {
        val prefix = teamName
            .filter { it.isLetterOrDigit() }
            .take(3)
            .uppercase()
            .padEnd(3, 'X')

        return "$prefix-${Random.nextInt(1000, 9999)}"
    }
}
