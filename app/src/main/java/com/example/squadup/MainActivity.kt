package com.example.squadup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.squadup.core.navigation.AppNavigation
import com.example.squadup.core.ui.theme.SquadUpTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SquadUpTheme {
                AppNavigation()
            }
        }
    }
}