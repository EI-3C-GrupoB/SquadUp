package com.example.squadup.features.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterRoute(
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(RegisterRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isRegisterSuccessful) {
        if (uiState.isRegisterSuccessful) {
            viewModel.clearRegisterSuccess()
            onRegisterSuccess()
        }
    }

    RegisterScreen(
        uiState = uiState,
        onFullNameChange = viewModel::onFullNameChange,
        onUsernameChange = viewModel::onUsernameChange,
        onEmailChange = viewModel::onEmailChange,
        onBirthDateChange = viewModel::onBirthDateChange,
        onPasswordChange = viewModel::onPasswordChange,
        onAccountTypeChange = viewModel::onAccountTypeChange,
        onModalityToggle = viewModel::onModalityToggle,
        onRegisterClick = viewModel::register,
        onLoginClick = onLoginClick,
        onBackClick = onBackClick
    )
}
