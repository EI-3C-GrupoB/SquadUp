package com.example.squadup.features.auth.forgotpassword

import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import kotlinx.serialization.Serializable

class ForgotPasswordRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun requestReset(email: String): Result<Unit> {
        return try {
            val response = supabaseClient.functions.invoke(
                function = "request-password-reset",
                body = RequestResetBody(email = email)
            )
            val result = response.body<FunctionResponse>()

            if (result.success) {
                Result.success(Unit)
            } else {
                Result.failure(ForgotPasswordException(R.string.forgotPassword_error_generic))
            }
        } catch (exception: Exception) {
            Result.failure(ForgotPasswordException(mapForgotPasswordError(exception)))
        }
    }

    private fun mapForgotPasswordError(exception: Exception): Int {
        val message = exception.message.orEmpty().lowercase()

        return when {
            "network" in message ||
                "unable to resolve host" in message ||
                "timeout" in message ||
                "failed to connect" in message -> R.string.forgotPassword_error_network
            else -> R.string.forgotPassword_error_generic
        }
    }
}

@Serializable
private data class RequestResetBody(val email: String)

@Serializable
private data class FunctionResponse(val success: Boolean, val error: String? = null)
