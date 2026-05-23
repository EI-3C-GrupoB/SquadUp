package com.example.squadup.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadUpTheme

enum class AccountRole {
    Admin,
    Organizer,
    Player
}

data class ManageAccountUi(
    val initials: String,
    val name: String,
    val email: String,
    val role: AccountRole
)

@Composable
fun ManageAccountsScreen(
    selectedRoute: String,
    searchQuery: String,
    users: List<ManageAccountUi>,
    selectedTypeFilter: AccountRole?,
    selectedRecentFilter: AccountRole?,
    showFilterDialog: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFilterClick: () -> Unit,
    onTypeFilterClick: (AccountRole?) -> Unit,
    onRecentFilterClick: (AccountRole?) -> Unit,
    onApplyFilterClick: () -> Unit,
    onBackFilterClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onBackToProfileClick: () -> Unit,
    onNavItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ManageAccountsTopBar(
                onNotificationsClick = onNotificationsClick,
                onSettingsClick = onSettingsClick
            )
        },
        bottomBar = {
            AppNavBar(
                selectedRoute = selectedRoute,
                onItemClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (showFilterDialog) 0.45f else 1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp)
                    .padding(top = 18.dp, bottom = 24.dp)
            ) {
                ManageAccountsCard(
                    searchQuery = searchQuery,
                    users = users,
                    onSearchQueryChange = onSearchQueryChange,
                    onFilterClick = onFilterClick,
                    onPreviousClick = onPreviousClick,
                    onNextClick = onNextClick,
                    onBackToProfileClick = onBackToProfileClick
                )
            }

            if (showFilterDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    AccountsFilterDialog(
                        selectedTypeFilter = selectedTypeFilter,
                        selectedRecentFilter = selectedRecentFilter,
                        onTypeFilterClick = onTypeFilterClick,
                        onRecentFilterClick = onRecentFilterClick,
                        onApplyFilterClick = onApplyFilterClick,
                        onBackFilterClick = onBackFilterClick
                    )
                }
            }
        }
    }
}

@Composable
private fun ManageAccountsTopBar(
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 18.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Profile",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SquadOrange,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificações",
                    tint = SquadGrayDark,
                    modifier = Modifier.size(17.dp)
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Definições",
                    tint = SquadGrayDark,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun ManageAccountsCard(
    searchQuery: String,
    users: List<ManageAccountUi>,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    onBackToProfileClick: () -> Unit
) {
    AdminAccountCard {
        Text(
            text = "Manage Accounts",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Here you can manage all accounts\non the platform. You can add, edit\nand delete accounts",
            fontSize = 11.sp,
            lineHeight = 15.sp,
            color = SquadTextSecondary
        )

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "User Directory",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccountsSearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Outlined.FilterList,
                contentDescription = "Filtrar",
                tint = SquadTextPrimary,
                modifier = Modifier
                    .size(22.dp)
                    .clickable(onClick = onFilterClick)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TableHeaderText(
                text = "User",
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableHeaderText(text = "Role")

                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        users.forEachIndexed { index, user ->
            AccountUserRow(user = user)

            if (index < users.lastIndex) {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Showing ${users.size} of\n24,512 users",
                fontSize = 10.sp,
                lineHeight = 14.sp,
                color = SquadTextPrimary,
                modifier = Modifier.weight(1f)
            )

            PaginationButton(
                text = "Previous",
                onClick = onPreviousClick
            )

            Spacer(modifier = Modifier.width(6.dp))

            PaginationButton(
                text = "Next",
                onClick = onNextClick
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "← Back to Profile",
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = SquadOrange,
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .clickable(onClick = onBackToProfileClick)
        )
    }
}

@Composable
private fun AdminAccountCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFD4D8E4)),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            content = content
        )
    }
}

@Composable
private fun AccountsSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 9.sp,
            color = SquadTextPrimary
        ),
        modifier = modifier
            .height(34.dp)
            .background(Color(0xFFF3F3F3), RoundedCornerShape(1.dp))
            .padding(horizontal = 8.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = SquadTextSecondary,
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = "Search athletes, organizers\nor administrators...",
                            fontSize = 9.sp,
                            lineHeight = 11.sp,
                            color = SquadTextSecondary,
                            maxLines = 2,
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
private fun TableHeaderText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        color = SquadTextPrimary,
        modifier = modifier
    )
}

@Composable
private fun AccountUserRow(
    user: ManageAccountUi
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(25.dp),
            color = Color(0xFFFFE2D2),
            shape = CircleShape
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user.initials,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB4511A)
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = user.name,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = user.email,
                fontSize = 9.sp,
                color = SquadTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        RoleBadge(role = user.role)
    }
}

