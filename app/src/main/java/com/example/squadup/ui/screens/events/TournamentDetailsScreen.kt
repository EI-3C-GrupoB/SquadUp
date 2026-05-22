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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.SportsHandball
import androidx.compose.material.icons.outlined.SportsVolleyball
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadError
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadWhite

@Composable
fun TournamentDetailsScreen(
    onBackClick: () -> Unit,
    onJoinIndividuallyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TournamentDetailsHeader(
                title = "Sand Pro Tour: Beach Open",
                onBackClick = onBackClick
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
                    .padding(horizontal = 14.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TournamentInfoCard(
                        title = "DATE",
                        value = "Oct 24, 2023",
                        icon = Icons.Outlined.CalendarMonth,
                        modifier = Modifier.weight(1f)
                    )

                    TournamentInfoCard(
                        title = "TIME",
                        value = "6:00 PM - 9:00 PM",
                        icon = Icons.Outlined.AccessTime,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                NoTeamNeededCard()

                Spacer(modifier = Modifier.height(18.dp))

                SectionTitle(
                    icon = Icons.Outlined.SportsHandball,
                    text = "Tournament Rules"
                )

                Spacer(modifier = Modifier.height(12.dp))

                TournamentRuleCard(
                    text = "Round robin format followed by single-elimination playoffs."
                )

                Spacer(modifier = Modifier.height(10.dp))

                TournamentRuleCard(
                    text = "Organizers assign teams based on skill level rankings to ensure balanced competition."
                )

                Spacer(modifier = Modifier.height(10.dp))

                TournamentRuleCard(
                    text = "Standard pro beach rules apply. Sets played to 21 points, cap at 25."
                )

                Spacer(modifier = Modifier.height(10.dp))

                TournamentRuleCard(
                    text = "Fair play is mandatory. Disrespectful conduct leads to immediate disqualification."
                )

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(
                    icon = Icons.Outlined.LocationOn,
                    text = "Venue"
                )

                Spacer(modifier = Modifier.height(12.dp))

                VenueMapCard()

                Spacer(modifier = Modifier.height(18.dp))

                RegistrationSummary()

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onJoinIndividuallyClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = SquadWhite
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SportsHandball,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Join Individually",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun TournamentDetailsHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Voltar",
                tint = SquadTextPrimary,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onBackClick)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrangeDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TournamentHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(178.dp)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF2A1308),
                        Color(0xFFE97828),
                        Color(0xFF151515)
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.SportsVolleyball,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.18f),
            modifier = Modifier
                .align(Alignment.Center)
                .size(112.dp)
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.72f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Text(
                text = "TOURNAMENT ENTRY",
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = SquadWhite,
                modifier = Modifier
                    .background(SquadOrange, RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sand Pro Tour: Beach Open",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SquadWhite
            )
        }
    }
}

@Composable
private fun TournamentInfoCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(86.dp),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SquadGrayLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = title,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 14.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary
            )
        }
    }
}

@Composable
private fun NoTeamNeededCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFF1EE),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SquadOrangeLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = null,
                    tint = SquadOrangeDark,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "No team needed",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrangeDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "In this tournament, participation is individual.\nThe teams will be formed by the event\norganizers after registration closes.",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = SquadTextSecondary
            )
        }
    }
}

@Composable
private fun SectionTitle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SquadOrangeDark,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )
    }
}

@Composable
private fun TournamentRuleCard(
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SquadGrayLight)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF10B981),
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = SquadTextSecondary
            )
        }
    }
}

@Composable
private fun VenueMapCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(86.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF25333A),
                        Color(0xFFC9E2C2),
                        Color(0xFF263740)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.LocationOn,
            contentDescription = null,
            tint = SquadOrange,
            modifier = Modifier
                .align(Alignment.Center)
                .size(30.dp)
        )

        Text(
            text = "Sunny Coast Beach Arena",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(SquadSurface, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun RegistrationSummary() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "REGISTRATION FEE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = SquadTextSecondary,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "$25.00",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrangeDark
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "SPOTS LEFT",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = SquadTextSecondary,
                letterSpacing = 0.5.sp
            )

            Text(
                text = "12 / 32",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=270dp,height=640dp,dpi=440"
)
@Composable
fun TournamentDetailsScreenPreview() {
    TournamentDetailsScreen(
        onBackClick = {},
        onJoinIndividuallyClick = {}
    )
}