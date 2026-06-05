package com.example.squadup.features.events.calendar

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalendarRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getCalendar(selectedDay: Int = LocalDate.now().dayOfMonth): Result<CalendarUiState> {
        return try {
            val games = supabaseClient
                .from("jogo")
                .select()
                .decodeList<CalendarGameRow>()
                .sortedBy { it.scheduledAt.orEmpty() }
            val events = supabaseClient
                .from("evento")
                .select()
                .decodeList<CalendarEventRow>()
                .associateBy { it.id }
            val gameTeams = supabaseClient
                .from("jogo_equipa")
                .select()
                .decodeList<CalendarGameTeamRow>()
            val teams = supabaseClient
                .from("equipa")
                .select()
                .decodeList<CalendarTeamRow>()
                .associateBy { it.id }

            val datedGames = games.mapNotNull { game ->
                val scheduledAt = game.scheduledAt.toLocalDateTimeOrNull() ?: return@mapNotNull null
                game to scheduledAt
            }
            val selectedDate = datedGames
                .firstOrNull { it.second.dayOfMonth == selectedDay }
                ?.second
                ?.toLocalDate()
                ?: LocalDate.now()
            val highlighted = datedGames.firstOrNull()

            Result.success(
                CalendarUiState(
                    monthTitle = selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)),
                    matchesScheduled = datedGames.size,
                    selectedDay = selectedDay,
                    eventDays = datedGames.map { it.second.dayOfMonth }.toSet(),
                    highlightedMatch = highlighted?.let { (game, dateTime) ->
                        game.toCalendarMatch(dateTime, gameTeams, teams)
                    } ?: CalendarMatchItem(),
                    dailySchedule = datedGames
                        .filter { it.second.dayOfMonth == selectedDay }
                        .map { (game, dateTime) ->
                            DailyScheduleItem(
                                time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                                title = game.eventId?.let { events[it]?.title }.orEmpty(),
                                location = game.address.orEmpty()
                            )
                        },
                    nextAwayGame = datedGames
                        .firstOrNull { it.second.isAfter(LocalDateTime.now()) }
                        ?.let { (game, dateTime) ->
                            AwayGameItem(
                                city = game.address.orEmpty(),
                                date = dateTime.format(DateTimeFormatter.ofPattern("MMM dd • HH:mm", Locale.US))
                            )
                        } ?: AwayGameItem(city = "", date = "")
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun CalendarGameRow.toCalendarMatch(
        dateTime: LocalDateTime,
        gameTeams: List<CalendarGameTeamRow>,
        teams: Map<Int, CalendarTeamRow>
    ): CalendarMatchItem {
        val linkedTeams = gameTeams.filter { it.gameId == id }
        val homeTeam = linkedTeams.getOrNull(0)?.teamId?.let { teams[it]?.name }.orEmpty()
        val awayTeam = linkedTeams.getOrNull(1)?.teamId?.let { teams[it]?.name }.orEmpty()

        return CalendarMatchItem(
            label = "UPCOMING: ${dateTime.format(DateTimeFormatter.ofPattern("MMM dd", Locale.US)).uppercase()}",
            title = listOf(homeTeam, awayTeam).filter { it.isNotBlank() }.joinToString(" vs.\n"),
            homeTeam = homeTeam,
            awayTeam = awayTeam
        )
    }

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }
}
