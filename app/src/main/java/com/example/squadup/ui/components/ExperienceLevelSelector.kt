package com.example.squadup.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary

@Composable
fun ExperienceLevelSelector(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Experience Level",
                fontSize = 16.sp,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = getExperienceLabel(value),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = SquadOrange
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..2f,
            steps = 1,
            colors = SliderDefaults.colors(
                thumbColor = SquadOrange,
                activeTrackColor = SquadOrange,
                inactiveTrackColor = SquadOrange.copy(alpha = 0.25f),
                activeTickColor = SquadOrange,
                inactiveTickColor = SquadOrange.copy(alpha = 0.25f)
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Novice",
                fontSize = 14.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "Pro",
                fontSize = 14.sp,
                color = SquadTextSecondary
            )
        }
    }
}

private fun getExperienceLabel(value: Float): String {
    return when {
        value < 0.75f -> "Novice"
        value < 1.5f -> "Intermediate"
        else -> "Pro"
    }
}