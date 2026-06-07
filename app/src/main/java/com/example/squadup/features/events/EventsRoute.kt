package com.example.squadup.features.events

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.squadup.core.app.AppViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@SuppressLint("MissingPermission")
@Composable
fun EventsRoute(
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onEventClick: (String) -> Unit,
    onViewCalendarClick: () -> Unit,
    onFilterByMyTeamsClick: () -> Unit,
    onMapClick: () -> Unit,
    onCreateEventClick: () -> Unit,
    appViewModel: AppViewModel,
    viewModel: EventsViewModel = viewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()

    fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    fun loadEventsFromCurrentLocation() {
        if (!hasLocationPermission()) {
            viewModel.onLocationUnavailable()
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    viewModel.observeEventsFromDeviceLocation(
                        latitude = lastLocation.latitude,
                        longitude = lastLocation.longitude
                    )
                } else {
                    val cancellationTokenSource = CancellationTokenSource()

                    fusedLocationClient
                        .getCurrentLocation(
                            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                            cancellationTokenSource.token
                        )
                        .addOnSuccessListener { currentLocation ->
                            if (currentLocation != null) {
                                viewModel.observeEventsFromDeviceLocation(
                                    latitude = currentLocation.latitude,
                                    longitude = currentLocation.longitude
                                )
                            } else {
                                viewModel.onLocationUnavailable()
                            }
                        }
                        .addOnFailureListener {
                            viewModel.onLocationUnavailable()
                        }
                }
            }
            .addOnFailureListener {
                viewModel.onLocationUnavailable()
            }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineGranted || coarseGranted) {
            loadEventsFromCurrentLocation()
        } else {
            viewModel.observeEventsFromProfileLocation()
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission()) {
            loadEventsFromCurrentLocation()
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    EventsScreen(
        uiState = uiState,
        selectedRoute = selectedRoute,
        onNavItemClick = onNavItemClick,
        onNotificationsClick = onNotificationsClick,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onSportFilterChange = viewModel::onSportFilterChange,
        onEventClick = onEventClick,
        onViewCalendarClick = onViewCalendarClick,
        onFilterByMyTeamsClick = onFilterByMyTeamsClick,
        onMapClick = onMapClick,
        onCreateEventClick = onCreateEventClick,
        isAdmin = appUiState.isAdmin,
        isAdminView = appUiState.isAdminView,
        onAdminViewChange = appViewModel::onAdminViewChange,
        selectedLanguage = appUiState.selectedLanguage,
        isDarkMode = appUiState.isDarkMode,
        onLanguageChange = appViewModel::onLanguageChange,
        onDarkModeChange = appViewModel::onDarkModeChange,
        notificationsCount = appUiState.notificationsCount
    )
}
