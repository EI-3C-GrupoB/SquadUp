package com.example.squadup.ui.screens.events

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.SportsVolleyball
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadWhite

data class TournamentTeamUi(
    val id: String,
    val name: String,
    val membersText: String,
    val icon: ImageVector,
    val iconBackground: Color
)

@Composable
fun SelectTournamentTeamScreen(
    searchQuery: String,
    selectedTeamId: String?,
    teams: List<TournamentTeamUi>,
    onSearchQueryChange: (String) -> Unit,
    onTeamSelected: (String) -> Unit,
    onCreateNewTeamClick: () -> Unit,
    onRegisterTeamClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            SelectTeamHeader(
                title = "Team Zone",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 8.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Select Your Team",
                fontSize = 14.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrangeDark
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Choose the squad you want to register for\nthe upcoming season tournament.",
                fontSize = 7.sp,
                lineHeight = 10.sp,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            TeamSearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange
            )

            Spacer(modifier = Modifier.height(10.dp))

            teams.forEach { team ->
                SelectableTeamCard(
                    team = team,
                    selected = selectedTeamId == team.id,
                    onClick = {
                        onTeamSelected(team.id)
                    }
                )

                Spacer(modifier = Modifier.height(7.dp))
            }

            Spacer(modifier = Modifier.height(3.dp))

            CreateNewTeamButton(
                onClick = onCreateNewTeamClick
            )

            Spacer(modifier = Modifier.height(11.dp))

            Button(
                onClick = onRegisterTeamClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = SquadWhite
                )
            ) {
                Text(
                    text = "Register Team",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(5.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SelectTeamHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Voltar",
                tint = SquadGrayDark,
                modifier = Modifier
                    .size(14.dp)
                    .clickable(onClick = onBackClick)
            )

            Spacer(modifier = Modifier.width(9.dp))

            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrangeDark
            )
        }
    }
}

@Composable
private fun TeamSearchField(
    value: String,
    onValueChange: (String) -> Unit
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 8.sp,
            color = SquadTextPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .background(
                color = Color(0xFFF3EDF3),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 7.dp),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = SquadTextSecondary,
                    modifier = Modifier.size(11.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "FIND TEAM",
                        fontSize = 5.sp,
                        fontWeight = FontWeight.Black,
                        color = SquadTextSecondary
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isBlank()) {
                            Text(
                                text = "Search by name...",
                                fontSize = 7.sp,
                                color = SquadTextSecondary
                            )
                        }

                        innerTextField()
                    }
                }
            }
        }
    )
}

@Composable
private fun SelectableTeamCard(
    team: TournamentTeamUi,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = SquadSurface,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) SquadOrangeDark else SquadOrangeLight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 7.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        color = team.iconBackground,
                        shape = RoundedCornerShape(5.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = team.icon,
                    contentDescription = null,
                    tint = SquadWhite,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = team.name,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Groups,
                        contentDescription = null,
                        tint = SquadTextSecondary,
                        modifier = Modifier.size(8.dp)
                    )

                    Spacer(modifier = Modifier.width(2.dp))

                    Text(
                        text = team.membersText,
                        fontSize = 6.5.sp,
                        color = SquadTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = if (selected) {
                    Icons.Outlined.CheckCircle
                } else {
                    Icons.Outlined.RadioButtonUnchecked
                },
                contentDescription = null,
                tint = if (selected) SquadOrangeDark else SquadOrangeLight,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}

@Composable
private fun CreateNewTeamButton(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp)
            .drawBehind {
                drawRoundRect(
                    color = SquadOrangeLight,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(8f, 6f),
                            phase = 0f
                        )
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                        x = 6.dp.toPx(),
                        y = 6.dp.toPx()
                    )
                )
            }
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.AddCircleOutline,
            contentDescription = null,
            tint = SquadTextPrimary,
            modifier = Modifier.size(10.dp)
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "Create New Team",
            fontSize = 7.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = SquadTextPrimary
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=185dp,height=400dp,dpi=440"
)
@Composable
fun SelectTournamentTeamScreenPreview() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTeamId by remember { mutableStateOf<String?>("slam_dunkers") }

    val teams = listOf(
        TournamentTeamUi(
            id = "slam_dunkers",
            name = "Slam Dunkers",
            membersText = "8 Members Registered",
            icon = Icons.Outlined.SportsBasketball,
            iconBackground = Color(0xFF102A44)
        ),
        TournamentTeamUi(
            id = "orange_crush",
            name = "The Orange Crush",
            membersText = "5 Members Registered",
            icon = Icons.Outlined.SportsVolleyball,
            iconBackground = Color(0xFF14291C)
        ),
        TournamentTeamUi(
            id = "electric_eagles",
            name = "Electric Eagles",
            membersText = "12 Members Registered",
            icon = Icons.Outlined.SportsSoccer,
            iconBackground = Color.Black
        )
    )

    SelectTournamentTeamScreen(
        searchQuery = searchQuery,
        selectedTeamId = selectedTeamId,
        teams = teams,
        onSearchQueryChange = { searchQuery = it },
        onTeamSelected = { selectedTeamId = it },
        onCreateNewTeamClick = {},
        onRegisterTeamClick = {},
        onBackClick = {}
    )
}