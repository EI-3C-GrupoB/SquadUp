package com.example.squadup.data.repositories

import androidx.annotation.StringRes
import com.example.squadup.R
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class LoginRepository(
    private val supabaseClient: SupabaseClient
) {

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            supabaseClient.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(
                LoginException(mapLoginError(exception))
            )
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(
                LoginException(mapLogoutError(exception))
            )
        }
    }

    @StringRes
    private fun mapLoginError(exception: Exception): Int {
        val message = exception.message.orEmpty().lowercase()

        return when {
            "invalid_credentials" in message ||
                    "invalid login credentials" in message -> {
                R.string.login_error_invalid_credentials
            }

            "email not confirmed" in message ||
                    "email_not_confirmed" in message -> {
                R.string.login_error_email_not_confirmed
            }

            "network" in message ||
                    "unable to resolve host" in message ||
                    "timeout" in message ||
                    "failed to connect" in message -> {
                R.string.login_error_network
            }

            "permission denied" in message ||
                    "missing internet permission" in message -> {
                R.string.login_error_missing_permission
            }

            else -> {
                R.string.login_error_generic
            }
        }
    }

    @StringRes
    private fun mapLogoutError(exception: Exception): Int {
        val message = exception.message.orEmpty().lowercase()

        return when {
            "network" in message ||
                    "unable to resolve host" in message ||
                    "timeout" in message ||
                    "failed to connect" in message -> {
                R.string.logout_error_network
            }

            else -> {
                R.string.logout_error_generic
            }
        }
    }
}

class LoginException(
    @StringRes val messageRes: Int
) : Exception()