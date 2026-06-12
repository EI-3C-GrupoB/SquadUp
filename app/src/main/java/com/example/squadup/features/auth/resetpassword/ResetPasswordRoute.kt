package com.example.squadup.features.auth.resetpassword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ResetPasswordRoute(
    email: String,
    onPasswordReset: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: ResetPasswordViewModel = viewModel(
        factory = ResetPasswordViewModelFactory(ResetPasswordRepository(), email)
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ResetPasswordScreen(
        uiState = uiState,
        onCodeChange = viewModel::onCodeChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onResetPasswordClick = { viewModel.resetPassword(onPasswordReset) },
        onBackClick = onBackClick
    )
}
