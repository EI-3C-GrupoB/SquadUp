package com.example.squadup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel
import com.example.squadup.core.navigation.AppNavigation
import com.example.squadup.core.ui.theme.SquadUpTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appViewModel: AppViewModel = viewModel(
                factory = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            )
            val appUiState = appViewModel.uiState.collectAsStateWithLifecycle()

            SquadUpTheme(darkTheme = appUiState.value.isDarkMode) {
                AppNavigation(providedAppViewModel = appViewModel)
            }
        }
    }
}
