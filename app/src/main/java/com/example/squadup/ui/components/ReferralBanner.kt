package com.example.squadup.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ReferralBanner(
    onReferClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF9098FF),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.ConfirmationNumber,
            contentDescription = null,
            tint = Color(0xFF6F75D8),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .height(64.dp)
        )

        Column {
            Text(
                text = "Team Up & Save",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E357E)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Invite 3 teammates and get\nyour next session for free.",
                fontSize = 15.sp,
                lineHeight = 20.sp,
                color = Color(0xFF2E357E)
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onReferClick,
                shape = RoundedCornerShape(999.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF28338F),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Refer Now",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}