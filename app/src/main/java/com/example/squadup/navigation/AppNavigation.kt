package com.example.squadup.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.squadup.ui.components.AppLanguage
import com.example.squadup.ui.screens.auth.login.LoginRoute
import com.example.squadup.ui.screens.onboarding.OnboardingRoute
import com.example.squadup.ui.utils.getCurrentLanguage
import com.example.squadup.ui.utils.setAppLanguage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var selectedLanguage by rememberSaveable {
        mutableStateOf(getCurrentLanguage(context))
    }

    fun changeLanguage(language: AppLanguage) {
        if (selectedLanguage == language) return
        
        selectedLanguage = language
        
        // Dá tempo à animação do switch para terminar antes de recriar a Activity
        scope.launch {
            delay(300)
            setAppLanguage(context, language)
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoutes.ONBOARDING
    ) {
        composable(AppRoutes.ONBOARDING) {
            OnboardingRoute(
                selectedLanguage = selectedLanguage,
                onLanguageChange = ::changeLanguage,
                onFinish = {
                    /*((navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.ONBOARDING) {
                            inclusive = true
                        }
                    }*/
                },
                onLoginClick = {
                    navController.navigate(AppRoutes.LOGIN)
                }
            )
        }

        composable(AppRoutes.LOGIN) {
            LoginRoute(
                selectedLanguage = selectedLanguage,
                onLanguageChange = ::changeLanguage,
                onLoginSuccess = {
                    navController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onForgotPasswordClick = {
                    //navController.navigate(AppRoutes.FORGOT_PASSWORD)
                },
                onCreateAccountClick = {
                    //navController.navigate(AppRoutes.REGISTER)
                }
            )
        }
    }
}