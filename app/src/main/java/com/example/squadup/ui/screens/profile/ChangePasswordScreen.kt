package com.example.squadup.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun ChangePasswordScreen(
    currentPassword: String,
    newPassword: String,
    confirmNewPassword: String,
    selectedRoute: String,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmNewPasswordChange: (String) -> Unit,
    onChangePasswordClick: () -> Unit,
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
                            tint = Color(0x00000000)
                        )
                    }

                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Definições",
                            tint = Color(0x00000000)
                        )
                    }
                }
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
                .background(SquadBackground)
                .padding(innerPadding)
                .padding(horizontal = 28.dp)
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            AuthCard {
                Text(
                    text = "Change Password",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "In order to change password, you\nmust first write your current\npassword and then write your new\none twice.",
                    fontSize = 16.sp,
                    lineHeight = 22.sp,
                    color = SquadTextSecondary
                )

                Spacer(modifier = Modifier.height(28.dp))

                AuthTextField(
                    value = currentPassword,
                    onValueChange = onCurrentPasswordChange,
                    label = "Current Password",
                    placeholder = "***************",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color(0x00000000)
                )

                Spacer(modifier = Modifier.height(16.dp))

                AuthTextField(
                    value = newPassword,
                    onValueChange = onNewPasswordChange,
                    label = "New Password",
                    placeholder = "***************",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                AuthTextField(
                    value = confirmNewPassword,
                    onValueChange = onConfirmNewPasswordChange,
                    label = "Confirm New Password",
                    placeholder = "***************",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(30.dp))

                PrimaryButton(
                    text = "Change Password",
                    onClick = onChangePasswordClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = Color(0x00000000)
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
fun ChangePasswordScreenPreview() {
    var currentPassword by remember { mutableStateOf("123456789012345") }
    var newPassword by remember { mutableStateOf("123456789012345") }
    var confirmNewPassword by remember { mutableStateOf("123456789012345") }

    ChangePasswordScreen(
        currentPassword = currentPassword,
        newPassword = newPassword,
        confirmNewPassword = confirmNewPassword,
        selectedRoute = "profile",
        onCurrentPasswordChange = { currentPassword = it },
        onNewPasswordChange = { newPassword = it },
        onConfirmNewPasswordChange = { confirmNewPassword = it },
        onChangePasswordClick = {},
        onBackToProfileClick = {},
        onNotificationsClick = {},
        onSettingsClick = {},
        onNavItemClick = {}
    )
}