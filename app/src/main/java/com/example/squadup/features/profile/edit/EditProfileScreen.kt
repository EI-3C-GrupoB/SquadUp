package com.example.squadup.features.profile.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.PlayStyle
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.*
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.toDisplayName

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    uiState: EditProfileUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onUsernameChange: (String) -> Unit,
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
    val context = LocalContext.current
    val playStyleOptions = PlayStyle.entries.map { it to stringResource(it.labelRes) }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = stringResource(R.string.profile_title),
                showBackButton = true,
                onBackClick = onBackClick,
                showNotificationsButton = true,
                showSettingsButton = true,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        },
        bottomBar = {
            AppNavBar(selectedRoute = selectedRoute, onItemClick = onNavItemClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(color = SquadOrange)
                Spacer(modifier = Modifier.height(16.dp))
            }

            uiState.errorMessage?.let { errorMessage ->
                Text(
                    text = stringResource(errorMessage),
                    color = SquadError,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Username Field
            AuthTextField(
                value = uiState.username,
                onValueChange = onUsernameChange,
                label = stringResource(R.string.editProfile_username_label),
                placeholder = stringResource(R.string.editProfile_username_placeholder),
                labelColor = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Play Style Field
            ProfileDropdownField(
                label = stringResource(R.string.editProfile_play_style_label),
                selectedValue = stringResource(uiState.selectedPlayStyle.labelRes),
                options = playStyleOptions.map { it.second },
                onValueSelected = { selected ->
                    val playStyle = playStyleOptions.first { it.second == selected }.first
                    onPlayStyleChange(playStyle)
                },
                labelColor = Color(0xFF757575) // Grayish label
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Preferred Sports Section
            Text(
                text = stringResource(R.string.editProfile_sport_label),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF757575), // Grayish label
                modifier = Modifier.padding(start = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SportType.entries.forEach { sport ->
                    val selected = sport in uiState.selectedSports
                    Surface(
                        onClick = { onSportToggle(sport) },
                        shape = RoundedCornerShape(10.dp),
                        color = if (selected) SquadOrangeLight else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (selected) SquadOrange else Color(0xFFE0E0E0)
                        ),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = sport.toDisplayName(context),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = if (selected) SquadOrangeDark else Color(0xFF757575)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

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
                border = androidx.compose.foundation.BorderStroke(1.dp, SquadError),
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
}
