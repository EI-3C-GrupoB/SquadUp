package com.example.squadup.features.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AuthCard
import com.example.squadup.core.ui.components.AuthTextField
import com.example.squadup.core.ui.components.PrimaryButton
import com.example.squadup.core.ui.components.ProfileDropdownField
import com.example.squadup.core.ui.theme.SquadBackground
import com.example.squadup.core.ui.theme.SquadError
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onFullNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAccountTypeChange: (AccountType) -> Unit,
    onModalityToggle: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val accountTypeOptions = listOf(
        AccountType.Player to stringResource(R.string.register_user_type_player),
        AccountType.Organizer to stringResource(R.string.register_user_type_organizer)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SquadBackground)
            .verticalScroll(rememberScrollState())
    ) {
        AppHeader(
            showLogo = true,
            showBackButton = true,
            onBackClick = onBackClick,
            actions = {
                Row(
                    modifier = Modifier.clickable(
                        enabled = !uiState.isLoading,
                        onClick = onLoginClick
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Login,
                        contentDescription = null,
                        tint = SquadOrange
                    )
                    Text(
                        text = stringResource(R.string.register_login_action),
                        fontSize = 16.sp,
                        color = SquadOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        AuthCard(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(R.string.register_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.register_subtitle),
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                value = uiState.fullName,
                onValueChange = onFullNameChange,
                label = stringResource(R.string.register_full_name_label),
                placeholder = stringResource(R.string.register_full_name_placeholder),
                leadingIcon = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                label = stringResource(R.string.register_username_label),
                placeholder = stringResource(R.string.register_username_placeholder),
                leadingIcon = Icons.Outlined.AlternateEmail
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.register_email_label),
                placeholder = stringResource(R.string.register_email_placeholder),
                leadingIcon = Icons.Outlined.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = uiState.birthDate,
                onValueChange = onBirthDateChange,
                label = stringResource(R.string.register_birth_date_label),
                placeholder = "YYYY-MM-DD",
                leadingIcon = Icons.Outlined.Cake
            )

            Spacer(modifier = Modifier.height(14.dp))

            ProfileDropdownField(
                label = stringResource(R.string.register_user_type_label),
                selectedValue = accountTypeOptions.first { it.first == uiState.accountType }.second,
                options = accountTypeOptions.map { it.second },
                onValueSelected = { selected ->
                    accountTypeOptions
                        .firstOrNull { it.second == selected }
                        ?.let { onAccountTypeChange(it.first) }
                },
                labelColor = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.register_password_label),
                placeholder = stringResource(R.string.register_password_placeholder),
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true
            )

            if (uiState.modalities.isNotEmpty()) {
                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = stringResource(R.string.register_sports_interests),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.modalities.forEach { modality ->
                        FilterChip(
                            selected = uiState.selectedModalities.contains(modality.name),
                            onClick = { onModalityToggle(modality.name) },
                            label = { Text(modality.name) }
                        )
                    }
                }
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = stringResource(uiState.errorMessage),
                    color = SquadError,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = if (uiState.isLoading) {
                    stringResource(R.string.register_button_loading)
                } else {
                    stringResource(R.string.register_button)
                },
                onClick = onRegisterClick,
                enabled = !uiState.isLoading,
                trailingIcon = Icons.Outlined.SportsSoccer
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
