package com.example.squadup.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.R

@Composable
fun ReferralBanner(
    onReferClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF9098FF), RoundedCornerShape(12.dp))
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
                text = stringResource(R.string.tickets_referral_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E357E)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.tickets_referral_subtitle),
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
                    text = stringResource(R.string.tickets_referral_button),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
