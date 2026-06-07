package com.example.squadup.features.events.moredetails

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Euro
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material.icons.outlined.SportsVolleyball
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.theme.SquadBackground
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadSurface
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary
import com.example.squadup.core.utils.AppLanguage
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

@Composable
fun MoreDetailsScreen(
    uiState: MoreDetailsUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onBackClick: () -> Unit,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                showBackButton = true,
                onBackClick = onBackClick,
                showNotificationsButton = true,
                onNotificationsClick = onNotificationsClick,
                showSettingsButton = true,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        },
        bottomBar = {
            AppNavBar(
                selectedRoute = selectedRoute,
                onItemClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SquadBackground)
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SquadOrange)
                }
            }

            uiState.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SquadBackground)
                        .padding(innerPadding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = SquadTextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SquadBackground)
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                ) {
                    HeroSection(
                        title = uiState.title,
                        entryType = uiState.entryType,
                        imageUrl = uiState.imageUrl
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                    ) {
                        Spacer(modifier = Modifier.height(18.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoCard(
                                icon = Icons.Outlined.CalendarMonth,
                                label = "DATA",
                                value = uiState.date.ifBlank { "Sem data" },
                                modifier = Modifier.weight(1f)
                            )

                            InfoCard(
                                icon = Icons.Outlined.AccessTime,
                                label = "HORA",
                                value = uiState.time.ifBlank { "Sem hora" },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            InfoCard(
                                icon = Icons.Outlined.SportsSoccer,
                                label = "MODALIDADE",
                                value = uiState.modalityName,
                                modifier = Modifier.weight(1f)
                            )

                            InfoCard(
                                icon = Icons.Outlined.Info,
                                label = "FORMATO",
                                value = uiState.formatName,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        StatusAndPriceCard(uiState = uiState)

                        Spacer(modifier = Modifier.height(18.dp))

                        TeamRequirementCard(
                            title = uiState.teamRequirementTitle,
                            description = uiState.teamRequirementDescription,
                            registeredTeams = uiState.registeredTeams,
                            maxTeams = uiState.maxTeams,
                            spotsLeft = uiState.spotsLeft
                        )

                        if (uiState.hasDescription) {
                            Spacer(modifier = Modifier.height(18.dp))

                            SectionTitle(
                                icon = Icons.Outlined.Description,
                                title = "Descrição"
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            TextCard(text = uiState.description)
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        SectionTitle(
                            icon = Icons.Outlined.Gavel,
                            title = "Regras"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (uiState.hasRules) {
                            uiState.rules.forEachIndexed { index, rule ->
                                RuleCard(text = rule)

                                if (index < uiState.rules.lastIndex) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        } else {
                            TextCard(text = "Este evento ainda não tem regras definidas.")
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        SectionTitle(
                            icon = Icons.Outlined.LocationOn,
                            title = "Localização"
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LocationPreviewCard(
                            venueName = uiState.venueName.ifBlank { "Morada não definida" },
                            latitude = uiState.latitude,
                            longitude = uiState.longitude
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                // Próxima fase: Join Event
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SquadOrange,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Participar",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroSection(
    title: String,
    entryType: String,
    imageUrl: String?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
    ) {
        if (!imageUrl.isNullOrBlank()) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF3B1A12),
                                Color(0xFFE26D2F),
                                Color(0xFFFFB35C),
                                Color(0xFF2E1A16)
                            )
                        )
                    )
            )

            Icon(
                imageVector = Icons.Outlined.SportsVolleyball,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.18f),
                modifier = Modifier
                    .size(104.dp)
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.72f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            Text(
                text = entryType,
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .background(SquadOrange, RoundedCornerShape(999.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(84.dp),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFF0E1DC))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun StatusAndPriceCard(
    uiState: MoreDetailsUiState
) {
    val paymentLabel = when {
        uiState.priceLabel.isNotBlank() && uiState.priceLabel != "Grátis" -> uiState.priceLabel
        uiState.entryFeeLabel.isNotBlank() && uiState.entryFeeLabel != "Grátis" -> uiState.entryFeeLabel
        else -> "Grátis"
    }

    val shouldShowRegistrationPeriod =
        uiState.registrationPeriod.isNotBlank() &&
                uiState.registrationPeriod != "Sem período definido"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFF0E1DC))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SectionTitle(
                icon = Icons.Outlined.Euro,
                title = "Informação do evento"
            )

            Spacer(modifier = Modifier.height(12.dp))

            DetailRow(label = "Estado", value = uiState.eventStatus)

            HorizontalDivider(
                color = Color(0xFFF0E1DC),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            DetailRow(label = "Preço de entrada", value = paymentLabel)

            if (shouldShowRegistrationPeriod) {
                HorizontalDivider(
                    color = Color(0xFFF0E1DC),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                DetailRow(
                    label = "Inscrições",
                    value = uiState.registrationPeriod
                )
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = SquadTextSecondary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value.ifBlank { "-" },
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = SquadTextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1.2f)
        )
    }
}

@Composable
private fun TeamRequirementCard(
    title: String,
    description: String,
    registeredTeams: Int,
    maxTeams: Int,
    spotsLeft: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFEEE9),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFFFFD3C7))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Groups,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrange,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MiniStatCard(
                    label = "Inscritas",
                    value = registeredTeams.toString(),
                    modifier = Modifier.weight(1f)
                )

                MiniStatCard(
                    label = "Máximo",
                    value = if (maxTeams > 0) maxTeams.toString() else "-",
                    modifier = Modifier.weight(1f)
                )

                MiniStatCard(
                    label = "Vagas",
                    value = if (maxTeams > 0) spotsLeft.toString() else "-",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MiniStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(58.dp),
        color = Color.White,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrange,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = label,
                fontSize = 10.sp,
                color = SquadTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun SectionTitle(
    icon: ImageVector,
    title: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = SquadOrange,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TextCard(
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = SquadTextSecondary,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun RuleCard(
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF0FB391),
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = SquadTextSecondary
            )
        }
    }
}

@Composable
private fun LocationPreviewCard(
    venueName: String,
    latitude: Double?,
    longitude: Double?
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val locationText = buildString {
        append(venueName)

        if (latitude != null && longitude != null) {
            append("\n")
            append("https://www.google.com/maps/search/?api=1&query=")
            append(latitude)
            append(",")
            append(longitude)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, Color(0xFFF0E1DC))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                if (latitude != null && longitude != null) {
                    MapLibreLocationPreview(
                        latitude = latitude,
                        longitude = longitude,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.05f),
                                        Color.Black.copy(alpha = 0.45f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Localização selecionada",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Copia para abrir no GPS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFFE7EFE7),
                                        Color(0xFFBFD6C7),
                                        Color(0xFFE6E6DF)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = SquadOrange,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Localização do evento",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = venueName,
                        fontSize = 12.sp,
                        color = SquadTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(locationText))

                        Toast.makeText(
                            context,
                            "Localização copiada",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Copiar",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun MapLibreLocationPreview(
    latitude: Double,
    longitude: Double,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val selectedLocation = remember(latitude, longitude) {
        LatLng(latitude, longitude)
    }

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

    LaunchedEffect(latitude, longitude) {
        mapView.getMapAsync { map ->
            map.setStyle("https://tiles.openfreemap.org/styles/liberty") {
                map.uiSettings.apply {
                    isCompassEnabled = false
                    isLogoEnabled = false
                    isAttributionEnabled = false
                    isZoomGesturesEnabled = false
                    isScrollGesturesEnabled = false
                    isRotateGesturesEnabled = false
                    isTiltGesturesEnabled = false
                }

                map.clear()

                map.cameraPosition = CameraPosition.Builder()
                    .target(selectedLocation)
                    .zoom(15.0)
                    .build()

                map.addMarker(
                    MarkerOptions()
                        .position(selectedLocation)
                        .title("Localização selecionada")
                )
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView }
    )
}