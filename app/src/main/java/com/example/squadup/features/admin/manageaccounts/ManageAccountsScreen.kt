package com.example.squadup.features.admin.manageaccounts

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
    onSearchQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit,
    onPendingRoleChange: (AccountRole?) -> Unit,
    onApplyFilter: () -> Unit,
    onFilterDismiss: () -> Unit,
    onToggleSort: () -> Unit,
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
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = null,
                            tint = SquadTextPrimary,
                            modifier = Modifier
                                .size(28.dp)
                                .clickable(onClick = onFilterClick)
                        )
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
                                .clickable(onClick = onToggleSort)
                        ) {
                            Text(
                                text = stringResource(R.string.manageAccounts_column_user),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SquadOrange
                            )
                            Icon(
                                imageVector = if (uiState.sortAscending) Icons.Outlined.KeyboardArrowDown else Icons.Outlined.KeyboardArrowUp,
                                contentDescription = null,
                                tint = SquadOrange,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = stringResource(R.string.manageAccounts_column_role),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SquadTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    uiState.filteredUsers.forEachIndexed { index, user ->
                        AccountUserRow(user = user)
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
                        selectedRoleFilter = uiState.pendingRoleFilter,
                        onRoleFilterClick = onPendingRoleChange,
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
private fun AccountUserRow(user: ManageAccountItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
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
    selectedRoleFilter: AccountRole?,
    onRoleFilterClick: (AccountRole?) -> Unit,
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
            Spacer(modifier = Modifier.height(12.dp))
            RoleFilterRow(
                selectedRole = selectedRoleFilter,
                onRoleClick = onRoleFilterClick
            )

            Spacer(modifier = Modifier.height(24.dp))

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
    selectedRole: AccountRole?,
    onRoleClick: (AccountRole?) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFE9E9ED),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            FilterSegment(
                text = stringResource(R.string.manageAccounts_filter_all),
                selected = selectedRole == null,
                onClick = { onRoleClick(null) },
                modifier = Modifier.weight(1f)
            )
            FilterSegment(
                text = stringResource(R.string.manageAccounts_role_admin),
                selected = selectedRole == AccountRole.Admin,
                onClick = { onRoleClick(AccountRole.Admin) },
                modifier = Modifier.weight(1f)
            )
            FilterSegment(
                text = stringResource(R.string.manageAccounts_role_organizer),
                selected = selectedRole == AccountRole.Organizer,
                onClick = { onRoleClick(AccountRole.Organizer) },
                modifier = Modifier.weight(1f)
            )
            FilterSegment(
                text = stringResource(R.string.manageAccounts_role_player),
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
        modifier = modifier.height(32.dp).clickable(onClick = onClick),
        color = if (selected) Color(0xFFFFD7C4) else Color.Transparent,
        shape = RoundedCornerShape(6.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) SquadOrange else SquadTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
