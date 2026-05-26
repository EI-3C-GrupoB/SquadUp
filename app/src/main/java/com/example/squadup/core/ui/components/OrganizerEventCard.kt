package com.example.squadup.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.EventStatus
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.toCardColor
import com.example.squadup.core.utils.toIcon
import com.example.squadup.core.utils.toLabel
import com.example.squadup.core.utils.toLabelBackground
import com.example.squadup.core.utils.toLabelColor

@Composable
fun OrganizerEventCard(
    title: String,
    price: String,
    nTeams: Int,
    dateLeft: String,
    registeredCount: Int,
    status: EventStatus,
    sportType: SportType,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = SquadWhite,
        shadowElevation = 2.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(sportType.toCardColor())
            ) {
                Text(
                    text = status.toLabel(context),
                    color = status.toLabelColor(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .background(status.toLabelBackground(), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Icon(
                    imageVector = sportType.toIcon(),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(64.dp)
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SquadTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = price,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadOrange
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$nTeams ${stringResource(R.string.organizerEventCard_teams)} • $dateLeft",
                    fontSize = 13.sp,
                    color = SquadTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.width((12 * 3 + 24).dp)) {
                        repeat(minOf(registeredCount, 3)) { index ->
                            Box(
                                modifier = Modifier
                                    .offset(x = (index * 12).dp)
                                    .size(24.dp)
                                    .background(SquadGray, CircleShape)
                                    .background(SquadOrangeLight, CircleShape)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "+$registeredCount ${stringResource(R.string.organizerEventCard_registered)}",
                        fontSize = 12.sp,
                        color = SquadTextSecondary
                    )
                }
            }
        }
    }
}