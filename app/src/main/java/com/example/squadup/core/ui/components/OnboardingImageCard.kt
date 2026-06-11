package com.example.squadup.core.ui.components

import androidx.compose.material3.MaterialTheme

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadWhite

@Composable
fun OnboardingImageCard(
    @DrawableRes image: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    primaryBadgeText: String = "LOCAL",
    secondaryBadgeText: String = "LIVE NOW"
) {
    val isLandscape = rememberIsLandscape()

    Box(
        modifier = modifier
            .fillMaxWidth(if (isLandscape) 0.62f else 0.90f)
            .height(if (isLandscape) 190.dp else 330.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 22.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OnboardingImageBadge(
                text = primaryBadgeText,
                backgroundColor = SquadOrange,
                textColor = SquadWhite
            )

            OnboardingImageBadge(
                text = secondaryBadgeText,
                backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                textColor = SquadWhite
            )
        }
    }
}

@Composable
private fun OnboardingImageBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}
