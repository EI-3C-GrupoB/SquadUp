package com.example.squadup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.SportsVolleyball
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

data class EventFilterUi(
    val label: String,
    val selected: Boolean = false
)

data class UpcomingEventUi(
    val month: String,
    val day: String,
    val title: String,
    val details: String,
    val muted: Boolean = false
)

data class ExploreEventUi(
    val title: String,
    val tag: String,
    val price: String,
    val date: String,
    val location: String,
    val icon: ImageVector,
    val gradient: List<Color>,
    val actionText: String
)

@Composable
fun EventsSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = 12.sp,
            color = SquadTextPrimary
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(SquadSurface, RoundedCornerShape(7.dp))
            .padding(horizontal = 12.dp),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = SquadTextSecondary,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isBlank()) {
                        Text(
                            text = "Search sports, teams, or venues...",
                            fontSize = 12.sp,
                            color = SquadTextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun EventFiltersRow(
    filters: List<EventFilterUi>,
    onFilterClick: (EventFilterUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { filter ->
            val background = if (filter.selected) SquadOrange else SquadGrayLight
            val content = if (filter.selected) Color.White else SquadTextPrimary

            Text(
                text = filter.label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = content,
                modifier = Modifier
                    .background(background, RoundedCornerShape(999.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun FeaturedEventCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .height(132.dp)
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF122116),
                            Color(0xFF1E3527),
                            Color(0xFF7B3A11)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.SportsSoccer,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.18f),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(82.dp)
            )

            Text(
                text = "FEATURED",
                fontSize = 8.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                modifier = Modifier
                    .background(SquadOrange, RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = "CHAMPIONSHIP SERIES",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = SquadOrange
                )

                Text(
                    text = "Finals: Tigers vs.\nKnights",
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )

                    Text(
                        text = " Oct 24, 7:30 PM   ",
                        fontSize = 10.sp,
                        color = Color.White
                    )

                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )

                    Text(
                        text = " Velocity Arena",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun UpcomingEventsCard(
    events: List<UpcomingEventUi>,
    onFilterByTeamsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upcoming",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "View Calendar",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrange
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            events.forEachIndexed { index, event ->
                UpcomingEventRow(event = event)

                if (index < events.lastIndex) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onFilterByTeamsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                shape = RoundedCornerShape(7.dp),
                border = BorderStroke(1.dp, SquadGrayLight),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = SquadSurface,
                    contentColor = SquadTextPrimary
                )
            ) {
                Text(
                    text = "Filter by My Teams",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun UpcomingEventRow(
    event: UpcomingEventUi,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (event.muted) Color(0xFFF8F8F8) else Color.White,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, SquadGrayLight)
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .width(42.dp)
                    .background(Color(0xFFF0F4FA), RoundedCornerShape(5.dp))
                    .padding(vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = event.month,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextSecondary
                )

                Text(
                    text = event.day,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = SquadTextPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = event.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (event.muted) SquadTextSecondary else SquadTextPrimary
                )

                Text(
                    text = event.details,
                    fontSize = 9.sp,
                    color = SquadTextSecondary
                )
            }
        }
    }
}

@Composable
fun ExploreEventCard(
    event: ExploreEventUi,
    onActionClick: (ExploreEventUi) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(132.dp)
                    .background(Brush.linearGradient(event.gradient))
                    .padding(10.dp)
            ) {
                Icon(
                    imageVector = event.icon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.88f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(92.dp)
                )

                Text(
                    text = event.tag.uppercase(),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    color = SquadTextPrimary,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .background(Color.White, RoundedCornerShape(2.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }

            Column(
                modifier = Modifier.padding(14.dp)
            ) {
                Row {
                    Text(
                        text = event.title,
                        fontSize = 15.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = event.price,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = SquadOrange
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = SquadTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )

                    Text(
                        text = " ${event.date}",
                        fontSize = 10.sp,
                        color = SquadTextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = SquadTextSecondary,
                        modifier = Modifier.size(13.dp)
                    )

                    Text(
                        text = " ${event.location}",
                        fontSize = 10.sp,
                        color = SquadTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (event.actionText == "Join") {
                    Button(
                        onClick = { onActionClick(event) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(7.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SquadOrange,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = event.actionText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { onActionClick(event) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp),
                        shape = RoundedCornerShape(7.dp),
                        border = BorderStroke(1.5.dp, SquadOrange),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SquadSurface,
                            contentColor = SquadOrange
                        )
                    ) {
                        Text(
                            text = event.actionText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun defaultExploreEvents(): List<ExploreEventUi> {
    return listOf(
        ExploreEventUi(
            title = "Metropolis United vs. Skyhawks\nFC",
            tag = "Soccer",
            price = "$45",
            date = "Tomorrow • 6:00 PM",
            location = "City Center Stadium",
            icon = Icons.Outlined.SportsSoccer,
            gradient = listOf(Color(0xFFE8EEF3), Color(0xFF58636B), Color(0xFF111111)),
            actionText = "Join"
        ),
        ExploreEventUi(
            title = "Youth Elite Invitations",
            tag = "Athletics",
            price = "Free",
            date = "Oct 30 • 10:00 AM",
            location = "North Park Track",
            icon = Icons.Outlined.DirectionsRun,
            gradient = listOf(Color(0xFFFFD99E), Color(0xFFD86622), Color(0xFF6C311B)),
            actionText = "Register"
        ),
        ExploreEventUi(
            title = "Sand Pro Tour: Beach Open",
            tag = "Volleyball",
            price = "$20",
            date = "Nov 02 • 12:00 PM",
            location = "Sunnyside Beach",
            icon = Icons.Outlined.SportsVolleyball,
            gradient = listOf(Color(0xFFFFCB45), Color(0xFF6D4F10), Color(0xFF17120A)),
            actionText = "Join"
        )
    )
}
