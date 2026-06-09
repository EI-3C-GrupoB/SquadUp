package com.example.squadup.features.events.calendar

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class CalendarRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getCalendar(
        year: Int = LocalDate.now().year,
        month: Int = LocalDate.now().monthValue,
        selectedDay: Int = LocalDate.now().dayOfMonth
    ): Result<CalendarUiState> {
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

            val yearMonth = YearMonth.of(year, month)
            val calendarCells = buildCalendarCells(yearMonth)

            val datedGames = games.mapNotNull { game ->
                val scheduledAt = game.scheduledAt.toLocalDateTimeOrNull() ?: return@mapNotNull null
                game to scheduledAt
            }

            val gamesThisMonth = datedGames.filter {
                it.second.year == year && it.second.monthValue == month
            }

            val safeDay = selectedDay.coerceIn(1, yearMonth.lengthOfMonth())

            val highlighted = gamesThisMonth.firstOrNull()
                ?: datedGames.firstOrNull { it.second.isAfter(LocalDateTime.now()) }
                ?: datedGames.firstOrNull()

            Result.success(
                CalendarUiState(
                    monthTitle = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("pt", "PT")))
                        .replaceFirstChar { it.uppercase() },
                    matchesScheduled = gamesThisMonth.size,
                    selectedDay = safeDay,
                    currentYear = year,
                    currentMonth = month,
                    eventDays = gamesThisMonth.map { it.second.dayOfMonth }.toSet(),
                    calendarCells = calendarCells,
                    highlightedMatch = highlighted?.let { (game, dateTime) ->
                        game.toCalendarMatch(dateTime, gameTeams, teams)
                    } ?: CalendarMatchItem(),
                    dailySchedule = gamesThisMonth
                        .filter { it.second.dayOfMonth == safeDay }
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
                                date = dateTime.format(DateTimeFormatter.ofPattern("dd MMM • HH:mm", Locale("pt", "PT")))
                            )
                        } ?: AwayGameItem(city = "", date = "")
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun buildCalendarCells(yearMonth: YearMonth): List<Int?> {
        // dayOfWeek: Monday=1 ... Sunday=7; we want Sunday=0 offset
        val firstDow = yearMonth.atDay(1).dayOfWeek.value % 7
        val daysInMonth = yearMonth.lengthOfMonth()
        return buildList {
            repeat(firstDow) { add(null) }
            for (i in 1..daysInMonth) add(i)
            while (size % 7 != 0) add(null)
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
        val dateLabel = dateTime.format(DateTimeFormatter.ofPattern("dd MMM", Locale("pt", "PT"))).uppercase()
        return CalendarMatchItem(
            label = "PRÓXIMO: $dateLabel",
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
