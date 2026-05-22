package com.example.squadup.ui.screens.team

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGray
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadWhite

@Composable
fun CreateTeamScreen(
    teamName: String,
    description: String,
    selectedSports: List<String>,
    isPrivateTeam: Boolean,
    onTeamNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSportToggle: (String) -> Unit,
    onPrivateTeamChange: (Boolean) -> Unit,
    onUploadLogoClick: () -> Unit,
    onCreateSquadClick: () -> Unit,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
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
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF5F0F2)
                    )
                    .padding(horizontal = 18.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LogoUploadBox(
                    onClick = onUploadLogoClick
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "High-performance squads start with\na strong identity. Add your team's\ncrest.",
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = SquadTextSecondary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(top = 14.dp)
            ) {
                FormLabel(text = "Team Name")

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = teamName,
                    onValueChange = onTeamNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words
                    ),
                    colors = createTeamTextFieldColors(),
                    shape = RoundedCornerShape(6.dp)
                )

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FormLabel(text = "Sport Type")

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "Required",
                        fontSize = 11.sp,
                        color = SquadTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SportTypeChip(
                        text = "Basketball",
                        selected = selectedSports.contains("Basketball"),
                        onClick = { onSportToggle("Basketball") }
                    )

                    SportTypeChip(
                        text = "Soccer",
                        selected = selectedSports.contains("Soccer"),
                        onClick = { onSportToggle("Soccer") }
                    )

                    SportTypeChip(
                        text = "Volleyball",
                        selected = selectedSports.contains("Volleyball"),
                        onClick = { onSportToggle("Volleyball") }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                FormLabel(text = "Team Description")

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp),
                    placeholder = {
                        Text(
                            text = "Describe your team's mission, training\nschedule, or skill level expectations...",
                            fontSize = 14.sp,
                            color = SquadTextSecondary
                        )
                    },
                    colors = createTeamTextFieldColors(),
                    shape = RoundedCornerShape(6.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrivateTeamOption(
                    checked = isPrivateTeam,
                    onCheckedChange = onPrivateTeamChange
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onCreateSquadClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = SquadTextPrimary
                    )
                ) {
                    Text(
                        text = "Create Squad",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.size(10.dp))

                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadGray,
                        contentColor = SquadTextPrimary
                    )
                ) {
                    Text(
                        text = "Back",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
private fun LogoUploadBox(
    onClick: () -> Unit
) {
    val borderColor = SquadOrangeLight

    Box(
        modifier = Modifier
            .size(112.dp)
            .drawBehind {
                drawOval(
                    color = borderColor,
                    size = Size(size.width, size.height),
                    style = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(12f, 8f),
                            phase = 0f
                        )
                    )
                )
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.CameraAlt,
                contentDescription = null,
                tint = SquadGrayDark,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Upload Logo",
                fontSize = 11.sp,
                color = SquadTextSecondary
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(24.dp)
                .background(SquadOrange, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = SquadTextPrimary,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun FormLabel(
    text: String
) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = SquadOrangeDark
    )
}

@Composable
private fun SportTypeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = SquadTextPrimary,
        modifier = Modifier
            .background(
                color = if (selected) SquadOrange else SquadSurface,
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 1.dp,
                color = if (selected) SquadOrange else SquadGray,
                shape = RoundedCornerShape(6.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 17.dp, vertical = 11.dp)
    )
}

@Composable
private fun PrivateTeamOption(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF7F0F3),
                shape = RoundedCornerShape(8.dp)
            )
            .border(
                width = 1.dp,
                color = SquadOrangeLight,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    color = Color(0xFFDCD8FF),
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = Color(0xFF5A5FEF),
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Private Team",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Text(
                text = "Membership by invite only",
                fontSize = 11.sp,
                color = SquadTextSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SquadWhite,
                checkedTrackColor = SquadOrange,
                uncheckedThumbColor = SquadWhite,
                uncheckedTrackColor = SquadGray,
                uncheckedBorderColor = SquadGray
            )
        )
    }
}

@Composable
private fun createTeamTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SquadOrange,
    unfocusedBorderColor = SquadGrayDark,
    focusedContainerColor = SquadSurface,
    unfocusedContainerColor = SquadSurface,
    focusedTextColor = SquadTextPrimary,
    unfocusedTextColor = SquadTextPrimary,
    cursorColor = SquadOrange,
    focusedPlaceholderColor = SquadTextSecondary,
    unfocusedPlaceholderColor = SquadTextSecondary
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateTeamScreenPreview() {
    var teamName by remember { mutableStateOf("CPL") }
    var description by remember { mutableStateOf("") }
    var isPrivateTeam by remember { mutableStateOf(false) }

    val selectedSports = remember {
        mutableStateListOf("Basketball")
    }

    CreateTeamScreen(
        teamName = teamName,
        description = description,
        selectedSports = selectedSports,
        isPrivateTeam = isPrivateTeam,
        onTeamNameChange = { teamName = it },
        onDescriptionChange = { description = it },
        onSportToggle = { sport ->
            if (selectedSports.contains(sport)) {
                selectedSports.remove(sport)
            } else {
                selectedSports.clear()
                selectedSports.add(sport)
            }
        },
        onPrivateTeamChange = { isPrivateTeam = it },
        onUploadLogoClick = {},
        onCreateSquadClick = {},
        onBackClick = {},
        onNotificationsClick = {},
        onSettingsClick = {}
    )
}