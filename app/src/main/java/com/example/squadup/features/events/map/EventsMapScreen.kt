package com.example.squadup.features.events.map

import androidx.compose.material3.MaterialTheme

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color as AndroidColor
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.toIcon
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import androidx.compose.ui.res.stringResource
import com.example.squadup.R

private const val OPEN_FREE_MAP_STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"

@Composable
fun EventsMapScreen(
    uiState: EventsMapUiState,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSportFilterChange: (SportType?) -> Unit,
    onPinSelected: (String?) -> Unit,
    onEventClick: (String) -> Unit,
    onRadiusChange: (Float) -> Unit,
    onMyLocationClick: () -> Unit,
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                title = "Map View",
                showBackButton = true,
                showNotificationsButton = true,
                showSettingsButton = true,
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                onAdminViewChange = onAdminViewChange,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MapLibreEventsMap(
                pins = uiState.visiblePins,
                selectedPin = uiState.selectedPin,
                onPinSelected = onPinSelected,
                userLatitude = uiState.userLatitude,
                userLongitude = uiState.userLongitude,
                userRadiusKm = uiState.userRadiusKm,
                centerOnUserTrigger = uiState.centerOnUserRequest,
                modifier = Modifier.fillMaxSize()
            )

            SportFilterRow(
                selectedSport = uiState.selectedSport,
                onSportFilterChange = onSportFilterChange,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 16.dp)
            )

            MapActionButtons(
                onClearSelectionClick = { onPinSelected(null) },
                onMyLocationClick = {
                    onPinSelected(null)
                    onMyLocationClick()
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 14.dp)
            )

            MapStatsCard(
                availableCount = uiState.availableCount,
                fullCount = uiState.fullCount,
                privateCount = uiState.privateCount,
                cancelledCount = uiState.cancelledCount,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 14.dp)
            )

            if (uiState.isLoading) {
                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = SquadOrange,
                            modifier = Modifier.size(22.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.map_loading),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Radius slider: shown when no pin is selected and user location is known
            if (uiState.selectedPin == null && uiState.userLatitude != null) {
                RadiusSliderCard(
                    radiusKm = uiState.userRadiusKm,
                    onRadiusChange = onRadiusChange,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                )
            }

            uiState.selectedPin?.let { selectedPin ->
                SelectedEventCard(
                    pin = selectedPin,
                    onOpenDetailsClick = { onEventClick(selectedPin.eventId) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 16.dp, vertical = 18.dp)
                )
            }
        }
    }
}

