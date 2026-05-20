package com.example.squadup.ui.screens.auth.register

import androidx.annotation.StringRes
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
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.SportsTennis
import androidx.compose.material.icons.outlined.SportsVolleyball
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.data.models.ModalityModel
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppLanguage
import com.example.squadup.ui.components.AuthCard
import com.example.squadup.ui.components.AuthTextField
import com.example.squadup.ui.components.ExperienceLevelSelector
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.components.SportInterestChip
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    fullName: String,
    username: String,
    email: String,
    birthDate: String,
    password: String,
    experienceLevel: Float,
    availableModalities: List<ModalityModel>,
    selectedSports: List<String>,
    @StringRes errorMessageRes: Int?,
    isLoading: Boolean,
    onFullNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onExperienceLevelChange: (Float) -> Unit,
    onSportToggle: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        val confirmEnabled = remember {
            derivedStateOf { datePickerState.selectedDateMillis != null }
        }

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            onBirthDateChange(formatter.format(Date(millis)))
                        }
                        showDatePicker = false
                    },
                    enabled = confirmEnabled.value
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SquadBackground)
            .verticalScroll(rememberScrollState())
    ) {
        AppHeader(
            showLogo = true,
            showBackButton = false,
            actions = {
                Row(
                    modifier = Modifier.clickable(
                        enabled = !isLoading,
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

        AuthCard(
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
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
                value = fullName,
                onValueChange = onFullNameChange,
                label = stringResource(R.string.register_full_name_label),
                placeholder = stringResource(R.string.register_full_name_placeholder),
                leadingIcon = Icons.Outlined.Person
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = stringResource(R.string.register_username_label),
                placeholder = stringResource(R.string.register_username_placeholder),
                leadingIcon = Icons.Outlined.AlternateEmail
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = email,
                onValueChange = onEmailChange,
                label = stringResource(R.string.register_email_label),
                placeholder = stringResource(R.string.register_email_placeholder),
                leadingIcon = Icons.Outlined.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = birthDate,
                onValueChange = { /* Não editável diretamente */ },
                label = stringResource(R.string.register_birth_date_label),
                placeholder = "YYYY-MM-DD",
                leadingIcon = Icons.Outlined.Cake,
                modifier = Modifier.clickable { showDatePicker = true },
                enabled = false // Desativamos o teclado mas mantemos o clique via modifier
            )

            Spacer(modifier = Modifier.height(14.dp))

            AuthTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(R.string.register_password_label),
                placeholder = stringResource(R.string.register_password_placeholder),
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
                text = stringResource(R.string.register_sports_interests),
                fontSize = 16.sp,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableModalities.forEach { modality ->
                    val icon = when (modality.nome) {
                        "Soccer" -> Icons.Outlined.SportsSoccer
                        "Basketball" -> Icons.Outlined.SportsBasketball
                        "Paddle" -> Icons.Outlined.SportsTennis
                        "Volleyball" -> Icons.Outlined.SportsVolleyball
                        "Futsal" -> Icons.Outlined.SportsSoccer
                        else -> Icons.Outlined.SportsSoccer
                    }

                    val displayName = when (modality.nome) {
                        "Soccer" -> stringResource(R.string.register_sport_soccer)
                        "Basketball" -> stringResource(R.string.register_sport_basketball)
                        "Paddle" -> stringResource(R.string.register_sport_paddle)
                        "Volleyball" -> stringResource(R.string.register_sport_volleyball)
                        "Futsal" -> stringResource(R.string.register_sport_futsal)
                        else -> modality.nome
                    }

                    SportInterestChip(
                        text = displayName,
                        selected = selectedSports.contains(modality.nome),
                        icon = icon,
                        onClick = { onSportToggle(modality.nome) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (errorMessageRes != null) {
                Text(
                    text = stringResource(id = errorMessageRes),
                    color = SquadOrange,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            PrimaryButton(
                text = if (isLoading) stringResource(R.string.register_button_loading) else stringResource(R.string.register_button),
                onClick = {
                    if (!isLoading) {
                        onRegisterClick()
                    }
                },
                trailingIcon = Icons.Outlined.SportsSoccer
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.register_terms_prefix))
                    append(" ") // Espaço garantido

                    withStyle(
                        SpanStyle(
                            color = SquadOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(stringResource(R.string.register_terms))
                    }

                    append(" ") // Espaço antes do "e" / "and"
                    append(stringResource(R.string.register_terms_middle))
                    append(" ") // Espaço depois do "e" / "and"

                    withStyle(
                        SpanStyle(
                            color = SquadOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    ) {
                        append(stringResource(R.string.register_privacy))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SquadTextSecondary
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
    var birthDate by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var experienceLevel by remember { mutableFloatStateOf(1f) }
    var selectedLanguage by remember { mutableStateOf(AppLanguage.PT) }

    val selectedSports = remember {
        mutableStateListOf("Soccer", "Basketball")
    }

    RegisterScreen(
        fullName = fullName,
        username = username,
        email = email,
        birthDate = birthDate,
        password = password,
        experienceLevel = experienceLevel,
        availableModalities = listOf(
            ModalityModel(1, "Soccer"),
            ModalityModel(2, "Basketball")
        ),
        selectedSports = selectedSports,
        errorMessageRes = null,
        isLoading = false,
        onFullNameChange = { fullName = it },
        onUsernameChange = { username = it },
        onEmailChange = { email = it },
        onBirthDateChange = { birthDate = it },
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
