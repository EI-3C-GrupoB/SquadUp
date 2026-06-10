package com.example.squadup.features.admin.manageaccounts

import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class ManageAccountsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getUsers(): Result<List<ManageAccountItem>> {
        return try {
            val rows = supabaseClient
                .from("utilizador")
                .select()
                .decodeList<ManageUserRow>()
            Result.success(rows.map { it.toManageAccountItem() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getUsersRealtime(): Flow<List<ManageAccountItem>> = flow {
        // Emitir estado inicial
        val initialUsers = getUsers().getOrDefault(emptyList())
        emit(initialUsers)

        val channel = supabaseClient.channel("manage_accounts_realtime")
        
        val changes = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
            table = "utilizador"
        }

        channel.subscribe()

        emitAll(
            changes.map {
                // Sempre que houver uma mudança, recarregamos a lista completa
                // para garantir consistência (especialmente em deletes ou updates complexos)
                getUsers().getOrDefault(emptyList())
            }
        )
    }
}

@Serializable
internal data class ManageUserRow(
    val id: Int,
    @SerialName("nome") val nome: String? = null,
    val username: String? = null,
    @SerialName("tipo_conta") val accountType: Int? = null,
    @SerialName("is_admin") val isAdmin: Boolean? = false
)

internal fun ManageUserRow.toManageAccountItem(): ManageAccountItem {
    val role = when {
        isAdmin == true -> AccountRole.Admin
        accountType == 2 || accountType == 3 -> AccountRole.Organizer
        else -> AccountRole.Player
    }
    val displayName = nome.orEmpty().ifBlank { username.orEmpty() }.ifBlank { "Utilizador #$id" }
    val initials = displayName.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }.ifBlank { "?" }
    return ManageAccountItem(
        id = id.toString(),
        initials = initials,
        name = displayName,
        email = username.orEmpty(),
        role = role
    )
}