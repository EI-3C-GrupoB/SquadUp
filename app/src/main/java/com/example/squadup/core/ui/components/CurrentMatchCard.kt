package com.example.squadup.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.utils.toIcon
import com.example.squadup.core.utils.toCardColor

@Composable
fun CurrentMatchCard(
    title: String,
    date: String,
    location: String,
    sportType: SportType,
    onViewDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFFB80000),
                            SquadOrange,
                            Color(0xFFFF2D00)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.currentMatchCard_label),
                    color = SquadOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(999.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                )

                Spacer(modifier = Modifier.height(26.dp))

                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 28.sp,
                    lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(text = date, color = Color.White, fontSize = 13.sp)

                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))

                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Text(text = location, color = Color.White, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onViewDetailsClick,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = SquadOrange
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.currentMatchCard_btn_details),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {},
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC107),
                            contentColor = Color(0xFF241600)
                        )
                    ) {
                        Icon(
                            imageVector = sportType.toIcon(),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}