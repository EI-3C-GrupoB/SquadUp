package com.example.squadup.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.components.PlayStyleIntensityCard
import com.example.squadup.ui.components.ProfileActionRow
import com.example.squadup.ui.components.ProfileStatCard
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun ProfileScreen(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
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
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F8F8))
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            ProfileAvatar()

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Alex Hunter",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color(0xFFE53935)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Logout",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ProfileStatCard(
                    icon = Icons.Default.SportsSoccer,
                    value = "24",
                    label = "MATCHES",
                    modifier = Modifier.weight(1f)
                )

                ProfileStatCard(
                    icon = Icons.Default.EmojiEvents,
                    value = "08",
                    label = "WINS",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ProfileStatCard(
                    icon = Icons.Default.Star,
                    value = "14",
                    label = "GOALS",
                    modifier = Modifier.weight(1f)
                )

                ProfileStatCard(
                    icon = Icons.Default.Groups,
                    value = "2",
                    label = "TEAMS",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            PlayStyleIntensityCard(
                intensityLabel = "High Energy",
                progress = 0.85f,
                description = "You prefer matches with competitive difficulty and high physical output."
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileActionRow(
                text = "Edit Profile",
                icon = Icons.Default.Person,
                onClick = onEditProfileClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            ProfileActionRow(
                text = "Change Password",
                icon = Icons.Default.Lock,
                onClick = onChangePasswordClick
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileAvatar() {
    Box(
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_squadup),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(86.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(4.dp)
        )

        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(SquadOrange)
                .padding(6.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(
        selectedRoute = "profile",
        onNavItemClick = {},
        onNotificationsClick = {},
        onSettingsClick = {},
        onEditProfileClick = {},
        onChangePasswordClick = {},
        onLogoutClick = {}
    )
}