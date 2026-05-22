package com.example.squadup.ui.screens.notifications

import android.inputmethodservice.Keyboard
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.NotificationImageCard
import com.example.squadup.ui.components.NotificationListItem
import com.example.squadup.ui.components.NotificationPrimaryCard
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun NotificationsScreen(
    onSettingsClick: () -> Unit,
    onGetDirectionsClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onGoBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SquadBackground)
    ) {
        AppHeader(
            showLogo = true,
            showBackButton = false,
            actions = {
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notificações",
                        tint = SquadOrange
                    )
                }

                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Definições",
                        tint = SquadTextSecondary
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Notifications",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = "Stay updated on your squad activities",
                fontSize = 14.sp,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Today",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "4 NEW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = SquadOrangeDark,
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            NotificationPrimaryCard(
                title = "Match Reminder",
                description = "Your game The Mavericks vs red Eagles starts at 6:30 PM. Don’t forget your jersey!",
                time = "2h ago",
                onPrimaryClick = onGetDirectionsClick,
                onDetailsClick = onDetailsClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            NotificationImageCard(
                title = "New Event Nearby",
                description = "Open Game: Sunday Morning Scrimmage at Viana do Castelo. Join 12\nothers!",
                distance = "0.8 miles"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Earlier",
                fontSize = 16.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SquadOrangeLight)
            ) {
                Column {
                    NotificationListItem(
                        title = "Waiting List Update",
                        description = "A spot opened up for the Summer League. Claim it now!",
                        time = "Yesterday",
                        icon = Icons.Outlined.Info,
                        iconBackground = SquadOrangeLight,
                        iconColor = SquadOrange
                    )

                    HorizontalDivider(color = Color(0x00000000))

                    NotificationListItem(
                        title = "System Update",
                        description = "SquadUp v2.4 is live. Improved team chat and match stats are now available.",
                        time = "2 days ago",
                        icon = Icons.Outlined.Update,
                        iconBackground = Color(0xFFEDEDED),
                        iconColor = SquadTextSecondary
                    )

                    HorizontalDivider(color = Color(0x00000000))

                    NotificationListItem(
                        title = "New Team Members",
                        description = "Kobbie Mainoo and 2 others joined your squad ”Midnight Hoops”.",
                        time = "Oct 12",
                        icon = Icons.Outlined.Groups,
                        iconBackground = Color(0xFFE0F2F1),
                        iconColor = Color(0xFF00897B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(26.dp))

            PrimaryButton(
                text = "Go back",
                onClick = onGoBackClick
            )

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreen(
        onSettingsClick = {},
        onGetDirectionsClick = {},
        onDetailsClick = {},
        onGoBackClick = {}
    )
}