@Composable
private fun MapLibreEventsMap(
    pins: List<EventsMapPin>,
    selectedPin: EventsMapPin?,
    onPinSelected: (String?) -> Unit,
    userLatitude: Double?,
    userLongitude: Double?,
    userRadiusKm: Float,
    centerOnUserTrigger: Int,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapLibre.getInstance(context.applicationContext)
        MapView(context).apply { onCreate(null) }
    }

    var styleReady by remember { mutableStateOf(false) }
    val initialCameraSet = remember { intArrayOf(0) }
    val lastCenterTrigger = remember { intArrayOf(0) }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Initialize style ONCE
    LaunchedEffect(mapView) {
        mapView.getMapAsync { map ->
            map.setStyle(OPEN_FREE_MAP_STYLE_URL) {
                map.uiSettings.apply {
                    isCompassEnabled = false
                    isLogoEnabled = false
                    isAttributionEnabled = true
                    isRotateGesturesEnabled = true
                    isTiltGesturesEnabled = true
                    isZoomGesturesEnabled = true
                    isScrollGesturesEnabled = true
                }
                styleReady = true
            }
        }
    }

    // Update markers + circle whenever data changes (only after style is ready)
    LaunchedEffect(styleReady, pins, selectedPin?.eventId, userLatitude, userLongitude, userRadiusKm, centerOnUserTrigger) {
        if (!styleReady) return@LaunchedEffect
        mapView.getMapAsync { map ->
            map.clear()

            val markerEventIds = mutableMapOf<Long, String>()

            // Radius circle (outline only) + user location pin
            if (userLatitude != null && userLongitude != null) {
                val userLatLng = LatLng(userLatitude, userLongitude)
                map.addPolyline(
                    PolylineOptions()
                        .addAll(createCirclePoints(userLatLng, userRadiusKm.toDouble()))
                        .color(AndroidColor.argb(210, 255, 100, 20))
                        .width(2.5f)
                )
                map.addMarker(
                    MarkerOptions()
                        .position(userLatLng)
                        .title("A tua localização")
                        .icon(IconFactory.getInstance(context).fromBitmap(createUserLocationBitmap()))
                )
            }

            // Event pins
            pins.forEach { pin ->
                val marker = map.addMarker(
                    MarkerOptions()
                        .position(LatLng(pin.latitude, pin.longitude))
                        .title(pin.title)
                        .snippet(pin.venue)
                        .icon(
                            IconFactory.getInstance(context).fromBitmap(
                                createPinBitmap(
                                    color = pinColor(pin.status),
                                    selected = selectedPin?.eventId == pin.eventId
                                )
                            )
                        )
                )
                marker?.let { markerEventIds[it.id] = pin.eventId }
            }

            map.setOnMarkerClickListener { marker ->
                onPinSelected(markerEventIds[marker.id])
                true
            }

            // Camera logic — "center on user" always wins over selected pin
            when {
                centerOnUserTrigger != lastCenterTrigger[0] && userLatitude != null && userLongitude != null -> {
                    lastCenterTrigger[0] = centerOnUserTrigger
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(LatLng(userLatitude, userLongitude), 13.0),
                        800
                    )
                }
                selectedPin != null -> {
                    map.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(selectedPin.latitude, selectedPin.longitude), 14.5
                        ), 600
                    )
                }
                initialCameraSet[0] == 0 -> {
                    initialCameraSet[0] = 1
                    when {
                        userLatitude != null && userLongitude != null ->
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(LatLng(userLatitude, userLongitude), 13.0),
                                600
                            )
                        pins.isNotEmpty() -> {
                            val bounds = LatLngBounds.Builder().apply {
                                pins.forEach { include(LatLng(it.latitude, it.longitude)) }
                            }.build()
                            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90), 700)
                        }
                        else ->
                            map.cameraPosition = CameraPosition.Builder()
                                .target(LatLng(41.6932, -8.8329))
                                .zoom(12.0)
                                .build()
                    }
                }
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView }
    )
}

private fun pinColor(status: EventsMapPinStatus): Int {
    return when (status) {
        EventsMapPinStatus.AVAILABLE -> AndroidColor.rgb(46, 125, 50)
        EventsMapPinStatus.FULL -> AndroidColor.rgb(211, 47, 47)
        EventsMapPinStatus.PRIVATE -> AndroidColor.rgb(123, 31, 162)
        EventsMapPinStatus.CANCELLED -> AndroidColor.rgb(117, 117, 117)
        EventsMapPinStatus.FINISHED -> AndroidColor.rgb(117, 117, 117)
    }
}

private fun createPinBitmap(color: Int, selected: Boolean): Bitmap {
    val size = if (selected) 84 else 68
    val radius = if (selected) 26f else 21f
    val centerX = size / 2f
    val centerY = if (selected) 30f else 24f

    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    canvas.drawCircle(centerX + 2f, centerY + 3f, radius, Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = AndroidColor.argb(70, 0, 0, 0)
    })
    canvas.drawCircle(centerX, centerY, radius, Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
    })
    canvas.drawCircle(centerX, centerY, radius, Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = AndroidColor.WHITE
        style = Paint.Style.STROKE
        strokeWidth = if (selected) 6f else 5f
    })
    canvas.drawCircle(centerX, centerY, if (selected) 8f else 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = AndroidColor.WHITE
    })
    return bitmap
}

private fun createUserLocationBitmap(): Bitmap {
    val size = 60
    val center = size / 2f
    val radius = 20f
    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    canvas.drawCircle(center + 2f, center + 3f, radius, Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.argb(60, 0, 0, 0)
    })
    canvas.drawCircle(center, center, radius, Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.rgb(25, 118, 210)
    })
    canvas.drawCircle(center, center, radius, Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 5f
    })
    canvas.drawCircle(center, center, 6f, Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = AndroidColor.WHITE
    })
    return bitmap
}

