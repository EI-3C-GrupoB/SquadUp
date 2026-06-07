package com.example.squadup.features.teams

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.SportType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class TeamsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    fun getTeamsRealtime(): Flow<TeamsUiState> = flow {
        val currentUser = getCurrentUserRow()

        if (currentUser == null) {
            emit(TeamsUiState())
            return@flow
        }

        val channelSuffix = "${currentUser.id}_${System.currentTimeMillis()}"

        val teamsChannel = supabaseClient.channel("teams_equipa_$channelSuffix")
        val registrationsChannel = supabaseClient.channel("teams_inscricao_$channelSuffix")
        val invitesChannel = supabaseClient.channel("teams_convite_$channelSuffix")

        val teamsChanges = teamsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "equipa"
            }
            .map { Unit }

        val registrationsChanges = registrationsChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "inscricao"
            }
            .map { Unit }

        val invitesChanges = invitesChannel
            .postgresChangeFlow<PostgresAction>(schema = "public") {
                table = "convite"
            }
            .map { Unit }

        teamsChannel.subscribe()
        registrationsChannel.subscribe()
        invitesChannel.subscribe()

        emitAll(
            merge(
                teamsChanges,
                registrationsChanges,
                invitesChanges
            )
                .onStart {
                    emit(Unit)
                }
                .map {
                    loadTeamsUiState(currentUser)
                }
        )
    }

    private suspend fun loadTeamsUiState(
        currentUser: TeamsUserRow
    ): TeamsUiState {
        val teams = supabaseClient
            .from("equipa")
            .select()
            .decodeList<TeamsTeamRow>()

        val users = supabaseClient
            .from("utilizador")
            .select()
            .decodeList<TeamsUserRow>()
            .associateBy { it.id }

        val allRegistrations = supabaseClient
            .from("inscricao")
            .select()
            .decodeList<TeamsRegistrationRow>()

        val teamMemberRegistrations = allRegistrations.filter { registration ->
            registration.eventId == null
        }

        val pendingInvites = supabaseClient
            .from("convite")
            .select {
                filter {
                    eq("convidado_user_id", currentUser.id)
                    eq("tipo", "pedido_adesao")
                    eq("estado", "pendente")
                }
            }
            .decodeList<TeamsInviteRow>()

        val myTeamIds = teamMemberRegistrations
            .filter { registration ->
                registration.userId == currentUser.id &&
                        registration.teamId != null
            }
            .mapNotNull { registration ->
                registration.teamId
            }
            .toSet() + teams
            .filter { team ->
                team.ownerId == currentUser.id
            }
            .map { team ->
                team.id
            }

        return TeamsUiState(
            myTeams = teams
                .filter { team ->
                    team.id in myTeamIds
                }
                .map { team ->
                    team.toTeamListItem(
                        registrations = teamMemberRegistrations,
                        users = users,
                        currentUserId = currentUser.id
                    )
                },
            discoverTeams = teams
                .filter { team ->
                    !team.isPrivate &&
                            team.id !in myTeamIds
                }
                .map { team ->
                    team.toTeamListItem(
                        registrations = teamMemberRegistrations,
                        users = users,
                        currentUserId = currentUser.id
                    )
                },
            pendingJoinRequests = pendingInvites
                .mapNotNull { invite ->
                    invite.teamId?.toString()
                }
                .toSet()
        )
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
        val teamRegistrations = registrations.filter { registration ->
            registration.teamId == id &&
                    registration.eventId == null
        }

        val currentUserRegistration = teamRegistrations.find { registration ->
            registration.userId == currentUserId
        }

        val isCaptain = ownerId == currentUserId ||
                currentUserRegistration?.role == "capitao" ||
                currentUserRegistration?.isCaptain == true

        val sport = SportType.entries.find { sportType ->
            sportType.value == (modalidadeId?.minus(1) ?: 0)
        } ?: SportType.SOCCER

        return TeamListItem(
            id = id.toString(),
            name = name,
            membersCount = teamRegistrations.size,
            sportType = sport,
            inviteCode = inviteCode,
            logoUrl = logoUrl,
            isCaptain = isCaptain,
            roster = teamRegistrations.mapNotNull { registration ->
                val userId = registration.userId ?: return@mapNotNull null
                val user = users[userId] ?: return@mapNotNull null

                TeamRosterMember(
                    id = user.id.toString(),
                    name = user.name,
                    role = if (
                        registration.isCaptain == true ||
                        registration.role == "capitao" ||
                        ownerId == user.id
                    ) {
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
            val currentUser = getCurrentUserRow()
                ?: throw Exception("Utilizador não encontrado")

            val invite = InviteInsert(
                teamId = teamId,
                invitedUserId = currentUser.id,
                tipo = "pedido_adesao",
                estado = "pendente"
            )

            val insertResult = supabaseClient
                .from("convite")
                .insert(invite) {
                    select()
                }

            val createdInvite = insertResult.decodeSingle<TeamsInviteRow>()

            val team = supabaseClient
                .from("equipa")
                .select {
                    filter {
                        eq("id", teamId)
                    }
                }
                .decodeSingle<TeamsTeamRow>()

            if (team.ownerId != null) {
                val notification = NotificationInsert(
                    userId = team.ownerId,
                    title = "Novo pedido de adesão",
                    description = "${currentUser.name} pediu para se juntar à equipa ${team.name}.",
                    tipo = "equipa",
                    referenceId = createdInvite.id,
                    referenceType = "convite"
                )

                supabaseClient
                    .from("notificacao")
                    .insert(notification)
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun requestToJoinTeamByInviteCode(inviteCode: String): Result<Unit> {
        return try {
            val normalizedInviteCode = inviteCode
                .trim()
                .uppercase()

            if (normalizedInviteCode.isBlank()) {
                throw Exception("Introduz um código de convite.")
            }

            val currentUser = getCurrentUserRow()
                ?: throw Exception("Utilizador não encontrado.")

            val matchingTeams = supabaseClient
                .from("equipa")
                .select {
                    filter {
                        eq("codigo_convite", normalizedInviteCode)
                    }
                }
                .decodeList<TeamsTeamRow>()

            val team = matchingTeams.firstOrNull()
                ?: throw Exception("Código de convite inválido.")

            if (team.ownerId == currentUser.id) {
                throw Exception("Não podes pedir para entrar na tua própria equipa.")
            }

            val existingRegistrations = supabaseClient
                .from("inscricao")
                .select {
                    filter {
                        eq("equipa_id", team.id)
                        eq("user_id", currentUser.id)
                    }
                }
                .decodeList<TeamsRegistrationRow>()
                .filter { registration ->
                    registration.eventId == null
                }

            if (existingRegistrations.isNotEmpty()) {
                throw Exception("Já pertences a esta equipa.")
            }

            val pendingRequests = supabaseClient
                .from("convite")
                .select {
                    filter {
                        eq("equipa_id", team.id)
                        eq("convidado_user_id", currentUser.id)
                        eq("tipo", "pedido_adesao")
                        eq("estado", "pendente")
                    }
                }
                .decodeList<TeamsInviteRow>()

            if (pendingRequests.isNotEmpty()) {
                throw Exception("Já tens um pedido pendente para esta equipa.")
            }

            val insertResult = supabaseClient
                .from("convite")
                .insert(
                    InviteInsert(
                        teamId = team.id,
                        invitedUserId = currentUser.id,
                        tipo = "pedido_adesao",
                        estado = "pendente"
                    )
                ) {
                    select()
                }

            val createdInvite = insertResult.decodeSingle<TeamsInviteRow>()

            if (team.ownerId != null) {
                supabaseClient
                    .from("notificacao")
                    .insert(
                        NotificationInsert(
                            userId = team.ownerId,
                            title = "Novo pedido de adesão",
                            description = "${currentUser.name} pediu para se juntar à equipa ${team.name}.",
                            tipo = "equipa",
                            referenceId = createdInvite.id,
                            referenceType = "convite"
                        )
                    )
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun promoteMemberToCaptain(
        teamId: Int,
        memberUserId: Int
    ): Result<Unit> {
        return try {
            val currentUser = getCurrentUserRow()
                ?: throw Exception("Utilizador não encontrado")

            val team = supabaseClient
                .from("equipa")
                .select {
                    filter {
                        eq("id", teamId)
                    }
                }
                .decodeSingle<TeamsTeamRow>()

            val registrations = supabaseClient
                .from("inscricao")
                .select {
                    filter {
                        eq("equipa_id", teamId)
                    }
                }
                .decodeList<TeamsRegistrationRow>()

            val teamMemberRegistrations = registrations.filter { registration ->
                registration.eventId == null
            }

            val currentUserRegistration = teamMemberRegistrations.find { registration ->
                registration.userId == currentUser.id
            }

            val currentUserIsCaptain = team.ownerId == currentUser.id ||
                    currentUserRegistration?.role == "capitao" ||
                    currentUserRegistration?.isCaptain == true

            if (!currentUserIsCaptain) {
                throw Exception("Apenas o capitão pode promover membros.")
            }

            val promotedMemberRegistration = teamMemberRegistrations.find { registration ->
                registration.userId == memberUserId
            }

            if (promotedMemberRegistration == null) {
                throw Exception("O utilizador selecionado não pertence a esta equipa.")
            }

            if (team.ownerId == memberUserId ||
                promotedMemberRegistration.role == "capitao" ||
                promotedMemberRegistration.isCaptain == true
            ) {
                return Result.success(Unit)
            }

            teamMemberRegistrations.forEach { registration ->
                supabaseClient
                    .from("inscricao")
                    .update(
                        TeamRegistrationCaptainFlagUpdate(
                            isCaptain = false
                        )
                    ) {
                        filter {
                            eq("id", registration.id)
                        }
                    }
            }

            teamMemberRegistrations
                .filter { registration ->
                    registration.role == "capitao" ||
                            registration.isCaptain == true
                }
                .forEach { registration ->
                    supabaseClient
                        .from("inscricao")
                        .update(
                            TeamRegistrationRoleUpdate(
                                role = "membro"
                            )
                        ) {
                            filter {
                                eq("id", registration.id)
                            }
                        }
                }

            supabaseClient
                .from("inscricao")
                .update(
                    TeamRegistrationCaptainUpdate(
                        role = "capitao",
                        isCaptain = true
                    )
                ) {
                    filter {
                        eq("id", promotedMemberRegistration.id)
                    }
                }

            supabaseClient
                .from("equipa")
                .update(
                    TeamCaptainUpdate(
                        ownerId = memberUserId
                    )
                ) {
                    filter {
                        eq("id", teamId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun removeMemberFromTeam(
        teamId: Int,
        memberUserId: Int
    ): Result<Unit> {
        return try {
            val currentUser = getCurrentUserRow()
                ?: throw Exception("Utilizador não encontrado")

            val team = supabaseClient
                .from("equipa")
                .select {
                    filter {
                        eq("id", teamId)
                    }
                }
                .decodeSingle<TeamsTeamRow>()

            val registrations = supabaseClient
                .from("inscricao")
                .select {
                    filter {
                        eq("equipa_id", teamId)
                    }
                }
                .decodeList<TeamsRegistrationRow>()

            val teamMemberRegistrations = registrations.filter { registration ->
                registration.eventId == null
            }

            val currentUserRegistration = teamMemberRegistrations.find { registration ->
                registration.userId == currentUser.id
            }

            val currentUserIsCaptain = team.ownerId == currentUser.id ||
                    currentUserRegistration?.role == "capitao" ||
                    currentUserRegistration?.isCaptain == true

            if (!currentUserIsCaptain) {
                throw Exception("Apenas o capitão pode remover membros.")
            }

            val memberRegistration = teamMemberRegistrations.find { registration ->
                registration.userId == memberUserId
            }

            if (memberRegistration == null) {
                throw Exception("O utilizador selecionado não pertence a esta equipa.")
            }

            val memberIsCaptain = team.ownerId == memberUserId ||
                    memberRegistration.role == "capitao" ||
                    memberRegistration.isCaptain == true

            if (memberIsCaptain) {
                throw Exception("Não podes remover o capitão da equipa.")
            }

            supabaseClient
                .from("inscricao")
                .delete {
                    filter {
                        eq("id", memberRegistration.id)
                    }
                }

            supabaseClient
                .from("notificacao")
                .insert(
                    NotificationInsert(
                        userId = memberUserId,
                        title = "Removido da equipa",
                        description = "Foste removido da equipa ${team.name}.",
                        tipo = "equipa",
                        referenceId = teamId,
                        referenceType = "equipa"
                    )
                )

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }
}

@Serializable
private data class TeamCaptainUpdate(
    @SerialName("user_id")
    val ownerId: Int
)

@Serializable
private data class TeamRegistrationCaptainFlagUpdate(
    @SerialName("is_capitao")
    val isCaptain: Boolean
)

@Serializable
private data class TeamRegistrationRoleUpdate(
    val role: String
)

@Serializable
private data class TeamRegistrationCaptainUpdate(
    val role: String,
    @SerialName("is_capitao")
    val isCaptain: Boolean
)