@Composable
private fun RoleBadge(
    role: AccountRole
) {
    val background = when (role) {
        AccountRole.Admin -> Color(0xFFFFD7C4)
        AccountRole.Organizer -> Color(0xFFFFD7C4)
        AccountRole.Player -> Color(0xFFE8E8E8)
    }

    val text = when (role) {
        AccountRole.Admin -> "Admin"
        AccountRole.Organizer -> "Organizer"
        AccountRole.Player -> "Player"
    }

    Surface(
        color = background,
        shape = RoundedCornerShape(999.dp)
    ) {
        Text(
            text = text,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun PaginationButton(
    text: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(31.dp),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, SquadGrayDark),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SquadSurface,
            contentColor = SquadTextPrimary
        ),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AccountsFilterDialog(
    selectedTypeFilter: AccountRole?,
    selectedRecentFilter: AccountRole?,
    onTypeFilterClick: (AccountRole?) -> Unit,
    onRecentFilterClick: (AccountRole?) -> Unit,
    onApplyFilterClick: () -> Unit,
    onBackFilterClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 38.dp),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Text(
                text = "S O R T   B Y",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Type",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterSegmentedRow(
                selectedRole = selectedTypeFilter,
                onRoleClick = onTypeFilterClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Most Recent",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            FilterSegmentedRow(
                selectedRole = selectedRecentFilter,
                onRoleClick = onRecentFilterClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onApplyFilterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Filter",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onBackFilterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBDBDBD),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Back",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FilterSegmentedRow(
    selectedRole: AccountRole?,
    onRoleClick: (AccountRole?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFE9E9ED),
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            FilterSegment(
                text = "Administrator",
                selected = selectedRole == AccountRole.Admin,
                onClick = { onRoleClick(AccountRole.Admin) },
                modifier = Modifier.weight(1f)
            )

            FilterSegment(
                text = "Organizer",
                selected = selectedRole == AccountRole.Organizer,
                onClick = { onRoleClick(AccountRole.Organizer) },
                modifier = Modifier.weight(1f)
            )

            FilterSegment(
                text = "Player",
                selected = selectedRole == AccountRole.Player,
                onClick = { onRoleClick(AccountRole.Player) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun FilterSegment(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(22.dp)
            .clickable(onClick = onClick),
        color = if (selected) Color(0xFFFFD7C4) else Color.Transparent,
        shape = RoundedCornerShape(4.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) SquadOrange else SquadTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun defaultManageAccountsUsers(): List<ManageAccountUi> {
    return listOf(
        ManageAccountUi(
            initials = "JD",
            name = "James D.",
            email = "james@squadup.com",
            role = AccountRole.Player
        ),
        ManageAccountUi(
            initials = "SK",
            name = "Sarah K.",
            email = "sarah.k@club.org",
            role = AccountRole.Organizer
        ),
        ManageAccountUi(
            initials = "ML",
            name = "Marcus L.",
            email = "ml@proleague.com",
            role = AccountRole.Player
        ),
        ManageAccountUi(
            initials = "SK",
            name = "Sarah K.",
            email = "sarah.k@club.org",
            role = AccountRole.Admin
        ),
        ManageAccountUi(
            initials = "SK",
            name = "Sarah K.",
            email = "sarah.k@club.org",
            role = AccountRole.Admin
        ),
        ManageAccountUi(
            initials = "ML",
            name = "Marcus L.",
            email = "ml@proleague.com",
            role = AccountRole.Player
        ),
        ManageAccountUi(
            initials = "ML",
            name = "Marcus L.",
            email = "ml@proleague.com",
            role = AccountRole.Player
        ),
        ManageAccountUi(
            initials = "ML",
            name = "Marcus L.",
            email = "ml@proleague.com",
            role = AccountRole.Player
        ),
        ManageAccountUi(
            initials = "ML",
            name = "Marcus L.",
            email = "ml@proleague.com",
            role = AccountRole.Player
        )
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ManageAccountsScreenPreview() {
    SquadUpTheme {
        var searchQuery by rememberSaveable { mutableStateOf("") }
        var showFilterDialog by rememberSaveable { mutableStateOf(false) }
        var selectedTypeFilter by remember { mutableStateOf<AccountRole?>(AccountRole.Admin) }
        var selectedRecentFilter by remember { mutableStateOf<AccountRole?>(null) }

        ManageAccountsScreen(
            selectedRoute = "profile",
            searchQuery = searchQuery,
            users = defaultManageAccountsUsers(),
            selectedTypeFilter = selectedTypeFilter,
            selectedRecentFilter = selectedRecentFilter,
            showFilterDialog = showFilterDialog,
            onSearchQueryChange = { searchQuery = it },
            onNotificationsClick = {},
            onSettingsClick = {},
            onFilterClick = { showFilterDialog = true },
            onTypeFilterClick = { selectedTypeFilter = it },
            onRecentFilterClick = { selectedRecentFilter = it },
            onApplyFilterClick = { showFilterDialog = false },
            onBackFilterClick = { showFilterDialog = false },
            onPreviousClick = {},
            onNextClick = {},
            onBackToProfileClick = {},
            onNavItemClick = {}
        )
    }
}