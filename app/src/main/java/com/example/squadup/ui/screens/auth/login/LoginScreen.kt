package com.example.squadup.ui.screens.auth.login

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppLanguage
import com.example.squadup.ui.components.AuthCard
import com.example.squadup.ui.components.AuthTextField
import com.example.squadup.ui.components.LanguageSwitch
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.components.SectionDivider
import com.example.squadup.ui.components.SocialLoginButton
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun LoginScreen(
    email: String,
    password: String,
    selectedLanguage: AppLanguage,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignInClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit,
    onCreateAccountClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: Int?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SquadBackground)
            .verticalScroll(rememberScrollState())
    ) {
        AppHeader(
            showLogo = true,
            actions = {
                LanguageSwitch(
                    selectedLanguage = selectedLanguage,
                    onLanguageChange = onLanguageChange
                )
            }
        )

        Spacer(modifier = Modifier.height(58.dp))

        Text(
            text = stringResource(id = R.string.login_title ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(id = R.string.login_subtitle),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            color = SquadTextSecondary
        )

        Spacer(modifier = Modifier.height(58.dp))

        AuthCard(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(id = R.string.login_emailLabel),
                placeholder = "alex@example.com",
                trailingIcon = Icons.Outlined.Email
            )

            Spacer(modifier = Modifier.height(18.dp))

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(id = R.string.login_passwordLabel),
                placeholder = "••••••••",
                isPassword = true,
                labelActionText = stringResource(id = R.string.login_forgot),
                onLabelActionClick = onForgotPasswordClick
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = if (isLoading) stringResource(id = R.string.login_btnMgs1) else stringResource(id = R.string.login_btnMgs2),
                onClick = {
                    if (!isLoading) {
                        onSignInClick()
                    }
                },
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(id = errorMessage),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadOrange
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            SectionDivider(
                text = stringResource(id = R.string.login_divider)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SocialLoginButton(
                    text = "Google",
                    icon = painterResource(id = R.drawable.logo_google),
                    onClick = onGoogleClick,
                    modifier = Modifier.weight(1f)
                )

                SocialLoginButton(
                    text = "Facebook",
                    icon = painterResource(id = R.drawable.logo_facebook),
                    onClick = onFacebookClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(id = R.string.login_createMgs1),
                fontSize = 14.sp,
                color = SquadTextSecondary
            )

            Text(
                text = stringResource(id = R.string.login_createMgs2),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadOrange,
                modifier = Modifier.clickable {
                    onCreateAccountClick()
                }
            )
        }
    }
}