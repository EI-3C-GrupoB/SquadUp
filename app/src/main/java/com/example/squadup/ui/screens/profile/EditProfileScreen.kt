package com.example.squadup.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
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
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.components.AuthCard
import com.example.squadup.ui.components.AuthTextField
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.components.ProfileDropdownField
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadBorder
import com.example.squadup.ui.theme.SquadError
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun EditProfileScreen(
    username: String,
    playStyle: String,
    preferredSport: String,
    selectedRoute: String,
    onUsernameChange: (String) -> Unit,
    onPlayStyleChange: (String) -> Unit,
    onPreferredSportChange: (String) -> Unit,
    onSaveChangesClick: () -> Unit,
    onDeleteAccountClick: () -> Unit,
    onBackToProfileClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavItemClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = "Profile",
                showBackButton = false,
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificações",
                            tint = SquadIconSecondary
                        )
                    }

                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Definições",
                            tint = SquadIconSecondary
                        )
                    }
                }
            )
        },
        bottomBar = {
            AppNavBar(
                selectedRoute = selectedRoute,
                onItemClick = onNavItemClick,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            AuthCard {
                Text(
                    text = "Edit Profile",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Here you can edit your username,\nyour play style ans preferred sport.\nYou can also delete your account.",
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = SquadTextSecondary
                )

                Spacer(modifier = Modifier.height(36.dp))

                AuthTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    label = "Change Username",
                    placeholder = "Username"
                )

                Spacer(modifier = Modifier.height(12.dp))

                ProfileDropdownField(
                    label = "Change Play style",
                    selectedValue = playStyle,
                    options = listOf(
                        "Low Energy",
                        "Balanced",
                        "High Energy"
                    ),
                    onValueSelected = onPlayStyleChange
                )

                Spacer(modifier = Modifier.height(20.dp))

                ProfileDropdownField(
                    label = "Change Preferred Sport",
                    selectedValue = preferredSport,
                    options = listOf(
                        "Football",
                        "Basketball",
                        "Tennis",
                        "Paddle",
                        "Volleyball",
                        "Futsal"
                    ),
                    onValueSelected = onPreferredSportChange
                )

                Spacer(modifier = Modifier.height(30.dp))

                OutlinedButton(
                    onClick = onDeleteAccountClick,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = SquadError
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = SquadError
                    )
                ) {
                    Text(
                        text = "Delete Account",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                PrimaryButton(
                    text = "Save Changes",
                    onClick = onSaveChangesClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = SquadBorder
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "← Back to Profile",
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable(onClick = onBackToProfileClick),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadOrange
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfileScreenPreview() {
    var username by remember { mutableStateOf("Alexandre Caçador") }
    var playStyle by remember { mutableStateOf("High Energy") }
    var preferredSport by remember { mutableStateOf("Football") }

    EditProfileScreen(
        username = username,
        playStyle = playStyle,
        preferredSport = preferredSport,
        selectedRoute = "profile",
        onUsernameChange = { username = it },
        onPlayStyleChange = { playStyle = it },
        onPreferredSportChange = { preferredSport = it },
        onSaveChangesClick = {},
        onDeleteAccountClick = {},
        onBackToProfileClick = {},
        onNotificationsClick = {},
        onSettingsClick = {},
        onNavItemClick = {}
    )
}