package com.example.squadup.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadSurface
import com.example.squadup.core.ui.theme.SquadTextSecondary

@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    title: String? = null,
    showLogo: Boolean = true,
    showBackButton: Boolean = false,
    showLoginButton: Boolean = false,
    showLanguageSwitch: Boolean = false,
    showNotificationsButton: Boolean = false,
    showSettingsButton: Boolean = false,
    selectedLanguage: AppLanguage = AppLanguage.EN,
    isDarkMode: Boolean = false,
    isAdmin: Boolean = false,
    isAdminView: Boolean = false,
    onLanguageChange: (AppLanguage) -> Unit = {},
    onDarkModeChange: (Boolean) -> Unit = {},
    onAdminViewChange: (Boolean) -> Unit = {},
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    if (showSettingsDialog) {
        SettingsDialog(
            selectedLanguage = selectedLanguage,
            isDarkMode = isDarkMode,
            onLanguageChange = onLanguageChange,
            onDarkModeChange = onDarkModeChange,
            onDismiss = { showSettingsDialog = false },
            isAdmin = isAdmin,
            isAdminView = isAdminView,
            onAdminViewChange = onAdminViewChange
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 1.dp
    ) {
        Column {
            // Espaço para a status bar do Android
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showBackButton) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = SquadOrange
                        )
                    }
                }

                if (showLogo) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_squadup),
                        contentDescription = "SquadUp logo",
                        modifier = Modifier.height(32.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SquadUp",
                        color = SquadOrange,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                } else if (title != null) {
                    Text(
                        text = title,
                        color = SquadOrange,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                if (showLanguageSwitch) {
                    LanguageSwitch(
                        selectedLanguage = selectedLanguage,
                        onLanguageChange = onLanguageChange
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (showLoginButton) {
                    TextButton(onClick = onLoginClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Login,
                            contentDescription = null,
                            tint = SquadOrange,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Login",
                            color = SquadOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (showNotificationsButton) {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificações",
                            tint = SquadTextSecondary
                        )
                    }
                }

                if (showSettingsButton) {
                    IconButton(onClick = { showSettingsDialog = !showSettingsDialog }) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Definições",
                            tint = if (showSettingsDialog) SquadOrange else SquadTextSecondary
                        )
                    }
                }

                actions()
            }
        }
    }
}