package com.example.squadup.ui.screens.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
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

data class RegisteredTeamUi(
    val initials: String,
    val name: String,
    val players: String
)

@Composable
fun TeamTournamentDetailsScreen(
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onJoinWithTeamClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val registeredTeams = listOf(
        RegisteredTeamUi("BH", "Brooklyn Hoops", "4 Players"),
        RegisteredTeamUi("ST", "Skyline Titans", "3 Players"),
        RegisteredTeamUi("NC", "Neon Cobras", "4 Players")
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            AppHeader(
                showLogo = true,
                title = "SquadUp",
                showBackButton = false,
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificações",
                            tint = SquadGrayDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Definições",
                            tint = SquadGrayDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            TournamentHero()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                VenueCard()

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SmallInfoCard(
                        icon = Icons.Outlined.CalendarMonth,
                        title = "Oct 24",
                        subtitle = "09:00 AM",
                        modifier = Modifier.weight(1f)
                    )

                    SmallInfoCard(
                        icon = Icons.Outlined.Payments,
                        title = "$45.00",
                        subtitle = "Per Team",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                BracketProgressCard()

                Spacer(modifier = Modifier.height(16.dp))

                RequirementsCard()

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "REGISTERED TEAMS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.6.sp,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                RegisteredTeamsCard(
                    teams = registeredTeams
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onJoinWithTeamClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = SquadWhite
                    )
                ) {
                    Text(
                        text = "J O I N   W I T H   T E A M",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.6.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TournamentHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF060606),
                        Color(0xFF101820),
                        Color(0xFF0A0A0A)
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.SportsBasketball,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.20f),
            modifier = Modifier
                .align(Alignment.Center)
                .size(122.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .width(72.dp)
                .height(92.dp)
                .background(
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp)
                )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.78f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeroBadge(
                    text = "BASKETBALL",
                    background = SquadOrange,
                    content = SquadWhite
                )

                HeroBadge(
                    text = "TOURNAMENT",
                    background = Color(0xFF00897B),
                    content = SquadWhite
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "City 3x3\nShowdown",
                fontSize = 28.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold,
                color = SquadWhite
            )
        }
    }
}

@Composable
private fun HeroBadge(
    text: String,
    background: Color,
    content: Color
) {
    Text(
        text = text,
        fontSize = 9.sp,
        fontWeight = FontWeight.Black,
        color = content,
        modifier = Modifier
            .background(background, RoundedCornerShape(999.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp)
    )
}

@Composable
private fun VenueCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SquadOrangeLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(SquadOrangeLight, RoundedCornerShape(9.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = SquadOrange,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Madison Square Park",
                        fontSize = 17.sp,
                        lineHeight = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary
                    )

                    Text(
                        text = "Manhattan, NY",
                        fontSize = 12.sp,
                        color = SquadTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            MiniMap()
        }
    }
}

@Composable
private fun MiniMap() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF2C3137),
                        Color(0xFFD9D9D9),
                        Color(0xFF3C3C44)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
    )
}

@Composable
private fun SmallInfoCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(96.dp),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SquadOrangeLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SquadOrange,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = SquadTextSecondary
            )
        }
    }
}

@Composable
private fun BracketProgressCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SquadOrangeLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tournament Bracket",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "5 / 8 Teams",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrangeDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { 0.625f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = SquadOrangeDark,
                trackColor = SquadGrayLight,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Only 3 spots remaining for registration!",
                fontSize = 10.sp,
                color = SquadTextPrimary
            )
        }
    }
}

@Composable
private fun RequirementsCard() {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.WarningAmber,
                contentDescription = null,
                tint = SquadOrangeDark,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Requirements",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(74.dp)
                    .background(SquadOrangeDark)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "You must join with a registered team.\nIndividual registrations are not available for\nthis tournament format.",
                fontSize = 12.sp,
                lineHeight = 17.sp,
                color = SquadTextPrimary
            )
        }
    }
}

@Composable
private fun RegisteredTeamsCard(
    teams: List<RegisteredTeamUi>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SquadOrangeLight)
    ) {
        Column {
            teams.forEachIndexed { index, team ->
                RegisteredTeamRow(team = team)

                if (index < teams.lastIndex) {
                    HorizontalDivider(
                        color = SquadGrayLight,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisteredTeamRow(
    team: RegisteredTeamUi
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(Color(0xFFF8F8F8), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = team.initials,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextSecondary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = team.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = team.players,
                fontSize = 11.sp,
                color = SquadTextSecondary
            )
        }

        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF00897B),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TeamTournamentDetailsScreenPreview() {
    TeamTournamentDetailsScreen(
        onNotificationsClick = {},
        onSettingsClick = {},
        onJoinWithTeamClick = {}
    )
}