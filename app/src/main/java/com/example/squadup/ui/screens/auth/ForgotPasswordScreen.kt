package com.example.squadup.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AuthCard
import com.example.squadup.ui.components.AuthTextField
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.theme.SquadBorder
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun ForgotPasswordScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    onSendResetLinkClick: () -> Unit,
    onBackToLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
    ) {
        AppHeader(
            showLogo = true,
            showBackButton = false
        )

        Spacer(modifier = Modifier.height(140.dp))

        AuthCard(
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Forgot Password?",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Enter your email address and we'll send you a link to reset your password.",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = SquadIconSecondary
            )

            Spacer(modifier = Modifier.height(30.dp))

            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Recovery Email",
                placeholder = "name@example.com",
                trailingIcon = Icons.Outlined.Email
            )

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "SEND RESET LINK",
                onClick = onSendResetLinkClick
            )

            Spacer(modifier = Modifier.height(30.dp))

            HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = 1.dp,
                color = SquadBorder
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "← Back to Login",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(onClick = onBackToLoginClick),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadOrange
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordScreenPreview() {
    var email by remember { mutableStateOf("") }

    ForgotPasswordScreen(
        email = email,
        onEmailChange = { email = it },
        onSendResetLinkClick = {},
        onBackToLoginClick = {}
    )
}