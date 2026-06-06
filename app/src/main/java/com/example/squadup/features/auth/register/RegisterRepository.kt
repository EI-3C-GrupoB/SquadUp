package com.example.squadup.features.auth.register

import androidx.annotation.StringRes
import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class RegisterRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun register(profile: RegisterProfile): Result<Unit> {
        return try {
            val authUser = supabaseClient.auth.signUpWith(Email) {
                email = profile.email
                password = profile.password
                data = buildJsonObject {
                    put("full_name", profile.fullName)
                    put("username", profile.username)
                    put("birth_date", profile.birthDate)
                    put("account_type", profile.accountType.name.lowercase())
                    put("modalities", JsonArray(profile.modalityNames.map(::JsonPrimitive)))
                }
            }

            if (authUser == null) {
                Result.failure(RegisterException(R.string.register_error_generic))
            } else {
                Result.success(Unit)
            }
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
