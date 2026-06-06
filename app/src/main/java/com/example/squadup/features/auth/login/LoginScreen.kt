package com.example.squadup.features.auth.login

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AuthCard
import com.example.squadup.core.ui.components.AuthTextField
import com.example.squadup.core.ui.components.PrimaryButton
import com.example.squadup.core.ui.theme.SquadBackground
import com.example.squadup.core.ui.theme.SquadError
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SquadBackground)
            .verticalScroll(rememberScrollState())
    ) {
        AppHeader(
            showLogo = true,
            showBackButton = true,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(58.dp))

        Text(
            text = stringResource(R.string.login_title),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.login_subtitle),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            color = SquadTextSecondary
        )

        Spacer(modifier = Modifier.height(58.dp))

        AuthCard(modifier = Modifier.padding(horizontal = 20.dp)) {
            AuthTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.login_emailLabel),
                placeholder = "alex@example.com",
                trailingIcon = Icons.Outlined.Email
            )

            Spacer(modifier = Modifier.height(18.dp))

            AuthTextField(
                value = uiState.password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.login_passwordLabel),
                placeholder = "********",
                isPassword = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = if (uiState.isLoading) {
                    stringResource(R.string.login_btnMgs1)
                } else {
                    stringResource(R.string.login_btnMgs2)
                },
                onClick = onSignInClick,
                enabled = !uiState.isLoading,
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward
            )

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(uiState.errorMessage),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadError
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(R.string.login_createMgs1),
                fontSize = 14.sp,
                color = SquadTextSecondary
            )

            Text(
                text = stringResource(R.string.login_createMgs2),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadOrange,
                modifier = Modifier.clickable(enabled = !uiState.isLoading) {
                    onCreateAccountClick()
                }
            )
        }
    }
}
