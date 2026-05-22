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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGrayDark
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadWhite

data class DateSpotUi(
    val day: String,
    val date: String,
    val selected: Boolean = false
)

data class SlotUi(
    val time: String,
    val participants: String,
    val selected: Boolean = false
)

@Composable
fun ChooseSpotScreen(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dates = remember {
        listOf(
            DateSpotUi("MON", "12", selected = true),
            DateSpotUi("TUE", "13"),
            DateSpotUi("WED", "14"),
            DateSpotUi("THU", "15")
        )
    }

    var selectedSlot by remember { mutableStateOf(0) }

    val slots = remember {
        listOf(
            SlotUi("6:30 PM - 7:30 PM", "3/4 Participants joined"),
            SlotUi("7:45 PM - 8:45 PM", "1/4 Participants joined"),
            SlotUi("9:00 PM - 10:00 PM", "0/4 Participants joined")
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            ChooseSpotHeader(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick,
                onSettingsClick = onSettingsClick
            )
        },
        containerColor = SquadBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(top = 18.dp, bottom = 28.dp)
        ) {
            Text(
                text = "CHOOSE YOUR SPOT",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = SquadOrangeDark
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Casual Tennis • Central Park Courts",
                fontSize = 11.sp,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                dates.forEach { date ->
                    DateCard(
                        date = date,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "AVAILABLE SLOTS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(12.dp))

            slots.forEachIndexed { index, slot ->
                SlotCard(
                    slot = slot.copy(selected = selectedSlot == index),
                    onClick = { selectedSlot = index }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            EligibilityCheckCard()

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(12.dp),
                        spotColor = SquadOrange.copy(alpha = 0.35f)
                    ),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = SquadWhite
                )
            ) {
                Text(
                    text = "C O N F I R M   S P O T",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.8.sp
                )
            }
        }
    }
}

@Composable
private fun ChooseSpotHeader(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 0.dp
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "Voltar",
                        tint = SquadGrayDark,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Select Date",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrange,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier.size(36.dp)
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
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "Definições",
                        tint = SquadGrayDark,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            HorizontalDivider(
                color = SquadGrayLight.copy(alpha = 0.55f),
                thickness = 1.dp
            )
        }
    }
}

@Composable
private fun DateCard(
    date: DateSpotUi,
    modifier: Modifier = Modifier
) {
    val background = if (date.selected) SquadOrange else Color(0xFFF0EDF3)
    val content = if (date.selected) SquadWhite else SquadTextPrimary

    Box(
        modifier = modifier
            .height(60.dp)
            .background(background, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.day,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = content
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = date.date,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = content
            )
        }
    }
}

@Composable
private fun SlotCard(
    slot: SlotUi,
    onClick: () -> Unit
) {
    val borderColor = if (slot.selected) SquadOrangeDark else SquadOrangeLight

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clickable(onClick = onClick),
        color = SquadSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = slot.time,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(5.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Groups,
                        contentDescription = null,
                        tint = SquadTextPrimary,
                        modifier = Modifier.size(13.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = slot.participants,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = SquadTextPrimary
                    )
                }
            }

            RadioButton(
                selected = slot.selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = SquadOrangeDark,
                    unselectedColor = SquadOrangeLight
                )
            )
        }
    }
}

@Composable
private fun EligibilityCheckCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFEDE8F0),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(13.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(Color(0xFF00796B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Verified,
                        contentDescription = null,
                        tint = SquadWhite,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "ELIGIBILITY CHECK",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.2.sp,
                    color = SquadTextPrimary
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                EligibilityItem(
                    label = "ACCOUNT BALANCE",
                    value = "$45.00",
                    modifier = Modifier.weight(1f)
                )

                EligibilityItem(
                    label = "SKILL MATCH",
                    value = "Intermediate",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun EligibilityItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(42.dp),
        color = SquadWhite,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = label,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = value,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF00796B)
                )
            }

            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF00796B),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun ChooseSpotScreenPreview() {
    ChooseSpotScreen(
        onBackClick = {},
        onNotificationsClick = {},
        onSettingsClick = {},
        onConfirmClick = {}
    )
}