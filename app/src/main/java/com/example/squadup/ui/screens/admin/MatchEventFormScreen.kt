package com.example.squadup.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadUpTheme

enum class MatchEventFormType {
    Goal,
    Fault,
    Substitution,
    TwoSubstitutions
}

private val BorderSoft = Color(0xFFD4D8E4)
private val InputBackground = Color(0xFFF1F3FF)
private val RedTeam = Color(0xFFFF0505)
private val BlueTeam = Color(0xFF00B7F1)

@Composable
fun MatchEventFormScreen(
    type: MatchEventFormType,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onRedTeamClick: () -> Unit,
    onBlueTeamClick: () -> Unit,
    onAddSubstitutionClick: () -> Unit,
    onSaveChangesClick: () -> Unit,
    onBackToMatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            MatchEventFormTopBar(
                onNotificationsClick = onNotificationsClick,
                onSettingsClick = onSettingsClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp)
                .padding(top = 24.dp, bottom = 24.dp)
        ) {
            MatchEventFormCard(
                type = type,
                onRedTeamClick = onRedTeamClick,
                onBlueTeamClick = onBlueTeamClick,
                onAddSubstitutionClick = onAddSubstitutionClick,
                onSaveChangesClick = onSaveChangesClick,
                onBackToMatchClick = onBackToMatchClick
            )
        }
    }
}

@Composable
private fun MatchEventFormTopBar(
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 18.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Match Overview",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = SquadOrange,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = onNotificationsClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Notificações",
                    tint = SquadGrayDark,
                    modifier = Modifier.size(19.dp)
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Definições",
                    tint = SquadGrayDark,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MatchEventFormCard(
    type: MatchEventFormType,
    onRedTeamClick: () -> Unit,
    onBlueTeamClick: () -> Unit,
    onAddSubstitutionClick: () -> Unit,
    onSaveChangesClick: () -> Unit,
    onBackToMatchClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, BorderSoft),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (type) {
                    MatchEventFormType.Goal -> "Goal"
                    MatchEventFormType.Fault -> "Fault"
                    MatchEventFormType.Substitution,
                    MatchEventFormType.TwoSubstitutions -> "Substitution"
                },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = when (type) {
                    MatchEventFormType.Goal -> "Select who scored the goal!"
                    MatchEventFormType.Fault -> "Select who commited the fault!"
                    MatchEventFormType.Substitution,
                    MatchEventFormType.TwoSubstitutions -> "Select the substitutions!"
                },
                fontSize = 13.sp,
                color = SquadTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            TeamSelectorRow(
                onRedTeamClick = onRedTeamClick,
                onBlueTeamClick = onBlueTeamClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            when (type) {
                MatchEventFormType.Goal -> GoalFields()
                MatchEventFormType.Fault -> FaultFields()
                MatchEventFormType.Substitution -> SingleSubstitutionFields(
                    onAddSubstitutionClick = onAddSubstitutionClick
                )
                MatchEventFormType.TwoSubstitutions -> TwoSubstitutionsFields(
                    onAddSubstitutionClick = onAddSubstitutionClick
                )
            }

            Spacer(
                modifier = Modifier.height(
                    when (type) {
                        MatchEventFormType.Goal -> 58.dp
                        MatchEventFormType.Fault -> 118.dp
                        MatchEventFormType.Substitution -> 58.dp
                        MatchEventFormType.TwoSubstitutions -> 26.dp
                    }
                )
            )

            Button(
                onClick = onSaveChangesClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Save Changes",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(
                modifier = Modifier.fillMaxWidth(),
                color = SquadGrayLight,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "← Back to Match",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = SquadOrange,
                modifier = Modifier.clickable(onClick = onBackToMatchClick)
            )
        }
    }
}

@Composable
private fun TeamSelectorRow(
    onRedTeamClick: () -> Unit,
    onBlueTeamClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TeamLogoButton(
            emoji = "🦁",
            backgroundColor = RedTeam,
            onClick = onRedTeamClick
        )

        TeamLogoButton(
            emoji = "🛡️",
            backgroundColor = BlueTeam,
            onClick = onBlueTeamClick
        )
    }
}

@Composable
private fun TeamLogoButton(
    emoji: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(76.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF111827)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 38.sp
            )
        }
    }
}

