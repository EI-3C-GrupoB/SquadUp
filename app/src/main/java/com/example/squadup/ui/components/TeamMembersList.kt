package com.example.squadup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.squadup.ui.theme.SquadBorder

data class TeamMemberUi(
    val name: String,
    val role: String,
    val isLeader: Boolean = false
)

@Composable
fun TeamMembersList(
    members: List<TeamMemberUi>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, SquadBorder)
    ) {
        Column {
            members.forEachIndexed { index, member ->
                TeamMemberItem(
                    name = member.name,
                    role = member.role,
                    isLeader = member.isLeader
                )

                if (index < members.lastIndex) {
                    HorizontalDivider(color = Color(0xFFF1F1F1))
                }
            }
        }
    }
}