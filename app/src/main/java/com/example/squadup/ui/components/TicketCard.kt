package com.example.squadup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun TicketCard(
    title: String,
    dateTime: String,
    location: String,
    seatInfo: String,
    accentColor: Color,
    onViewDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0x00000000)),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(168.dp)
                    .background(accentColor)
            )

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        TicketStatusBadge()

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = title,
                            fontSize = 23.sp,
                            lineHeight = 27.sp,
                            fontWeight = FontWeight.Bold,
                            color = SquadTextPrimary
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        TicketInfoRow(
                            icon = Icons.Outlined.CalendarMonth,
                            text = dateTime
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        TicketInfoRow(
                            icon = Icons.Outlined.LocationOn,
                            text = location
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(62.dp)
                                .background(Color(0xFFF3F4F6), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "▦",
                                fontSize = 28.sp,
                                color = SquadTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "▦",
                            fontSize = 20.sp,
                            color = SquadOrangeDark
                        )
                    }
                }

                HorizontalDivider(color = Color(0x00000000))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0x00000000))
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = seatInfo,
                        fontSize = 14.sp,
                        color = SquadTextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = onViewDetailsClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SquadOrangeDark,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "View Details",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TicketStatusBadge() {
    Row(
        modifier = Modifier
            .background(
                color = Color(0xFFE0F2F1),
                shape = RoundedCornerShape(999.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF00897B),
            modifier = Modifier.size(14.dp)
        )

        Text(
            text = "CONFIRMED",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00897B)
        )
    }
}

@Composable
private fun TicketInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SquadTextPrimary,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = text,
            fontSize = 15.sp,
            color = SquadTextPrimary
        )
    }
}