package com.example.squadup.ui.screens.auth

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AuthCard
import com.example.squadup.ui.components.AuthTextField
import com.example.squadup.ui.components.LanguageSelector
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.components.SectionDivider
import com.example.squadup.ui.components.SocialLoginButton
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun LoginScreen(
    email: String,
    password: String,
    selectedLanguage: String,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onBackClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignInClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color(0xFFF8F8F8))
    ) {
        AppHeader(
            showLogo = true,
            showBackButton = false,
            onBackClick = onBackClick,
            actions = {
                LanguageSelector(
                    selectedLanguage = selectedLanguage,
                    onLanguageSelected = onLanguageSelected
                )
            }
        )

        Spacer(modifier = Modifier.height(58.dp))

        Text(
            text = "Welcome Back",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Ready for game day? Sign in to your squad.",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            color = SquadIconSecondary
        )

        Spacer(modifier = Modifier.height(58.dp))

        AuthCard(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email Address",
                placeholder = "Pedro@example.com",
                trailingIcon = Icons.Outlined.Email
            )

            Spacer(modifier = Modifier.height(18.dp))

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "••••••••",
                isPassword = true,
                labelActionText = "Forgot Password?",
                onLabelActionClick = onForgotPasswordClick
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = "SIGN IN",
                onClick = onSignInClick,
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward
            )

            Spacer(modifier = Modifier.height(28.dp))

            SectionDivider(
                text = "OR CONTINUE WITH"
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                SocialLoginButton(
                    text = "Google",
                    icon = painterResource(id = R.drawable.logo_squadup),
                    onClick = onGoogleClick,
                    modifier = Modifier.weight(1f)
                )

                SocialLoginButton(
                    text = "Facebook",
                    icon = painterResource(id = R.drawable.logo_squadup),
                    onClick = onFacebookClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "New to the squad? ",
                fontSize = 14.sp,
                color = SquadIconSecondary
            )

            Text(
                text = "Create an Account",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadOrange,
                modifier = Modifier.clickable {
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen(
        email = "e@.pt",
        password = "",
        selectedLanguage = "PT",
        onEmailChange = {},
        onPasswordChange = {},
        onLanguageSelected = {},
        onBackClick = {},
        onForgotPasswordClick = {},
        onSignInClick = {},
        onGoogleClick = {},
        onFacebookClick = {}
    )
}