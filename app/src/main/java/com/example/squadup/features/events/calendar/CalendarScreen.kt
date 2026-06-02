package com.example.squadup.features.events.calendar

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.SportsSoccer
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.theme.SquadBackground
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadSurface
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary
import com.example.squadup.core.ui.theme.SquadUpTheme
import com.example.squadup.core.utils.AppLanguage

@Composable
fun CalendarScreen(
    uiState: CalendarUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onTodayClick: () -> Unit,
    onDayClick: (Int) -> Unit,
    onMatchDetailsClick: () -> Unit,
    onTravelInfoClick: () -> Unit,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                showBackButton = false,
                showNotificationsButton = true,
                showSettingsButton = true,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
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
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = null,
                        tint = SquadTextPrimary
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.monthTitle,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary
                    )

                    Text(
                        text = "${uiState.matchesScheduled} Matches Scheduled",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SquadTextSecondary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .background(SquadOrangeLight, RoundedCornerShape(999.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = SquadOrange,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable(onClick = onPreviousMonthClick)
                        )

                        Text(
                            text = "Today",
                            color = SquadTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(horizontal = 14.dp)
                                .clickable(onClick = onTodayClick)
                        )

                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                            contentDescription = null,
                            tint = SquadOrange,
                            modifier = Modifier
                                .size(22.dp)
                                .clickable(onClick = onNextMonthClick)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(36.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))

            MonthCalendarCard(
                selectedDay = uiState.selectedDay,
                eventDays = uiState.eventDays,
                onDayClick = onDayClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            MatchHighlightCard(
                item = uiState.highlightedMatch,
                onMatchDetailsClick = onMatchDetailsClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            DailyScheduleCard(
                items = uiState.dailySchedule
            )

            Spacer(modifier = Modifier.height(18.dp))

            AwayGameCard(
                item = uiState.nextAwayGame,
                onTravelInfoClick = onTravelInfoClick
            )

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun MonthCalendarCard(
    selectedDay: Int,
    eventDays: Set<Int>,
    onDayClick: (Int) -> Unit
) {
    val weekDays = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")

    val cells = listOf<Int?>(
        null, null, null, 1, 2, 3, 4,
        5, 6, 7, 8, 9, 10, 11,
        12, 13, 14, 15, 16, 17, 18,
        19, 20, 21, 22, 23, 24, 25,
        26, 27, 28, 29, 30, null, null
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            cells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        CalendarDayCell(
                            day = day,
                            selected = day == selectedDay,
                            hasEvent = day != null && day in eventDays,
                            onClick = {
                                if (day != null) {
                                    onDayClick(day)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int?,
    selected: Boolean,
    hasEvent: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clickable(enabled = day != null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (day != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .background(
                            color = if (selected) SquadOrangeLight else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = day.toString(),
                        fontSize = 12.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
                        color = if (selected) SquadOrange else SquadTextPrimary
                    )
                }

                if (selected) {
                    Text(
                        text = "TODAY",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadOrange
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(
                                color = if (hasEvent) SquadOrange else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchHighlightCard(
    item: CalendarMatchItem,
    onMatchDetailsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFFBF8),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = item.label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .background(SquadOrange, RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = item.title,
                fontSize = 24.sp,
                lineHeight = 29.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamSide(
                    iconTint = SquadOrange,
                    label = item.homeTeam,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "VS",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE1E7F1),
                    modifier = Modifier.padding(horizontal = 14.dp)
                )

                TeamSide(
                    iconTint = Color(0xFF9CA9BD),
                    label = item.awayTeam,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onMatchDetailsClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Match Details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = {},
                    modifier = Modifier.width(76.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC928),
                        contentColor = SquadTextPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ConfirmationNumber,
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamSide(
    iconTint: Color,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(iconTint.copy(alpha = 0.18f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.SportsSoccer,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DailyScheduleCard(
    items: List<DailyScheduleItem>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = "DAILY SCHEDULE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp,
                color = Color(0xFFA5B0C4)
            )

            Spacer(modifier = Modifier.height(18.dp))

            items.forEachIndexed { index, item ->
                DailyScheduleRow(item = item)

                if (index < items.lastIndex) {
                    Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }
    }
}

@Composable
private fun DailyScheduleRow(
    item: DailyScheduleItem
) {
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = item.time,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = SquadOrange,
            modifier = Modifier.width(70.dp)
        )

        Column {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = item.location,
                fontSize = 12.sp,
                color = SquadTextSecondary
            )
        }
    }
}

@Composable
private fun AwayGameCard(
    item: AwayGameItem,
    onTravelInfoClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadOrange,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 3.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(
                            SquadOrange,
                            Color(0xFFFF6500),
                            Color(0xFFFF7A1A)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.SportsSoccer,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.12f),
                modifier = Modifier
                    .size(96.dp)
                    .align(Alignment.BottomEnd)
            )

            Column {
                Text(
                    text = item.label,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = item.city,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = item.date,
                    fontSize = 14.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.clickable(onClick = onTravelInfoClick),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "View Travel Info",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Calendar Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun CalendarScreenPreview() {
    SquadUpTheme {
        CalendarScreen(
            uiState = CalendarUiState(
                dailySchedule = listOf(
                    DailyScheduleItem("08:00", "Morning Conditioning", "Main Field"),
                    DailyScheduleItem("10:30", "Tactical Review", "Media Room"),
                    DailyScheduleItem("15:00", "Scrimmage Match", "Practice Area B")
                )
            ),
            selectedRoute = "events",
            onNavItemClick = {},
            onBackClick = {},
            onPreviousMonthClick = {},
            onNextMonthClick = {},
            onTodayClick = {},
            onDayClick = {},
            onMatchDetailsClick = {},
            onTravelInfoClick = {},
            selectedLanguage = AppLanguage.EN,
            isDarkMode = false,
            onLanguageChange = {},
            onDarkModeChange = {}
        )
    }
}