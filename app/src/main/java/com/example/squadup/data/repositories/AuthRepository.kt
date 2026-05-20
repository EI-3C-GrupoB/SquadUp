package com.example.squadup.data.repositories

import androidx.annotation.StringRes
import com.example.squadup.R
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository(
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
                AuthException(mapLoginError(exception))
            )
        }
    }

    suspend fun register(
        email: String,
        password: String
    ): Result<String> {
        return try {
            supabaseClient.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }

            // No Supabase 3.x, o signUpWith retorna Unit. 
            // O utilizador criado pode ser obtido através do currentUserOrNull()
            val authUserId = supabaseClient.auth.currentUserOrNull()?.id

            println("REGISTER_USER_ID: $authUserId")

            if (authUserId == null) {
                Result.failure(
                    AuthException(R.string.register_error_auth_user_missing)
                )
            } else {
                Result.success(authUserId)
            }
        } catch (exception: Exception) {
            println("ERRO_REGISTER_AUTH: ${exception.message}")
            exception.printStackTrace()

            Result.failure(
                AuthException(mapRegisterError(exception))
            )
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            supabaseClient.auth.signOut()

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(
                AuthException(mapLogoutError(exception))
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
    private fun mapRegisterError(exception: Exception): Int {
        val message = exception.message.orEmpty().lowercase()

        return when {
            "user already registered" in message ||
                    "already registered" in message ||
                    "email already" in message ||
                    "already exists" in message -> {
                R.string.register_error_email_already_exists
            }

            "password" in message &&
                    ("weak" in message || "short" in message) -> {
                R.string.register_error_weak_password
            }

            "invalid email" in message ||
                    "email_address_invalid" in message -> {
                R.string.register_error_invalid_email
            }

            "rate_limit" in message || "rate limit" in message || "over_email_send_rate_limit" in message -> {
                R.string.register_error_rate_limit
            }

            "network" in message ||
                    "unable to resolve host" in message ||
                    "timeout" in message ||
                    "failed to connect" in message -> {
                R.string.register_error_network
            }

            "permission denied" in message ||
                    "missing internet permission" in message -> {
                R.string.register_error_missing_permission
            }

            else -> {
                R.string.register_error_generic
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

class AuthException(
    @StringRes val messageRes: Int
) : Exception()