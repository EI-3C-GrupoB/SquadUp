package com.example.squadup.ui.screens.team

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.ManualInviteCard
import com.example.squadup.ui.components.ShareInviteOptionsRow
import com.example.squadup.ui.components.ShareInviteOptionUi
import com.example.squadup.ui.components.SuggestedContactUi
import com.example.squadup.ui.components.SuggestedContactsCard
import com.example.squadup.ui.components.TeamInviteCodeCard
import com.example.squadup.ui.components.defaultShareInviteOptions
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGray
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun InviteSquadScreen(
    inviteCode: String,
    manualInviteValue: String,
    onManualInviteChange: (String) -> Unit,
    onCopyInviteCodeClick: () -> Unit,
    onShareOptionClick: (ShareInviteOptionUi) -> Unit,
    onSyncContactsClick: () -> Unit,
    onInviteContactClick: (SuggestedContactUi) -> Unit,
    onSendManualInviteClick: () -> Unit,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contacts = remember {
        listOf(
            SuggestedContactUi(
                name = "Marcus Jensen",
                handle = "@marcus_lifts • In your\ncontacts",
                avatarColor = Color(0xFF1F2937)
            ),
            SuggestedContactUi(
                name = "Sarah Chen",
                handle = "@schen_pro • Frequent\nsquad mate",
                avatarColor = Color(0xFFD9777B),
                sent = true
            ),
            SuggestedContactUi(
                name = "David Okafor",
                handle = "@dave_hoops • In your\ncontacts",
                avatarColor = Color(0xFFB8AAA0)
            )
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            AppHeader(
                showLogo = true,
                title = "Teams",
                showBackButton = false,
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificações",
                            tint = SquadGrayDark
                        )
                    }

                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Definições",
                            tint = SquadGrayDark
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 18.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Invite Your Squad",
                fontSize = 27.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Black,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Grow your team and dominate the next\ntournament.",
                fontSize = 15.sp,
                lineHeight = 21.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(22.dp))

            TeamInviteCodeCard(
                inviteCode = inviteCode,
                onCopyClick = onCopyInviteCodeClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "Share Invite Link",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            ShareInviteOptionsRow(
                options = defaultShareInviteOptions(),
                onOptionClick = onShareOptionClick,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Suggested Contacts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Sync Contacts",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrangeDark,
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .clickable(onClick = onSyncContactsClick)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            SuggestedContactsCard(
                contacts = contacts,
                onInviteClick = onInviteContactClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            ManualInviteCard(
                value = manualInviteValue,
                onValueChange = onManualInviteChange,
                onSendClick = onSendManualInviteClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadGray,
                    contentColor = Color(0xFF632600)
                )
            ) {
                Text(
                    text = "Back",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InviteSquadScreenPreview() {
    var inviteValue by remember { mutableStateOf("") }

    InviteSquadScreen(
        inviteCode = "SQUAD-\nX92",
        manualInviteValue = inviteValue,
        onManualInviteChange = { inviteValue = it },
        onCopyInviteCodeClick = {},
        onShareOptionClick = {},
        onSyncContactsClick = {},
        onInviteContactClick = {},
        onSendManualInviteClick = {},
        onBackClick = {},
        onNotificationsClick = {},
        onSettingsClick = {}
    )
}
