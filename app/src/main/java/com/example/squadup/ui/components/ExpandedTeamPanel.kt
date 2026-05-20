package com.example.squadup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadBorder
import com.example.squadup.ui.theme.SquadIconSecondary
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

data class TeamRosterMemberUi(
    val name: String,
    val role: String,
    val isCaptain: Boolean = false
)

@Composable
fun ExpandedTeamPanel(
    teamName: String,
    sport: String,
    location: String,
    inviteCode: String,
    members: List<TeamRosterMemberUi>,
    isManagingRoster: Boolean,
    onInviteMembersClick: () -> Unit,
    onTeamSettingsClick: () -> Unit,
    onCopyInviteCodeClick: () -> Unit,
    onPromoteClick: (TeamRosterMemberUi) -> Unit,
    onRemoveClick: (TeamRosterMemberUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, SquadBorder)
    ) {
        Column {
            TeamHeroHeader(
                teamName = teamName,
                sport = sport,
                location = location
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "INVITE CODE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextSecondary,
                    letterSpacing = 1.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = inviteCode,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .background(Color(0xFFC9BDB8), RoundedCornerShape(6.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    )

                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        tint = SquadOrange,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(18.dp)
                            .clickable(onClick = onCopyInviteCodeClick)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onInviteMembersClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, SquadOrange),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = SquadOrange
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Send,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Invite\nMembers",
                            fontSize = 12.sp,
                            lineHeight = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (isManagingRoster) {
                        Button(
                            onClick = onTeamSettingsClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SquadOrange,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Team\nSettings",
                                fontSize = 12.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = onTeamSettingsClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, SquadOrange),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = SquadOrange
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "Team\nSettings",
                                fontSize = 12.sp,
                                lineHeight = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Row {
                    Text(
                        text = "Roster Management",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SquadTextPrimary
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${members.size} Players Active",
                        fontSize = 12.sp,
                        color = SquadTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                members.forEach { member ->
                    TeamRosterMemberRow(
                        name = member.name,
                        role = member.role,
                        isCaptain = member.isCaptain,
                        isManaging = isManagingRoster,
                        onPromoteClick = { onPromoteClick(member) },
                        onRemoveClick = { onRemoveClick(member) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun TeamHeroHeader(
    teamName: String,
    sport: String,
    location: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(132.dp)
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1D1208),
                        Color(0xFF5B2605),
                        Color(0xFF1D1208)
                    )
                )
            )
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(SquadOrange, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.SportsBasketball,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = teamName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = sport.uppercase(),
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(SquadOrange, RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )

            Text(
                text = location,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                color = Color.White
            )
        }
    }
}