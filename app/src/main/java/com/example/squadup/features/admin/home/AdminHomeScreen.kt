package com.example.squadup.features.admin.home

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.SportsScore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.squadup.R
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadWhite
import com.example.squadup.core.utils.AppLanguage
import java.text.NumberFormat
import java.util.Locale

@Composable
fun AdminHomeScreen(
    uiState: AdminHomeUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    isAdmin: Boolean,
    isAdminView: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onAdminViewChange: (Boolean) -> Unit,
    notificationsCount: Int = 0
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                showBackButton = false,
                showNotificationsButton = true,
                notificationsCount = notificationsCount,
                onNotificationsClick = onNotificationsClick,
                showSettingsButton = true,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                onAdminViewChange = onAdminViewChange,
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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SquadOrange)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_dashboard_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.admin_dashboard_subtitle),
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(18.dp))

                AdminMetricCard(
                    title = stringResource(R.string.admin_total_users),
                    value = uiState.totalUsers.formatNumber(),
                    subtitle = stringResource(R.string.admin_growth_last_month, uiState.usersGrowthPercent.toInt()),
                    icon = Icons.Outlined.Groups,
                    subtitleColor = Color(0xFF00A85A)
                )

                Spacer(modifier = Modifier.height(14.dp))

                AdminMetricCard(
                    title = stringResource(R.string.admin_active_matches),
                    value = uiState.activeMatches.formatNumber(),
                    subtitle = stringResource(R.string.admin_in_x_sports, uiState.activeSportsCount),
                    icon = Icons.Outlined.SportsScore
                )

                Spacer(modifier = Modifier.height(14.dp))

                AdminMetricCard(
                    title = stringResource(R.string.admin_active_events),
                    value = uiState.activeEvents.formatNumber(),
                    subtitle = stringResource(R.string.admin_available_events),
                    icon = Icons.Outlined.Event
                )

                Spacer(modifier = Modifier.height(14.dp))

                SportPopularityCard(
                    items = uiState.sportPopularity
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subtitleColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = value,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SquadOrange
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = subtitleColor
                )
            }

            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(34.dp),
                color = SquadOrangeLight,
                shape = CircleShape
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = SquadOrange,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SportPopularityCard(
    items: List<SportPopularityItem>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_sport_popularity),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (items.isEmpty()) {
                Text(
                    text = stringResource(R.string.admin_no_popularity_data),
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                items.forEachIndexed { index, item ->
                    SportPopularityRow(item = item)

                    if (index != items.lastIndex) {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SportPopularityRow(
    item: SportPopularityItem
) {
    Column {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = item.sportName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.align(Alignment.CenterStart)
            )

            Text(
                text = "${item.percentage}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrange,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        LinearProgressIndicator(
            progress = { item.percentage.coerceIn(0, 100) / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(999.dp)),
            color = SquadOrange,
            trackColor = SquadGrayLight
        )
    }
}

private fun Int.formatNumber(): String {
    return NumberFormat
        .getNumberInstance(Locale.US)
        .format(this)
}
