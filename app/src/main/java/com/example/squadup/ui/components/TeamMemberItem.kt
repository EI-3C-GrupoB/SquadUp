package com.example.squadup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun TeamMemberItem(
    name: String,
    role: String,
    isLeader: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Person,
            contentDescription = null,
            tint = if (isLeader) Color.White else SquadOrange,
            modifier = Modifier
                .size(34.dp)
                .background(
                    color = if (isLeader) Color(0xFF1F2937) else SquadOrangeLight,
                    shape = CircleShape
                )
                .padding(8.dp)
        )

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = name,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = SquadTextPrimary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = role,
            fontSize = 11.sp,
            color = if (isLeader) SquadOrange else Color(0x00000000)
        )
    }
}