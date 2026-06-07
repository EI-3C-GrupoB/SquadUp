package com.example.squadup.features.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.squadup.R
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.components.EmptyStateCard
import com.example.squadup.core.ui.components.SquadFab
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.toDisplayName
import com.example.squadup.core.utils.toIcon

@Composable
fun EventsScreen(
    uiState: EventsUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSportFilterChange: (SportType?) -> Unit,
    onNotificationsClick: () -> Unit,
    onEventClick: (String) -> Unit,
    onViewCalendarClick: () -> Unit,
    onFilterByMyTeamsClick: () -> Unit,
    onMapClick: () -> Unit,
    onCreateEventClick: () -> Unit,
    onRefreshLocationClick: () -> Unit,
    isAdmin: Boolean,
    isAdminView: Boolean,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    notificationsCount: Int = 0
) {
    val context = LocalContext.current
    var selectedEvent by remember { mutableStateOf<BrowseEventItem?>(null) }

    selectedEvent?.let { event ->
        EventSummaryBottomSheet(
            event = event,
            onDismiss = { selectedEvent = null },
            onRegisterClick = { onEventClick(event.id) }
        )
    }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = true,
                showBackButton = false,
                showNotificationsButton = true,
                notificationsCount = notificationsCount,
                showSettingsButton = true,
                onNotificationsClick = onNotificationsClick,
                isAdmin = isAdmin,
                isAdminView = isAdminView,
                onAdminViewChange = onAdminViewChange,
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
        },
        floatingActionButton = {
            SquadFab(onClick = onCreateEventClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.events_title),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = null,
                    tint = SquadTextSecondary,
                    modifier = Modifier
                        .size(26.dp)
                        .clickable(onClick = onMapClick)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.events_search_placeholder),
                        color = SquadTextSecondary,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = SquadTextSecondary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SquadOrange,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SportFilterChip(
                    label = stringResource(R.string.events_filter_all),
                    selected = uiState.selectedSport == null,
                    onClick = { onSportFilterChange(null) }
                )

                SportType.entries.forEach { sport ->
                    SportFilterChip(
                        label = sport.toDisplayName(context),
                        selected = uiState.selectedSport == sport,
                        onClick = { onSportFilterChange(sport) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LocationSourceCard(
                locationSource = uiState.locationSource,
                isLoading = uiState.isLoading,
                onRefreshClick = onRefreshLocationClick,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            uiState.filteredFeaturedEvent?.let { featured ->
                FeaturedEventCard(
                    event = featured,
                    onClick = { onEventClick(featured.id) },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (uiState.filteredUpcomingEvents.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.events_upcoming),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = stringResource(R.string.events_view_calendar),
                        fontSize = 13.sp,
                        color = SquadOrange,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable(onClick = onViewCalendarClick)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        uiState.filteredUpcomingEvents.forEachIndexed { index, item ->
                            UpcomingEventRow(
                                item = item,
                                onClick = { onEventClick(item.id) }
                            )

                            if (index < uiState.filteredUpcomingEvents.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = SquadGrayLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onFilterByMyTeamsClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SquadGray),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = SquadTextPrimary
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.events_filter_my_teams),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (uiState.filteredBrowseEvents.isEmpty() && uiState.filteredUpcomingEvents.isEmpty()) {
                EmptyStateCard(
                    title = "Sem eventos encontrados",
                    message = when (uiState.locationSource) {
                        EventsLocationSource.DEVICE -> "Não existem eventos perto da tua localização actual para os filtros seleccionados."
                        EventsLocationSource.PROFILE -> "Não existem eventos perto da localização do teu perfil para os filtros seleccionados."
                        EventsLocationSource.UNKNOWN -> "Não existem eventos disponíveis para os filtros seleccionados."
                    },
                    icon = Icons.Outlined.CalendarMonth,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            } else {
                uiState.filteredBrowseEvents.forEach { event ->
                    BrowseEventCard(
                        event = event,
                        onClick = { selectedEvent = event },
                        onDetailsClick = { onEventClick(event.id) },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(88.dp))
        }
    }
}

@Composable
private fun LocationSourceCard(
    locationSource: EventsLocationSource,
    isLoading: Boolean,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val message = when (locationSource) {
        EventsLocationSource.DEVICE -> "A mostrar eventos perto da tua localização actual"
        EventsLocationSource.PROFILE -> "A mostrar eventos perto da localização do teu perfil"
        EventsLocationSource.UNKNOWN -> "A preparar localização dos eventos"
    }

    val subMessage = when (locationSource) {
        EventsLocationSource.DEVICE -> "Se estiveres no emulador, confirma a localização em Extended Controls."
        EventsLocationSource.PROFILE -> "A localização actual não foi usada. Podes tentar actualizar novamente."
        EventsLocationSource.UNKNOWN -> "A app vai tentar usar primeiro a localização actual."
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.LocationOn,
                contentDescription = null,
                tint = SquadOrange,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SquadTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subMessage,
                    fontSize = 11.sp,
                    color = SquadTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(
                onClick = onRefreshClick,
                enabled = !isLoading
            ) {
                Text(
                    text = if (isLoading) "A carregar" else "Actualizar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun SportFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SquadOrange,
            selectedLabelColor = Color.White,
            containerColor = Color.White,
            labelColor = SquadTextSecondary
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = SquadOrange,
            borderColor = SquadGray
        )
    )
}

@Composable
private fun FeaturedEventCard(
    event: FeaturedEventItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(14.dp))
        ) {
            if (!event.imageUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(event.imageUrl),
                    contentDescription = event.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(event.sportType.color)
                )

                Icon(
                    imageVector = event.sportType.toIcon(),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.12f),
                    modifier = Modifier
                        .size(160.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
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
                                Color.Black.copy(alpha = 0.78f)
                            )
                        )
                    )
            )

            Text(
                text = stringResource(R.string.events_featured_badge),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .background(SquadOrange, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(14.dp)
            ) {
                Text(
                    text = event.seriesName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.8f),
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = event.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(13.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = event.dateTime,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (event.distance.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "• ${event.distance}",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier
                            .size(13.dp)
                            .padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = event.venue,
                        fontSize = 12.sp,
                        lineHeight = 14.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun UpcomingEventRow(
    item: UpcomingEventItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(SquadOrangeLight, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(
                text = item.month,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrange,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.day,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrange,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = buildString {
                    append(item.sportType.toDisplayName(context))
                    append(" • ")
                    append(item.time)

                    if (item.distance.isNotBlank()) {
                        append(" • ")
                        append(item.distance)
                    }
                },
                fontSize = 12.sp,
                color = SquadTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = SquadTextSecondary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun BrowseEventCard(
    event: BrowseEventItem,
    onClick: () -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 2.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            ) {
                if (!event.imageUrl.isNullOrBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(event.imageUrl),
                        contentDescription = event.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.05f),
                                        Color.Black.copy(alpha = 0.35f)
                                    )
                                )
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        event.sportType.color,
                                        event.sportType.color.copy(alpha = 0.7f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = event.sportType.toIcon(),
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(110.dp)
                        )
                    }
                }

                Text(
                    text = event.sportType.toDisplayName(context).uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = event.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = event.price,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadOrange,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = SquadTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = buildString {
                            append(event.dateTime)

                            if (event.distance.isNotBlank()) {
                                append(" • ")
                                append(event.distance)
                            }
                        },
                        fontSize = 13.sp,
                        color = SquadTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = SquadTextSecondary,
                        modifier = Modifier
                            .size(14.dp)
                            .padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = event.venue,
                        fontSize = 13.sp,
                        lineHeight = 16.sp,
                        color = SquadTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = stringResource(R.string.events_see_details),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventSummaryBottomSheet(
    event: BrowseEventItem,
    onDismiss: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                event.sportType.color,
                                event.sportType.color.copy(alpha = 0.6f)
                            )
                        )
                    )
            ) {
                Icon(
                    imageVector = event.sportType.toIcon(),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.65f)
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
                        text = event.entryType,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .background(SquadOrange, RoundedCornerShape(999.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = event.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 18.dp)) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp),
                        color = Color(0xFFF8F8F8),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CalendarMonth,
                                    contentDescription = null,
                                    tint = SquadOrange,
                                    modifier = Modifier.size(13.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = "DATE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SquadTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = event.dateTime.substringBefore(" •").ifBlank { event.dateTime },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = SquadTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp),
                        color = Color(0xFFF8F8F8),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = SquadOrange,
                                    modifier = Modifier.size(13.dp)
                                )

                                Spacer(modifier = Modifier.width(4.dp))

                                Text(
                                    text = "VENUE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SquadTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = event.venue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = SquadTextPrimary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFEEE9),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD3C7))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (event.requiresTeam) {
                                Icons.Outlined.Groups
                            } else {
                                Icons.Outlined.PersonAdd
                            },
                            contentDescription = null,
                            tint = SquadOrange,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = if (event.requiresTeam) {
                                    stringResource(R.string.events_sheet_team_required)
                                } else {
                                    stringResource(R.string.events_sheet_no_team)
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SquadOrange,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = if (event.requiresTeam) {
                                    stringResource(R.string.events_sheet_team_required_sub)
                                } else {
                                    stringResource(R.string.events_sheet_no_team_sub)
                                },
                                fontSize = 11.sp,
                                color = SquadTextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Groups,
                        contentDescription = null,
                        tint = SquadTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = stringResource(
                            R.string.events_sheet_spots,
                            event.spotsLeft,
                            event.totalSpots
                        ),
                        fontSize = 13.sp,
                        color = SquadTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = event.price,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadOrange,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = if (event.price == "Free") {
                            stringResource(R.string.events_sheet_register_free)
                        } else {
                            stringResource(R.string.events_sheet_register_paid, event.price)
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Outlined.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
