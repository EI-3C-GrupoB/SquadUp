package com.example.squadup.features.teams.createteam

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.theme.SquadBackground
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadSurface
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary
import com.example.squadup.core.ui.theme.SquadWhite
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.toIcon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateTeamScreen(
    uiState: CreateTeamUiState,
    onBackClick: () -> Unit,
    onCreateTeamClick: () -> Unit,
    onTeamNameChange: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onSportTypeSelected: (SportType) -> Unit,
    onTeamDescriptionChange: (String) -> Unit,
    onPrivateTeamChange: (Boolean) -> Unit,
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
                title = "Create Team",
                showBackButton = true,
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick,
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
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            CreateTeamHero()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 22.dp, bottom = 24.dp)
            ) {
                CreateTeamLabel(
                    text = "Team Name",
                    required = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                CreateTeamTextField(
                    value = uiState.teamName,
                    onValueChange = onTeamNameChange,
                    placeholder = "Ex: Midnight Hoops",
                    singleLine = true,
                    minHeight = 50.dp
                )

                Spacer(modifier = Modifier.height(22.dp))

                CreateTeamLabel(
                    text = "Sport Type",
                    required = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        SportType.BASKETBALL,
                        SportType.SOCCER,
                        SportType.VOLLEYBALL,
                        SportType.PADDLE,
                        SportType.FUTSAL
                    ).forEach { sportType ->
                        SportTypeChip(
                            sportType = sportType,
                            selected = uiState.selectedSportType == sportType,
                            onClick = { onSportTypeSelected(sportType) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                CreateTeamLabel(
                    text = "Team Description",
                    required = false
                )

                Spacer(modifier = Modifier.height(8.dp))

                CreateTeamTextField(
                    value = uiState.teamDescription,
                    onValueChange = onTeamDescriptionChange,
                    placeholder = "Describe your team's mission, training schedule, or skill level expectations...",
                    singleLine = false,
                    minHeight = 118.dp
                )

                Spacer(modifier = Modifier.height(22.dp))

                PrivateTeamCard(
                    checked = uiState.isPrivateTeam,
                    onCheckedChange = onPrivateTeamChange
                )

                Spacer(modifier = Modifier.height(26.dp))

                Button(
                    onClick = onCreateTeamClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = SquadWhite
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 5.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Groups,
                        contentDescription = null,
                        modifier = Modifier.size(19.dp)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "Create Squad",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateTeamHero() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFF8F4),
                        Color(0xFFF3EEF4),
                        Color(0xFFF0ECEB)
                    )
                )
            )
            .padding(horizontal = 26.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SportsBasketball,
            contentDescription = null,
            tint = SquadOrange.copy(alpha = 0.07f),
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.CenterEnd)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(104.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(94.dp)
                        .background(
                            color = Color.Transparent,
                            shape = CircleShape
                        )
                )

                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(
                            color = Color.Transparent,
                            shape = CircleShape
                        )
                )

                Surface(
                    modifier = Modifier
                        .size(94.dp),
                    color = SquadWhite.copy(alpha = 0.55f),
                    shape = CircleShape,
                    border = BorderStroke(1.5.dp, SquadOrange.copy(alpha = 0.35f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = null,
                                tint = SquadTextPrimary.copy(alpha = 0.75f),
                                modifier = Modifier.size(28.dp)
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "Upload Logo",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = SquadTextSecondary
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(26.dp)
                        .background(SquadOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = null,
                        tint = SquadWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Create your squad identity",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Add the core details so players know what your team is about.",
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CreateTeamLabel(
    text: String,
    required: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9A3A00),
            modifier = Modifier.weight(1f)
        )

        if (required) {
            Text(
                text = "Required",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextSecondary
            )
        }
    }
}

@Composable
private fun CreateTeamTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean,
    minHeight: androidx.compose.ui.unit.Dp
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        textStyle = TextStyle(
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = SquadTextPrimary,
            fontWeight = FontWeight.Medium
        ),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .background(SquadWhite, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopStart
            ) {
                if (value.isBlank()) {
                    Text(
                        text = placeholder,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color(0xFF7B8496)
                    )
                }

                innerTextField()
            }
        }
    )
}

@Composable
private fun SportTypeChip(
    sportType: SportType,
    selected: Boolean,
    onClick: () -> Unit
) {
    val label = when (sportType) {
        SportType.SOCCER -> "Soccer"
        SportType.BASKETBALL -> "Basketball"
        SportType.PADDLE -> "Paddle"
        SportType.VOLLEYBALL -> "Volleyball"
        SportType.FUTSAL -> "Futsal"
    }

    Surface(
        modifier = Modifier
            .height(42.dp)
            .clickable(onClick = onClick),
        color = if (selected) SquadOrange else SquadSurface,
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) SquadOrange else SquadOrange.copy(alpha = 0.35f)
        ),
        shadowElevation = if (selected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = sportType.toIcon(),
                contentDescription = null,
                tint = if (selected) SquadWhite else SquadOrange,
                modifier = Modifier.size(17.dp)
            )

            Spacer(modifier = Modifier.size(7.dp))

            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) SquadWhite else SquadTextPrimary
            )
        }
    }
}

@Composable
private fun PrivateTeamCard(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) },
        color = Color(0xFFFFF7F3),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SquadOrange.copy(alpha = 0.28f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFFE4E0FF), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (checked) Icons.Outlined.Lock else Icons.Outlined.VisibilityOff,
                    contentDescription = null,
                    tint = Color(0xFF5B5FD6),
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

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Membership by invite only",
                    fontSize = 12.sp,
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
                    uncheckedTrackColor = Color(0xFFD8D8D8)
                )
            )
        }
    }
}