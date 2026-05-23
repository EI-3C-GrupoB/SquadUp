package com.example.squadup.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadUpTheme

data class AdminUserUi(
    val initials: String,
    val name: String,
    val email: String,
    val role: String,
    val badgeColor: Color
)

data class SportPopularityUi(
    val name: String,
    val percentage: Int,
    val color: Color
)

@Composable
fun AdminHomeScreen(
    selectedRoute: String,
    searchQuery: String,
    users: List<AdminUserUi>,
    sportPopularity: List<SportPopularityUi>,
    onSearchQueryChange: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
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
                .padding(horizontal = 18.dp)
                .padding(top = 18.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Global Admin Dashboard",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Real-time overview of the SquadUp ecosystem",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(18.dp))

            TotalUsersCard()

            Spacer(modifier = Modifier.height(18.dp))

            ActiveMatchesCard()

            Spacer(modifier = Modifier.height(18.dp))

            ActiveEventsCard()

            Spacer(modifier = Modifier.height(22.dp))

            UserDirectoryCard(
                searchQuery = searchQuery,
                users = users,
                onSearchQueryChange = onSearchQueryChange,
                onPreviousClick = onPreviousClick,
                onNextClick = onNextClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            SportPopularityCard(
                items = sportPopularity
            )
        }
    }
}

@Composable
private fun AdminCard(
    modifier: Modifier = Modifier,
    borderColor: Color = SquadGrayLight,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
private fun TotalUsersCard() {
    AdminCard {
        Text(
            text = "TOTAL USERS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.2.sp,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "24,512",
            fontSize = 28.sp,
            color = SquadOrange,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "↗ +12% from last month",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF10B981)
        )
    }
}

@Composable
private fun ActiveMatchesCard() {
    AdminCard {
        Text(
            text = "Active Matches",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "1,804",
            fontSize = 30.sp,
            fontWeight = FontWeight.Medium,
            color = SquadOrange
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "In 5 Different Sports",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextSecondary
        )
    }
}

@Composable
private fun ActiveEventsCard() {
    AdminCard {
        Text(
            text = "ACTIVE EVENTS",
            fontSize = 12.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.2.sp,
            color = SquadTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "184",
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { 0.75f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = SquadOrange,
            trackColor = SquadGrayLight,
            strokeCap = StrokeCap.Round
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "75% capacity reached",
            fontSize = 13.sp,
            color = SquadTextPrimary
        )
    }
}

@Composable
private fun UserDirectoryCard(
    searchQuery: String,
    users: List<AdminUserUi>,
    onSearchQueryChange: (String) -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit
) {
    AdminCard {
        Text(
            text = "User Directory",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        AdminSearchField(
            value = searchQuery,
            onValueChange = onSearchQueryChange
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "User",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "Role",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        users.forEachIndexed { index, user ->
            AdminUserRow(user = user)

            if (index < users.lastIndex) {
                Spacer(modifier = Modifier.height(14.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Showing 3 of\n24,512 users",
                fontSize = 12.sp,
                lineHeight = 16.sp,
                color = SquadTextPrimary,
                modifier = Modifier.weight(1f)
            )

            OutlinedButton(
                onClick = onPreviousClick,
                modifier = Modifier.height(38.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, SquadGrayDark),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SquadSurface,
                    contentColor = SquadTextPrimary
                )
            ) {
                Text(
                    text = "Previous",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = onNextClick,
                modifier = Modifier.height(38.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, SquadGrayDark),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SquadSurface,
                    contentColor = SquadTextPrimary
                )
            ) {
                Text(
                    text = "Next",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminSearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 13.sp,
            color = SquadTextPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(SquadGrayLight, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = SquadTextSecondary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(9.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = "Search athletes, coaches, or teams...",
                            fontSize = 13.sp,
                            color = SquadTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    innerTextField()
                }
            }
        }
    )
}

@Composable
private fun AdminUserRow(
    user: AdminUserUi
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(user.badgeColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.initials,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrangeDark
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = user.email,
                fontSize = 12.sp,
                color = SquadTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = user.role,
            fontSize = 11.sp,
            color = SquadTextPrimary,
            modifier = Modifier
                .background(
                    color = if (user.role == "Organizer") {
                        SquadOrangeLight
                    } else {
                        SquadGrayLight
                    },
                    shape = RoundedCornerShape(999.dp)
                )
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun SportPopularityCard(
    items: List<SportPopularityUi>
) {
    AdminCard(
        borderColor = SquadGrayDark
    ) {
        Text(
            text = "Sport Popularity",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(18.dp))

        items.forEachIndexed { index, item ->
            SportPopularityRow(item = item)

            if (index < items.lastIndex) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SportPopularityRow(
    item: SportPopularityUi
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = item.name,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${item.percentage}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = item.color
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        LinearProgressIndicator(
            progress = { item.percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = item.color,
            trackColor = Color(0xFF263238),
            strokeCap = StrokeCap.Round
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun AdminHomeScreenPreview() {
    SquadUpTheme {
        var searchQuery by remember { mutableStateOf("") }

        val users = listOf(
            AdminUserUi(
                initials = "JD",
                name = "James D.",
                email = "james@squadup.com",
                role = "Athlete",
                badgeColor = SquadGrayLight
            ),
            AdminUserUi(
                initials = "SK",
                name = "Sarah K.",
                email = "sarah.k@club.org",
                role = "Organizer",
                badgeColor = SquadOrangeLight
            ),
            AdminUserUi(
                initials = "ML",
                name = "Marcus L.",
                email = "ml@proleague.com",
                role = "Coach",
                badgeColor = SquadGrayLight
            )
        )

        val popularity = listOf(
            SportPopularityUi(
                name = "Basketball",
                percentage = 42,
                color = SquadOrange
            ),
            SportPopularityUi(
                name = "Football",
                percentage = 28,
                color = Color(0xFFFFB58A)
            ),
            SportPopularityUi(
                name = "Paddle",
                percentage = 15,
                color = Color(0xFF7ED6D1)
            )
        )

        AdminHomeScreen(
            selectedRoute = "home",
            searchQuery = searchQuery,
            users = users,
            sportPopularity = popularity,
            onSearchQueryChange = { searchQuery = it },
            onNotificationsClick = {},
            onSettingsClick = {},
            onPreviousClick = {},
            onNextClick = {},
            onNavItemClick = {}
        )
    }
}