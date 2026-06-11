package com.example.squadup.features.auth.register

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.Cake
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.UserRole
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AuthCard
import com.example.squadup.core.ui.components.AuthTextField
import com.example.squadup.core.ui.components.DateTimePickerMode
import com.example.squadup.core.ui.components.LocationPickerDialog
import com.example.squadup.core.ui.components.PrimaryButton
import com.example.squadup.core.ui.components.ProfileDropdownField
import com.example.squadup.core.ui.components.responsiveContentWidth
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
import com.example.squadup.core.ui.components.responsiveVerticalSpacing
import com.example.squadup.core.ui.components.SelectedLocation
import com.example.squadup.core.ui.components.SquadDateTimePickerField
import com.example.squadup.core.ui.theme.SquadError
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadWhite
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    uiState: RegisterUiState,
    onFullNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAccountTypeChange: (UserRole) -> Unit,
    onModalityToggle: (String) -> Unit,
    onLocationChange: (SelectedLocation?) -> Unit,
    onPlayStyleChange: (PlayStyle) -> Unit,
    onNotificationRadiusChange: (Int) -> Unit,
    onShowLocationPickerChange: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val cardTopSpacing = responsiveVerticalSpacing(30.dp)

    val accountTypeOptions = listOf(
        UserRole.PLAYER to stringResource(R.string.register_user_type_player),
        UserRole.ORGANIZER to stringResource(R.string.register_user_type_organizer),
        UserRole.PLAYER_ORGANIZER to stringResource(R.string.register_user_type_player_organizer)
    )

    val playStyleLabel = stringResource(uiState.playStyle.labelRes)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .responsiveContentWidth(760.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(cardTopSpacing))

            AuthCard(modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(20.dp))) {
                Text(
                    text = stringResource(R.string.register_title),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.register_subtitle),
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Full name ─────────────────────────────────────────────────
                AuthTextField(
                    value = uiState.fullName,
                    onValueChange = onFullNameChange,
                    label = stringResource(R.string.register_full_name_label),
                    placeholder = stringResource(R.string.register_full_name_placeholder),
                    leadingIcon = Icons.Outlined.Person
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Username ──────────────────────────────────────────────────
                AuthTextField(
                    value = uiState.username,
                    onValueChange = onUsernameChange,
                    label = stringResource(R.string.register_username_label),
                    placeholder = stringResource(R.string.register_username_placeholder),
                    leadingIcon = Icons.Outlined.AlternateEmail
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Email ─────────────────────────────────────────────────────
                AuthTextField(
                    value = uiState.email,
                    onValueChange = onEmailChange,
                    label = stringResource(R.string.register_email_label),
                    placeholder = stringResource(R.string.register_email_placeholder),
                    leadingIcon = Icons.Outlined.Email
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Date of birth ─────────────────────────────────────────────
                SquadDateTimePickerField(
                    value = uiState.birthDate,
                    onValueChange = onBirthDateChange,
                    label = stringResource(R.string.register_birth_date_label),
                    placeholder = "YYYY-MM-DD",
                    mode = DateTimePickerMode.DATE,
                    leadingIcon = Icons.Outlined.Cake
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Location ──────────────────────────────────────────────────
                LocationField(
                    address = uiState.location?.address,
                    onClick = { onShowLocationPickerChange(true) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Account type (3 options via dropdown) ─────────────────────
                ProfileDropdownField(
                    label = stringResource(R.string.register_user_type_label),
                    selectedValue = accountTypeOptions.first { it.first == uiState.accountType }.second,
                    options = accountTypeOptions.map { it.second },
                    onValueSelected = { selected ->
                        accountTypeOptions
                            .firstOrNull { it.second == selected }
                            ?.let { onAccountTypeChange(it.first) }
                    },
                    labelColor = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Play style ────────────────────────────────────────────────
                PlayStyleSlider(
                    playStyle = uiState.playStyle,
                    label = playStyleLabel,
                    onPlayStyleChange = onPlayStyleChange
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Notification radius ───────────────────────────────────────
                NotificationRadiusSlider(
                    radius = uiState.notificationRadius,
                    onRadiusChange = onNotificationRadiusChange
                )

                Spacer(modifier = Modifier.height(14.dp))

                // ── Password ──────────────────────────────────────────────────
                AuthTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChange,
                    label = stringResource(R.string.register_password_label),
                    placeholder = stringResource(R.string.register_password_placeholder),
                    leadingIcon = Icons.Outlined.Lock,
                    isPassword = true
                )

                // ── Sports interests ──────────────────────────────────────────
                if (uiState.modalities.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(22.dp))

                    Text(
                        text = stringResource(R.string.register_sports_interests),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
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

                // ── Error ─────────────────────────────────────────────────────
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

    // ── Location picker dialog ────────────────────────────────────────────────
    if (uiState.showLocationPicker) {
        LocationPickerDialog(
            onLocationSelected = { location ->
                onLocationChange(location)
                onShowLocationPickerChange(false)
            },
            onDismiss = { onShowLocationPickerChange(false) }
        )
    }
}

// ── Location field ────────────────────────────────────────────────────────────

@Composable
private fun LocationField(
    address: String?,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.register_location_label),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, SquadGrayLight, RoundedCornerShape(12.dp))
                .background(SquadWhite)
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = address ?: stringResource(R.string.register_location_placeholder),
                    color = if (address != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// ── Play style slider ─────────────────────────────────────────────────────────

@Composable
private fun PlayStyleSlider(
    playStyle: PlayStyle,
    label: String,
    onPlayStyleChange: (PlayStyle) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.register_play_style_label),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrange
            )
        }

        Slider(
            value = playStyle.level.toFloat(),
            onValueChange = { value ->
                onPlayStyleChange(PlayStyle.fromLevel(value.roundToInt()))
            },
            steps = 1,           // 3 snap positions: LOW(1), MEDIUM(2), HIGH(3)
            valueRange = 1f..3f,
            colors = SliderDefaults.colors(
                thumbColor = SquadOrange,
                activeTrackColor = SquadOrange,
                inactiveTrackColor = SquadOrangeLight,
                activeTickColor = SquadOrange,
                inactiveTickColor = SquadOrangeLight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(PlayStyle.LOW.labelRes),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(PlayStyle.HIGH.labelRes),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Notification radius slider ─────────────────────────────────────────────────

@Composable
private fun NotificationRadiusSlider(
    radius: Int,
    onRadiusChange: (Int) -> Unit
) {
    val unit = stringResource(R.string.register_notification_radius_unit)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.register_notification_radius_label),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$radius $unit",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrange
            )
        }

        Slider(
            value = radius.toFloat(),
            onValueChange = { onRadiusChange(it.roundToInt()) },
            valueRange = 5f..100f,
            colors = SliderDefaults.colors(
                thumbColor = SquadOrange,
                activeTrackColor = SquadOrange,
                inactiveTrackColor = SquadOrangeLight,
                activeTickColor = SquadOrange,
                inactiveTickColor = SquadOrangeLight
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "5 $unit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = "100 $unit", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
