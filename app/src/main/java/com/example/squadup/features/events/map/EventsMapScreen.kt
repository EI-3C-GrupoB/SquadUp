package com.example.squadup.features.events.map

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
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadSurface
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.toIcon
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapView

private const val OPEN_FREE_MAP_STYLE_URL = "https://tiles.openfreemap.org/styles/liberty"

@Composable
fun EventsMapScreen(
    uiState: EventsMapUiState,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSportFilterChange: (SportType?) -> Unit,
    onPinSelected: (String?) -> Unit,
    onEventClick: (String) -> Unit,
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
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 14.dp)
            )

            MapStatsCard(
                availableCount = uiState.availableCount,
                fullCount = uiState.fullCount,
                privateCount = uiState.privateCount,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 80.dp, end = 14.dp)
            )

            if (uiState.isLoading) {
                Surface(
                    modifier = Modifier.align(Alignment.Center),
                    color = SquadSurface,
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
                            text = "Loading events...",
                            color = SquadTextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            uiState.selectedPin?.let { selectedPin ->
                SelectedEventCard(
                    pin = selectedPin,
                    onOpenDetailsClick = {
                        onEventClick(selectedPin.eventId)
                    },
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapLibre.getInstance(context.applicationContext)

        MapView(context).apply {
            onCreate(null)
        }
    }

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

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(pins, selectedPin?.eventId) {
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

                map.clear()

                val markerEventIds = mutableMapOf<Long, String>()

                pins.forEach { pin ->
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(LatLng(pin.latitude, pin.longitude))
                            .title(pin.title)
                            .snippet(pin.venue)
                            .icon(
                                IconFactory
                                    .getInstance(context)
                                    .fromBitmap(
                                        createPinBitmap(
                                            color = pinColor(pin.status),
                                            selected = selectedPin?.eventId == pin.eventId
                                        )
                                    )
                            )
                    )

                    marker?.let {
                        markerEventIds[it.id] = pin.eventId
                    }
                }

                map.setOnMarkerClickListener { marker ->
                    onPinSelected(markerEventIds[marker.id])
                    true
                }

                when {
                    selectedPin != null -> {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(selectedPin.latitude, selectedPin.longitude),
                                14.5
                            ),
                            600
                        )
                    }

                    pins.isNotEmpty() -> {
                        val bounds = LatLngBounds.Builder().apply {
                            pins.forEach { pin ->
                                include(LatLng(pin.latitude, pin.longitude))
                            }
                        }.build()

                        map.animateCamera(
                            CameraUpdateFactory.newLatLngBounds(bounds, 90),
                            700
                        )
                    }

                    else -> {
                        map.cameraPosition = CameraPosition.Builder()
                            .target(LatLng(41.6932, -8.8329)) // Viana do Castelo
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
        EventsMapPinStatus.AVAILABLE -> AndroidColor.rgb(46, 125, 50)     // verde
        EventsMapPinStatus.FULL -> AndroidColor.rgb(211, 47, 47)          // vermelho
        EventsMapPinStatus.PRIVATE -> AndroidColor.rgb(123, 31, 162)      // roxo
        EventsMapPinStatus.CANCELLED -> AndroidColor.rgb(117, 117, 117)   // cinzento
        EventsMapPinStatus.FINISHED -> AndroidColor.rgb(117, 117, 117)    // cinzento
    }
}

private fun createPinBitmap(
    color: Int,
    selected: Boolean
): Bitmap {
    val size = if (selected) 58 else 46
    val radius = if (selected) 18f else 15f

    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = AndroidColor.argb(70, 0, 0, 0)
    }

    val pinPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
    }

    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = AndroidColor.WHITE
        style = Paint.Style.STROKE
        strokeWidth = if (selected) 5f else 4f
    }

    val centerX = size / 2f
    val centerY = if (selected) 22f else 18f

    canvas.drawCircle(centerX + 2f, centerY + 3f, radius, shadowPaint)
    canvas.drawCircle(centerX, centerY, radius, pinPaint)
    canvas.drawCircle(centerX, centerY, radius, borderPaint)

    canvas.drawCircle(
        centerX,
        centerY,
        if (selected) 6f else 5f,
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = AndroidColor.WHITE
        }
    )

    return bitmap
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
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SportFilterChip(
            text = "All",
            selected = selectedSport == null,
            onClick = { onSportFilterChange(null) }
        )

        SportType.entries.forEach { sportType ->
            SportFilterChip(
                text = sportType.name.lowercase().replaceFirstChar { it.uppercase() },
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
        color = if (selected) SquadOrange else SquadSurface,
        shape = RoundedCornerShape(999.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon?.toIcon() ?: Icons.Outlined.SportsSoccer,
                contentDescription = null,
                tint = if (selected) Color.White else SquadOrange,
                modifier = Modifier.size(18.dp)
            )

            Text(
                text = text,
                color = if (selected) Color.White else SquadTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MapActionButtons(
    onClearSelectionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FloatingMapButton(
            icon = Icons.Outlined.Tune,
            onClick = {}
        )

        FloatingMapButton(
            icon = Icons.Outlined.MyLocation,
            onClick = onClearSelectionClick
        )
    }
}

@Composable
private fun FloatingMapButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.size(46.dp),
        color = SquadSurface,
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
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = SquadSurface.copy(alpha = 0.95f),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 6.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            MapStatRow("Available", availableCount, Color(0xFF2E7D32))
            MapStatRow("Full", fullCount, Color(0xFFD32F2F))
            MapStatRow("Private", privateCount, Color(0xFF7B1FA2))
        }
    }
}

@Composable
private fun MapStatRow(
    label: String,
    value: Int,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "$value $label",
            fontSize = 11.sp,
            color = SquadTextPrimary,
            fontWeight = FontWeight.SemiBold
        )
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
        color = SquadSurface,
        shape = RoundedCornerShape(18.dp),
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pin.sportType.name.lowercase().replaceFirstChar { it.uppercase() },
                    color = SquadOrange,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(SquadOrangeLight, RoundedCornerShape(999.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = pin.startsAt.ifBlank { "Soon" },
                    color = SquadOrange,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = pin.title,
                color = SquadTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = SquadTextSecondary,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = pin.venue.ifBlank { "Location not available" },
                    color = SquadTextSecondary,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.People,
                        contentDescription = null,
                        tint = SquadTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "${pin.registeredCount}/${pin.totalSpots} registered",
                        color = SquadTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onOpenDetailsClick,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "View Details",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}