package com.example.squadup.ui.components.exemples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadUpTheme

@Composable
fun ComponentsPreviewPage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SquadBackground)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "AppHeader",
            color = SquadTextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )

        AppHeader()

        AppHeader(
            showLoginButton = true
        )

        AppHeader(
            showNotificationsButton = true,
            showSettingsButton = true
        )

        AppHeader(
            showBackButton = true,
            showLogo = false,
            title = "Profile",
            showNotificationsButton = true,
            showSettingsButton = true
        )

        AppHeader(
            showBackButton = true,
            showLogo = false,
            title = "Create Event (1 of 3)"
        )

        AppHeader(
            showLanguageSwitch = true
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ComponentsPreviewPagePreview() {
    SquadUpTheme {
        ComponentsPreviewPage()
    }
}