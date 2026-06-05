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
    suspend fun getHome(): Result<HomeUiState> {
        return try {
            val currentUser = getCurrentUserRow()
            val events = supabaseClient.from("evento").select().decodeList<HomeEventRow>()
            val modalities = supabaseClient.from("modalidade").select().decodeList<HomeModalityRow>().associateBy { it.id }
            val teams = supabaseClient.from("equipa").select().decodeList<HomeTeamRow>()
            val registrations = supabaseClient.from("inscricao").select().decodeList<HomeRegistrationRow>()
            val games = supabaseClient.from("jogo").select().decodeList<HomeGameRow>()
            val gameTeams = supabaseClient.from("jogo_equipa").select().decodeList<HomeGameTeamRow>()

            Result.success(
                HomeUiState(
                    isLoggedIn = currentUser != null,
                    displayName = currentUser?.name.orEmpty(),
                    role = currentUser?.let { getUserRole(it.id) },
                    currentMatch = buildCurrentMatch(games, events, teams, gameTeams, modalities),
                    nearbyEvents = events
                        .filter { it.status != "cancelado" }
                        .sortedBy { it.startDate.orEmpty() }
                        .take(5)
                        .map { it.toHomeEvent(modalities) },
                    eventsCreated = currentUser?.let { user -> events.count { it.creatorId == user.id } } ?: 0,
                    activeTeams = currentUser?.let { user ->
                        teams.count { it.ownerId == user.id } +
                                registrations.filter { it.userId == user.id && it.teamId != null }.mapNotNull { it.teamId }.distinct().size
                    } ?: 0,
                    totalRevenue = currentUser?.let { user ->
                        events
                            .filter { it.creatorId == user.id }
                            .sumOf { event ->
                                val price = event.price ?: event.entryFee ?: 0.0
                                val registered = registrations.count { it.eventId == event.id }
                                price * registered
                            }
                    } ?: 0.0,
                    myEvents = currentUser?.let { user ->
                        events.filter { it.creatorId == user.id }.map { event ->
                            event.toOrganizerEvent(
                                registrations = registrations.filter { it.eventId == event.id },
                                modalities = modalities
                            )
                        }
                    }.orEmpty(),
                    teams = currentUser?.let { user ->
                        val userTeamIds = registrations
                            .filter { it.userId == user.id && it.teamId != null }
                            .mapNotNull { it.teamId }
                            .toSet() + teams.filter { it.ownerId == user.id }.map { it.id }

                        teams.filter { it.id in userTeamIds }.map { team ->
                            HomeTeam(
                                id = team.id.toString(),
                                name = team.name,
                                nMembers = registrations.count { it.teamId == team.id },
                                sportType = SportType.SOCCER,
                                badge = if (team.ownerId == user.id) "LEADER" else null
                            )
                        }
                    }.orEmpty()
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun getCurrentUserRow(): HomeUserRow? {
        val authUserId = supabaseClient.auth.currentUserOrNull()?.id ?: return null
        return runCatching {
            supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("auth_user_id", authUserId)
                    }
                }
                .decodeSingle<HomeUserRow>()
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
            .decodeList<HomeUserTypeLinkRow>()
        val userTypes = supabaseClient.from("tipo_utilizador").select().decodeList<HomeUserTypeRow>()
        val selectedTypeIds = links.map { it.userTypeId }.toSet()
        val roles = userTypes.filter { it.id in selectedTypeIds }.map { it.type.lowercase() }
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
            .filter { it.status != "cancelado" }
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
