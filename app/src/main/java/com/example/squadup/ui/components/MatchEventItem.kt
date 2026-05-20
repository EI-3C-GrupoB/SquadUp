package com.example.squadup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary

enum class MatchEventType {
    Goal,
    Foul
}

@Composable
fun MatchEventItem(
    type: MatchEventType,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (type) {
        MatchEventType.Goal -> Icons.Outlined.SportsSoccer
        MatchEventType.Foul -> Icons.Outlined.Warning
    }

    val iconBackground = when (type) {
        MatchEventType.Goal -> Color(0xFFFFE1D3)
        MatchEventType.Foul -> Color(0xFFF1F1F1)
    }

    val iconTint = when (type) {
        MatchEventType.Goal -> SquadOrange
        MatchEventType.Foul -> Color(0x00000000)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(28.dp)
                .background(iconBackground, CircleShape)
                .padding(7.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = SquadTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = subtitle,
                color = Color(0x00000000),
                fontSize = 12.sp
            )
        }
    }
}