private fun createCirclePoints(center: LatLng, radiusKm: Double, numPoints: Int = 64): List<LatLng> {
    val earthRadius = 6371.0
    val d = radiusKm / earthRadius
    val latRad = Math.toRadians(center.latitude)
    val lonRad = Math.toRadians(center.longitude)
    return (0..numPoints).map { i ->
        val angle = Math.toRadians((360.0 / numPoints) * i)
        val newLatRad = asin(sin(latRad) * cos(d) + cos(latRad) * sin(d) * cos(angle))
        val newLonRad = lonRad + atan2(
            sin(angle) * sin(d) * cos(latRad),
            cos(d) - sin(latRad) * sin(newLatRad)
        )
        LatLng(Math.toDegrees(newLatRad), Math.toDegrees(newLonRad))
    }
}

@Composable
private fun SportFilterRow(
    selectedSport: SportType?,
    onSportFilterChange: (SportType?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = responsiveHorizontalPadding(14.dp)),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SportFilterChip(
            text = stringResource(R.string.map_filter_all),
            selected = selectedSport == null,
            onClick = { onSportFilterChange(null) }
        )
        SportType.entries.forEach { sportType ->
            SportFilterChip(
                text = stringResource(sportType.labelRes),
                selected = selectedSport == sportType,
                onClick = { onSportFilterChange(sportType) },
                icon = sportType
            )
        }
    }
}

@Composable
private fun SportFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: SportType? = null
) {
    Surface(
        modifier = Modifier
            .height(38.dp)
            .clickable(onClick = onClick),
        color = if (selected) SquadOrange else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(999.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(14.dp)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon.toIcon(),
                    contentDescription = null,
                    tint = if (selected) Color.White else SquadOrange,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = text,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MapActionButtons(
    onClearSelectionClick: () -> Unit,
    onMyLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FloatingMapButton(icon = Icons.Outlined.Tune, onClick = onClearSelectionClick)
        FloatingMapButton(icon = Icons.Outlined.MyLocation, onClick = onMyLocationClick)
    }
}

@Composable
private fun FloatingMapButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(46.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = CircleShape,
        shadowElevation = 6.dp,
        onClick = onClick
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SquadOrange,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun MapStatsCard(
    availableCount: Int,
    fullCount: Int,
    privateCount: Int,
    cancelledCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            MapStatRow(stringResource(R.string.map_status_available), availableCount, Color(0xFF2E7D32))
            MapStatRow(stringResource(R.string.map_status_full), fullCount, Color(0xFFD32F2F))
            MapStatRow(stringResource(R.string.map_status_private), privateCount, Color(0xFF7B1FA2))
            MapStatRow(stringResource(R.string.map_status_cancelled), cancelledCount, Color(0xFF757575))
        }
    }
}

@Composable
private fun MapStatRow(label: String, value: Int, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$value $label",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RadiusSliderCard(
    radiusKm: Float,
    onRadiusChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 8.dp
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(SquadOrange.copy(alpha = 0.25f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .padding(start = 3.dp)
                        .size(7.dp)
                        .background(SquadOrange, CircleShape)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stringResource(R.string.map_search_radius),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${radiusKm.toInt()} km",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrange
                )
            }
            Slider(
                value = radiusKm,
                onValueChange = onRadiusChange,
                valueRange = 1f..50f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                colors = SliderDefaults.colors(
                    thumbColor = SquadOrange,
                    activeTrackColor = SquadOrange,
                    inactiveTrackColor = SquadOrange.copy(alpha = 0.2f)
                )
            )
        }
    }
}

@Composable
private fun SelectedEventCard(
    pin: EventsMapPin,
    onOpenDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 10.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(pin.sportType.labelRes),
                    color = SquadOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(SquadOrangeLight, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = pin.startsAt.ifBlank { stringResource(R.string.map_coming_soon) },
                    color = SquadOrange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = pin.title, color = MaterialTheme.colorScheme.onSurface, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = pin.venue.ifBlank { stringResource(R.string.map_no_location) },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.map_registered_count, pin.registeredCount, pin.totalSpots),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = onOpenDetailsClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SquadOrange, contentColor = Color.White)
                ) {
                    Text(text = stringResource(R.string.map_view_details), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
