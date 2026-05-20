package com.example.squadup.ui.screens.auth.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.data.repositories.AuthRepository
import com.example.squadup.data.repositories.ModalityRepository
import com.example.squadup.data.repositories.UserRepository
import com.example.squadup.data.supabase.SupabaseClientProvider
import com.example.squadup.ui.components.AppLanguage

@Composable
fun RegisterRoute(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    onTermsClick: () -> Unit = {},
    onPrivacyClick: () -> Unit = {}
) {
    val supabaseClient = SupabaseClientProvider.client

    val viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(
            authRepository = AuthRepository(supabaseClient),
            userRepository = UserRepository(supabaseClient),
            modalityRepository = ModalityRepository(supabaseClient)
        )
    )

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isRegisterSuccessful) {
        if (uiState.isRegisterSuccessful) {
            viewModel.clearRegisterSuccess()
            onRegisterSuccess()
        }
    }

    RegisterScreen(
        fullName = uiState.fullName,
        username = uiState.username,
        email = uiState.email,
        birthDate = uiState.birthDate,
        password = uiState.password,
        experienceLevel = uiState.experienceLevel,
        availableModalities = uiState.availableModalities,
        selectedSports = uiState.selectedSports,
        errorMessageRes = uiState.errorMessageRes,
        isLoading = uiState.isLoading,
        onFullNameChange = viewModel::onFullNameChange,
        onUsernameChange = viewModel::onUsernameChange,
        onEmailChange = viewModel::onEmailChange,
        onBirthDateChange = viewModel::onBirthDateChange,
        onPasswordChange = viewModel::onPasswordChange,
        onExperienceLevelChange = viewModel::onExperienceLevelChange,
        onSportToggle = viewModel::onSportToggle,
        onRegisterClick = viewModel::register,
        onLoginClick = onLoginClick,
        onTermsClick = onTermsClick,
        onPrivacyClick = onPrivacyClick
    )
}
