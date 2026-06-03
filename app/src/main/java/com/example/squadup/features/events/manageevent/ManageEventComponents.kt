package com.example.squadup.features.events.manageevent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.ui.theme.*

@Composable
internal fun MetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isWarning: Boolean = false
) {
    val accentColor = if (isWarning) Color(0xFFFFA000) else SquadOrange
    Surface(
        modifier = modifier,
        color = if (isWarning) Color(0xFFFFF8E1) else Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(icon, null, tint = accentColor, modifier = Modifier.size(18.dp))
            Column {
                Text(value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold,
                    color = if (isWarning) accentColor else SquadTextPrimary)
                Text(label, fontSize = 10.sp, color = SquadTextSecondary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
internal fun MetricRow(metrics: List<Triple<ImageVector, String, String>>, warnings: Set<Int> = emptySet()) {
    metrics.chunked(2).forEachIndexed { rowIdx, row ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            row.forEachIndexed { colIdx, (icon, label, value) ->
                val globalIdx = rowIdx * 2 + colIdx
                MetricCard(
                    icon = icon,
                    label = label,
                    value = value,
                    modifier = Modifier.weight(1f),
                    isWarning = globalIdx in warnings
                )
            }
            if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
internal fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, null, tint = SquadOrange, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = SquadTextPrimary)
            Text(text = label, fontSize = 11.sp, color = SquadTextSecondary, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
internal fun StatusBadge(status: EventStatus) {
    val (labelRes, bgColor) = when (status) {
        EventStatus.DRAFT               -> R.string.event_status_draft               to SquadGray
        EventStatus.REGISTRATION_OPEN   -> R.string.event_status_registration_open   to Color(0xFF2F9D73)
        EventStatus.REGISTRATION_CLOSED -> R.string.event_status_registration_closed to Color(0xFF607D8B)
        EventStatus.ONGOING             -> R.string.event_status_ongoing             to SquadOrange
        EventStatus.FINISHED            -> R.string.event_status_finished            to SquadGrayDark
        EventStatus.CANCELLED           -> R.string.event_status_cancelled           to SquadError
    }
    Text(
        text = stringResource(labelRes),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier
            .background(bgColor, RoundedCornerShape(999.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
