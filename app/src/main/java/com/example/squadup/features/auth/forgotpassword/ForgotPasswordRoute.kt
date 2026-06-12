package com.example.squadup.features.auth.forgotpassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ForgotPasswordRoute(
    onCodeSent: (email: String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: ForgotPasswordViewModel = viewModel(
        factory = ForgotPasswordViewModelFactory(ForgotPasswordRepository())
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForgotPasswordScreen(
        uiState = uiState,
        onEmailChange = viewModel::onEmailChange,
        onSendCodeClick = { viewModel.sendCode(onCodeSent) },
        onBackClick = onBackClick
    )
}
