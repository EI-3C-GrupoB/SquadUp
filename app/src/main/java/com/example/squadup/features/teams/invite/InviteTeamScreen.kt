package com.example.squadup.features.teams.invite

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadWhite
import com.example.squadup.core.utils.AppLanguage
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext

@Composable
fun InviteTeamScreen(
    uiState: InviteTeamUiState,
    onBackClick: () -> Unit,
    onUsernameOrEmailChange: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onInviteContactClick: (String) -> Unit,
    onSendInviteClick: () -> Unit,
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                title = "Invite",
                showBackButton = true,
                onNotificationsClick = onNotificationsClick,
                onBackClick = onBackClick,
                showNotificationsButton = true,
                showSettingsButton = true,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onAdminViewChange = onAdminViewChange,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = responsiveHorizontalPadding(18.dp))
                .padding(top = 18.dp, bottom = 28.dp)
        ) {
            Text(
                text = "Invite Your Squad",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Grow your team and prepare for the next tournament.",
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(18.dp))

            InviteCodeCard(
                inviteCode = uiState.inviteCode
            )

            Spacer(modifier = Modifier.height(20.dp))

            InviteByUsernameCard(
                value = uiState.usernameOrEmail,
                onValueChange = onUsernameOrEmailChange,
                onSendClick = onSendInviteClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            InviteSectionHeader(
                title = "Suggested Contacts",
                action = "Sync Contacts"
            )

            Spacer(modifier = Modifier.height(12.dp))

            SuggestedContactsCard(
                contacts = uiState.suggestedContacts,
                onInviteContactClick = onInviteContactClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            InviteSectionHeader(
                title = "Share Invite Link",
                action = null
            )

            Spacer(modifier = Modifier.height(14.dp))

            ShareInviteOptions(
                inviteCode = uiState.inviteCode
            )
        }
    }
}

@Composable
private fun InviteCodeCard(
    inviteCode: String
) {
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = "TEAM INVITE CODE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF9A3A00),
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFFF5F0),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = SquadOrange.copy(alpha = 0.35f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = inviteCode,
                        fontSize = 23.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = SquadOrange,
                        letterSpacing = 2.2.sp,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            copyInviteCode(
                                context = context,
                                inviteCode = inviteCode
                            )
                        },
                        modifier = Modifier
                            .height(38.dp)
                            .width(92.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SquadOrange,
                            contentColor = SquadWhite
                        ),
                        contentPadding = ButtonDefaults.ContentPadding
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "Copy",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InviteSectionHeader(
    title: String,
    action: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        if (action != null) {
            Text(
                text = action,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrange
            )
        }
    }
}

@Composable
private fun ShareInviteOptions(
    inviteCode: String
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ShareOption(
            label = "WhatsApp",
            icon = Icons.Outlined.Share,
            backgroundColor = Color(0xFFE7FFF0),
            iconColor = Color(0xFF00A85A),
            onClick = {
                shareInviteCode(
                    context = context,
                    inviteCode = inviteCode,
                    packageName = "com.whatsapp"
                )
            }
        )

        ShareOption(
            label = "Instagram",
            icon = Icons.Outlined.PhotoCamera,
            backgroundColor = Color(0xFFFFEDF5),
            iconColor = Color(0xFFE83A7A),
            onClick = {
                shareInviteCode(
                    context = context,
                    inviteCode = inviteCode,
                    packageName = "com.instagram.android"
                )
            }
        )

        ShareOption(
            label = "Messenger",
            icon = Icons.Outlined.Forum,
            backgroundColor = Color(0xFFEAF3FF),
            iconColor = Color(0xFF276EF1),
            onClick = {
                shareInviteCode(
                    context = context,
                    inviteCode = inviteCode,
                    packageName = "com.facebook.orca"
                )
            }
        )
    }
}

@Composable
private fun ShareOption(
    label: String,
    icon: ImageVector,
    backgroundColor: Color,
    iconColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(backgroundColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun SuggestedContactsCard(
    contacts: List<SuggestedContactItem>,
    onInviteContactClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            contacts.forEachIndexed { index, contact ->
                SuggestedContactRow(
                    contact = contact,
                    onInviteClick = { onInviteContactClick(contact.id) }
                )

                if (index < contacts.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(SquadGrayLight.copy(alpha = 0.6f))
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestedContactRow(
    contact: SuggestedContactItem,
    onInviteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(initials = contact.initials)

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = contact.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${contact.username} • ${contact.subtitle}",
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        InviteStatusButton(
            status = contact.status,
            onClick = onInviteClick
        )
    }
}

@Composable
private fun ContactAvatar(
    initials: String
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(SquadOrangeLight, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = SquadOrange
        )
    }
}

@Composable
private fun InviteStatusButton(
    status: InviteStatus,
    onClick: () -> Unit
) {
    val enabled = status == InviteStatus.INVITE

    val label = when (status) {
        InviteStatus.INVITE -> "Invite"
        InviteStatus.SENT -> "✓ Sent"
        InviteStatus.MEMBER -> "Member"
    }

    val backgroundColor = when (status) {
        InviteStatus.INVITE -> MaterialTheme.colorScheme.surface
        InviteStatus.SENT -> Color(0xFF26323F)
        InviteStatus.MEMBER -> Color(0xFFE0E0E0)
    }

    val textColor = when (status) {
        InviteStatus.INVITE -> SquadOrange
        InviteStatus.SENT -> SquadWhite
        InviteStatus.MEMBER -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Surface(
        modifier = Modifier
            .height(36.dp)
            .clickable(enabled = enabled, onClick = onClick),
        color = backgroundColor,
        shape = RoundedCornerShape(999.dp),
        border = if (status == InviteStatus.INVITE) {
            BorderStroke(1.4.dp, SquadOrange)
        } else {
            null
        }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
private fun InviteByUsernameCard(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFF2EDF2),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Invite via Username or Email",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                        .padding(horizontal = responsiveHorizontalPadding(12.dp)),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.isBlank()) {
                                Text(
                                    text = "Enter username or email",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onSendClick,
                    modifier = Modifier
                        .height(42.dp)
                        .width(88.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = SquadWhite
                    ),
                    contentPadding = ButtonDefaults.ContentPadding
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "Send",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
private fun copyInviteCode(
    context: Context,
    inviteCode: String
) {
    if (inviteCode.isBlank()) return

    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("SquadUp invite code", inviteCode)

    clipboard.setPrimaryClip(clip)

    Toast.makeText(
        context,
        "Código copiado.",
        Toast.LENGTH_SHORT
    ).show()
}

private fun shareInviteCode(
    context: Context,
    inviteCode: String,
    packageName: String
) {
    if (inviteCode.isBlank()) return

    val message = "Junta-te à minha equipa no SquadUp com este código: $inviteCode"

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, message)
        setPackage(packageName)
    }

    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
        }

        context.startActivity(
            Intent.createChooser(
                fallbackIntent,
                "Partilhar convite"
            )
        )
    }
}