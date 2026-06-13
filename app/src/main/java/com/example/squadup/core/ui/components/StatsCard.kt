package com.example.squadup.core.ui.components

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadWhite

enum class StatsCardStyle {
    DEFAULT, HIGHLIGHT
}

@Composable
fun StatsCard(
    label: String,
    value: String,
    style: StatsCardStyle = StatsCardStyle.DEFAULT,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (style) {
        StatsCardStyle.DEFAULT -> MaterialTheme.colorScheme.surface
        StatsCardStyle.HIGHLIGHT -> SquadOrange
    }

    val valueColor = when (style) {
        StatsCardStyle.DEFAULT -> MaterialTheme.colorScheme.onSurface
        StatsCardStyle.HIGHLIGHT -> SquadWhite
    }

    val labelColor = when (style) {
        StatsCardStyle.DEFAULT -> MaterialTheme.colorScheme.onSurfaceVariant
        StatsCardStyle.HIGHLIGHT -> SquadWhite.copy(alpha = 0.85f)
    }

    val iconColor = when (style) {
        StatsCardStyle.DEFAULT -> SquadOrange
        StatsCardStyle.HIGHLIGHT -> SquadWhite
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = label,
                fontSize = 12.sp,
                color = labelColor,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}