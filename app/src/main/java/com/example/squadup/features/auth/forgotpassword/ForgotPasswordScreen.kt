package com.example.squadup.features.auth.forgotpassword

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import com.example.squadup.core.ui.components.responsiveContentWidth
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
import com.example.squadup.core.ui.components.responsiveVerticalSpacing
import com.example.squadup.core.ui.theme.SquadError

@Composable
fun ForgotPasswordScreen(
    uiState: ForgotPasswordUiState,
    onEmailChange: (String) -> Unit,
    onSendCodeClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val topSpacing = responsiveVerticalSpacing(58.dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppHeader(
            showLogo = true,
            showBackButton = true,
            onBackClick = onBackClick
        )

        Spacer(modifier = Modifier.height(topSpacing))

        Text(
            text = stringResource(R.string.forgotPassword_title),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.forgotPassword_subtitle),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = responsiveHorizontalPadding(32.dp)),
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(topSpacing))

        AuthCard(
            modifier = Modifier
                .padding(horizontal = responsiveHorizontalPadding(20.dp))
                .responsiveContentWidth(560.dp)
        ) {
            AuthTextField(
                value = uiState.email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.forgotPassword_emailLabel),
                placeholder = "alex@example.com",
                trailingIcon = Icons.Outlined.Email,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = if (uiState.isLoading) {
                    stringResource(R.string.forgotPassword_button_loading)
                } else {
                    stringResource(R.string.forgotPassword_button)
                },
                onClick = onSendCodeClick,
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
    }
}
