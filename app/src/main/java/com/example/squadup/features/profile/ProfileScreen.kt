package com.example.squadup.features.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.UserRole
import com.example.squadup.core.ui.components.*
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    isAdmin: Boolean,
    isAdminView: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onTicketsClick: () -> Unit,
    onMyEventsClick: () -> Unit,
    onManageAccountsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onLoginClick: () -> Unit,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    notificationsCount: Int = 0
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                title = stringResource(R.string.profile_title),
                showBackButton = false,
                showNotificationsButton = uiState.isLoggedIn,
                notificationsCount = notificationsCount,
                showSettingsButton = true,
                showLoginButton = !uiState.isLoggedIn,
                onLoginClick = onLoginClick,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                onAdminViewChange = onAdminViewChange,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange,
                onNotificationsClick = onNotificationsClick
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!uiState.isLoggedIn) {
                Spacer(modifier = Modifier.height(40.dp))

                EmptyStateCard(
                    title = "Perfil de Convidado",
                    message = "Inicia sessão para veres as tuas estatísticas, gerires o teu perfil e acederes aos teus bilhetes e eventos.",
                    icon = Icons.Outlined.Person,
                    actionText = "Login / Registo",
                    onActionClick = onLoginClick
                )

                Spacer(modifier = Modifier.height(40.dp))
            } else {
                Spacer(modifier = Modifier.height(22.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(color = SquadOrange)
                Spacer(modifier = Modifier.height(16.dp))
            }

            uiState.errorMessage?.let { errorMessage ->
                Text(
                    text = stringResource(errorMessage),
                    color = SquadError,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            ProfileAvatar(
                isPlayer = uiState.isPlayer,
                onTicketsClick = onTicketsClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = uiState.displayName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            uiState.role?.let { role ->
                val roleLabel = when (role) {
                    UserRole.PLAYER           -> stringResource(R.string.role_player)
                    UserRole.ORGANIZER        -> stringResource(R.string.role_organizer)
                    UserRole.PLAYER_ORGANIZER -> stringResource(R.string.role_player_organizer)
                }
                Text(
                    text = roleLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(SquadOrange, RoundedCornerShape(999.dp))
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onLogoutClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = SquadError
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.profile_logout),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                ProfileStatCard(
                    icon = Icons.Outlined.SportsSoccer,
                    value = uiState.matchesPlayed.toString(),
                    label = stringResource(R.string.profile_stat_matches),
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    icon = Icons.Outlined.EmojiEvents,
                    value = uiState.wins.toString().padStart(2, '0'),
                    label = stringResource(R.string.profile_stat_wins),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                ProfileStatCard(
                    icon = Icons.Outlined.StarOutline,
                    value = uiState.goals.toString(),
                    label = stringResource(R.string.profile_stat_goals),
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    icon = Icons.Outlined.Groups,
                    value = uiState.teams.toString(),
                    label = stringResource(R.string.profile_stat_teams),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            uiState.playStyle?.let { style ->
                PlayStyleIntensityCard(
                    intensityLabel = stringResource(style.labelRes),
                    progress = style.progress,
                    description = stringResource(style.descriptionRes)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.isAdmin) {
                ProfileActionRow(
                    text = stringResource(R.string.profile_admin_manage_accounts),
                    icon = Icons.Outlined.ManageAccounts,
                    onClick = onManageAccountsClick
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (uiState.isOrganizer) {
                ProfileActionRow(
                    text = stringResource(R.string.home_my_events),
                    icon = Icons.Outlined.CalendarMonth,
                    onClick = onMyEventsClick
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            ProfileActionRow(
                text = stringResource(R.string.profile_action_edit),
                icon = Icons.Default.Person,
                onClick = onEditProfileClick
            )

            Spacer(modifier = Modifier.height(10.dp))

            ProfileActionRow(
                text = stringResource(R.string.profile_action_change_password),
                icon = Icons.Default.Lock,
                onClick = onChangePasswordClick
            )

            Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    isPlayer: Boolean,
    onTicketsClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        if (isPlayer) {
            Icon(
                imageVector = Icons.Default.ConfirmationNumber,
                contentDescription = null,
                tint = SquadOrange,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clickable { onTicketsClick() }
                    .size(32.dp)
                    .padding(top = 4.dp)
            )
        }

        Box(contentAlignment = Alignment.BottomEnd) {
            Image(
                painter = painterResource(id = R.drawable.logo_squadup),
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(86.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(4.dp)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(SquadOrange)
                    .padding(6.dp)
            )
        }
    }
}
