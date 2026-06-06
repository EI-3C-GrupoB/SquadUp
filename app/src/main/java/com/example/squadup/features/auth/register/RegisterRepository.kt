package com.example.squadup.features.auth.register

import androidx.annotation.StringRes
import com.example.squadup.R
import com.example.squadup.core.SupabaseClientProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.SignOutScope
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class RegisterRepository(
    private val supabaseClient: SupabaseClient = SupabaseClientProvider.client
) {
    suspend fun register(profile: RegisterProfile): Result<Unit> {
        return try {
            android.util.Log.d("RegisterRepo", "register() called for ${profile.email}")

            try {
                supabaseClient.auth.signOut(SignOutScope.LOCAL)
            } catch (_: Exception) {
            }

            val signUpUser = supabaseClient.auth.signUpWith(Email) {
                email = profile.email
                password = profile.password
            }

            var authUserId = signUpUser?.id ?: supabaseClient.auth.currentUserOrNull()?.id
            android.util.Log.d("RegisterRepo", "auth user after signup: ${authUserId ?: "NULL"}")

            if (authUserId == null) {
                runCatching {
                    supabaseClient.auth.signInWith(Email) {
                        email = profile.email
                        password = profile.password
                    }
                }.onFailure { exception ->
                    android.util.Log.w("RegisterRepo", "signIn after signup failed: ${exception.message}")
                }
                authUserId = supabaseClient.auth.currentUserOrNull()?.id
            }

            if (authUserId == null) {
                android.util.Log.e("RegisterRepo", "No auth user id after signup")
                return Result.failure(RegisterException(R.string.register_error_generic))
            }

            var rawUtilizadorId: Int? = null
            repeat(5) { attempt ->
                try {
                    rawUtilizadorId = supabaseClient
                        .from("utilizador")
                        .select { filter { eq("auth_user_id", authUserId) } }
                        .decodeSingle<UtilizadorRow>()
                        .id
                    return@repeat
                } catch (exception: Exception) {
                    android.util.Log.w(
                        "RegisterRepo",
                        "attempt ${attempt + 1}: utilizador row not ready yet - ${exception.message}"
                    )
                    if (attempt == 4) throw exception
                    kotlinx.coroutines.delay(500)
                }
            }
            val utilizadorId = rawUtilizadorId
                ?: throw Exception("utilizador row not created by trigger")

            supabaseClient.from("utilizador").update({
                set("nome", profile.fullName)
                set("username", profile.username)
                set("data_nascimento", profile.birthDate)
                set("tipo_conta", profile.accountType.value)
                set("play_style", profile.playStyle.level)
                set("raio_notificacao", profile.notificationRadius)
            }) {
                filter { eq("id", utilizadorId) }
            }

            profile.location?.let { loc ->
                val locId = supabaseClient
                    .from("localizacao")
                    .insert(
                        LocInsert(
                            geopos = "SRID=4326;POINT(${loc.lng} ${loc.lat})",
                            morada = loc.address
                        )
                    ) {
                        select()
                    }
                    .decodeSingle<LocRow>()
                    .id

                supabaseClient.from("utilizador").update({
                    set("loc_id", locId)
                }) {
                    filter { eq("id", utilizadorId) }
                }
            }

            if (profile.modalityIds.isNotEmpty()) {
                supabaseClient.from("utilizador_modalidade").insert(
                    profile.modalityIds.map { modalidadeId ->
                        UtilizadorModalidade(
                            userId = utilizadorId,
                            modalidadeId = modalidadeId
                        )
                    }
                )
            }

            if (supabaseClient.auth.currentUserOrNull() == null) {
                runCatching {
                    supabaseClient.auth.signInWith(Email) {
                        email = profile.email
                        password = profile.password
                    }
                }.onFailure { exception ->
                    android.util.Log.w("RegisterRepo", "signIn after profile update failed: ${exception.message}")
                }
            }

            Result.success(Unit)
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
        android.util.Log.e("RegisterRepo", "register failed: ${exception.message}", exception)
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

@Serializable
private data class UtilizadorRow(val id: Int)

@Serializable
private data class LocRow(val id: Long)

@Serializable
private data class LocInsert(
    val geopos: String,
    val morada: String?
)

@Serializable
private data class UtilizadorModalidade(
    @SerialName("user_id") val userId: Int,
    @SerialName("modalidade_id") val modalidadeId: Int
)
