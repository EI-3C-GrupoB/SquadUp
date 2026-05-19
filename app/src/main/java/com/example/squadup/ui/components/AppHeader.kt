package com.example.squadup.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    title: String? = null,
    showLogo: Boolean = true,
    showBackButton: Boolean = false,
    showLoginButton: Boolean = false,
    showNotificationsButton: Boolean = false,
    showSettingsButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onLoginClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            if (showLoginButton) {
                TextButton(onClick = onLoginClick) {
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
                        tint = SquadIconSecondary
                    )
                }
            }

            if (showSettingsButton) {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Definições",
                        tint = SquadIconSecondary
                    )
                }
            }

            actions()
        }
    }
}