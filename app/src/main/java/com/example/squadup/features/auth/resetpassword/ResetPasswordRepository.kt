package com.example.squadup.features.auth.resetpassword

import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import kotlinx.serialization.Serializable

class ResetPasswordRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun confirmReset(email: String, code: String, newPassword: String): Result<Unit> {
        return try {
            val response = supabaseClient.functions.invoke(
                function = "confirm-password-reset",
                body = ConfirmResetBody(email = email, token = code, newPassword = newPassword)
            )
            val result = response.body<FunctionResponse>()

            if (result.success) {
                Result.success(Unit)
            } else {
                Result.failure(ResetPasswordException(mapErrorCode(result.error)))
            }
        } catch (exception: Exception) {
            Result.failure(ResetPasswordException(mapResetPasswordError(exception)))
        }
    }

    private fun mapErrorCode(error: String?): Int {
        return when (error) {
            "invalid_or_expired_code" -> R.string.resetPassword_error_invalid_code
            "weak_password" -> R.string.resetPassword_error_weak_password
            else -> R.string.resetPassword_error_generic
        }
    }

    private fun mapResetPasswordError(exception: Exception): Int {
        val message = exception.message.orEmpty().lowercase()

        return when {
            "network" in message ||
                "unable to resolve host" in message ||
                "timeout" in message ||
                "failed to connect" in message -> R.string.resetPassword_error_network
            else -> R.string.resetPassword_error_generic
        }
    }
}

@Serializable
private data class ConfirmResetBody(val email: String, val token: String, val newPassword: String)

@Serializable
private data class FunctionResponse(val success: Boolean, val error: String? = null)
