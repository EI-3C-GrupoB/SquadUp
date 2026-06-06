package com.example.squadup.features.home

import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.UserRole
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getHome(passedUserId: Int? = null, passedDisplayName: String = ""): Result<HomeUiState> {
        return try {
            // isLoggedIn determinado pela sessão Supabase — independente de qualquer query à BD
            val authSession = supabaseClient.auth.currentUserOrNull()
            val isLoggedIn = authSession != null

            // userId e displayName: do AppViewModel (passados) ou query à BD como fallback
            val userId: Int?
            val displayName: String

            if (passedUserId != null) {
                userId = passedUserId
                displayName = passedDisplayName
            } else if (authSession != null) {
                val row = runCatching {
                    supabaseClient.from("utilizador").select {
                        filter { eq("auth_user_id", authSession.id) }
                    }.decodeSingle<HomeUserRow>()
                }.getOrNull()
                userId = row?.id
                displayName = row?.name.orEmpty()
            } else {
                userId = null
                displayName = ""
            }

            val role = userId?.let { getUserRole(it) }

            val events = supabaseClient.from("evento").select().decodeList<HomeEventRow>()
            val modalities = supabaseClient.from("modalidade").select()
                .decodeList<HomeModalityRow>().associateBy { it.id }

            // Buscar tabelas de suporte uma única vez, filtrar em Kotlin
            val allRegistrations = supabaseClient.from("inscricao").select()
                .decodeList<HomeRegistrationRow>()
            val allTeams = supabaseClient.from("equipa").select()
                .decodeList<HomeTeamRow>()

            val games = supabaseClient.from("jogo").select {
                filter { neq("estado_jogo", "cancelado") }
            }.decodeList<HomeGameRow>()

            val gameTeams = if (games.isNotEmpty()) {
                supabaseClient.from("jogo_equipa").select().decodeList<HomeGameTeamRow>()
            } else emptyList()

            // Dados específicos do utilizador calculados em memória
            val userRegistrations = allRegistrations.filter { it.userId == userId }
            val userTeamIds = userRegistrations.mapNotNull { it.teamId }.toSet()
            val ownedTeams = allTeams.filter { it.ownerId == userId }
            val allRelevantTeamIds = userTeamIds + ownedTeams.map { it.id }

            Result.success(
                HomeUiState(
                    isLoggedIn = isLoggedIn,
                    displayName = displayName,
                    role = role,
                    currentMatch = buildCurrentMatch(games, events, ownedTeams, gameTeams, modalities),
                    nearbyEvents = events
                        .filter { it.status != "cancelado" }
                        .sortedBy { it.startDate.orEmpty() }
                        .take(5)
                        .map { it.toHomeEvent(modalities) },
                    eventsCreated = userId?.let { id -> events.count { it.creatorId == id } } ?: 0,
                    activeTeams = allRelevantTeamIds.size,
                    totalRevenue = userId?.let { id ->
                        events.filter { it.creatorId == id }.sumOf { event ->
                            val price = event.price ?: event.entryFee ?: 0.0
                            val registered = allRegistrations.count { it.eventId == event.id }
                            price * registered
                        }
                    } ?: 0.0,
                    myEvents = userId?.let { id ->
                        events.filter { it.creatorId == id }.map { event ->
                            event.toOrganizerEvent(
                                registrations = allRegistrations.filter { it.eventId == event.id },
                                modalities = modalities
                            )
                        }
                    }.orEmpty(),
                    teams = allTeams
                        .filter { it.id in allRelevantTeamIds }
                        .map { team ->
                            HomeTeam(
                                id = team.id.toString(),
                                name = team.name,
                                nMembers = allRegistrations.count { it.teamId == team.id },
                                sportType = SportType.SOCCER,
                                badge = if (ownedTeams.any { it.id == team.id }) "LEADER" else null
                            )
                        }
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getUserRole(userId: Int): UserRole {
        val links = supabaseClient
            .from("utilizador_tipoutilizador")
            .select { filter { eq("user_id", userId) } }
            .decodeList<HomeUserTypeLinkRow>()

        if (links.isEmpty()) return UserRole.PLAYER

        val userTypes = supabaseClient
            .from("tipo_utilizador")
            .select()
            .decodeList<HomeUserTypeRow>()

        val selectedTypeIds = links.map { it.userTypeId }.toSet()
        val roles = userTypes
            .filter { it.id in selectedTypeIds }
            .map { it.type.lowercase() }

        val isPlayer = roles.any { it == "player" || it == "jogador" }
        val isOrganizer = roles.any { it == "organizer" || it == "organizador" }

        return when {
            isPlayer && isOrganizer -> UserRole.PLAYER_ORGANIZER
            isOrganizer -> UserRole.ORGANIZER
            else -> UserRole.PLAYER
        }
    }

    private fun buildCurrentMatch(
        games: List<HomeGameRow>,
        events: List<HomeEventRow>,
        teams: List<HomeTeamRow>,
        gameTeams: List<HomeGameTeamRow>,
        modalities: Map<Int, HomeModalityRow>
    ): HomeMatch? {
        val game = games
            .sortedBy { it.scheduledAt.orEmpty() }
            .firstOrNull() ?: return null

        val event = game.eventId?.let { id -> events.firstOrNull { it.id == id } }
        val linkedTeams = gameTeams.filter { it.gameId == game.id }
        val title = linkedTeams
            .mapNotNull { link -> teams.firstOrNull { it.id == link.teamId }?.name }
            .take(2)
            .joinToString(" vs. ")
            .ifBlank { event?.title.orEmpty() }

        return HomeMatch(
            id = game.id.toString(),
            title = title,
            date = game.scheduledAt.toShortDateTime(),
            location = game.address.orEmpty(),
            sportType = sportTypeFrom(event?.modalityId?.let { modalities[it]?.name })
        )
    }

    private fun HomeEventRow.toHomeEvent(modalities: Map<Int, HomeModalityRow>): HomeEvent {
        return HomeEvent(
            id = id.toString(),
            title = title,
            location = address.orEmpty(),
            distance = "",
            intensity = 0f,
            sportType = sportTypeFrom(modalityId?.let { modalities[it]?.name }),
            status = status.toEventStatus()
        )
    }

    private fun HomeEventRow.toOrganizerEvent(
        registrations: List<HomeRegistrationRow>,
        modalities: Map<Int, HomeModalityRow>
    ): HomeOrganizerEvent {
        return HomeOrganizerEvent(
            id = id.toString(),
            title = title,
            price = priceLabel(price ?: entryFee),
            nTeams = registrations.mapNotNull { it.teamId }.distinct().size,
            dateLeft = startDate.toShortDateTime(),
            registeredCount = registrations.size,
            status = status.toEventStatus(),
            sportType = sportTypeFrom(modalityId?.let { modalities[it]?.name })
        )
    }

    private fun String?.toEventStatus(): EventStatus {
        return when (this) {
            "rascunho" -> EventStatus.DRAFT
            "publicado" -> EventStatus.REGISTRATION_OPEN
            "a_decorrer" -> EventStatus.ONGOING
            "terminado" -> EventStatus.FINISHED
            "cancelado" -> EventStatus.CANCELLED
            else -> EventStatus.DRAFT
        }
    }

    private fun String?.toShortDateTime(): String {
        val dateTime = toLocalDateTimeOrNull() ?: return ""
        return dateTime.format(DateTimeFormatter.ofPattern("MMM dd • HH:mm", Locale.US))
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }

    private fun priceLabel(value: Double?): String {
        return if (value == null || value == 0.0) "Free" else "€${value.toIntIfWhole()}"
    }

    private fun Double.toIntIfWhole(): String {
        return if (this % 1.0 == 0.0) toInt().toString() else "%.2f".format(Locale.US, this)
    }

    private fun sportTypeFrom(name: String?): SportType {
        val normalized = name.orEmpty().lowercase()
        return when {
            normalized in listOf("soccer", "football", "futebol") -> SportType.SOCCER
            normalized in listOf("basketball", "basquetebol") -> SportType.BASKETBALL
            normalized in listOf("paddle", "padel") -> SportType.PADDLE
            normalized in listOf("volleyball", "voleibol") -> SportType.VOLLEYBALL
            normalized == "futsal" -> SportType.FUTSAL
            else -> SportType.SOCCER
        }
    }
}
