package com.example.squadup.features.admin.manageaccounts.createuser

import android.util.Log
import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.features.admin.manageaccounts.AccountRole
import com.example.squadup.features.admin.manageaccounts.toAccountType
import com.example.squadup.features.admin.manageaccounts.toIsAdmin
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class CreateUserRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun createUser(
        name: String,
        username: String,
        email: String,
        password: String,
        role: AccountRole
    ): Result<Unit> {
        return try {
            val request = CreateUserRequest(
                name = name.trim(),
                username = username.trim(),
                email = email.trim(),
                password = password,
                role = role
            )

            validateRequest(request)

            val signUpUser = supabaseClient.auth.signUpWith(Email) {
                this.email = request.email
                this.password = request.password
            }

            val authUserId = signUpUser?.id
                ?: supabaseClient.auth.currentUserOrNull()?.id
                ?: return Result.failure(
                    IllegalStateException("Não foi possível obter o ID do utilizador criado.")
                )

            upsertUserProfile(
                request = request,
                authUserId = authUserId
            )

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("CreateUserRepository", "Error creating user", exception)
            Result.failure(exception)
        }
    }

    private suspend fun upsertUserProfile(
        request: CreateUserRequest,
        authUserId: String
    ) {
        val existingProfile = supabaseClient
            .from("utilizador")
            .select {
                filter {
                    eq("auth_user_id", authUserId)
                }
            }
            .decodeList<CreateUserProfileRow>()
            .firstOrNull()

        if (existingProfile != null) {
            supabaseClient
                .from("utilizador")
                .update({
                    set("nome", request.name)
                    set("username", request.username)
                    set("email", request.email)
                    set("is_admin", request.role.toIsAdmin())
                    set("tipo_conta", request.role.toAccountType())
                    set("estado_conta", "ativo")
                }) {
                    filter {
                        eq("id", existingProfile.id)
                    }
                }
            return
        }

        supabaseClient
            .from("utilizador")
            .insert(
                CreateUserProfileInsert(
                    name = request.name,
                    username = request.username,
                    email = request.email,
                    authUserId = authUserId,
                    isAdmin = request.role.toIsAdmin(),
                    accountType = request.role.toAccountType()
                )
            )
    }

    private fun validateRequest(request: CreateUserRequest) {
        when {
            request.name.isBlank() -> throw IllegalArgumentException("O nome é obrigatório.")
            request.username.isBlank() -> throw IllegalArgumentException("O username é obrigatório.")
            request.email.isBlank() -> throw IllegalArgumentException("O email é obrigatório.")
            "@" !in request.email -> throw IllegalArgumentException("O email não é válido.")
            request.password.length < 6 -> throw IllegalArgumentException("A password deve ter pelo menos 6 caracteres.")
        }
    }
}
