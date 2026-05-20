package com.example.squadup.data.repositories

import com.example.squadup.data.models.ModalityModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class ModalityRepository(
    private val supabaseClient: SupabaseClient
) {
    suspend fun getModalities(): Result<List<ModalityModel>> {
        return try {
            val modalities = supabaseClient
                .from("modalidade")
                .select()
                .decodeList<ModalityModel>()
            Result.success(modalities)
        } catch (exception: Exception) {
            println("ERRO_GET_MODALITIES: ${exception.message}")
            Result.failure(exception)
        }
    }
}
