package com.example.squadup.ui.screens.match

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.MatchEventList
import com.example.squadup.ui.components.MatchEventType
import com.example.squadup.ui.components.MatchEventUi
import com.example.squadup.ui.components.MatchOverviewTab
import com.example.squadup.ui.components.MatchScoreCard
import com.example.squadup.ui.components.MatchStatUi
import com.example.squadup.ui.components.MatchStatsList
import com.example.squadup.ui.components.MatchTabSelector
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun MatchOverviewScreen(
    selectedTab: MatchOverviewTab,
    onTabSelected: (MatchOverviewTab) -> Unit,
    onBackClick: () -> Unit,
    onGoToDirectionsClick: () -> Unit
) {
    val events = listOf(
        MatchEventUi(
            type = MatchEventType.Goal,
            title = "Goal - Lions VC",
            subtitle = "J. Smith • 32:15"
        ),
        MatchEventUi(
            type = MatchEventType.Foul,
            title = "Foul - Blue Hawks",
            subtitle = "M. Jordan • 28:04"
        ),
        MatchEventUi(
            type = MatchEventType.Goal,
            title = "Goal - Lions VC",
            subtitle = "B. Fernandes • 20:57"
        ),
        MatchEventUi(
            type = MatchEventType.Foul,
            title = "Yellow Card - Red Dragons",
            subtitle = "A. Costa • 14:22"
        )
    )

    val stats = listOf(
        MatchStatUi(
            label = "Possession",
            homeValue = "58%",
            awayValue = "42%",
            progress = 0.58f
        ),
        MatchStatUi(
            label = "Shots",
            homeValue = "12",
            awayValue = "8",
            progress = 0.60f
        ),
        MatchStatUi(
            label = "Fouls",
            homeValue = "5",
            awayValue = "7",
            progress = 0.42f
        ),
        MatchStatUi(
            label = "Corners",
            homeValue = "6",
            awayValue = "3",
            progress = 0.66f
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        AppHeader(
            showLogo = false,
            title = "Live Match Overview",
            showBackButton = true,
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(26.dp))

            Text(
                text = "Live Match Overview",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Track the game in real-time",
                fontSize = 14.sp,
                color = SquadIconSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            MatchScoreCard(
                homeTeamName = "Red Dragons",
                awayTeamName = "Blue Hawks",
                homeScore = 2,
                awayScore = 1,
                time = "38:42",
                homeLogo = painterResource(id = R.drawable.logo_squadup),
                awayLogo = painterResource(id = R.drawable.logo_squadup)
            )

            Spacer(modifier = Modifier.height(24.dp))

            MatchTabSelector(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                MatchOverviewTab.Events -> {
                    MatchEventList(events = events)
                }

                MatchOverviewTab.Stats -> {
                    MatchStatsList(stats = stats)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            PrimaryButton(
                text = "GO TO DIRECTIONS",
                onClick = onGoToDirectionsClick,
                trailingIcon = Icons.AutoMirrored.Filled.ArrowForward
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MatchOverviewEventsPreview() {
    var selectedTab by remember {
        mutableStateOf(MatchOverviewTab.Events)
    }

    MatchOverviewScreen(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onBackClick = {},
        onGoToDirectionsClick = {}
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MatchOverviewStatsPreview() {
    var selectedTab by remember {
        mutableStateOf(MatchOverviewTab.Stats)
    }

    MatchOverviewScreen(
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        onBackClick = {},
        onGoToDirectionsClick = {}
    )
}