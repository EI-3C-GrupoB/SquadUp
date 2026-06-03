package com.example.squadup.features.profile.changepassword

import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class ChangePasswordRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun changePassword(request: ChangePasswordRequest): Result<Unit> {
        return try {
            val email = supabaseClient.auth.currentUserOrNull()?.email
                ?: return Result.failure(ChangePasswordException(R.string.profile_error_not_logged_in))

            supabaseClient.auth.signInWith(Email) {
                this.email = email
                password = request.currentPassword
            }

            supabaseClient.auth.updateUser {
                password = request.newPassword
            }

            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(ChangePasswordException(mapChangePasswordError(exception)))
        }
    }

    private fun mapChangePasswordError(exception: Exception): Int {
        val message = exception.message.orEmpty().lowercase()

        return when {
            "invalid_credentials" in message ||
                    "invalid login credentials" in message -> R.string.changePassword_error_current_invalid
            "password" in message &&
                    ("weak" in message || "short" in message) -> R.string.changePassword_error_weak
            "network" in message ||
                    "unable to resolve host" in message ||
                    "timeout" in message ||
                    "failed to connect" in message -> R.string.changePassword_error_network
            else -> R.string.changePassword_error_generic
        }
    }
}
