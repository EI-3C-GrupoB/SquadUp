package com.example.squadup.ui.screens.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGray
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadWhite

@Composable
fun EventCalendarScreen(
    selectedRoute: String,
    onBackClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onTodayClick: () -> Unit,
    onMatchDetailsClick: () -> Unit,
    onTravelInfoClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNavItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
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
                            tint = SquadGrayDark
                        )
                    }

                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Definições",
                            tint = SquadGrayDark
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 18.dp, bottom = 24.dp)
        ) {
            CalendarHeader(
                onBackClick = onBackClick,
                onPreviousMonthClick = onPreviousMonthClick,
                onNextMonthClick = onNextMonthClick,
                onTodayClick = onTodayClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            MonthCalendarCard()

            Spacer(modifier = Modifier.height(20.dp))

            UpcomingMatchCard(
                onMatchDetailsClick = onMatchDetailsClick
            )

            Spacer(modifier = Modifier.height(26.dp))

            DailyScheduleCard()

            Spacer(modifier = Modifier.height(18.dp))

            TravelInfoCard(
                onTravelInfoClick = onTravelInfoClick
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    onBackClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onTodayClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = "Voltar",
            tint = SquadTextPrimary,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(24.dp)
                .clickable(onClick = onBackClick)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "April 2026",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = "4 Matches Scheduled",
                fontSize = 13.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .background(SquadOrangeLight, RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Mês anterior",
                    tint = SquadOrange,
                    modifier = Modifier
                        .size(17.dp)
                        .clickable(onClick = onPreviousMonthClick)
                )

                Text(
                    text = "Today",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SquadTextPrimary,
                    modifier = Modifier
                        .padding(horizontal = 22.dp)
                        .clickable(onClick = onTodayClick)
                )

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = "Próximo mês",
                    tint = SquadOrange,
                    modifier = Modifier
                        .size(17.dp)
                        .clickable(onClick = onNextMonthClick)
                )
            }
        }
    }
}

@Composable
private fun MonthCalendarCard() {
    val weekDays = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    val eventDays = setOf(2, 9, 12, 19, 26)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SquadGrayLight)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF6F8FB))
            ) {
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 10.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextSecondary
                    )
                }
            }

            val days = listOf(
                listOf(null, null, 1, 2, 3, 4, 5),
                listOf(6, 7, 8, 9, 10, 11, 12),
                listOf(13, 14, 15, 16, 17, 18, 19),
                listOf(20, 21, 22, 23, 24, 25, 26),
                listOf(27, 28, 29, 30, 31, null, null)
            )

            days.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    week.forEach { day ->
                        CalendarDayCell(
                            day = day,
                            isToday = day == 9,
                            hasEvent = day in eventDays,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int?,
    isToday: Boolean,
    hasEvent: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(
                color = if (isToday) SquadOrangeLight.copy(alpha = 0.45f) else SquadSurface
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = day.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isToday) SquadOrange else SquadTextPrimary
                )

                if (isToday) {
                    Text(
                        text = "TODAY",
                        fontSize = 6.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadOrange
                    )
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                }

                if (hasEvent && !isToday) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(SquadOrange, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun UpcomingMatchCard(
    onMatchDetailsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SquadGrayLight)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "UPCOMING: OCT 12",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SquadWhite,
                modifier = Modifier
                    .background(SquadOrange, RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "The Mavericks vs.\nRed Eagles",
                fontSize = 21.sp,
                lineHeight = 25.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamMiniBadge(
                    icon = Icons.Outlined.SportsSoccer,
                    name = "Strikers",
                    color = SquadOrangeLight,
                    iconColor = SquadOrange
                )

                Text(
                    text = "VS",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFE8ECF2),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                TeamMiniBadge(
                    icon = Icons.Outlined.Groups,
                    name = "Metro",
                    color = Color(0xFFEAF0F8),
                    iconColor = SquadTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Button(
                    onClick = onMatchDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = SquadWhite
                    )
                ) {
                    Text(
                        text = "Match Details",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .width(58.dp)
                        .height(44.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC928),
                        contentColor = SquadTextPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.TravelExplore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamMiniBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    color: Color,
    iconColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(25.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )
    }
}

@Composable
private fun DailyScheduleCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SquadGrayLight)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "DAILY SCHEDULE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = Color(0xFFB6C2D3)
            )

            Spacer(modifier = Modifier.height(18.dp))

            ScheduleRow(
                time = "08:00",
                title = "Morning Conditioning",
                location = "Main Field",
                highlighted = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            ScheduleRow(
                time = "10:30",
                title = "Tactical Review",
                location = "Media Room",
                highlighted = false
            )

            Spacer(modifier = Modifier.height(16.dp))

            ScheduleRow(
                time = "15:00",
                title = "Scrimmage Match",
                location = "Practice Area B",
                highlighted = false
            )
        }
    }
}

@Composable
private fun ScheduleRow(
    time: String,
    title: String,
    location: String,
    highlighted: Boolean
) {
    Row(
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = time,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (highlighted) SquadOrange else Color(0xFF9AA7BA),
            modifier = Modifier.width(58.dp)
        )

        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = location,
                fontSize = 11.sp,
                color = SquadTextSecondary
            )
        }
    }
}

@Composable
private fun TravelInfoCard(
    onTravelInfoClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(126.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        SquadOrange,
                        Color(0xFFFF5F00)
                    )
                ),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onTravelInfoClick)
            .padding(18.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.TravelExplore,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.16f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(78.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Text(
                text = "Next Away Game",
                fontSize = 11.sp,
                color = SquadOrangeLight
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Chicago Fire",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SquadWhite
            )

            Text(
                text = "Oct 19 · Soldier Field",
                fontSize = 13.sp,
                color = SquadWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "View Travel Info",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadWhite
                )

                Spacer(modifier = Modifier.width(4.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = SquadWhite,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun EventCalendarScreenPreview() {
    EventCalendarScreen(
        selectedRoute = "events",
        onBackClick = {},
        onPreviousMonthClick = {},
        onNextMonthClick = {},
        onTodayClick = {},
        onMatchDetailsClick = {},
        onTravelInfoClick = {},
        onNotificationsClick = {},
        onSettingsClick = {},
        onNavItemClick = {}
    )
}