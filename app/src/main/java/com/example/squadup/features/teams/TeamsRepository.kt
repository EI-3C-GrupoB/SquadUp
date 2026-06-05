package com.example.squadup.features.teams

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class TeamsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getTeams(): Result<TeamsUiState> {
        return try {
            val currentUser = getCurrentUserRow()
            val teams = supabaseClient.from("equipa").select().decodeList<TeamsTeamRow>()
            val users = supabaseClient.from("utilizador").select().decodeList<TeamsUserRow>().associateBy { it.id }
            val registrations = supabaseClient.from("inscricao").select().decodeList<TeamsRegistrationRow>()

            val myTeamIds = registrations
                .filter { it.userId == currentUser?.id && it.teamId != null }
                .mapNotNull { it.teamId }
                .toSet() + teams.filter { it.ownerId == currentUser?.id }.map { it.id }

            Result.success(
                TeamsUiState(
                    myTeams = teams
                        .filter { it.id in myTeamIds }
                        .map { it.toTeamListItem(registrations, users) },
                    discoverTeams = teams
                        .filterNot { it.id in myTeamIds }
                        .map { it.toTeamListItem(registrations, users) }
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserRow(): TeamsUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null

        return runCatching {
            supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<TeamsUserRow>()
        }.getOrNull()
    }

    private fun TeamsTeamRow.toTeamListItem(
        registrations: List<TeamsRegistrationRow>,
        users: Map<Int, TeamsUserRow>
    ): TeamListItem {
        val teamRegistrations = registrations.filter { it.teamId == id }

        return TeamListItem(
            id = id.toString(),
            name = name,
            membersCount = teamRegistrations.size,
            sportType = SportType.SOCCER,
            location = "",
            inviteCode = inviteCode,
            roster = teamRegistrations.mapNotNull { registration ->
                val user = registration.userId?.let { users[it] } ?: return@mapNotNull null
                TeamRosterMember(
                    id = user.id.toString(),
                    name = user.name,
                    role = if (registration.isCaptain == true || registration.role == "capitao") {
                        TeamRosterRole.CAPTAIN
                    } else {
                        TeamRosterRole.MEMBER
                    }
                )
            }
        )
    }
}
