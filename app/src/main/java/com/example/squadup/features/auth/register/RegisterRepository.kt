package com.example.squadup.features.auth.register

import androidx.annotation.StringRes
import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from

class RegisterRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun register(profile: RegisterProfile): Result<Unit> {
        return try {
            val authUser = supabaseClient.auth.signUpWith(Email) {
                email = profile.email
                password = profile.password
            }

            val authUserId = authUser?.id ?: supabaseClient.auth.currentUserOrNull()?.id
            if (authUserId == null) {
                return Result.failure(RegisterException(R.string.register_error_auth_user_missing))
            }

            val createdUser = supabaseClient
                .from("utilizador")
                .insert(
                    UserProfileInsert(
                        authUserId = authUserId,
                        fullName = profile.fullName,
                        username = profile.username,
                        email = profile.email,
                        birthDate = profile.birthDate
                    )
                ) {
                    select()
                }
                .decodeSingle<CreatedUser>()

            addUserTypeIfAvailable(createdUser.id, profile.accountType)
            addUserModalities(createdUser.id, profile.modalityNames)

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(RegisterException(mapRegisterError(exception)))
        }
    }

    suspend fun getModalities(): Result<List<Modality>> {
        return try {
            Result.success(
                supabaseClient
                    .from("modalidade")
                    .select()
                    .decodeList<Modality>()
            )
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    private suspend fun addUserModalities(userId: Int, modalityNames: List<String>) {
        if (modalityNames.isEmpty()) return

        val modalities = supabaseClient
            .from("modalidade")
            .select()
            .decodeList<Modality>()

        val userModalities = modalityNames.mapNotNull { modalityName ->
            modalities
                .firstOrNull { it.name.equals(modalityName, ignoreCase = true) }
                ?.let { UserModalityInsert(userId = userId, modalityId = it.id) }
        }

        if (userModalities.isNotEmpty()) {
            supabaseClient
                .from("utilizador_modalidade")
                .insert(userModalities)
        }
    }

    private suspend fun addUserTypeIfAvailable(userId: Int, accountType: AccountType) {
        val userTypes = supabaseClient
            .from("tipo_utilizador")
            .select()
            .decodeList<UserType>()

        val typeId = userTypes.firstOrNull { userType ->
            accountType.databaseAliases.any { alias ->
                userType.type.equals(alias, ignoreCase = true)
            }
        }?.id ?: return

        supabaseClient
            .from("utilizador_tipoutilizador")
            .insert(UserTypeInsert(userId = userId, userTypeId = typeId))
    }

    @StringRes
    private fun mapRegisterError(exception: Exception): Int {
        val message = exception.message.orEmpty().lowercase()

        return when {
            "user already registered" in message ||
                    "already registered" in message ||
                    "email already" in message ||
                    "already exists" in message -> R.string.register_error_email_already_exists
            "password" in message &&
                    ("weak" in message || "short" in message) -> R.string.register_error_weak_password
            "invalid email" in message ||
                    "email_address_invalid" in message -> R.string.register_error_invalid_email
            "rate_limit" in message ||
                    "rate limit" in message ||
                    "over_email_send_rate_limit" in message -> R.string.register_error_rate_limit
            "network" in message ||
                    "unable to resolve host" in message ||
                    "timeout" in message ||
                    "failed to connect" in message -> R.string.register_error_network
            "permission denied" in message ||
                    "missing internet permission" in message -> R.string.register_error_missing_permission
            else -> R.string.register_error_generic
        }
    }
}
