package com.example.squadup.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadSurface
import com.example.squadup.core.ui.theme.SquadTextSecondary
import com.example.squadup.core.ui.theme.SquadWhite
import com.example.squadup.core.utils.AppLanguage

@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    title: String? = null,
    showLogo: Boolean = true,
    showBackButton: Boolean = false,
    showLoginButton: Boolean = false,
    showLanguageSwitch: Boolean = false,
    showNotificationsButton: Boolean = false,
    notificationsCount: Int = 0,
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
    onAdminPageClick: () -> Unit = {},
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
            onAdminViewChange = onAdminViewChange,
            onAdminPageClick = onAdminPageClick
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 1.dp
    ) {
        Column {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
            )

            androidx.compose.foundation.layout.Row(
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

                    Spacer(modifier = Modifier.width(4.dp))
                }

                if (showLogo) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_squadup),
                        contentDescription = "SquadUp logo",
                        modifier = Modifier.height(32.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))
                }

                Text(
                    text = title ?: "SquadUp",
                    color = SquadOrange,
                    fontSize = 20.sp,
                    fontWeight = if (showLogo) FontWeight.ExtraBold else FontWeight.Bold
                )

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
                        Box {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "Notificações",
                                tint = SquadTextSecondary
                            )
                            
                            if (notificationsCount > 0) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .align(Alignment.TopEnd)
                                        .background(SquadOrange, CircleShape)
                                        .padding(1.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (notificationsCount > 9) "9+" else notificationsCount.toString(),
                                        color = SquadWhite,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
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
