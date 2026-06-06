package com.example.squadup.features.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.squadup.R
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerRepository: RegisterRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    init {
        loadModalities()
    }

    fun onFullNameChange(value: String) {
        _uiState.value = _uiState.value.copy(fullName = value, errorMessage = null)
    }

    fun onUsernameChange(value: String) {
        _uiState.value = _uiState.value.copy(username = value, errorMessage = null)
    }

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value, errorMessage = null)
    }

    fun onBirthDateChange(value: String) {
        _uiState.value = _uiState.value.copy(birthDate = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value, errorMessage = null)
    }

    fun onAccountTypeChange(value: UserRole) {
        _uiState.value = _uiState.value.copy(accountType = value, errorMessage = null)
    }

    fun onModalityToggle(value: String) {
        val current = _uiState.value
        val updated = if (value in current.selectedModalities) {
            current.selectedModalities - value
        } else {
            current.selectedModalities + value
        }
        _uiState.value = current.copy(selectedModalities = updated, errorMessage = null)
    }

    fun onLocationChange(value: SelectedLocation?) {
        _uiState.value = _uiState.value.copy(location = value, errorMessage = null)
    }

    fun onPlayStyleChange(value: PlayStyle) {
        _uiState.value = _uiState.value.copy(playStyle = value)
    }

    fun onNotificationRadiusChange(value: Int) {
        _uiState.value = _uiState.value.copy(notificationRadius = value)
    }

    fun onShowLocationPicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showLocationPicker = show)
    }

    fun register() {
        val currentState = _uiState.value

        when {
            currentState.fullName.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = R.string.register_validation_full_name_required)
                return
            }
            currentState.username.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = R.string.register_validation_username_required)
                return
            }
            currentState.email.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = R.string.register_validation_email_required)
                return
            }
            currentState.birthDate.isBlank() -> {
                _uiState.value = currentState.copy(errorMessage = R.string.register_validation_birth_date_required)
                return
            }
            !Regex("""\d{4}-\d{2}-\d{2}""").matches(currentState.birthDate) -> {
                _uiState.value = currentState.copy(errorMessage = R.string.register_validation_birth_date_format)
                return
            }
            currentState.password.length < 6 -> {
                _uiState.value = currentState.copy(errorMessage = R.string.register_validation_password_min_length)
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

            registerRepository
                .register(
                    RegisterProfile(
                        fullName = currentState.fullName.trim(),
                        username = currentState.username.trim(),
                        email = currentState.email.trim(),
                        birthDate = currentState.birthDate.trim(),
                        password = currentState.password,
                        accountType = currentState.accountType,
                        modalityIds = currentState.modalities
                            .filter { it.name in currentState.selectedModalities }
                            .map { it.id },
                        location = currentState.location,
                        playStyle = currentState.playStyle,
                        notificationRadius = currentState.notificationRadius
                    )
                )
                .onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRegisterSuccessful = true
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = (exception as? RegisterException)?.messageRes
                            ?: R.string.register_error_generic
                    )
                }
        }
    }

    fun clearRegisterSuccess() {
        _uiState.value = _uiState.value.copy(isRegisterSuccessful = false)
    }

    private fun loadModalities() {
        viewModelScope.launch {
            registerRepository.getModalities().onSuccess { modalities ->
                _uiState.value = _uiState.value.copy(modalities = modalities)
            }
        }
    }
}

class RegisterViewModelFactory(
    private val registerRepository: RegisterRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(registerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