@Composable
private fun GoalFields() {
    FormSelectField(
        label = "Goal scorer",
        value = "J. Smith"
    )

    Spacer(modifier = Modifier.height(14.dp))

    FormSelectField(
        label = "Type of goal",
        value = "Penalti ; Off-side; Goal",
        muted = true
    )

    Spacer(modifier = Modifier.height(14.dp))

    FormSelectField(
        label = "Assist",
        value = "Optional",
        muted = true
    )
}

@Composable
private fun FaultFields() {
    FormSelectField(
        label = "Player",
        value = "M. Jordan"
    )

    Spacer(modifier = Modifier.height(24.dp))

    FormSelectField(
        label = "Card",
        value = "Optional",
        muted = true
    )
}

@Composable
private fun SingleSubstitutionFields(
    onAddSubstitutionClick: () -> Unit
) {
    FormSelectField(
        label = "Player in",
        value = "M. Jordan"
    )

    Spacer(modifier = Modifier.height(24.dp))

    FormSelectField(
        label = "Player Out",
        value = "Optional",
        muted = true
    )

    Spacer(modifier = Modifier.height(28.dp))

    AddSubstitutionButton(
        onClick = onAddSubstitutionClick
    )
}

@Composable
private fun TwoSubstitutionsFields(
    onAddSubstitutionClick: () -> Unit
) {
    FormSelectField(
        label = "Player in",
        value = "M. Jordan"
    )

    Spacer(modifier = Modifier.height(24.dp))

    FormSelectField(
        label = "Player Out",
        value = "K. Mainoo"
    )

    Spacer(modifier = Modifier.height(24.dp))

    FormSelectField(
        label = "Player In",
        value = "Antony"
    )

    Spacer(modifier = Modifier.height(24.dp))

    FormSelectField(
        label = "Player Out",
        value = "Casemiro"
    )

    Spacer(modifier = Modifier.height(22.dp))

    AddSubstitutionButton(
        onClick = onAddSubstitutionClick
    )
}

@Composable
private fun FormSelectField(
    label: String,
    value: String,
    muted: Boolean = false,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(3.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clickable(onClick = onClick),
            color = InputBackground,
            shape = RoundedCornerShape(2.dp),
            border = BorderStroke(1.dp, Color(0xFF8E95A8))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    fontSize = 12.sp,
                    color = if (muted) SquadTextSecondary else SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = SquadTextPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun AddSubstitutionButton(
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(44.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.AddCircleOutline,
            contentDescription = "Adicionar substituição",
            tint = SquadOrange,
            modifier = Modifier.size(36.dp)
        )
    }
}

@Preview(
    name = "Goal",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MatchEventGoalFormPreview() {
    SquadUpTheme {
        MatchEventFormScreen(
            type = MatchEventFormType.Goal,
            onNotificationsClick = {},
            onSettingsClick = {},
            onRedTeamClick = {},
            onBlueTeamClick = {},
            onAddSubstitutionClick = {},
            onSaveChangesClick = {},
            onBackToMatchClick = {}
        )
    }
}

@Preview(
    name = "Fault",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MatchEventFaultFormPreview() {
    SquadUpTheme {
        MatchEventFormScreen(
            type = MatchEventFormType.Fault,
            onNotificationsClick = {},
            onSettingsClick = {},
            onRedTeamClick = {},
            onBlueTeamClick = {},
            onAddSubstitutionClick = {},
            onSaveChangesClick = {},
            onBackToMatchClick = {}
        )
    }
}

@Preview(
    name = "Substitution",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MatchEventSubstitutionFormPreview() {
    SquadUpTheme {
        MatchEventFormScreen(
            type = MatchEventFormType.Substitution,
            onNotificationsClick = {},
            onSettingsClick = {},
            onRedTeamClick = {},
            onBlueTeamClick = {},
            onAddSubstitutionClick = {},
            onSaveChangesClick = {},
            onBackToMatchClick = {}
        )
    }
}

@Preview(
    name = "Two substitutions",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun MatchEventTwoSubstitutionsFormPreview() {
    SquadUpTheme {
        MatchEventFormScreen(
            type = MatchEventFormType.TwoSubstitutions,
            onNotificationsClick = {},
            onSettingsClick = {},
            onRedTeamClick = {},
            onBlueTeamClick = {},
            onAddSubstitutionClick = {},
            onSaveChangesClick = {},
            onBackToMatchClick = {}
        )
    }
}