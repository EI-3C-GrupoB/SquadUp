package com.example.squadup.features.admin.manageaccounts.edituser

import android.util.Log
import com.example.squadup.core.SupabaseClientProvider
import com.example.squadup.features.admin.manageaccounts.AccountRole
import com.example.squadup.features.admin.manageaccounts.toAccountType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class EditUserRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun loadUser(userId: Int): Result<EditUserUiState> {
        return try {
            val row = supabaseClient
                .from("utilizador")
                .select {
                    filter {
                        eq("id", userId)
                    }
                }
                .decodeSingle<EditUserRow>()

            Result.success(row.toEditUserUiState())
        } catch (exception: Exception) {
            Log.e("EditUserRepository", "Error loading user $userId", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateUserRole(
        userId: Int,
        role: AccountRole,
        isAdmin: Boolean
    ): Result<Unit> {
        return try {
            supabaseClient
                .from("utilizador")
                .update({
                    set("is_admin", isAdmin)
                    set("tipo_conta", role.toAccountType())
                }) {
                    filter {
                        eq("id", userId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("EditUserRepository", "Error updating user role $userId", exception)
            Result.failure(exception)
        }
    }

    suspend fun updateSuspension(
        userId: Int,
        isSuspended: Boolean
    ): Result<Unit> {
        return try {
            supabaseClient
                .from("utilizador")
                .update({
                    set("estado_conta", if (isSuspended) "suspenso" else "ativo")
                }) {
                    filter {
                        eq("id", userId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("EditUserRepository", "Error updating suspension $userId", exception)
            Result.failure(exception)
        }
    }

    suspend fun deleteUserProfile(userId: Int): Result<Unit> {
        return try {
            supabaseClient
                .from("utilizador")
                .delete {
                    filter {
                        eq("id", userId)
                    }
                }

            Result.success(Unit)
        } catch (exception: Exception) {
            Log.e("EditUserRepository", "Error deleting user profile $userId", exception)
            Result.failure(exception)
        }
    }
}
