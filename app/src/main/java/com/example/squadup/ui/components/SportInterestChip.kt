package com.example.squadup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadBorder
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun SportInterestChip(
    text: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        modifier = modifier,
        label = {
            Text(
                text = text,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) SquadOrange else SquadBorder
        ),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SquadOrange,
            selectedLabelColor = androidx.compose.ui.graphics.Color.White,
            selectedLeadingIconColor = androidx.compose.ui.graphics.Color.White,
            containerColor = androidx.compose.ui.graphics.Color.White,
            labelColor = SquadTextPrimary,
            iconColor = SquadTextPrimary
        )
    )
}