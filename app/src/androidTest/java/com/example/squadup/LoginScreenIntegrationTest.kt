package com.example.squadup

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.squadup.features.auth.login.LoginScreen
import com.example.squadup.features.auth.login.LoginUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loginScreen_displaysSignInButton() {
        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState(),
                onEmailChange = {},
                onPasswordChange = {},
                onSignInClick = {},
                onCreateAccountClick = {},
                onForgotPasswordClick = {},
                onBackClick = {},
                onDismissSuspendedDialog = {}
            )
        }

        composeTestRule.onNodeWithText("Sign In", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun loginScreen_suspendedDialog_appearsWhenFlagIsSet() {
        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState(showAccountSuspendedDialog = true),
                onEmailChange = {},
                onPasswordChange = {},
                onSignInClick = {},
                onCreateAccountClick = {},
                onForgotPasswordClick = {},
                onBackClick = {},
                onDismissSuspendedDialog = {}
            )
        }

        composeTestRule.onNodeWithText("Account Suspended", ignoreCase = true).assertIsDisplayed()
    }

    @Test
    fun loginScreen_suspendedDialog_dismissButtonCallsCallback() {
        var dismissed = false

        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState(showAccountSuspendedDialog = true),
                onEmailChange = {},
                onPasswordChange = {},
                onSignInClick = {},
                onCreateAccountClick = {},
                onForgotPasswordClick = {},
                onBackClick = {},
                onDismissSuspendedDialog = { dismissed = true }
            )
        }

        composeTestRule.onNodeWithText("OK", ignoreCase = true).performClick()
        assertTrue(dismissed)
    }

    @Test
    fun loginScreen_noDialog_whenSuspendedFlagIsFalse() {
        composeTestRule.setContent {
            LoginScreen(
                uiState = LoginUiState(showAccountSuspendedDialog = false),
                onEmailChange = {},
                onPasswordChange = {},
                onSignInClick = {},
                onCreateAccountClick = {},
                onForgotPasswordClick = {},
                onBackClick = {},
                onDismissSuspendedDialog = {}
            )
        }

        composeTestRule
            .onNodeWithText("Account Suspended", ignoreCase = true)
            .assertDoesNotExist()
    }
}
