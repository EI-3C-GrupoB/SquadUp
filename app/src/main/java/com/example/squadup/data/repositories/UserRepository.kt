package com.example.squadup.data.repositories

import com.example.squadup.data.models.CreatedUserModel
import com.example.squadup.data.models.UserModalityModel
import com.example.squadup.data.models.UserProfileModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class UserRepository(
    private val supabaseClient: SupabaseClient
) {

    suspend fun createUserProfile(
        userProfile: UserProfileModel
    ): Result<Int> {
        return try {
            val createdUser = supabaseClient
                .from("utilizador")
                .upsert(userProfile) {
                    select()
                }
                .decodeSingle<CreatedUserModel>()

            Result.success(createdUser.id)
        } catch (exception: Exception) {
            println("ERRO_CREATE_USER_PROFILE: ${exception.message}")
            exception.printStackTrace()
            Result.failure(exception)
        }
    }

    suspend fun addUserModalities(
        userId: Int,
        modalityIds: List<Int>
    ): Result<Unit> {
        return try {
            val userModalities = modalityIds.map { modalityId ->
                UserModalityModel(
                    userId = userId,
                    modalityId = modalityId
                )
            }

            supabaseClient
                .from("utilizador_modalidade")
                .insert(userModalities)

            Result.success(Unit)
        } catch (exception: Exception) {
            println("ERRO_ADD_USER_MODALITIES: ${exception.message}")
            exception.printStackTrace()
            Result.failure(exception)
        }
    }
}