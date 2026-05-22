package com.example.squadup.ui.screens.tickets

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.components.MiniMapPreview
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.components.TicketDetailInfoRow
import com.example.squadup.ui.components.TicketQrCard
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun TicketDetailsScreen(
    selectedRoute: String,
    onBackClick: () -> Unit,
    onAddToCalendarClick: () -> Unit,
    onShareTicketClick: () -> Unit,
    onSupportClick: () -> Unit,
    onNavItemClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = "TICKET DETAILS",
                showBackButton = true,
                onBackClick = onBackClick
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
                        text = "Evening Tennis Singles",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Standard Entry • Participant",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SquadTextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0x00000000)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TicketDetailInfoRow(
                        icon = Icons.Default.CalendarMonth,
                        label = "Date & Time",
                        value = "Oct 24, 2023 • 6:30 PM"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TicketDetailInfoRow(
                        icon = Icons.Outlined.LocationOn,
                        label = "Location",
                        value = "Riverside Sports Complex,\nCourt #4",
                        iconBackground = Color(0xFFE2E0FF),
                        iconColor = Color(0xFF5B5FEF)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    MiniMapPreview(
                        modifier = Modifier.padding(horizontal = 42.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        text = "Add to Calendar",
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
                        Icon(
                            imageVector = Icons.Outlined.Share,
                            contentDescription = null
                        )

                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                        Text(
                            text = "Share Ticket",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "ⓘ  Need help with your ticket? Contact Support",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onSupportClick),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = SquadTextSecondary
            )
        }
    }
}

@Composable
private fun TicketConfirmedBadge() {
    Surface(
        color = Color(0xFFE0F2F1),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0xFF9AD8CF)
        )
    ) {
        androidx.compose.foundation.layout.Row(
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
                text = "CONFIRMED",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF00796B)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TicketDetailsScreenPreview() {
    TicketDetailsScreen(
        selectedRoute = "profile",
        onBackClick = {},
        onAddToCalendarClick = {},
        onShareTicketClick = {},
        onSupportClick = {},
        onNavItemClick = {}
    )
}