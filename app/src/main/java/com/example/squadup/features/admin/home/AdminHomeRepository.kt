package com.example.squadup.features.admin.home

import android.util.Log
import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.GameStatus
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class AdminHomeRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {

    suspend fun loadDashboard(): Result<AdminHomeDashboard> {
        return try {
            val users = loadUsers()
            val events = loadEvents()
            val matches = loadMatches()
            val sports = loadSports()

            val activeEvents = events.count { event ->
                event.status.toEventStatus() in activeEventStatuses
            }

            val activeMatches = matches.count { match ->
                match.status.toGameStatus() in activeMatchStatuses
            }

            val activeSportsCount = matches
                .filter { match -> match.status.toGameStatus() in activeMatchStatuses }
                .mapNotNull { match ->
                    events.firstOrNull { event -> event.id == match.eventId }?.sportId
                }
                .toSet()
                .size

            val sportPopularity = buildSportPopularity(
                events = events,
                sports = sports
            )

            Result.success(
                AdminHomeDashboard(
                    totalUsers = users.size,
                    usersGrowthPercent = calculateUsersGrowthPercent(users),
                    activeMatches = activeMatches,
                    activeSportsCount = activeSportsCount,
                    activeEvents = activeEvents,
                    sportPopularity = sportPopularity
                )
            )
        } catch (exception: Exception) {
            Log.e("AdminHomeRepository", "Error loading admin dashboard", exception)
            Result.failure(exception)
        }
    }

    private suspend fun loadUsers(): List<AdminUserCountRow> {
        return supabaseClient
            .from("utilizador")
            .select()
            .decodeList()
    }

    private suspend fun loadEvents(): List<AdminEventRow> {
        return supabaseClient
            .from("evento")
            .select()
            .decodeList()
    }

    private suspend fun loadMatches(): List<AdminMatchRow> {
        return supabaseClient
            .from("jogo")
            .select()
            .decodeList()
    }

    private suspend fun loadSports(): List<AdminSportRow> {
        return supabaseClient
            .from("modalidade")
            .select()
            .decodeList()
    }

    private fun buildSportPopularity(
        events: List<AdminEventRow>,
        sports: List<AdminSportRow>
    ): List<SportPopularityItem> {
        val eventsWithSport = events.mapNotNull { event ->
            event.sportId
        }

        if (eventsWithSport.isEmpty()) {
            return emptyList()
        }

        val totalEvents = eventsWithSport.size.toFloat()

        return eventsWithSport
            .groupingBy { sportId -> sportId }
            .eachCount()
            .mapNotNull { (sportId, count) ->
                val sportName = sports.firstOrNull { sport -> sport.id == sportId }?.name
                    ?: return@mapNotNull null

                SportPopularityItem(
                    sportName = sportName,
                    percentage = ((count / totalEvents) * 100).toInt()
                )
            }
            .sortedByDescending { item -> item.percentage }
            .take(3)
    }

    private fun calculateUsersGrowthPercent(
        users: List<AdminUserCountRow>
    ): Float {
        val currentMonth = YearMonth.now()
        val previousMonth = currentMonth.minusMonths(1)

        val currentMonthUsers = users.count { user ->
            user.createdAt.toYearMonthOrNull() == currentMonth
        }

        val previousMonthUsers = users.count { user ->
            user.createdAt.toYearMonthOrNull() == previousMonth
        }

        if (previousMonthUsers == 0) {
            return if (currentMonthUsers > 0) 100f else 0f
        }

        return ((currentMonthUsers - previousMonthUsers).toFloat() / previousMonthUsers) * 100f
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

    private fun String?.toGameStatus(): GameStatus {
        return when (this) {
            "a_decorrer" -> GameStatus.LIVE
            "intervalo" -> GameStatus.WARM_UP
            "terminado" -> GameStatus.FINISHED
            "cancelado" -> GameStatus.CANCELLED
            else -> GameStatus.SCHEDULED
        }
    }

    private fun String?.toYearMonthOrNull(): YearMonth? {
        if (isNullOrBlank()) {
            return null
        }

        return runCatching {
            val normalized = replace(" ", "T").take(19)
            YearMonth.from(LocalDateTime.parse(normalized))
        }.recoverCatching {
            YearMonth.from(LocalDate.parse(this, DateTimeFormatter.ISO_LOCAL_DATE))
        }.getOrNull()
    }

    private companion object {
        val activeEventStatuses = setOf(
            EventStatus.REGISTRATION_OPEN,
            EventStatus.ONGOING
        )

        val activeMatchStatuses = setOf(
            GameStatus.WARM_UP,
            GameStatus.LIVE
        )
    }
}