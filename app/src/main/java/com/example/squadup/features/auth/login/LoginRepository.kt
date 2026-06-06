package com.example.squadup.features.auth.login

import androidx.annotation.StringRes
import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class LoginRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun login(credentials: LoginCredentials): Result<Unit> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                email = credentials.email
                password = credentials.password
            }
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(LoginException(mapLoginError(exception)))
        }
    }

    @StringRes
    private fun mapLoginError(exception: Exception): Int {
        val message = exception.message.orEmpty().lowercase()

        return when {
            "invalid_credentials" in message ||
                    "invalid login credentials" in message -> R.string.login_error_invalid_credentials
            "email not confirmed" in message ||
                    "email_not_confirmed" in message -> R.string.login_error_email_not_confirmed
            "network" in message ||
                    "unable to resolve host" in message ||
                    "timeout" in message ||
                    "failed to connect" in message -> R.string.login_error_network
            "permission denied" in message ||
                    "missing internet permission" in message -> R.string.login_error_missing_permission
            else -> R.string.login_error_generic
        }
    }
}
