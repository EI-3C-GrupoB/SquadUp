package com.example.squadup.ui.screens.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.squadup.R
import com.example.squadup.data.models.UserProfileModel
import com.example.squadup.data.repositories.AuthException
import com.example.squadup.data.repositories.AuthRepository
import com.example.squadup.data.repositories.ModalityRepository
import com.example.squadup.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val modalityRepository: ModalityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    init {
        fetchModalities()
    }

    private fun fetchModalities() {
        viewModelScope.launch {
            modalityRepository.getModalities()
                .onSuccess { modalities ->
                    _uiState.value = _uiState.value.copy(
                        availableModalities = modalities
                    )
                }
        }
    }

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            fullName = value,
            errorMessageRes = null
        )
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(
            username = value,
            errorMessageRes = null
        )
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(
            email = value,
            errorMessageRes = null
        )
    }

    fun onBirthDateChange(value: String) {
        _uiState.value = _uiState.value.copy(
            birthDate = value,
            errorMessageRes = null
        )
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(
            password = value,
            errorMessageRes = null
        )
    }

    fun onExperienceLevelChange(value: Float) {
        _uiState.value = _uiState.value.copy(
            experienceLevel = value,
            errorMessageRes = null
        )
    }

    fun onSportToggle(sport: String) {
        val currentState = _uiState.value

        val updatedSports = if (currentState.selectedSports.contains(sport)) {
            currentState.selectedSports - sport
        } else {
            currentState.selectedSports + sport
        }

        _uiState.value = currentState.copy(
            selectedSports = updatedSports,
            errorMessageRes = null
        )
    }

    fun register() {
        val currentState = _uiState.value

        when {
            currentState.fullName.isBlank() -> {
                _uiState.value = currentState.copy(
                    errorMessageRes = R.string.register_validation_full_name_required
                )
                return
            }

            currentState.username.isBlank() -> {
                _uiState.value = currentState.copy(
                    errorMessageRes = R.string.register_validation_username_required
                )
                return
            }

            currentState.email.isBlank() -> {
                _uiState.value = currentState.copy(
                    errorMessageRes = R.string.register_validation_email_required
                )
                return
            }

            currentState.birthDate.isBlank() -> {
                _uiState.value = currentState.copy(
                    errorMessageRes = R.string.register_validation_birth_date_required
                )
                return
            }

            currentState.password.length < 6 -> {
                _uiState.value = currentState.copy(
                    errorMessageRes = R.string.register_validation_password_min_length
                )
                return
            }

            currentState.selectedSports.isEmpty() -> {
                _uiState.value = currentState.copy(
                    errorMessageRes = R.string.register_validation_sport_required
                )
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessageRes = null
            )

            authRepository.register(
                email = currentState.email.trim(),
                password = currentState.password
            ).onSuccess { authUserId ->
                createUserProfile(authUserId)
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = when (exception) {
                        is AuthException -> exception.messageRes
                        else -> R.string.register_error_generic
                    }
                )
            }
        }
    }

    private suspend fun createUserProfile(authUserId: String) {
        val currentState = _uiState.value

        val userProfile = UserProfileModel(
            authUserId = authUserId,
            fullName = currentState.fullName.trim(),
            username = currentState.username.trim(),
            email = currentState.email.trim(),
            birthDate = currentState.birthDate,
            experienceLevel = currentState.experienceLevel.toInt()
        )

        userRepository.createUserProfile(userProfile)
            .onSuccess { userId ->
                val modalityIds = mapSportsToModalityIds(currentState.selectedSports)

                userRepository.addUserModalities(
                    userId = userId,
                    modalityIds = modalityIds
                ).onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegisterSuccessful = true
                    )
                }.onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessageRes = R.string.register_error_profile_create
                    )
                }
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessageRes = R.string.register_error_profile_create
                )
            }
    }

    private fun mapSportsToModalityIds(sports: List<String>): List<Int> {
        val modalities = _uiState.value.availableModalities
        return sports.mapNotNull { sportName ->
            modalities.find { it.nome == sportName }?.id
        }
    }

    fun clearRegisterSuccess() {
        _uiState.value = _uiState.value.copy(
            isRegisterSuccessful = false
        )
    }
}