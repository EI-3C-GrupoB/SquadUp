package com.example.squadup.features.events.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel
import kotlinx.coroutines.launch

@Composable
fun EventsMapRoute(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onEventClick: (String) -> Unit,
    appViewModel: AppViewModel,
    viewModel: EventsMapViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Try to get initial location on entry
    LaunchedEffect(Unit) {
        fetchLocation(context) { lat, lon ->
            viewModel.onUserLocationReady(lat, lon)
        }
    }

    EventsMapScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onNotificationsClick = onNotificationsClick,
        onSportFilterChange = viewModel::onSportFilterChange,
        onPinSelected = viewModel::onPinSelected,
        onEventClick = onEventClick,
        onRadiusChange = viewModel::onRadiusChange,
        onMyLocationClick = {
            scope.launch {
                fetchLocation(context) { lat, lon ->
                    viewModel.requestCenterOnUser(lat, lon)
                }
            }
        },
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = appViewModel::onAdminViewChange,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange
    )
}

private fun fetchLocation(context: android.content.Context, onResult: (Double, Double) -> Unit) {
    try {
        val hasPermission =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (!hasPermission) return

        val lm = context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            ?: lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        location?.let { onResult(it.latitude, it.longitude) }
    } catch (_: Exception) {}
}
