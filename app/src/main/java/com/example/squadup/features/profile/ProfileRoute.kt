package com.example.squadup.features.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel

@Composable
fun ProfileRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onTicketsClick: () -> Unit,
    onMyEventsClick: () -> Unit,
    onManageAccountsClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onLoginClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri ?: return@rememberLauncherForActivityResult
            viewModel.uploadAvatar(uri, context) { newUrl ->
                appViewModel.onPhotoUrlChange(newUrl)
            }
        }
    )

    LaunchedEffect(appUiState.userId) {
        viewModel.loadProfile()
    }

    val effectiveUiState = uiState.copy(
        isLoggedIn = appUiState.isLoggedIn,
        displayName = if (uiState.displayName.isBlank()) appUiState.displayName else uiState.displayName,
        photoUrl = uiState.photoUrl ?: appUiState.photoUrl,
        isAdmin = if (!uiState.isLoggedIn) appUiState.isAdmin else uiState.isAdmin
    )

    ProfileScreen(
        uiState = effectiveUiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = { appViewModel.onAdminViewChange(it) },
        onTicketsClick = onTicketsClick,
        onMyEventsClick = onMyEventsClick,
        onManageAccountsClick = onManageAccountsClick,
        onEditProfileClick = onEditProfileClick,
        onAvatarClick = { photoPickerLauncher.launch("image/*") },
        onChangePasswordClick = onChangePasswordClick,
        onLogoutClick = { appViewModel.logout(onLogoutClick) },
        onNotificationsClick = onNotificationsClick,
        onLoginClick = onLoginClick,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = { appViewModel.onLanguageChange(it) },
        onDarkModeChange = { appViewModel.onDarkModeChange(it) },
        notificationsCount = appUiState.notificationsCount
    )
}
