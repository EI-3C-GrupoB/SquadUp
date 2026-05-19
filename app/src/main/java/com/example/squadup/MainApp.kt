package com.example.squadup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.squadup.ui.components.AppHeader
import com.example.squadup.ui.components.AppNavBar
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadUpTheme

@Composable
fun MainApp() {
    var selectedRoute by remember {
        mutableStateOf("home")
    }

    Scaffold(
        topBar = {
            AppHeader(
                showNotificationsButton = true,
                showSettingsButton = true
            )
        },
        bottomBar = {
            AppNavBar(
                selectedRoute = selectedRoute,
                onItemClick = { route ->
                    selectedRoute = route
                }
            )
        },
        containerColor = SquadBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            when (selectedRoute) {
                "home" -> Text(text = "Home")
                "events" -> Text(text = "Events")
                "teams" -> Text(text = "Teams")
                "profile" -> Text(text = "Profile")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainAppPreview() {
    SquadUpTheme {
        MainApp()
    }
}