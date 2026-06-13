package com.example.squadup.features.events.calendar

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
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

            // Map eventId → ticketId for the current logged-in user
            val userTicketsByEvent: Map<Int, Int> = runCatching {
                val authId = supabaseClient.auth.currentUserOrNull()?.id ?: return@runCatching emptyMap()
                val currentUser = supabaseClient.from("utilizador")
                    .select { filter { eq("auth_user_id", authId) } }
                    .decodeSingle<CalendarUserRow>()
                supabaseClient.from("bilhete")
                    .select { filter { eq("user_id", currentUser.id) } }
                    .decodeList<CalendarBilheteRow>()
                    .associate { it.eventId to it.id }
            }.getOrDefault(emptyMap())

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

            // Jogos do dia, com os jogos ainda por jogar primeiro (terminados/cancelados no fim)
            val gamesOnSelectedDay = gamesThisMonth
                .filter { it.second.dayOfMonth == safeDay }
                .sortedWith(compareBy({ it.first.isFinished() }, { it.second }))

            // O card "PRÓXIMO" deve apontar para um jogo ativo, nunca para um já terminado
            val now = LocalDateTime.now()
            val highlighted = if (gamesOnSelectedDay.isNotEmpty()) {
                gamesOnSelectedDay.firstOrNull { !it.first.isFinished() } ?: gamesOnSelectedDay.first()
            } else {
                datedGames.firstOrNull { it.second.isAfter(now) && !it.first.isFinished() }
                    ?: datedGames.firstOrNull { it.second.isAfter(now) }
                    ?: datedGames.firstOrNull()
            }

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
                        game.toCalendarMatch(dateTime, gameTeams, teams, userTicketsByEvent)
                    },
                    dailySchedule = gamesOnSelectedDay.map { (game, dateTime) ->
                        val linkedTeams = gameTeams.filter { it.gameId == game.id }.sortedBy { it.teamId }
                        val home = linkedTeams.getOrNull(0)?.teamId?.let { teams[it]?.name }.orEmpty()
                        val away = linkedTeams.getOrNull(1)?.teamId?.let { teams[it]?.name }.orEmpty()
                        DailyScheduleItem(
                            gameId = game.id,
                            eventId = game.eventId ?: 0,
                            ticketId = game.eventId?.let { userTicketsByEvent[it] } ?: 0,
                            time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            homeTeam = home,
                            awayTeam = away,
                            eventName = game.eventId?.let { events[it]?.title }.orEmpty(),
                            location = game.address.orEmpty(),
                            isFinished = game.isFinished()
                        )
                    }
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private fun buildCalendarCells(yearMonth: YearMonth): List<Int?> {
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
        teams: Map<Int, CalendarTeamRow>,
        ticketsByEvent: Map<Int, Int> = emptyMap()
    ): CalendarMatchItem {
        val linkedTeams = gameTeams.filter { it.gameId == id }.sortedBy { it.teamId }
        val homeTeam = linkedTeams.getOrNull(0)?.teamId?.let { teams[it]?.name }.orEmpty()
        val awayTeam = linkedTeams.getOrNull(1)?.teamId?.let { teams[it]?.name }.orEmpty()
        val dateLabel = dateTime.format(DateTimeFormatter.ofPattern("dd MMM", Locale("pt", "PT"))).uppercase()
        val finished = isFinished()
        return CalendarMatchItem(
            label = if (finished) "TERMINADO: $dateLabel" else "PRÓXIMO: $dateLabel",
            homeTeam = homeTeam,
            awayTeam = awayTeam,
            time = dateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
            location = address.orEmpty(),
            gameId = id,
            eventId = eventId ?: 0,
            ticketId = eventId?.let { ticketsByEvent[it] } ?: 0,
            isFinished = finished
        )
    }

    private fun CalendarGameRow.isFinished(): Boolean =
        status == "terminado" || status == "cancelado"

    private fun String?.toLocalDateTimeOrNull(): LocalDateTime? {
        if (isNullOrBlank()) return null
        return runCatching { LocalDateTime.parse(replace(" ", "T").take(19)) }.getOrNull()
    }
}
