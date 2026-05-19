package com.example.squadup.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.SportsTennis
import androidx.compose.material.icons.outlined.SportsVolleyball
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AuthCard
import com.example.squadup.ui.components.AuthTextField
import com.example.squadup.ui.components.ExperienceLevelSelector
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.components.SportInterestChip
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    fullName: String,
    username: String,
    email: String,
    password: String,
    experienceLevel: Float,
    selectedSports: List<String>,
    onFullNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onExperienceLevelChange: (Float) -> Unit,
    onSportToggle: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .verticalScroll(rememberScrollState())
    ) {
        AppHeader(
            showLogo = true,
            showBackButton = false,
            actions = {
                Text(
                    text = "↪ Login",
                    fontSize = 16.sp,
                    color = SquadOrange,
                    modifier = Modifier.clickable(onClick = onLoginClick)
                )
            }
        )

        Spacer(modifier = Modifier.height(30.dp))

        AuthCard(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Text(
                text = "Register Profile",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Step into the arena. It takes less than a minute.",
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = SquadIconSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            AuthTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = "Full Name",
                placeholder = "Alex Hunter",
                leadingIcon = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = "Username",
                placeholder = "Alex Hunter",
                leadingIcon = Icons.Outlined.AlternateEmail
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = "Email Address",
                placeholder = "AlexH@example.com",
                leadingIcon = Icons.Outlined.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = "Password",
                placeholder = "••••••••",
                leadingIcon = Icons.Outlined.Lock,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(22.dp))

            ExperienceLevelSelector(
                value = experienceLevel,
                onValueChange = onExperienceLevelChange
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sports Interests",
                fontSize = 16.sp,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SportInterestChip(
                    text = "Soccer",
                    selected = selectedSports.contains("Soccer"),
                    icon = Icons.Outlined.SportsSoccer,
                    onClick = { onSportToggle("Soccer") }
                )

                SportInterestChip(
                    text = "Basketball",
                    selected = selectedSports.contains("Basketball"),
                    icon = Icons.Outlined.SportsBasketball,
                    onClick = { onSportToggle("Basketball") }
                )

                SportInterestChip(
                    text = "Paddle",
                    selected = selectedSports.contains("Paddle"),
                    icon = Icons.Outlined.SportsTennis,
                    onClick = { onSportToggle("Paddle") }
                )

                SportInterestChip(
                    text = "Volleyball",
                    selected = selectedSports.contains("Volleyball"),
                    icon = Icons.Outlined.SportsVolleyball,
                    onClick = { onSportToggle("Volleyball") }
                )

                SportInterestChip(
                    text = "Futsal",
                    selected = selectedSports.contains("Futsal"),
                    icon = Icons.Outlined.SportsSoccer,
                    onClick = { onSportToggle("Futsal") }
                )
            }

            Spacer(modifier = Modifier.height(34.dp))

            PrimaryButton(
                text = "SQUAD UP",
                onClick = onRegisterClick,
                trailingIcon = Icons.Default.RocketLaunch
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = buildAnnotatedString {
                    append("By registering, you agree to our ")

                    withStyle(
                        SpanStyle(
                            color = SquadOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("Terms\nof Service")
                    }

                    append(" and ")

                    withStyle(
                        SpanStyle(
                            color = SquadOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append("Privacy Policy.")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SquadIconSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var experienceLevel by remember { mutableFloatStateOf(1f) }

    val selectedSports = remember {
        mutableStateListOf("Soccer", "Basketball")
    }

    RegisterScreen(
        fullName = fullName,
        username = username,
        email = email,
        password = password,
        experienceLevel = experienceLevel,
        selectedSports = selectedSports,
        onFullNameChange = { fullName = it },
        onUsernameChange = { username = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onExperienceLevelChange = { experienceLevel = it },
        onSportToggle = { sport ->
            if (selectedSports.contains(sport)) {
                selectedSports.remove(sport)
            } else {
                selectedSports.add(sport)
            }
        },
        onRegisterClick = {},
        onLoginClick = {},
        onTermsClick = {},
        onPrivacyClick = {}
    )
}