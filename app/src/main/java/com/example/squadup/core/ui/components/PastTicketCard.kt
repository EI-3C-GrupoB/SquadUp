package com.example.squadup.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.enums.TicketStatus
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary
import com.example.squadup.core.utils.toIcon
import com.example.squadup.core.utils.toLabel
import com.example.squadup.core.utils.toLabelBackground
import com.example.squadup.core.utils.toLabelColor

@Composable
fun PastTicketCard(
    title: String,
    status: TicketStatus,
    date: String,
    location: String,
    sportType: SportType,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                sportType.color,
                                sportType.color.copy(alpha = 0.6f),
                                sportType.color.copy(alpha = 0.85f)
                            )
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = sportType.toIcon(),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = status.toLabel(context),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = status.toLabelColor(),
                        modifier = Modifier
                            .background(status.toLabelBackground(), RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                PastTicketInfoRow(Icons.Outlined.CalendarMonth, date)
                Spacer(modifier = Modifier.height(4.dp))
                PastTicketInfoRow(Icons.Outlined.LocationOn, location)
            }
        }
    }
}

@Composable
private fun PastTicketInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SquadTextSecondary,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 14.sp, color = SquadTextSecondary)
    }
}
