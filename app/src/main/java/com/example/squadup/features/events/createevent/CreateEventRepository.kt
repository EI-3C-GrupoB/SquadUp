package com.example.squadup.features.events.createevent

import android.content.Context
import android.net.Uri
import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.EventParticipationType
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.UserRole
import com.example.squadup.core.permissions.EventPermissions
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import java.time.LocalDateTime
import java.util.UUID

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

    suspend fun createEvent(
        state: CreateEventUiState,
        context: Context
    ): Result<Unit> {
        return try {
            val user = getCurrentUserRow()
                ?: throw Exception("Utilizador não encontrado.")

            val userRole = UserRole.fromInt(user.accountType)

            if (!EventPermissions.canCreateEvent(userRole)) {
                throw Exception("A tua conta não tem permissão para criar eventos.")
            }

            if (state.eventName.isBlank()) {
                throw Exception("Preenche o nome do evento.")
            }

            if (state.selectedSport == null) {
                throw Exception("Seleciona uma modalidade.")
            }

            if (state.latitude == null || state.longitude == null || state.venue.isBlank()) {
                throw Exception("Seleciona a localização do evento.")
            }

            if (state.eventDate.isBlank() || state.startTime.isBlank()) {
                throw Exception("Seleciona a data e a hora de início.")
            }

            if (!state.allowTeams && !state.allowFreeAgents) {
                throw Exception("Seleciona pelo menos um tipo de participação.")
            }

            val entryFee = state.entryFee
                .replace(",", ".")
                .toDoubleOrNull()
                ?: 0.0

            if (entryFee < 0) {
                throw Exception("A taxa de inscrição não pode ser negativa.")
            }

            val startDateTime = combineDateTime(
                date = state.eventDate,
                time = state.startTime
            )

            val endDateTime = combineDateTime(
                date = state.eventDate,
                time = state.endTime
            )

            if (startDateTime != null && endDateTime != null) {
                val start = LocalDateTime.parse(startDateTime)
                val end = LocalDateTime.parse(endDateTime)
                if (!end.isAfter(start)) {
                    throw IllegalArgumentException("A data/hora de fim tem de ser depois da data/hora de início.")
                }
            }

            val registrationStartDateTime = combineOptionalDateTime(
                date = state.registrationStartDate,
                time = state.registrationStartTime
            )

            val registrationEndDateTime = combineOptionalDateTime(
                date = state.registrationEndDate,
                time = state.registrationEndTime
            )

            validateRegistrationPeriod(
                registrationStartDateTime = registrationStartDateTime,
                registrationEndDateTime = registrationEndDateTime,
                eventStartDateTime = startDateTime
            )

            if (state.latitude != null && state.longitude != null && startDateTime != null && endDateTime != null) {
                checkLocationConflict(
                    latitude = state.latitude,
                    longitude = state.longitude,
                    startDateTime = startDateTime,
                    endDateTime = endDateTime
                )
            }

            val modalities = supabaseClient
                .from("modalidade")
                .select()
                .decodeList<CreateEventModalityRow>()

            val formats = supabaseClient
                .from("formato")
                .select()
                .decodeList<CreateEventFormatRow>()

            val modalityId = state.selectedSport.let { sport ->
                modalities.firstOrNull { modality ->
                    sport.matchesModality(modality.name)
                }?.id
            } ?: throw Exception("Modalidade não encontrada na base de dados.")

            val tipoEvento = state.eventFormat.toDatabaseType()
            val formatId = formats.firstOrNull { format ->
                format.name.equals(state.format, ignoreCase = true)
            }?.id ?: formats.firstOrNull { format ->
                // Fallback: match by likely Portuguese format name patterns
                val n = format.name.lowercase()
                when (tipoEvento) {
                    "jogo_amigavel" -> "único" in n || "amigável" in n || "single" in n
                    "liga" -> "liga" in n && "eliminat" !in n
                    "torneio" -> "eliminat" in n || "torneio" in n
                    "outro" -> "livre" in n || "free" in n
                    else -> false
                }
            }?.id

            val coverImageUrl = state.coverImageUri?.let { uri ->
                uploadEventCoverImage(
                    uri = uri,
                    context = context,
                    userId = user.id
                )
            }

            val participationType = resolveParticipationType(state)

            val maxTeamsValue = when (participationType) {
                EventParticipationType.TEAM,
                EventParticipationType.INDIVIDUAL_AND_TEAM -> state.maxTeams

                else -> null
            }

            val participationLimitValue = when (participationType) {
                EventParticipationType.INDIVIDUAL,
                EventParticipationType.INDIVIDUAL_AND_TEAM -> state.maxTeams

                else -> null
            }

            supabaseClient
                .from("evento")
                .insert(
                    CreateEventInsertRow(
                        title = state.eventName.trim(),
                        description = state.description.takeIf { it.isNotBlank() },
                        imageUrl = coverImageUrl,
                        address = state.venue.trim(),
                        latitude = state.latitude,
                        longitude = state.longitude,
                        isPrivate = !state.isPublicEvent,
                        codigoAcesso = if (!state.isPublicEvent) generateAccessCode() else null,
                        recorrente = if (state.isRecurring) true else null,
                        tipoRecorrencia = if (state.isRecurring) state.recurrenceType.name.lowercase() else null,
                        startDate = startDateTime,
                        endDate = endDateTime,
                        registrationStartDate = registrationStartDateTime,
                        registrationEndDate = registrationEndDateTime,
                        maxTeams = maxTeamsValue,
                        participationLimit = participationLimitValue,
                        price = entryFee,
                        entryFee = entryFee,
                        eventStatus = "publicado",
                        participationType = participationType.dbValue,
                        rules = state.generalRules.takeIf { it.isNotBlank() },
                        creatorId = user.id,
                        modalityId = modalityId,
                        formatId = formatId,
                        eventType = tipoEvento
                    )
                )

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun checkLocationConflict(
        latitude: Double,
        longitude: Double,
        startDateTime: String,
        endDateTime: String
    ) {
        val delta = 0.005 // ~500 m

        val conflicts = runCatching {
            supabaseClient
                .from("evento")
                .select {
                    filter {
                        gte("latitude", latitude - delta)
                        lte("latitude", latitude + delta)
                        gte("longitude", longitude - delta)
                        lte("longitude", longitude + delta)
                        neq("estado_evento", "cancelado")
                        neq("estado_evento", "terminado")
                        lt("data_inicio", endDateTime)
                        gt("data_fim", startDateTime)
                    }
                }
                .decodeList<CreateEventConflictRow>()
        }.getOrDefault(emptyList())

        if (conflicts.isNotEmpty()) {
            throw Exception("Já existe um evento neste local nesse horário.")
        }
    }

    private fun resolveParticipationType(state: CreateEventUiState): EventParticipationType {
        return when {
            state.allowTeams && state.allowFreeAgents -> EventParticipationType.INDIVIDUAL_AND_TEAM
            state.allowTeams -> EventParticipationType.TEAM
            else -> EventParticipationType.INDIVIDUAL
        }
    }

    private fun generateAccessCode(): String {
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        return (1..6).map { chars.random() }.joinToString("")
    }

    private suspend fun uploadEventCoverImage(
        uri: Uri,
        context: Context,
        userId: Int
    ): String {
        val bytes = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        } ?: throw Exception("Não foi possível ler a imagem selecionada.")

        val fileName = "event_${userId}_${UUID.randomUUID()}.jpg"

        supabaseClient.storage
            .from("event-covers")
            .upload(
                path = fileName,
                data = bytes
            )

        return supabaseClient.storage
            .from("event-covers")
            .publicUrl(fileName)
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
        val user = supabaseClient
            .from("utilizador")
            .select(io.github.jan.supabase.postgrest.query.Columns.raw("tipo_conta")) {
                filter {
                    eq("id", userId)
                }
            }
            .decodeSingle<CreateEventUserRow>()

        return UserRole.fromInt(user.accountType)
    }

    private suspend fun getUserTeams(userId: Int): List<NotifyTeamItem> {
        val teams = supabaseClient
            .from("equipa")
            .select()
            .decodeList<CreateEventTeamRow>()
            .filter { team ->
                team.ownerId == userId
            }

        if (teams.isEmpty()) return emptyList()

        val memberships = supabaseClient
            .from("inscricao")
            .select()
            .decodeList<CreateEventTeamMemberRow>()
            .filter { registration ->
                registration.eventId == null
            }
            .groupingBy { registration ->
                registration.teamId
            }
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

    private fun combineDateTime(
        date: String,
        time: String
    ): String? {
        if (date.isBlank() || time.isBlank()) return null
        return "${date}T${time}:00"
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

    private fun combineOptionalDateTime(
        date: String,
        time: String
    ): String? {
        if (date.isBlank() && time.isBlank()) return null

        if (date.isBlank() || time.isBlank()) {
            throw IllegalArgumentException("Preenche a data e a hora do período de inscrições.")
        }

        return "${date}T${time}:00"
    }

    private fun validateRegistrationPeriod(
        registrationStartDateTime: String?,
        registrationEndDateTime: String?,
        eventStartDateTime: String?
    ) {
        if (registrationStartDateTime == null && registrationEndDateTime == null) return

        if (registrationStartDateTime == null || registrationEndDateTime == null) {
            throw IllegalArgumentException("Define o início e o fim das inscrições.")
        }

        val registrationStart = LocalDateTime.parse(registrationStartDateTime)
        val registrationEnd = LocalDateTime.parse(registrationEndDateTime)
        val eventStart = eventStartDateTime?.let {
            LocalDateTime.parse(it)
        }

        if (!registrationStart.isBefore(registrationEnd)) {
            throw IllegalArgumentException("O início das inscrições tem de ser antes do fim.")
        }

        if (eventStart != null && registrationEnd.isAfter(eventStart)) {
            throw IllegalArgumentException("As inscrições têm de terminar antes do início do evento.")
        }
    }
}
