package com.example.squadup.features.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.squadup.core.app.AppViewModel

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel,
    appViewModel: AppViewModel,
    onFinish: () -> Unit,
    onLoginClick: () -> Unit
) {
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    OnboardingScreen(
        selectedLanguage = appUiState.selectedLanguage,
        onLanguageChange = appViewModel::onLanguageChange,
        onNotificationsClick = {},
        onFinish = {
            viewModel.onFinish {
                onFinish()
            }
        },
        onLoginClick = onLoginClick
    )
}