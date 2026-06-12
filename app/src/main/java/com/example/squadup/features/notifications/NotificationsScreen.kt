package com.example.squadup.features.notifications

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Directions
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.Update
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.components.EmptyStateCard
import androidx.compose.ui.res.stringResource
import com.example.squadup.R
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadWhite
import com.example.squadup.core.utils.AppLanguage

@Composable
fun NotificationsScreen(
    uiState: NotificationsUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onRespondToJoinRequest: (NotificationItem, Boolean) -> Unit,
    onDeleteNotification: (String) -> Unit,
    onBackClick: () -> Unit,
    onEventNotificationClick: (String) -> Unit = {},
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    notificationsCount: Int = 0
) {
    var selectedNotificationForDialog by remember { mutableStateOf<NotificationItem?>(null) }

    if (selectedNotificationForDialog != null) {
        val notification = selectedNotificationForDialog!!
        AlertDialog(
            onDismissRequest = { selectedNotificationForDialog = null },
            title = { Text(text = notification.title) },
            text = { Text(text = notification.description) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRespondToJoinRequest(notification, true)
                        selectedNotificationForDialog = null
                    }
                ) {
                    Text("Aceitar", color = SquadOrange)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onRespondToJoinRequest(notification, false)
                        selectedNotificationForDialog = null
                    }
                ) {
                    Text("Recusar", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                title = "SquadUp",
                showBackButton = false,
                showNotificationsButton = true,
                notificationsCount = notificationsCount,
                showSettingsButton = true,
                onNotificationsClick = onNotificationsClick,
                onBackClick = onBackClick,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onAdminViewChange = onAdminViewChange,
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
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = responsiveHorizontalPadding(18.dp))
                .padding(top = 18.dp, bottom = 28.dp)
        ) {
            Text(
                text = stringResource(R.string.notifications_page_title),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.notifications_subtitle),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.todayNotifications.isEmpty() && uiState.earlierNotifications.isEmpty()) {
                EmptyStateCard(
                    title = stringResource(R.string.notifications_empty_title),
                    message = stringResource(R.string.notifications_empty_message),
                    icon = Icons.Outlined.Notifications
                )
            } else {
                if (uiState.todayNotifications.isNotEmpty()) {
                    NotificationSectionHeader(
                        title = stringResource(R.string.notifications_section_today),
                        badgeText = stringResource(R.string.notifications_new_badge, uiState.newNotificationsCount)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            uiState.todayNotifications.forEach { notification ->
                                NotificationCard(
                                    notification = notification,
                                    onRespond = onRespondToJoinRequest,
                                    onDelete = { onDeleteNotification(notification.id) },
                                    onClick = {
                                        when {
                                            notification.type == NotificationType.TEAM && notification.referenceType == "convite" ->
                                                selectedNotificationForDialog = notification
                                            notification.referenceType == "evento" && notification.referenceId != null ->
                                                onEventNotificationClick(notification.referenceId.toString())
                                        }
                                    }
                                )
                            }
                        }

                    Spacer(modifier = Modifier.height(22.dp))
                }

                if (uiState.earlierNotifications.isNotEmpty()) {
                    NotificationSectionHeader(
                        title = stringResource(R.string.notifications_section_earlier),
                        badgeText = null
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SquadGrayLight)
                    ) {
                        Column {
                            uiState.earlierNotifications.forEachIndexed { index, notification ->
                                CompactNotificationRow(
                                    notification = notification,
                                    onDelete = { onDeleteNotification(notification.id) }
                                )

                                if (index < uiState.earlierNotifications.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(12.dp)),
                                        color = SquadGrayLight
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationSectionHeader(
    title: String,
    badgeText: String?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (badgeText != null) {
            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = badgeText.uppercase(),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = SquadWhite,
                modifier = Modifier
                    .background(SquadOrange, RoundedCornerShape(999.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun NotificationCard(
    notification: NotificationItem,
    onRespond: (NotificationItem, Boolean) -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SquadOrange.copy(alpha = 0.35f)),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                NotificationIcon(type = notification.type)

                Spacer(modifier = Modifier.size(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = notification.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = notification.timeLabel,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        androidx.compose.material3.IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = notification.description,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (notification.distanceLabel != null) {
                Spacer(modifier = Modifier.height(10.dp))

                NotificationImagePreview(
                    distanceLabel = notification.distanceLabel
                )
            }

            if (notification.primaryAction != null || notification.secondaryAction != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (notification.primaryAction != null) {
                        Button(
                            onClick = { onRespond(notification, true) },
                            modifier = Modifier.height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SquadOrange,
                                contentColor = SquadWhite
                            ),
                            contentPadding = ButtonDefaults.ContentPadding
                        ) {
                            Text(
                                text = notification.primaryAction,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (notification.secondaryAction != null) {
                        OutlinedButton(
                            onClick = { onRespond(notification, false) },
                            modifier = Modifier.height(38.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Text(
                                text = notification.secondaryAction,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationImagePreview(
    distanceLabel: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF2A1003),
                        Color(0xFF7A3A10),
                        Color(0xFF111111)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Icon(
            imageVector = Icons.Outlined.SportsBasketball,
            contentDescription = null,
            tint = SquadWhite.copy(alpha = 0.16f),
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(topEnd = 8.dp))
                .padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Place,
                contentDescription = null,
                tint = SquadWhite,
                modifier = Modifier.size(12.dp)
            )

            Spacer(modifier = Modifier.size(4.dp))

            Text(
                text = distanceLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SquadWhite
            )
        }
    }
}

@Composable
private fun CompactNotificationRow(
    notification: NotificationItem,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 74.dp)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        NotificationIcon(type = notification.type)

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = notification.timeLabel,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.width(8.dp))

                androidx.compose.material3.IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notification.description,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun NotificationIcon(
    type: NotificationType
) {
    val icon: ImageVector
    val backgroundColor: Color
    val iconColor: Color

    when (type) {
        NotificationType.MATCH -> {
            icon = Icons.Outlined.SportsBasketball
            backgroundColor = Color(0xFFFFE8D8)
            iconColor = SquadOrange
        }

        NotificationType.EVENT -> {
            icon = Icons.Outlined.Notifications
            backgroundColor = Color(0xFFE8F0FF)
            iconColor = Color(0xFF4A6CF7)
        }

        NotificationType.UPDATE -> {
            icon = Icons.Outlined.Update
            backgroundColor = Color(0xFFECECEC)
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant
        }

        NotificationType.TEAM -> {
            icon = Icons.Outlined.Groups
            backgroundColor = Color(0xFFE7FFF0)
            iconColor = Color(0xFF00A85A)
        }
    }

    Box(
        modifier = Modifier
            .size(34.dp)
            .background(backgroundColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(18.dp)
        )
    }
}