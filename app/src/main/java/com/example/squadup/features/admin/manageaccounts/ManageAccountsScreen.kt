package com.example.squadup.features.admin.manageaccounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.*
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ManageAccountsScreen(
    uiState: ManageAccountsUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onUserClick: (String) -> Unit,
    onCreateUserClick: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onTogglePendingRole: (AccountRole) -> Unit,
    onApplyFilter: () -> Unit,
    onFilterDismiss: () -> Unit,
    onSortByName: () -> Unit,
    onSortByRole: () -> Unit,
    onPreviousClick: () -> Unit,
    onNextClick: () -> Unit,
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = stringResource(R.string.manageAccounts_title),
                showBackButton = true,
                onBackClick = onBackClick,
                showNotificationsButton = true,
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
            AppNavBar(selectedRoute = selectedRoute, onItemClick = onNavItemClick)
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
                    .alpha(if (uiState.showFilterDialog) 0.45f else 1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 24.dp)
            ) {
                AdminAccountCard {
                    Text(
                        text = stringResource(R.string.manageAccounts_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = stringResource(R.string.manageAccounts_subtitle),
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = SquadTextSecondary
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    Text(
                        text = stringResource(R.string.manageAccounts_directory),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AccountsSearchField(
                            value = uiState.searchQuery,
                            onValueChange = onSearchQueryChange,
                            modifier = Modifier.weight(1f)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Filter Icon
                        IconButton(onClick = onFilterClick) {
                            Icon(
                                imageVector = Icons.Outlined.FilterList,
                                contentDescription = null,
                                tint = SquadTextPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Add User Button (Better Design)
                        Surface(
                            onClick = onCreateUserClick,
                            color = SquadOrange,
                            shape = CircleShape,
                            modifier = Modifier.size(36.dp),
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Adicionar",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = onSortByName
                                )
                        ) {
                            Text(
                                text = stringResource(R.string.manageAccounts_column_user),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.currentSortOrder == SortOrder.NameAZ || uiState.currentSortOrder == SortOrder.NameZA) 
                                    SquadOrange else SquadTextPrimary
                            )
                            if (uiState.currentSortOrder == SortOrder.NameAZ || uiState.currentSortOrder == SortOrder.NameZA) {
                                Icon(
                                    imageVector = if (uiState.currentSortOrder == SortOrder.NameAZ) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowUp,
                                    contentDescription = null,
                                    tint = SquadOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onSortByRole
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.manageAccounts_column_role),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.currentSortOrder == SortOrder.RoleAZ || uiState.currentSortOrder == SortOrder.RoleZA) 
                                    SquadOrange else SquadTextPrimary
                            )
                            if (uiState.currentSortOrder == SortOrder.RoleAZ || uiState.currentSortOrder == SortOrder.RoleZA) {
                                Icon(
                                    imageVector = if (uiState.currentSortOrder == SortOrder.RoleAZ) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowUp,
                                    contentDescription = null,
                                    tint = SquadOrange,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    uiState.filteredUsers.forEachIndexed { index, user ->
                        AccountUserRow(user = user, onClick = { onUserClick(user.id) })
                        if (index < uiState.filteredUsers.lastIndex) {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    val formattedTotal = NumberFormat.getNumberInstance(Locale.US)
                        .format(uiState.totalUsers)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.manageAccounts_showing,
                                uiState.filteredUsers.size,
                                formattedTotal
                            ),
                            fontSize = 14.sp,
                            color = SquadTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        PaginationButton(
                            text = stringResource(R.string.manageAccounts_previous),
                            onClick = onPreviousClick
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        PaginationButton(
                            text = stringResource(R.string.manageAccounts_next),
                            onClick = onNextClick
                        )
                    }
                }
            }

            if (uiState.showFilterDialog) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    AccountsFilterDialog(
                        selectedRoleFilters = uiState.pendingRoleFilters,
                        onToggleRoleFilter = onTogglePendingRole,
                        onApplyFilterClick = onApplyFilter,
                        onBackFilterClick = onFilterDismiss
                    )
                }
            }
        }
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
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFD4D8E4)),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
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
        textStyle = TextStyle(fontSize = 14.sp, color = SquadTextPrimary),
        modifier = modifier
            .height(44.dp)
            .background(Color(0xFFF3F3F3), RoundedCornerShape(8.dp))
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
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = stringResource(R.string.manageAccounts_search_placeholder),
                            fontSize = 14.sp,
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
private fun AccountUserRow(user: ManageAccountItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            color = Color(0xFFFFE2D2),
            shape = CircleShape
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = user.initials,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB4511A)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = user.email,
                fontSize = 14.sp,
                color = SquadTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        RoleBadge(role = user.role)
    }
}

@Composable
private fun RoleBadge(role: AccountRole) {
    val background = when (role) {
        AccountRole.Admin     -> Color(0xFFFFD7C4)
        AccountRole.Organizer -> Color(0xFFD4E8FF)
        AccountRole.Player    -> Color(0xFFE8E8E8)
    }
    val textColor = when (role) {
        AccountRole.Admin     -> SquadOrangeDark
        AccountRole.Organizer -> Color(0xFF1A5CA8)
        AccountRole.Player    -> Color(0xFF757575)
    }
    val label = when (role) {
        AccountRole.Admin     -> stringResource(R.string.manageAccounts_role_admin)
        AccountRole.Organizer -> stringResource(R.string.manageAccounts_role_organizer)
        AccountRole.Player    -> stringResource(R.string.manageAccounts_role_player)
    }

    Surface(color = background, shape = RoundedCornerShape(999.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun PaginationButton(text: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(42.dp),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.5.dp, Color(0xFF1F1F1F)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = SquadSurface,
            contentColor = SquadTextPrimary
        ),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AccountsFilterDialog(
    selectedRoleFilters: Set<AccountRole>,
    onToggleRoleFilter: (AccountRole) -> Unit,
    onApplyFilterClick: () -> Unit,
    onBackFilterClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 38.dp),
        color = SquadSurface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            Text(
                text = stringResource(R.string.manageAccounts_filter_by),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp,
                color = SquadTextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.manageAccounts_filter_role),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))
            RoleFilterRow(
                selectedRoles = selectedRoleFilters,
                onRoleToggle = onToggleRoleFilter
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = onApplyFilterClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.manageAccounts_filter_apply),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onBackFilterClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFBDBDBD),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.manageAccounts_filter_back),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun RoleFilterRow(
    selectedRoles: Set<AccountRole>,
    onRoleToggle: (AccountRole) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterSegment(
            text = stringResource(R.string.manageAccounts_role_admin),
            selected = AccountRole.Admin in selectedRoles,
            onClick = { onRoleToggle(AccountRole.Admin) },
            modifier = Modifier.weight(1f)
        )
        FilterSegment(
            text = stringResource(R.string.manageAccounts_role_organizer),
            selected = AccountRole.Organizer in selectedRoles,
            onClick = { onRoleToggle(AccountRole.Organizer) },
            modifier = Modifier.weight(1f)
        )
        FilterSegment(
            text = stringResource(R.string.manageAccounts_role_player),
            selected = AccountRole.Player in selectedRoles,
            onClick = { onRoleToggle(AccountRole.Player) },
            modifier = Modifier.weight(1f)
        )
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
        onClick = onClick,
        modifier = modifier.height(36.dp),
        color = Color.White,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) SquadOrange else Color(0xFFE0E0E0)
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) SquadOrange else SquadTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
