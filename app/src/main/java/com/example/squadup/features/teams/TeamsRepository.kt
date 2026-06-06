package com.example.squadup.features.teams

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresListDataFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class TeamsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    fun getTeamsRealtime(): Flow<TeamsUiState> = flow {
        val currentUser = getCurrentUserRow()
        
        val channel = supabaseClient.channel("teams_realtime")
        val teamsFlow = channel.postgresListDataFlow<TeamsTeamRow, Int>(
            schema = "public",
            table = "equipa",
            primaryKey = TeamsTeamRow::id
        )
        
        emitAll(teamsFlow.map { teams ->
            val users = supabaseClient.from("utilizador").select().decodeList<TeamsUserRow>().associateBy { it.id }
            val registrations = supabaseClient.from("inscricao").select().decodeList<TeamsRegistrationRow>()
            val pendingInvites: List<TeamsInviteRow> = if (currentUser != null) {
                supabaseClient.from("convite")
                    .select {
                        filter {
                            eq("convidado_user_id", currentUser.id)
                            eq("tipo", "pedido_adesao")
                            eq("estado", "pendente")
                        }
                    }
                    .decodeList<TeamsInviteRow>()
            } else emptyList()

            val myTeamIds = registrations
                .filter { it.userId == currentUser?.id && it.teamId != null }
                .mapNotNull { it.teamId }
                .toSet() + teams.filter { it.ownerId == currentUser?.id }.map { it.id }

            TeamsUiState(
                myTeams = teams
                    .filter { it.id in myTeamIds }
                    .map { it.toTeamListItem(registrations, users, currentUser?.id) },
                discoverTeams = teams
                    .filter { !it.isPrivate }
                    .map { it.toTeamListItem(registrations, users, currentUser?.id) },
                pendingJoinRequests = pendingInvites.mapNotNull { it.teamId?.toString() }.toSet()
            )
        })
        
        channel.subscribe()
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
        users: Map<Int, TeamsUserRow>,
        currentUserId: Int?
    ): TeamListItem {
        val teamRegistrations = registrations.filter { it.teamId == id }
        val currentUserReg = teamRegistrations.find { it.userId == currentUserId }
        
        // O utilizador é capitão se for o owner da equipa OU se tiver a role de capitão na inscrição
        val isCaptain = ownerId == currentUserId || 
                        currentUserReg?.role == "capitao" || 
                        currentUserReg?.isCaptain == true

        // Mapear o modalidadeId da BD de volta para o enum SportType
        val sport = SportType.entries.find { it.value == (modalidadeId?.minus(1) ?: 0) } ?: SportType.SOCCER

        return TeamListItem(
            id = id.toString(),
            name = name,
            membersCount = teamRegistrations.size,
            sportType = sport,
            inviteCode = inviteCode,
            logoUrl = logoUrl,
            isCaptain = isCaptain,
            roster = teamRegistrations.mapNotNull { registration ->
                val user = registration.userId?.let { users[it] } ?: return@mapNotNull null
                TeamRosterMember(
                    id = user.id.toString(),
                    name = user.name,
                    role = if (registration.isCaptain == true || registration.role == "capitao" || ownerId == user.id) {
                        TeamRosterRole.CAPTAIN
                    } else {
                        TeamRosterRole.MEMBER
                    }
                )
            }
        )
    }

    suspend fun requestToJoinTeam(teamId: Int): Result<Unit> {
        return try {
            val currentUser = getCurrentUserRow() ?: throw Exception("Utilizador não encontrado")
            
            // 1. Criar o pedido de adesão na tabela 'convite'
            supabaseClient.from("convite").insert(
                mapOf(
                    "equipa_id" to teamId,
                    "convidado_user_id" to currentUser.id,
                    "tipo" to "pedido_adesao",
                    "estado" to "pendente"
                )
            )

            // 2. Obter o capitão/dono da equipa para notificar
            val team = supabaseClient.from("equipa")
                .select { filter { eq("id", teamId) } }
                .decodeSingle<TeamsTeamRow>()

            if (team.ownerId != null) {
                // 3. Criar notificação para o capitão
                supabaseClient.from("notificacao").insert(
                    mapOf(
                        "user_id" to team.ownerId,
                        "titulo" to "Novo pedido de adesão",
                        "descricao" to "${currentUser.name} pediu para se juntar à equipa ${team.name}.",
                        "tipo" to "equipa",
                        "referencia_id" to teamId,
                        "referencia_tipo" to "equipa"
                    )
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
