package com.example.squadup.features.profile.tickets.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.ui.components.*
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage

@Composable
fun TicketDetailsScreen(
    uiState: TicketDetailsUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onAddToCalendarClick: () -> Unit,
    onShareTicketClick: () -> Unit,
    onSupportClick: () -> Unit,
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
                title = stringResource(R.string.ticketDetails_title),
                showBackButton = true,
                onBackClick = onBackClick,
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
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TicketConfirmedBadge()

                    Spacer(modifier = Modifier.height(14.dp))

                    TicketQrCard()

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = uiState.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = uiState.ticketType,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SquadTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SquadGrayLight)
                    Spacer(modifier = Modifier.height(14.dp))

                    TicketDetailInfoRow(
                        icon = Icons.Default.CalendarMonth,
                        label = stringResource(R.string.ticketDetails_date_label),
                        value = uiState.dateTime,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TicketDetailInfoRow(
                        icon = Icons.Outlined.LocationOn,
                        label = stringResource(R.string.ticketDetails_location_label),
                        value = "${uiState.locationName}\n${uiState.locationDetail}",
                        iconBackground = Color(0xFFE2E0FF),
                        iconColor = Color(0xFF5B5FEF),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    MiniMapPreview(modifier = Modifier.padding(horizontal = 42.dp))

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        text = stringResource(R.string.ticketDetails_add_calendar),
                        onClick = onAddToCalendarClick,
                        trailingIcon = Icons.Default.CalendarMonth
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onShareTicketClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, SquadTextSecondary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = SquadTextPrimary
                        )
                    ) {
                        Icon(imageVector = Icons.Outlined.Share, contentDescription = null)
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                        Text(
                            text = stringResource(R.string.ticketDetails_share),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = stringResource(R.string.ticketDetails_support),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSupportClick),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TicketConfirmedBadge() {
    Surface(
        color = Color(0xFFE0F2F1),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(1.dp, Color(0xFF9AD8CF))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF00897B),
                modifier = Modifier.height(14.dp)
            )
            Spacer(modifier = Modifier.padding(horizontal = 3.dp))
            Text(
                text = stringResource(R.string.tickets_confirmed),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00796B)
            )
        }
    }
}
