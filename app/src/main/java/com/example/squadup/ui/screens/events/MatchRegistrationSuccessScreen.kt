package com.example.squadup.ui.screens.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadWhite

@Composable
fun MatchRegistrationSuccessScreen(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onAddToCalendarClick: () -> Unit,
    onInviteFriendsClick: () -> Unit,
    onViewTicketsClick: () -> Unit,
    onBackToHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MatchRegistrationSuccessHeader(
                title = "Match Registration",
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            SuccessIcon()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Successfully Joined",
                fontSize = 24.sp,
                lineHeight = 28.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "You’re on the roster! Get ready to\nbring your A-game to the court.",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            JoinedMatchCard()

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = onAddToCalendarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = SquadWhite
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Add to Calendar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onInviteFriendsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    width = 1.4.dp,
                    color = SquadTextPrimary
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SquadSurface,
                    contentColor = SquadTextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.PersonAdd,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Invite Friends",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "View My Tickets",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrangeDark,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable(onClick = onViewTicketsClick)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.clickable(onClick = onBackToHomeClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Home,
                    contentDescription = null,
                    tint = SquadTextSecondary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = "Back to Home",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}

@Composable
private fun MatchRegistrationSuccessHeader(
    title: String,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Voltar",
                tint = SquadOrange,
                modifier = Modifier
                    .size(20.dp)
                    .clickable(onClick = onBackClick)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrangeDark,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificações",
                    tint = SquadGrayDark,
                    modifier = Modifier.size(21.dp)
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Definições",
                    tint = SquadGrayDark,
                    modifier = Modifier.size(21.dp)
                )
            }
        }
    }
}

@Composable
private fun SuccessIcon() {
    Box(
        modifier = Modifier
            .size(96.dp)
            .shadow(
                elevation = 14.dp,
                shape = CircleShape,
                ambientColor = SquadOrange.copy(alpha = 0.30f),
                spotColor = SquadOrange.copy(alpha = 0.30f)
            )
            .background(SquadOrange, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.EmojiEvents,
            contentDescription = null,
            tint = SquadWhite,
            modifier = Modifier.size(52.dp)
        )
    }
}

@Composable
private fun JoinedMatchCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SquadOrangeLight)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Basketball • 3v3",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextSecondary,
                    modifier = Modifier
                        .background(
                            color = SquadGrayLight,
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(SquadOrangeDark, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = SquadWhite,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "City 3x3 Showdown",
                fontSize = 19.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = SquadGrayLight)

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                MatchInfoBlock(
                    icon = Icons.Outlined.CalendarMonth,
                    label = "Date",
                    value = "Oct 24, 2023",
                    modifier = Modifier.weight(1f)
                )

                MatchInfoBlock(
                    icon = Icons.Outlined.Timer,
                    label = "Time",
                    value = "6:00 PM",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = SquadGrayLight)

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = SquadTextPrimary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(7.dp))

                Text(
                    text = "Westside Community Courts, NY",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SquadTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarStack()

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "11/20 Spots Filled",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrangeDark
                )
            }
        }
    }
}

@Composable
private fun MatchInfoBlock(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SquadTextPrimary,
            modifier = Modifier.size(19.dp)
        )

        Spacer(modifier = Modifier.width(7.dp))

        Column {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextSecondary
            )

            Text(
                text = value,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )
        }
    }
}

@Composable
private fun AvatarStack() {
    Row(
        horizontalArrangement = Arrangement.spacedBy((-7).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .size(25.dp)
                    .background(
                        color = when (index) {
                            0 -> Color(0xFF30424A)
                            1 -> Color(0xFF394B58)
                            else -> Color(0xFF1F2937)
                        },
                        shape = CircleShape
                    )
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = null,
                    tint = SquadWhite,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(25.dp)
                .background(SquadGrayLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+8",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextSecondary
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=360dp,height=740dp,dpi=440"
)
@Composable
fun MatchRegistrationSuccessScreenPreview() {
    MatchRegistrationSuccessScreen(
        onBackClick = {},
        onNotificationsClick = {},
        onSettingsClick = {},
        onAddToCalendarClick = {},
        onInviteFriendsClick = {},
        onViewTicketsClick = {},
        onBackToHomeClick = {}
    )
}