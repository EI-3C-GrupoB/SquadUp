package com.example.squadup.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadBorder
import com.example.squadup.ui.theme.SquadTextPrimary

@Composable
fun SocialLoginButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = 1.dp,
            color = SquadBorder
        ),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = androidx.compose.ui.graphics.Color.White,
            contentColor = SquadTextPrimary
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = androidx.compose.ui.graphics.Color.Unspecified
            )

            androidx.compose.foundation.layout.Spacer(
                modifier = Modifier.size(8.dp)
            )

            Text(
                text = text,
                fontSize = 14.sp
            )
        }
    }
}