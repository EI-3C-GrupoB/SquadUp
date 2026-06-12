package com.example.squadup.features.events.moredetails

import androidx.compose.material3.MaterialTheme

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.squadup.core.ui.components.rememberIsLandscape
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.utils.AppLanguage
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import androidx.compose.ui.res.stringResource
import com.example.squadup.R

@Composable
fun MoreDetailsScreen(
    uiState: MoreDetailsUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onBackClick: () -> Unit,
    onManageEventClick: (Int) -> Unit,
    onJoinIndividuallyClick: () -> Unit,
    onJoinWithTeamClick: () -> Unit,
    onDismissTeamPicker: () -> Unit,
    onTeamSelected: (Int) -> Unit,
    onPaymentClick: () -> Unit = {},
    onViewTicketClick: () -> Unit = {},
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {
    if (uiState.isTeamPickerVisible) {
        TeamPickerBottomSheet(
            uiState = uiState,
            onDismiss = onDismissTeamPicker,
            onTeamSelected = onTeamSelected
        )
    }

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
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SquadOrange)
                }
            }

            uiState.errorMessage != null && !uiState.isTeamPickerVisible -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.errorMessage,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
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
                            .padding(horizontal = responsiveHorizontalPadding(18.dp))
                    ) {
                        Spacer(modifier = Modifier.height(18.dp))

                        // Privacy + registration status badges
                        val statusBadges = buildList {
                            if (uiState.isPrivate) add("Privado" to Color(0xFF6B7280))
                            if (uiState.registrationStatusLabel.isNotBlank()) {
                                val c = when (uiState.registrationStatusLabel) {
                                    "Cheio" -> Color(0xFFDC2626)
                                    "Inscrições encerradas" -> Color(0xFF6B7280)
                                    else -> Color(0xFF2563EB)
                                }
                                add(uiState.registrationStatusLabel to c)
                            }
                            if (uiState.eventStatus.isNotBlank() && uiState.eventStatus != "Estado não definido") {
                                val c = when (uiState.eventStatus) {
                                    "Em curso", "Activo" -> Color(0xFF059669)
                                    "Terminado" -> Color(0xFF6B7280)
                                    "Cancelado" -> Color(0xFFDC2626)
                                    else -> SquadOrange
                                }
                                add(uiState.eventStatus to c)
                            }
                        }
                        if (statusBadges.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                statusBadges.forEach { (label, color) ->
                                    Surface(
                                        color = color.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = color,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

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

                        EventActionButtons(
                            uiState = uiState,
                            onManageEventClick = {
                                uiState.eventId?.let(onManageEventClick)
                            },
                            onJoinIndividuallyClick = onJoinIndividuallyClick,
                            onJoinWithTeamClick = onJoinWithTeamClick,
                            onPaymentClick = onPaymentClick,
                            onViewTicketClick = onViewTicketClick
                        )

                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamPickerBottomSheet(
    uiState: MoreDetailsUiState,
    onDismiss: () -> Unit,
    onTeamSelected: (Int) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = {
            if (!uiState.isJoining) {
                onDismiss()
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = responsiveHorizontalPadding(20.dp))
                .padding(bottom = 28.dp)
        ) {
            Text(
                text = stringResource(R.string.more_details_choose_team),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.more_details_team_hint),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!uiState.errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFFFFF5F5),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFFD6D6))
                ) {
                    Text(
                        text = uiState.errorMessage,
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        color = Color(0xFFB3261E),
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                uiState.isLoadingAvailableTeams -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SquadOrange)
                    }
                }

                uiState.availableTeams.isEmpty() -> {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFF0E1DC))
                    ) {
                        Text(
                            text = stringResource(R.string.more_details_no_teams),
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                else -> {
                    val isLandscape = rememberIsLandscape()

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isLandscape) 200.dp else 260.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = uiState.availableTeams,
                            key = { it.id }
                        ) { team ->
                            Button(
                                onClick = { onTeamSelected(team.id) },
                                enabled = !uiState.isJoining,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface,
                                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                border = BorderStroke(1.dp, Color(0xFFF0E1DC))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Groups,
                                        contentDescription = null,
                                        tint = SquadOrange,
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Text(
                                        text = team.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    if (uiState.isJoining &&
                                        uiState.joiningRegistrationType == "pedido_evento_equipa"
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp,
                                            color = SquadOrange
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onDismiss,
                enabled = !uiState.isJoining,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.more_details_cancel),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EventActionButtons(
    uiState: MoreDetailsUiState,
    onManageEventClick: () -> Unit,
    onJoinIndividuallyClick: () -> Unit,
    onJoinWithTeamClick: () -> Unit,
    onPaymentClick: () -> Unit = {},
    onViewTicketClick: () -> Unit = {}
) {
    val regStatus = uiState.userEventRegistrationStatus
    val hasUserEventRegistration = !regStatus.isNullOrBlank()

    val isPaidEvent = (uiState.eventPrice ?: 0.0) > 0.0
    val paymentDone = uiState.paymentStatus == "pago"

    val pendingTeamText = stringResource(R.string.more_details_pending_team)
    val pendingIndividualText = stringResource(R.string.more_details_pending_individual)
    val rejectedText = stringResource(R.string.more_details_rejected)
    val registeredText = stringResource(R.string.more_details_registered)
    val pendingButtonText = when {
        regStatus == "pendente" && uiState.userEventRegistrationType == "pedido_evento_equipa" ->
            pendingTeamText
        regStatus == "pendente" ->
            pendingIndividualText
        regStatus == "recusada" ->
            rejectedText
        else ->
            registeredText
    }

    when {
        uiState.canManageEvent -> {
            Button(
                onClick = onManageEventClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.more_details_manage_event),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        regStatus == "aceite" && isPaidEvent && !paymentDone -> {
            Button(
                onClick = onPaymentClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.more_details_complete_payment),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        regStatus == "aceite" -> {
            Button(
                onClick = onViewTicketClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2E7D32),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = stringResource(R.string.more_details_view_ticket),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        hasUserEventRegistration -> {
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                )
            ) {
                Text(
                    text = pendingButtonText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        uiState.canParticipateIndividually && uiState.canParticipateWithTeam -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onJoinIndividuallyClick,
                    enabled = !uiState.isJoining,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SquadOrange,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = when {
                            uiState.joiningRegistrationType == "evento_individual" ->
                                stringResource(R.string.more_details_join_individual_loading)
                            uiState.isJoining ->
                                stringResource(R.string.more_details_blocked)
                            else ->
                                stringResource(R.string.more_details_join_individual)
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onJoinWithTeamClick,
                    enabled = !uiState.isJoining,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = SquadOrange,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (uiState.isJoining) {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                        } else {
                            SquadOrange
                        }
                    )
                ) {
                    Text(
                        text = when {
                            uiState.joiningRegistrationType == "pedido_evento_equipa" ->
                                stringResource(R.string.more_details_join_team_loading)
                            uiState.isJoining ->
                                stringResource(R.string.more_details_blocked)
                            else ->
                                stringResource(R.string.more_details_join_team)
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        uiState.canParticipateIndividually -> {
            Button(
                onClick = onJoinIndividuallyClick,
                enabled = !uiState.isJoining,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(
                    text = if (uiState.joiningRegistrationType == "evento_individual") {
                        stringResource(R.string.more_details_join_individual_loading)
                    } else {
                        stringResource(R.string.more_details_join_individual)
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        uiState.canParticipateWithTeam -> {
            Button(
                onClick = onJoinWithTeamClick,
                enabled = !uiState.isJoining,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = SquadOrange,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (uiState.isJoining) {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
                    } else {
                        SquadOrange
                    }
                )
            ) {
                Text(
                    text = if (uiState.joiningRegistrationType == "pedido_evento_equipa") {
                        stringResource(R.string.more_details_join_team_loading)
                    } else {
                        stringResource(R.string.more_details_join_team)
                    },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        else -> Unit
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
        color = MaterialTheme.colorScheme.surface,
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
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
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
        color = MaterialTheme.colorScheme.surface,
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value.ifBlank { "-" },
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
        color = MaterialTheme.colorScheme.surface,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
            color = MaterialTheme.colorScheme.onSurface,
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
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(10.dp),
        shadowElevation = 1.dp
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
        color = MaterialTheme.colorScheme.surface,
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
        color = MaterialTheme.colorScheme.surface,
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
                            text = stringResource(R.string.location_selected),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = stringResource(R.string.location_copy_hint),
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
                        text = stringResource(R.string.location_event_label),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = venueName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                            context.getString(R.string.location_copied_toast),
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
                        text = stringResource(R.string.location_copy),
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
