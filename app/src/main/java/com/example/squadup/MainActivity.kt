package com.example.squadup

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.squadup.navigation.AppNavigation
import com.example.squadup.ui.components.AppLanguage
import com.example.squadup.ui.screens.onboarding.OnboardingScreen
import com.example.squadup.ui.theme.SquadUpTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SquadUpTheme {
                AppNavigation()
            }
        }
    }
}