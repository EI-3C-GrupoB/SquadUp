package com.example.squadup.features.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.squadup.core.app.AppViewModel

@Composable
fun OnboardingRoute(
    viewModel: OnboardingViewModel,
    appViewModel: AppViewModel,
    onFinish: () -> Unit,
    onLoginClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    OnboardingScreen(
        selectedLanguage = appUiState.selectedLanguage,
        onLanguageChange = { language ->
            appViewModel.onLanguageChange(language)
        },
        onFinish = { viewModel.onFinish { onFinish() } },
        onLoginClick = { onLoginClick() }
    )
}