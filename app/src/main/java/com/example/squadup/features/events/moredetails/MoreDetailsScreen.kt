package com.example.squadup.features.events.moredetails

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.SportsVolleyball
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.theme.SquadBackground
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadSurface
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary
import com.example.squadup.core.ui.theme.SquadUpTheme
import com.example.squadup.core.utils.AppLanguage

@Composable
fun MoreDetailsScreen(
    uiState: MoreDetailsUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                showBackButton = true,
                onBackClick = onBackClick,
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
        ) {
            MoreDetailsTitleBar(
                title = uiState.title
            )

            HeroSection(
                title = uiState.title,
                entryType = uiState.entryType
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
            ) {
                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoCard(
                        icon = Icons.Outlined.CalendarMonth,
                        label = "DATE",
                        value = uiState.date,
                        modifier = Modifier.weight(1f)
                    )

                    InfoCard(
                        icon = Icons.Outlined.AccessTime,
                        label = "TIME",
                        value = uiState.time,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                TeamRequirementCard(
                    title = uiState.teamRequirementTitle,
                    description = uiState.teamRequirementDescription
                )

                Spacer(modifier = Modifier.height(18.dp))

                SectionTitle(
                    icon = Icons.Outlined.Gavel,
                    title = "Tournament Rules"
                )

                Spacer(modifier = Modifier.height(12.dp))

                uiState.rules.forEachIndexed { index, rule ->
                    RuleCard(text = rule)

                    if (index < uiState.rules.lastIndex) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                SectionTitle(
                    icon = Icons.Outlined.LocationOn,
                    title = "Venue"
                )

                Spacer(modifier = Modifier.height(12.dp))

                VenueMapCard(
                    venueName = uiState.venueName
                )

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun MoreDetailsTitleBar(
    title: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = SquadOrange,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun HeroSection(
    title: String,
    entryType: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF3B1A12),
                        Color(0xFFE26D2F),
                        Color(0xFFFFB35C),
                        Color(0xFF2E1A16)
                    )
                )
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.SportsVolleyball,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.18f),
            modifier = Modifier
                .size(104.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Text(
                text = entryType,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .background(SquadOrange, RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(84.dp),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFF0E1DC))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary
            )
        }
    }
}

@Composable
private fun TeamRequirementCard(
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFEEE9),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFFD3C7))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrange
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = SquadTextSecondary
            )
        }
    }
}

@Composable
private fun SectionTitle(
    icon: ImageVector,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SquadOrange,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )
    }
}

@Composable
private fun RuleCard(
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF0FB391),
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
private fun VenueMapCard(
    venueName: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(94.dp),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 1.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFE7EFE7),
                            Color(0xFFBFD6C7),
                            Color(0xFFE6E6DF)
                        )
                    )
                )
        ) {
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.85f),
                thickness = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
            )

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(30.dp)
                    .background(SquadOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = venueName,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
                    .background(Color.White, RoundedCornerShape(4.dp))
                    .padding(horizontal = 7.dp, vertical = 4.dp)
            )
        }
    }
}

@Preview(
    name = "More Details Screen",
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun MoreDetailsScreenPreview() {
    SquadUpTheme {
        MoreDetailsScreen(
            uiState = MoreDetailsUiState(),
            selectedRoute = "events",
            onNavItemClick = {},
            onBackClick = {},
            selectedLanguage = AppLanguage.EN,
            isDarkMode = false,
            onLanguageChange = {},
            onDarkModeChange = {}
        )
    }
}