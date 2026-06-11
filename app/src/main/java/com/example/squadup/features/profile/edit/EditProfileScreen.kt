package com.example.squadup.features.profile.edit

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.components.AuthTextField
import com.example.squadup.core.ui.components.PrimaryButton
import com.example.squadup.core.ui.components.ProfileDropdownField
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
import com.example.squadup.core.ui.theme.SquadError
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeDark
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadWhite
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.toDisplayName

import com.example.squadup.core.ui.components.LocationPickerDialog
import com.example.squadup.core.ui.components.SelectedLocation

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
    onLocationChange: (SelectedLocation?) -> Unit,
    onShowLocationPickerChange: (Boolean) -> Unit,
    onPlayStyleChange: (PlayStyle) -> Unit,
    onSportToggle: (SportType) -> Unit,
    onSaveChangesClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onBackClick: () -> Unit,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    val playStyleOptions = PlayStyle.entries.map { it to stringResource(it.labelRes) }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = "Edit Profile",
                showBackButton = true,
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick,
                showNotificationsButton = true,
                showSettingsButton = true,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        },
        bottomBar = {
            AppNavBar(
                selectedRoute = selectedRoute,
                onItemClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = responsiveHorizontalPadding(24.dp))
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    AuthTextField(
                        value = uiState.name,
                        onValueChange = onNameChange,
                        label = stringResource(R.string.editProfile_name_label),
                        placeholder = stringResource(R.string.editProfile_name_placeholder),
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    AuthTextField(
                        value = uiState.username,
                        onValueChange = onUsernameChange,
                        label = stringResource(R.string.editProfile_username_label),
                        placeholder = stringResource(R.string.editProfile_username_placeholder),
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LocationSelectorField(
                        address = uiState.location?.address,
                        onClick = { onShowLocationPickerChange(true) }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ProfileDropdownField(
                        label = stringResource(R.string.editProfile_play_style_label),
                        selectedValue = stringResource(uiState.selectedPlayStyle.labelRes),
                        options = playStyleOptions.map { it.second },
                        onValueSelected = { selected ->
                            val playStyle = playStyleOptions.first { it.second == selected }.first
                            onPlayStyleChange(playStyle)
                        },
                        labelColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            PreferredSportsSection(
                selectedSports = uiState.selectedSports.toSet(),
                onSportToggle = onSportToggle
            )

            Spacer(modifier = Modifier.height(28.dp))

            PrimaryButton(
                text = stringResource(R.string.editProfile_save),
                onClick = onSaveChangesClick,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onDeleteAccountClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = SquadError
                ),
                border = BorderStroke(1.dp, SquadError),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = stringResource(R.string.editProfile_delete),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

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

@Composable
private fun LocationSelectorField(
    address: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.editProfile_location_label),
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
                .background(MaterialTheme.colorScheme.surface)
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
                    text = address ?: stringResource(R.string.editProfile_location_placeholder),
                    color = if (address != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PreferredSportsSection(
    selectedSports: Set<SportType>,
    onSportToggle: (SportType) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.editProfile_sport_label),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = 3
            ) {
                SportType.entries.forEach { sport ->
                    PreferredSportChip(
                        text = sport.toDisplayName(context),
                        selected = sport in selectedSports,
                        onClick = { onSportToggle(sport) },
                        modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(6.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun PreferredSportChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(46.dp),
        color = if (selected) SquadOrangeLight else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            width = 1.2.dp,
            color = if (selected) SquadOrange else Color(0xFFDADADA)
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(18.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) SquadOrangeDark else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
