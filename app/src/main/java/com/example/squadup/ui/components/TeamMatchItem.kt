package com.example.squadup.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun TeamMatchItem(
    title: String,
    date: String,
    time: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 14.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = SquadTextPrimary,
            modifier = Modifier.weight(1f)
        )

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = date,
                fontSize = 11.sp,
                color = Color(0x00000000)
            )

            Text(
                text = time,
                fontSize = 11.sp,
                color = Color(0x00000000)
            )
        }
    }
}