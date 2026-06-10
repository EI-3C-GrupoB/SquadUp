package com.example.squadup.features.admin.manageaccounts.edituser

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.*
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.features.admin.manageaccounts.AccountRole

@Composable
fun EditUserScreen(
    uiState: EditUserUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onBackClick: () -> Unit,
    onRoleChange: (AccountRole) -> Unit,
    onSendMessageClick: () -> Unit,
    onToggleSuspend: () -> Unit,
    onDeleteClick: () -> Unit,
    onSaveClick: () -> Unit,
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
                title = stringResource(R.string.editUser_title),
                showBackButton = true,
                onBackClick = onBackClick,
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
            AppNavBar(selectedRoute = selectedRoute, onItemClick = onNavItemClick)
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
            Spacer(modifier = Modifier.height(28.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(
                    color = SquadOrange,
                    modifier = Modifier.padding(top = 36.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                return@Column
            }

            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFFFE2D2), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.userInitials,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB4511A)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = uiState.userName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.editUser_subtitle).uppercase(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp,
                color = SquadTextSecondary
            )

            if (uiState.userEmail.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = uiState.userEmail,
                    fontSize = 13.sp,
                    color = SquadTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Role Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.editUser_assign_role),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = SquadTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    RoleCard(
                        icon = Icons.Outlined.AdminPanelSettings,
                        iconBackground = Color(0xFFFFE2D2),
                        iconTint = SquadOrangeDark,
                        title = stringResource(R.string.manageAccounts_role_admin),
                        description = stringResource(R.string.editUser_role_admin_desc),
                        selected = uiState.selectedRole == AccountRole.Admin,
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        onClick = { onRoleChange(AccountRole.Admin) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RoleCard(
                        icon = Icons.Outlined.EventNote,
                        iconBackground = Color(0xFFD4E8FF),
                        iconTint = Color(0xFF1A5CA8),
                        title = stringResource(R.string.manageAccounts_role_organizer),
                        description = stringResource(R.string.editUser_role_organizer_desc),
                        selected = uiState.selectedRole == AccountRole.Organizer,
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        onClick = { onRoleChange(AccountRole.Organizer) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RoleCard(
                        icon = Icons.Outlined.Person,
                        iconBackground = Color(0xFFE8E8E8),
                        iconTint = SquadGrayDark,
                        title = stringResource(R.string.manageAccounts_role_player),
                        description = stringResource(R.string.editUser_role_player_desc),
                        selected = uiState.selectedRole == AccountRole.Player,
                        enabled = !uiState.isSaving && !uiState.isDeleting,
                        onClick = { onRoleChange(AccountRole.Player) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Save button
            PrimaryButton(
                text = if (uiState.isSaving) "A guardar..." else stringResource(R.string.editUser_save),
                onClick = onSaveClick,
                enabled = !uiState.isSaving && !uiState.isDeleting
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Send Message
            OutlinedButton(
                onClick = onSendMessageClick,
                enabled = !uiState.isSaving && !uiState.isDeleting,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.5.dp, SquadOrange),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SquadOrange)
            ) {
                Text(
                    text = stringResource(R.string.editUser_send_message),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Suspend / Reactivate
            OutlinedButton(
                onClick = onToggleSuspend,
                enabled = !uiState.isSaving && !uiState.isDeleting,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(
                    1.5.dp,
                    if (uiState.isSuspended) Color(0xFF2E7D32) else Color(0xFFE65100)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (uiState.isSuspended) Color(0xFF2E7D32) else Color(0xFFE65100)
                )
            ) {
                Text(
                    text = stringResource(
                        if (uiState.isSuspended) R.string.editUser_reactivate
                        else R.string.editUser_suspend
                    ),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delete
            OutlinedButton(
                onClick = onDeleteClick,
                enabled = !uiState.isSaving && !uiState.isDeleting,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.5.dp, SquadError),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SquadError)
            ) {
                Text(
                    text = stringResource(R.string.editUser_delete),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (uiState.errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = uiState.errorMessage,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadError
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.editUser_cancel),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBackClick() }
                    .padding(vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun RoleCard(
    icon: ImageVector,
    iconBackground: Color,
    iconTint: Color,
    title: String,
    description: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) SquadOrangeLight else Color.White,
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) SquadOrange else Color(0xFFE0E0E0)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackground, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) SquadOrangeDark else SquadTextPrimary
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = SquadTextSecondary
                )
            }

            RadioButton(
                selected = selected,
                onClick = if (enabled) onClick else null,
                enabled = enabled,
                colors = RadioButtonDefaults.colors(
                    selectedColor = SquadOrange,
                    unselectedColor = Color(0xFFBDBDBD)
                )
            )
        }
    }
}
