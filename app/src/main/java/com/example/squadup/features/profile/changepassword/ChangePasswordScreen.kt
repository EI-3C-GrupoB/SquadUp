package com.example.squadup.features.profile.changepassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.*
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage

@Composable
fun ChangePasswordScreen(
    uiState: ChangePasswordUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmNewPasswordChange: (String) -> Unit,
    onChangePasswordClick: () -> Unit,
    onBackClick: () -> Unit,
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = stringResource(R.string.profile_title),
                showBackButton = true,
                onBackClick = onBackClick,
                showNotificationsButton = true,
                showSettingsButton = true,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                onAdminViewChange = onAdminViewChange,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        },
        bottomBar = {
            AppNavBar(selectedRoute = selectedRoute, onItemClick = onNavItemClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            AuthCard {
                Text(
                    text = stringResource(R.string.changePassword_heading),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = stringResource(R.string.changePassword_subtitle),
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = SquadTextSecondary
                )

                Spacer(modifier = Modifier.height(28.dp))

                if (uiState.errorMessage != null) {
                    Text(
                        text = stringResource(uiState.errorMessage),
                        color = SquadError,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }

                AuthTextField(
                    value = uiState.currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    label = stringResource(R.string.changePassword_current_label),
                    placeholder = "•••••••••••••••",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(color = SquadGrayLight)

                Spacer(modifier = Modifier.height(24.dp))

                AuthTextField(
                    value = uiState.newPassword,
                    onValueChange = onNewPasswordChange,
                    label = stringResource(R.string.changePassword_new_label),
                    placeholder = "•••••••••••••••",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                AuthTextField(
                    value = uiState.confirmNewPassword,
                    onValueChange = onConfirmNewPasswordChange,
                    label = stringResource(R.string.changePassword_confirm_label),
                    placeholder = "•••••••••••••••",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(30.dp))

                PrimaryButton(
                    text = stringResource(R.string.changePassword_button),
                    onClick = onChangePasswordClick,
                    enabled = !uiState.isLoading
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
