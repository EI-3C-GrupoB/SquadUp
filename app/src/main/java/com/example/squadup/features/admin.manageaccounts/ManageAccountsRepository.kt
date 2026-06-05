package com.example.squadup.features.admin.manageaccounts

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import com.example.squadup.core.SupabaseClientProvider

class ManageAccountsRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun getUsers(): Result<ManageAccountsUiState> {
        return try {
            val users = supabaseClient.from("utilizador").select().decodeList<ManageAccountsUserRow>()
            val links = supabaseClient
                .from("utilizador_tipoutilizador")
                .select()
                .decodeList<ManageAccountsUserTypeLinkRow>()
            val userTypes = supabaseClient.from("tipo_utilizador").select().decodeList<ManageAccountsUserTypeRow>()

            Result.success(
                ManageAccountsUiState(
                    totalUsers = users.size,
                    users = users.map { user ->
                        ManageAccountItem(
                            id = user.id.toString(),
                            initials = user.name.initials(),
                            name = user.name,
                            email = user.email,
                            role = resolveRole(user, links, userTypes)
                        )
                    }
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun getUser(userId: String): Result<EditAdminUserData> {
        return try {
            val user = supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("id", userId.toIntOrNull() ?: -1)
                    }
                }
                .decodeSingle<ManageAccountsUserRow>()
            val links = supabaseClient
                .from("utilizador_tipoutilizador")
                .select {
                    filter {
                        eq("user_id", user.id)
                    }
                }
                .decodeList<ManageAccountsUserTypeLinkRow>()
            val userTypes = supabaseClient.from("tipo_utilizador").select().decodeList<ManageAccountsUserTypeRow>()

            Result.success(
                EditAdminUserData(
                    id = user.id.toString(),
                    name = user.name,
                    email = user.email,
                    role = resolveRole(user, links, userTypes),
                    isSuspended = user.accountStatus == "suspenso" || user.accountStatus == "banido"
                )
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun createUser(username: String, email: String, role: AccountRole): Result<Unit> {
        return try {
            val createdUser = supabaseClient
                .from("utilizador")
                .insert(
                    ManageAccountsUserInsertRow(
                        name = username,
                        username = username,
                        email = email,
                        isAdmin = role == AccountRole.Admin
                    )
                ) {
                    select()
                }
                .decodeSingle<ManageAccountsCreatedUserRow>()

            replaceUserRole(createdUser.id, role)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun updateUser(userId: String, role: AccountRole, isSuspended: Boolean): Result<Unit> {
        return try {
            val id = userId.toIntOrNull() ?: return Result.success(Unit)
            supabaseClient
                .from("utilizador")
                .update(
                    ManageAccountsUserUpdateRow(
                        isAdmin = role == AccountRole.Admin,
                        accountStatus = if (isSuspended) "suspenso" else "ativo"
                    )
                ) {
                    filter {
                        eq("id", id)
                    }
                }

            replaceUserRole(id, role)
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            supabaseClient
                .from("utilizador")
                .delete {
                    filter {
                        eq("id", userId.toIntOrNull() ?: -1)
                    }
                }
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun replaceUserRole(userId: Int, role: AccountRole) {
        supabaseClient
            .from("utilizador_tipoutilizador")
            .delete {
                filter {
                    eq("user_id", userId)
                }
            }

        if (role == AccountRole.Admin) return

        val userTypes = supabaseClient.from("tipo_utilizador").select().decodeList<ManageAccountsUserTypeRow>()
        val targetType = userTypes.firstOrNull { type ->
            when (role) {
                AccountRole.Organizer -> type.type.equals("organizer", true) ||
                        type.type.equals("organizador", true)
                AccountRole.Player -> type.type.equals("player", true) ||
                        type.type.equals("jogador", true)
                AccountRole.Admin -> false
            }
        } ?: return

        supabaseClient
            .from("utilizador_tipoutilizador")
            .insert(
                ManageAccountsUserTypeInsertRow(
                    userTypeId = targetType.id,
                    userId = userId
                )
            )
    }

    private fun resolveRole(
        user: ManageAccountsUserRow,
        links: List<ManageAccountsUserTypeLinkRow>,
        userTypes: List<ManageAccountsUserTypeRow>
    ): AccountRole {
        if (user.isAdmin == true) return AccountRole.Admin

        val selectedTypeIds = links.filter { it.userId == user.id }.map { it.userTypeId }.toSet()
        val roles = userTypes.filter { it.id in selectedTypeIds }.map { it.type.lowercase() }

        return when {
            roles.any { it == "organizer" || it == "organizador" } -> AccountRole.Organizer
            else -> AccountRole.Player
        }
    }

    private fun String.initials(): String {
        return split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
    }
}

data class EditAdminUserData(
    val id: String,
    val name: String,
    val email: String,
    val role: AccountRole,
    val isSuspended: Boolean
)
