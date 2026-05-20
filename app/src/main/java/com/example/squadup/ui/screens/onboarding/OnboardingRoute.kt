package com.example.squadup.ui.screens.onboarding

import androidx.compose.runtime.Composable
import com.example.squadup.ui.components.AppLanguage

@Composable
fun OnboardingRoute(
    selectedLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onFinish: () -> Unit,
    onLoginClick: () -> Unit
) {
    OnboardingScreen(
        selectedLanguage = selectedLanguage,
        onLanguageChange = onLanguageChange,
        onFinish = onFinish,
        onLoginClick = onLoginClick
    )
}