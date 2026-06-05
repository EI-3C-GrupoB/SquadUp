package com.example.squadup.features.events.createevent

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from

class CreateEventRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getUserContext(): Result<CreateEventUserContext> {
        return try {
            val user = getCurrentUserRow()
            val role = user?.let { getUserRole(it.id) }
            val teams = user?.let { getUserTeams(it.id) }.orEmpty()
            val formats = supabaseClient
                .from("formato")
                .select()
                .decodeList<CreateEventFormatRow>()
                .map { it.name }

            Result.success(
                CreateEventUserContext(
                    userRole = role,
                    userTeams = teams,
                    formatOptions = formats.ifEmpty {
                        EventFormat.entries.map { it.name.replace("_", " ") }
                    }
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun createEvent(state: CreateEventUiState): Result<Unit> {
        return try {
            val user = getCurrentUserRow()
            val modalities = supabaseClient
                .from("modalidade")
                .select()
                .decodeList<CreateEventModalityRow>()
            val formats = supabaseClient
                .from("formato")
                .select()
                .decodeList<CreateEventFormatRow>()

            val modalityId = state.selectedSport?.let { sport ->
                modalities.firstOrNull { sport.matchesModality(it.name) }?.id
            }
            val formatId = formats.firstOrNull { it.name.equals(state.format, ignoreCase = true) }?.id
            val entryFee = state.entryFee.replace(",", ".").toDoubleOrNull() ?: 0.0

            supabaseClient
                .from("evento")
                .insert(
                    CreateEventInsertRow(
                        title = state.eventName.trim(),
                        address = state.venue.takeIf { it.isNotBlank() },
                        isPrivate = !state.isPublicEvent,
                        startDate = combineDateTime(state.eventDate, state.startTime),
                        endDate = combineDateTime(state.eventDate, state.endTime),
                        maxTeams = state.maxTeams.takeIf { state.allowTeams },
                        price = entryFee,
                        entryFee = entryFee,
                        eventType = state.eventFormat.toDatabaseType(),
                        rules = state.generalRules.takeIf { it.isNotBlank() },
                        creatorId = user?.id,
                        modalityId = modalityId,
                        formatId = formatId
                    )
                )

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserRow(): CreateEventUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        return runCatching {
            supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<CreateEventUserRow>()
        }.getOrNull()
    }

    private suspend fun getUserRole(userId: Int): UserRole {
        val links = supabaseClient
            .from("utilizador_tipoutilizador")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<CreateEventUserTypeLinkRow>()

        val userTypes = supabaseClient
            .from("tipo_utilizador")
            .select()
            .decodeList<CreateEventUserTypeRow>()

        val selectedTypeIds = links.map { it.userTypeId }.toSet()
        val normalizedRoles = userTypes
            .filter { it.id in selectedTypeIds }
            .map { it.type.lowercase() }

        val isPlayer = normalizedRoles.any { it == "player" || it == "jogador" }
        val isOrganizer = normalizedRoles.any { it == "organizer" || it == "organizador" }

        return when {
            isPlayer && isOrganizer -> UserRole.PLAYER_ORGANIZER
            isOrganizer -> UserRole.ORGANIZER
            else -> UserRole.PLAYER
        }
    }

    private suspend fun getUserTeams(userId: Int): List<NotifyTeamItem> {
        val teams = supabaseClient
            .from("equipa")
            .select()
            .decodeList<CreateEventTeamRow>()
            .filter { it.ownerId == userId }

        if (teams.isEmpty()) return emptyList()

        val memberships = supabaseClient
            .from("inscricao")
            .select()
            .decodeList<CreateEventTeamMemberRow>()
            .groupingBy { it.teamId }
            .eachCount()

        return teams.map { team ->
            NotifyTeamItem(
                id = team.id.toString(),
                name = team.name,
                nMembers = memberships[team.id] ?: 0,
                sportType = SportType.SOCCER
            )
        }
    }

    private fun combineDateTime(date: String, time: String): String? {
        if (date.isBlank()) return null
        val normalizedTime = time.takeIf { it.isNotBlank() } ?: "00:00"
        return "${date.trim()}T${normalizedTime.trim()}:00"
    }

    private fun EventFormat.toDatabaseType(): String {
        return when (this) {
            EventFormat.SINGLE_MATCH -> "jogo_amigavel"
            EventFormat.LEAGUE -> "liga"
            EventFormat.KNOCKOUT,
            EventFormat.GROUP_KNOCKOUT -> "torneio"
            EventFormat.FREE -> "outro"
        }
    }

    private fun SportType.matchesModality(value: String): Boolean {
        val normalized = value.lowercase()
        return when (this) {
            SportType.SOCCER -> normalized in listOf("soccer", "football", "futebol")
            SportType.BASKETBALL -> normalized in listOf("basketball", "basquetebol")
            SportType.PADDLE -> normalized in listOf("paddle", "padel")
            SportType.VOLLEYBALL -> normalized in listOf("volleyball", "voleibol")
            SportType.FUTSAL -> normalized == "futsal"
        }
    }
}
