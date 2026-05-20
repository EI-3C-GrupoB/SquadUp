package com.example.squadup.ui.screens.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.data.repositories.AuthRepository
import com.example.squadup.data.supabase.SupabaseClientProvider
import com.example.squadup.ui.components.AppLanguage

@Composable
fun LoginRoute(
    selectedLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onLoginSuccess: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onCreateAccountClick: () -> Unit
) {
    val repository = AuthRepository(
        supabaseClient = SupabaseClientProvider.client
    )

    val viewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(repository)
    )

    val uiState by viewModel.uiState.collectAsState()

    LoginScreen(
        email = uiState.email,
        password = uiState.password,
        selectedLanguage = selectedLanguage,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLanguageChange = onLanguageChange,
        onForgotPasswordClick = onForgotPasswordClick,
        onSignInClick = {
            viewModel.login(
                onSuccess = onLoginSuccess
            )
        },
        onGoogleClick = {
            // Depois fazemos login com Google
        },
        onFacebookClick = {
            // Depois fazemos login com Facebook
        },
        onCreateAccountClick = onCreateAccountClick,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage
    )
}