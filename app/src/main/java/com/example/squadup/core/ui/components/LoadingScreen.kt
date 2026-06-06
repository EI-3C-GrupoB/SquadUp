package com.example.squadup.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.theme.SquadBackground
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadSurface
import com.example.squadup.core.ui.theme.SquadTextPrimary

@Composable
fun LoadingScreen(
    message: String? = null
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SquadBackground)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            color = SquadSurface,
            shadowElevation = 1.dp
        ) {
            AppHeader(
                showLogo = true
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = SquadOrange,
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp)
            )
            if (message != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    color = SquadTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
