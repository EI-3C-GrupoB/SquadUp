package com.example.squadup.features.admin.manageaccounts.createuser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.*
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.features.admin.manageaccounts.AccountRole

@Composable
fun CreateUserScreen(
    uiState: CreateUserUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRoleChange: (AccountRole) -> Unit,
    onCreateClick: () -> Unit,
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    val roleOptions = listOf(
        AccountRole.Admin to stringResource(R.string.manageAccounts_role_admin),
        AccountRole.Organizer to stringResource(R.string.manageAccounts_role_organizer)
    )

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = stringResource(R.string.createUser_title),
                showBackButton = true,
                onBackClick = onBackClick,
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
                    text = stringResource(R.string.createUser_heading),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.createUser_subtitle),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = SquadTextSecondary
                )

                Spacer(modifier = Modifier.height(28.dp))

                AuthTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.createUser_email_label),
                    placeholder = "email@gmail.com"
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = uiState.username,
                    onValueChange = onUsernameChange,
                    label = stringResource(R.string.createUser_username_label),
                    placeholder = "username"
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    label = stringResource(R.string.createUser_password_label),
                    placeholder = "••••••••••••••••",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileDropdownField(
                    label = stringResource(R.string.createUser_account_type_label),
                    selectedValue = roleOptions.first { it.first == uiState.selectedRole }.second,
                    options = roleOptions.map { it.second },
                    onValueSelected = { selected ->
                        val role = roleOptions.first { it.second == selected }.first
                        onRoleChange(role)
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                PrimaryButton(
                    text = stringResource(R.string.createUser_button),
                    onClick = onCreateClick
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
