package com.example.squadup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadGray
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

data class ShareInviteOptionUi(
    val label: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconColor: Color
)

data class SuggestedContactUi(
    val name: String,
    val handle: String,
    val avatarColor: Color,
    val sent: Boolean = false
)

@Composable
fun TeamInviteCodeCard(
    inviteCode: String,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFFFFBF7),
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Text(
                text = "TEAM INVITE CODE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawRoundRect(
                            color = SquadOrange.copy(alpha = 0.4f),
                            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx()),
                            style = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(
                                    intervals = floatArrayOf(12f, 8f),
                                    phase = 0f
                                )
                            )
                        )
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = inviteCode,
                    fontSize = 26.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp,
                    color = SquadOrange,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = onCopyClick,
                    shape = RoundedCornerShape(7.dp),
                    contentPadding = ButtonDefaults.ContentPadding,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Copy",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ShareInviteOptionsRow(
    options: List<ShareInviteOptionUi>,
    onOptionClick: (ShareInviteOptionUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        options.forEach { option ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onOptionClick(option) }
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(option.backgroundColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = option.icon,
                        contentDescription = null,
                        tint = option.iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = option.label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )
            }
        }
    }
}

@Composable
fun SuggestedContactRow(
    contact: SuggestedContactUi,
    onInviteClick: (SuggestedContactUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(contact.avatarColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(25.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = contact.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = contact.handle,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = SquadTextSecondary
            )
        }

        if (contact.sent) {
            Button(
                onClick = { },
                modifier = Modifier.height(38.dp),
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                ),
                contentPadding = ButtonDefaults.ContentPadding
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "Sent",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            OutlinedButton(
                onClick = { onInviteClick(contact) },
                modifier = Modifier.height(38.dp),
                shape = RoundedCornerShape(999.dp),
                border = BorderStroke(1.5.dp, SquadOrange),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SquadSurface,
                    contentColor = SquadOrange
                ),
                contentPadding = ButtonDefaults.ContentPadding
            ) {
                Text(
                    text = "Invite",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SuggestedContactsCard(
    contacts: List<SuggestedContactUi>,
    onInviteClick: (SuggestedContactUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            contacts.forEachIndexed { index, contact ->
                SuggestedContactRow(
                    contact = contact,
                    onInviteClick = onInviteClick
                )

                if (index < contacts.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(SquadGrayLight)
                    )
                }
            }
        }
    }
}

@Composable
fun ManualInviteCard(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFEFE9EE),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Invite via Username or Email",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = SquadTextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .background(SquadSurface, RoundedCornerShape(6.dp))
                        .border(1.dp, SquadGrayLight, RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.isBlank()) {
                                Text(
                                    text = "Enter username or email",
                                    fontSize = 14.sp,
                                    color = SquadTextSecondary
                                )
                            }

                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onSendClick,
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Send",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

fun defaultShareInviteOptions(): List<ShareInviteOptionUi> {
    return listOf(
        ShareInviteOptionUi(
            label = "WhatsApp",
            icon = Icons.Outlined.Share,
            backgroundColor = Color(0xFFE6FAEF),
            iconColor = Color(0xFF10B969)
        ),
        ShareInviteOptionUi(
            label = "Instagram",
            icon = Icons.Outlined.CameraAlt,
            backgroundColor = Color(0xFFFFEDF6),
            iconColor = Color(0xFFFF3C86)
        ),
        ShareInviteOptionUi(
            label = "Messenger",
            icon = Icons.Outlined.Sms,
            backgroundColor = Color(0xFFE9F3FF),
            iconColor = Color(0xFF337DFF)
        ),
        ShareInviteOptionUi(
            label = "More",
            icon = Icons.Outlined.MoreHoriz,
            backgroundColor = Color(0xFFE8E6EA),
            iconColor = Color(0xFF6B646D)
        )
    )
}
