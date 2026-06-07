package com.example.squadup.features.events.createevent

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.RocketLaunch
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.rememberAsyncImagePainter
import com.example.squadup.R
import com.example.squadup.core.enums.EventFormat
import com.example.squadup.core.enums.RecurrenceType
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.DateTimePickerMode
import com.example.squadup.core.ui.components.LocationPickerDialog
import com.example.squadup.core.ui.components.PrimaryButton
import com.example.squadup.core.ui.components.ProfileDropdownField
import com.example.squadup.core.ui.components.SelectedLocation
import com.example.squadup.core.ui.components.SquadDateTimePickerField
import com.example.squadup.core.ui.theme.SquadGray
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadOrangeLight
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.toDisplayName
import com.example.squadup.core.utils.toIcon
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

@Composable
fun CreateEventScreen(
    uiState: CreateEventUiState,
    onBackClick: () -> Unit,
    onNextStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onGoToStep: (CreateEventStep) -> Unit,

    // Step 1
    onEventNameChange: (String) -> Unit,
    onNotificationsClick: () -> Unit,
    onPrivacyChange: (Boolean) -> Unit,
    onSportSelect: (SportType) -> Unit,
    onCoverImageSelected: (Uri?) -> Unit,
    formatOptions: List<String>,

    // Step 2
    onEventFormatChange: (EventFormat) -> Unit,
    onFormatChange: (String) -> Unit,
    onMaxTeamsChange: (Int) -> Unit,
    onGeneralRulesChange: (String) -> Unit,
    onPublicEventToggle: (Boolean) -> Unit,
    onEntryFeeChange: (String) -> Unit,
    onAllowTeamsToggle: (Boolean) -> Unit,
    onAllowFreeAgentsToggle: (Boolean) -> Unit,

    // Step 3
    onVenueChange: (String) -> Unit,
    onLocationSelected: (SelectedLocation) -> Unit,
    onEventDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onRegistrationStartDateChange: (String) -> Unit,
    onRegistrationStartTimeChange: (String) -> Unit,
    onRegistrationEndDateChange: (String) -> Unit,
    onRegistrationEndTimeChange: (String) -> Unit,
    onRecurringToggle: (Boolean) -> Unit,
    onShowRecurrenceDialog: (Boolean) -> Unit,
    onRecurrenceTypeChange: (RecurrenceType) -> Unit,
    onRecurringDayToggle: (Int) -> Unit,

    // Review
    onTeamNotifyToggle: (String) -> Unit,
    onCreateEvent: () -> Unit,

    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    isAdmin: Boolean,
    isAdminView: Boolean,
    onAdminViewChange: (Boolean) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    val titleRes = when (uiState.currentStep) {
        CreateEventStep.BASIC_INFO -> R.string.createEvent_step1_title
        CreateEventStep.FORMAT_PLAYERS -> R.string.createEvent_step2_title
        CreateEventStep.LOCATION_TIME -> R.string.createEvent_step3_title
        CreateEventStep.REVIEW -> R.string.createEvent_review_title
    }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = stringResource(titleRes),
                showBackButton = true,
                onBackClick = if (uiState.currentStep == CreateEventStep.BASIC_INFO) {
                    onBackClick
                } else {
                    onPreviousStep
                },
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
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                if (uiState.currentStep != CreateEventStep.REVIEW) {
                    StepProgressBar(
                        totalSteps = 3,
                        currentStep = uiState.progressStep,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                    )
                }

                when (uiState.currentStep) {
                    CreateEventStep.BASIC_INFO -> BasicInfoStep(
                        uiState = uiState,
                        onEventNameChange = onEventNameChange,
                        onPrivacyChange = onPrivacyChange,
                        onSportSelect = onSportSelect,
                        onNextStep = onNextStep
                    )

                    CreateEventStep.FORMAT_PLAYERS -> FormatPlayersStep(
                        uiState = uiState,
                        formatOptions = formatOptions,
                        onEventFormatChange = onEventFormatChange,
                        onFormatChange = onFormatChange,
                        onMaxTeamsChange = onMaxTeamsChange,
                        onGeneralRulesChange = onGeneralRulesChange,
                        onPublicEventToggle = onPublicEventToggle,
                        onEntryFeeChange = onEntryFeeChange,
                        onAllowTeamsToggle = onAllowTeamsToggle,
                        onAllowFreeAgentsToggle = onAllowFreeAgentsToggle,
                        onNextStep = onNextStep
                    )

                    CreateEventStep.LOCATION_TIME -> LocationTimeStep(
                        uiState = uiState,
                        onVenueChange = onVenueChange,
                        onLocationSelected = onLocationSelected,
                        onEventDateChange = onEventDateChange,
                        onStartTimeChange = onStartTimeChange,
                        onEndTimeChange = onEndTimeChange,
                        onRegistrationStartDateChange = onRegistrationStartDateChange,
                        onRegistrationStartTimeChange = onRegistrationStartTimeChange,
                        onRegistrationEndDateChange = onRegistrationEndDateChange,
                        onRegistrationEndTimeChange = onRegistrationEndTimeChange,
                        onRecurringToggle = onRecurringToggle,
                        onNextStep = onNextStep
                    )

                    CreateEventStep.REVIEW -> ReviewStep(
                        uiState = uiState,
                        onEditSteps = { onGoToStep(CreateEventStep.BASIC_INFO) },
                        onCoverImageSelected = onCoverImageSelected,
                        onTeamNotifyToggle = onTeamNotifyToggle,
                        onCreateEvent = onCreateEvent
                    )
                }
            }

            if (uiState.showRecurrenceDialog) {
                RecurrenceDialog(
                    uiState = uiState,
                    onRecurrenceTypeChange = onRecurrenceTypeChange,
                    onDayToggle = onRecurringDayToggle,
                    onSave = { onShowRecurrenceDialog(false) },
                    onCancel = {
                        onShowRecurrenceDialog(false)
                        onRecurringToggle(false)
                    }
                )
            }
        }
    }
}

@Composable
private fun StepProgressBar(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        repeat(totalSteps) { index ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(4.dp)
                    .background(
                        color = if (index < currentStep) SquadOrange else SquadGrayLight,
                        shape = RoundedCornerShape(999.dp)
                    )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BasicInfoStep(
    uiState: CreateEventUiState,
    onEventNameChange: (String) -> Unit,
    onPrivacyChange: (Boolean) -> Unit,
    onSportSelect: (SportType) -> Unit,
    onNextStep: () -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        StepHeaderImage(
            heading = stringResource(R.string.createEvent_step1_heading),
            subtitle = stringResource(R.string.createEvent_step1_subtitle),
            imageRes = R.drawable.create_event_1
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionLabel(stringResource(R.string.createEvent_event_name_label))

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = uiState.eventName,
            onValueChange = onEventNameChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.createEvent_event_name_placeholder),
                    color = SquadTextSecondary,
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = null,
                    tint = SquadTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SquadOrange,
                unfocusedBorderColor = SquadGrayLight,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        SectionLabel(stringResource(R.string.createEvent_privacy_label))

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PrivacyButton(
                label = stringResource(R.string.createEvent_privacy_public),
                icon = Icons.Outlined.Public,
                selected = uiState.isPublic,
                onClick = { onPrivacyChange(true) },
                modifier = Modifier.weight(1f)
            )

            PrivacyButton(
                label = stringResource(R.string.createEvent_privacy_private),
                icon = Icons.Outlined.Lock,
                selected = !uiState.isPublic,
                onClick = { onPrivacyChange(false) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionLabel(stringResource(R.string.createEvent_sports_label))

        Spacer(modifier = Modifier.height(10.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SportType.entries.forEach { sport ->
                SportChip(
                    label = sport.toDisplayName(context),
                    icon = sport.toIcon(),
                    selected = uiState.selectedSport == sport,
                    onClick = { onSportSelect(sport) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = stringResource(R.string.createEvent_next_step),
            onClick = onNextStep,
            trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FormatPlayersStep(
    uiState: CreateEventUiState,
    formatOptions: List<String>,
    onEventFormatChange: (EventFormat) -> Unit,
    onFormatChange: (String) -> Unit,
    onMaxTeamsChange: (Int) -> Unit,
    onGeneralRulesChange: (String) -> Unit,
    onPublicEventToggle: (Boolean) -> Unit,
    onEntryFeeChange: (String) -> Unit,
    onAllowTeamsToggle: (Boolean) -> Unit,
    onAllowFreeAgentsToggle: (Boolean) -> Unit,
    onNextStep: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        StepHeaderImage(
            heading = stringResource(R.string.createEvent_step2_heading),
            imageRes = R.drawable.basketball_court,
            colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
        )

        Spacer(modifier = Modifier.height(24.dp))

        SectionLabel(stringResource(R.string.createEvent_event_format_label))

        Spacer(modifier = Modifier.height(10.dp))

        EventFormat.entries.forEach { format ->
            EventFormatCard(
                format = format,
                selected = uiState.eventFormat == format,
                onClick = { onEventFormatChange(format) }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.eventFormat != EventFormat.SINGLE_MATCH) {
            ProfileDropdownField(
                label = stringResource(R.string.createEvent_format_label),
                selectedValue = uiState.format,
                options = formatOptions,
                onValueSelected = onFormatChange
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionLabel(stringResource(R.string.createEvent_max_teams_label))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onMaxTeamsChange(-1) },
                    modifier = Modifier
                        .size(42.dp)
                        .background(SquadGrayLight, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = null,
                        tint = SquadTextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Slider(
                    value = uiState.maxTeams.toFloat(),
                    onValueChange = { onMaxTeamsChange(it.toInt() - uiState.maxTeams) },
                    valueRange = 2f..64f,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = SquadOrange,
                        activeTrackColor = SquadOrange,
                        inactiveTrackColor = SquadOrangeLight
                    )
                )

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(SquadOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.maxTeams.toString(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Text(
                text = stringResource(
                    R.string.createEvent_max_teams_hint,
                    uiState.maxTeams * 11,
                    uiState.maxTeams * 5
                ),
                fontSize = 12.sp,
                color = SquadTextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SquadGrayLight)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = stringResource(R.string.createEvent_rules_label),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                BasicTextField(
                    value = uiState.generalRules,
                    onValueChange = onGeneralRulesChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = SquadTextPrimary
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (uiState.generalRules.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.createEvent_rules_placeholder),
                                    fontSize = 14.sp,
                                    color = SquadTextSecondary
                                )
                            }

                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(
                        R.string.createEvent_rules_counter,
                        uiState.generalRules.length
                    ),
                    fontSize = 11.sp,
                    color = SquadTextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SectionLabel(stringResource(R.string.createEvent_participation_label))

        Spacer(modifier = Modifier.height(10.dp))

        ParticipationToggleRow(
            icon = Icons.Outlined.Groups,
            label = stringResource(R.string.createEvent_allow_teams),
            subtitle = stringResource(R.string.createEvent_allow_teams_sub),
            checked = uiState.allowTeams,
            onCheckedChange = onAllowTeamsToggle
        )

        Spacer(modifier = Modifier.height(10.dp))

        ParticipationToggleRow(
            icon = Icons.Outlined.PersonAdd,
            label = stringResource(R.string.createEvent_allow_free_agents),
            subtitle = stringResource(R.string.createEvent_allow_free_agents_sub),
            checked = uiState.allowFreeAgents,
            onCheckedChange = onAllowFreeAgentsToggle
        )

        Spacer(modifier = Modifier.height(20.dp))

        SectionLabel(stringResource(R.string.createEvent_entry_privacy_label))

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Public,
                contentDescription = null,
                tint = SquadOrange,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.createEvent_public_event),
                fontSize = 14.sp,
                color = SquadTextPrimary,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = uiState.isPublicEvent,
                onCheckedChange = onPublicEventToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = SquadOrange
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        SectionLabel(stringResource(R.string.createEvent_entry_fee_label))

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = uiState.entryFee,
            onValueChange = onEntryFeeChange,
            placeholder = {
                Text(
                    text = "0.00",
                    color = SquadTextSecondary
                )
            },
            prefix = {
                Text(
                    text = "€ ",
                    color = SquadTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SquadOrange,
                unfocusedBorderColor = SquadGrayLight
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = stringResource(R.string.createEvent_next_step),
            onClick = onNextStep,
            trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun LocationTimeStep(
    uiState: CreateEventUiState,
    @Suppress("UNUSED_PARAMETER") onVenueChange: (String) -> Unit,
    onLocationSelected: (SelectedLocation) -> Unit,
    onEventDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onRegistrationStartDateChange: (String) -> Unit,
    onRegistrationStartTimeChange: (String) -> Unit,
    onRegistrationEndDateChange: (String) -> Unit,
    onRegistrationEndTimeChange: (String) -> Unit,
    onRecurringToggle: (Boolean) -> Unit,
    onNextStep: () -> Unit
) {
    var showMapDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        LocationPreviewCard(
            latitude = uiState.latitude,
            longitude = uiState.longitude,
            onClick = { showMapDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { showMapDialog = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SquadGray),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = SquadTextPrimary
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Map,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.createEvent_select_on_map),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (uiState.latitude != null && uiState.longitude != null) {
            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = SquadOrangeLight,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.LocationOn,
                        contentDescription = null,
                        tint = SquadOrange,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = uiState.venue.ifBlank {
                            "Lat: %.5f, Lng: %.5f".format(
                                uiState.latitude,
                                uiState.longitude
                            )
                        },
                        color = SquadTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.createEvent_schedule_label),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        SquadDateTimePickerField(
            value = uiState.eventDate,
            onValueChange = onEventDateChange,
            label = stringResource(R.string.createEvent_event_date_label),
            placeholder = "YYYY-MM-DD",
            mode = DateTimePickerMode.DATE,
            leadingIcon = Icons.Outlined.CalendarMonth
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SquadDateTimePickerField(
                value = uiState.startTime,
                onValueChange = onStartTimeChange,
                label = stringResource(R.string.createEvent_start_time_label),
                placeholder = "09:00",
                mode = DateTimePickerMode.TIME,
                leadingIcon = Icons.Outlined.AccessTime,
                modifier = Modifier.weight(1f)
            )

            SquadDateTimePickerField(
                value = uiState.endTime,
                onValueChange = onEndTimeChange,
                label = stringResource(R.string.createEvent_end_time_label),
                placeholder = "11:30",
                mode = DateTimePickerMode.TIME,
                leadingIcon = Icons.Outlined.AccessTime,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Período de inscrições",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Opcional. Se deixares vazio, as inscrições ficam abertas até ao início do evento.",
            fontSize = 12.sp,
            lineHeight = 17.sp,
            color = SquadTextSecondary
        )

        Spacer(modifier = Modifier.height(14.dp))

        SquadDateTimePickerField(
            value = uiState.registrationStartDate,
            onValueChange = onRegistrationStartDateChange,
            label = "Data de início das inscrições",
            placeholder = "YYYY-MM-DD",
            mode = DateTimePickerMode.DATE,
            leadingIcon = Icons.Outlined.CalendarMonth
        )

        Spacer(modifier = Modifier.height(14.dp))

        SquadDateTimePickerField(
            value = uiState.registrationStartTime,
            onValueChange = onRegistrationStartTimeChange,
            label = "Hora de início das inscrições",
            placeholder = "09:00",
            mode = DateTimePickerMode.TIME,
            leadingIcon = Icons.Outlined.AccessTime
        )

        Spacer(modifier = Modifier.height(14.dp))

        SquadDateTimePickerField(
            value = uiState.registrationEndDate,
            onValueChange = onRegistrationEndDateChange,
            label = "Data de fim das inscrições",
            placeholder = "YYYY-MM-DD",
            mode = DateTimePickerMode.DATE,
            leadingIcon = Icons.Outlined.CalendarMonth
        )

        Spacer(modifier = Modifier.height(14.dp))

        SquadDateTimePickerField(
            value = uiState.registrationEndTime,
            onValueChange = onRegistrationEndTimeChange,
            label = "Hora de fim das inscrições",
            placeholder = "23:59",
            mode = DateTimePickerMode.TIME,
            leadingIcon = Icons.Outlined.AccessTime
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(SquadOrangeLight, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Autorenew,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.createEvent_recurring_label),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SquadTextPrimary
                )

                Text(
                    text = stringResource(R.string.createEvent_recurring_subtitle),
                    fontSize = 12.sp,
                    color = SquadTextSecondary
                )
            }

            Switch(
                checked = uiState.isRecurring,
                onCheckedChange = onRecurringToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = SquadOrange
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        PrimaryButton(
            text = stringResource(R.string.createEvent_review_event),
            onClick = onNextStep,
            trailingIcon = Icons.AutoMirrored.Outlined.ArrowForward
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showMapDialog) {
        LocationPickerDialog(
            onDismiss = { showMapDialog = false },
            onLocationSelected = { location ->
                onLocationSelected(location)
                showMapDialog = false
            }
        )
    }
}

@Composable
private fun ReviewStep(
    uiState: CreateEventUiState,
    onEditSteps: () -> Unit,
    onTeamNotifyToggle: (String) -> Unit,
    onCoverImageSelected: (Uri?) -> Unit,
    onCreateEvent: () -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.createEvent_review_cover_label),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        val coverImagePicker = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri ->
            onCoverImageSelected(uri)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    coverImagePicker.launch("image/*")
                }
                .background(
                    brush = Brush.linearGradient(
                        listOf(Color(0xFF2F9D73), Color(0xFF1A6B3C))
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.coverImageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(uiState.coverImageUri),
                    contentDescription = "Capa do evento",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    color = SquadOrange,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = "Alterar imagem",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Image,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Adicionar capa do evento",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Toca para escolher uma imagem",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.createEvent_review_summary_label),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = stringResource(R.string.createEvent_review_edit_steps),
                fontSize = 13.sp,
                color = SquadOrange,
                modifier = Modifier.clickable(onClick = onEditSteps)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        ReviewCard {
            Text(
                text = stringResource(R.string.createEvent_review_branding_label),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextSecondary,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = uiState.eventName.ifBlank { "—" },
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            uiState.selectedSport?.let { sport ->
                Surface(
                    color = sport.color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = sport.toDisplayName(context),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = sport.color,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ReviewCard(modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.createEvent_review_date_label),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.eventDate.ifBlank { "—" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                if (uiState.startTime.isNotBlank()) {
                    Text(
                        text = uiState.startTime,
                        fontSize = 12.sp,
                        color = SquadTextSecondary
                    )
                }
            }

            ReviewCard(modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.createEvent_review_venue_label),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = uiState.venue.ifBlank { "—" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )
            }
        }

        val hasRegistrationPeriod = uiState.registrationStartDate.isNotBlank() ||
                uiState.registrationStartTime.isNotBlank() ||
                uiState.registrationEndDate.isNotBlank() ||
                uiState.registrationEndTime.isNotBlank()

        if (hasRegistrationPeriod) {
            Spacer(modifier = Modifier.height(10.dp))

            ReviewCard {
                Text(
                    text = "PERÍODO DE INSCRIÇÕES",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextSecondary,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = buildString {
                        append("Início: ")
                        append(uiState.registrationStartDate.ifBlank { "—" })

                        if (uiState.registrationStartTime.isNotBlank()) {
                            append(" às ")
                            append(uiState.registrationStartTime)
                        }

                        append("\n")

                        append("Fim: ")
                        append(uiState.registrationEndDate.ifBlank { "—" })

                        if (uiState.registrationEndTime.isNotBlank()) {
                            append(" às ")
                            append(uiState.registrationEndTime)
                        }
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SquadTextPrimary,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ReviewCard {
            Text(
                text = stringResource(R.string.createEvent_review_format_label),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextSecondary,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            val formatName = stringResource(uiState.eventFormat.labelRes)
            val details = if (uiState.eventFormat != EventFormat.SINGLE_MATCH) {
                "$formatName • ${uiState.format} • ${uiState.maxTeams} Max Teams"
            } else {
                formatName
            }

            Text(
                text = details,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadTextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        ReviewCard {
            Text(
                text = stringResource(R.string.createEvent_review_participation_label),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextSecondary,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.allowTeams) {
                    ParticipationBadge(
                        icon = Icons.Outlined.Groups,
                        label = stringResource(R.string.createEvent_participation_teams)
                    )
                }

                if (uiState.allowFreeAgents) {
                    ParticipationBadge(
                        icon = Icons.Outlined.PersonAdd,
                        label = stringResource(R.string.createEvent_participation_free_agents)
                    )
                }
            }
        }

        if (uiState.isPlayerOrganizer && uiState.userTeams.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF8F8F8),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SquadGrayLight)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = stringResource(R.string.createEvent_notify_teams_label),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary
                    )

                    Text(
                        text = stringResource(R.string.createEvent_notify_teams_subtitle),
                        fontSize = 12.sp,
                        color = SquadTextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    uiState.userTeams.forEach { team ->
                        val checked = team.id in uiState.teamsToNotify

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onTeamNotifyToggle(team.id) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { onTeamNotifyToggle(team.id) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = SquadOrange
                                )
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = team.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SquadTextPrimary
                                )

                                Text(
                                    text = "${team.nMembers} members",
                                    fontSize = 12.sp,
                                    color = SquadTextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(team.sportType.color, CircleShape)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryButton(
            text = stringResource(R.string.createEvent_create_button),
            onClick = onCreateEvent,
            trailingIcon = Icons.Outlined.RocketLaunch
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun RecurrenceDialog(
    uiState: CreateEventUiState,
    onRecurrenceTypeChange: (RecurrenceType) -> Unit,
    onDayToggle: (Int) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")

    Dialog(onDismissRequest = onCancel) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = stringResource(R.string.createEvent_recurrence_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(20.dp))

                listOf(
                    RecurrenceType.DAILY to stringResource(R.string.createEvent_recurrence_daily),
                    RecurrenceType.WEEKLY to stringResource(R.string.createEvent_recurrence_weekly),
                    RecurrenceType.MONTHLY to stringResource(R.string.createEvent_recurrence_monthly)
                ).forEach { (type, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRecurrenceTypeChange(type) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 15.sp,
                            fontWeight = if (uiState.recurrenceType == type) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            color = if (uiState.recurrenceType == type) {
                                SquadOrange
                            } else {
                                SquadTextPrimary
                            },
                            modifier = Modifier.weight(1f)
                        )

                        RadioButton(
                            selected = uiState.recurrenceType == type,
                            onClick = { onRecurrenceTypeChange(type) },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = SquadOrange
                            )
                        )
                    }

                    if (type == RecurrenceType.WEEKLY && uiState.recurrenceType == RecurrenceType.WEEKLY) {
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            dayLabels.forEachIndexed { index, label ->
                                val selected = index in uiState.recurringDays

                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            color = if (selected) SquadOrange else SquadGrayLight,
                                            shape = CircleShape
                                        )
                                        .clickable { onDayToggle(index) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.White else SquadTextSecondary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = stringResource(R.string.createEvent_recurrence_save),
                    onClick = onSave
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.createEvent_recurrence_cancel),
                    fontSize = 14.sp,
                    color = SquadTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onCancel)
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun EventFormatCard(
    format: EventFormat,
    selected: Boolean,
    onClick: () -> Unit
) {
    val icon = when (format) {
        EventFormat.SINGLE_MATCH -> Icons.Outlined.Flag
        EventFormat.LEAGUE -> Icons.Outlined.Leaderboard
        EventFormat.KNOCKOUT -> Icons.Outlined.EmojiEvents
        EventFormat.GROUP_KNOCKOUT -> Icons.Outlined.Groups
        EventFormat.FREE -> Icons.Outlined.Edit
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (selected) SquadOrangeLight else Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) SquadOrange else SquadGrayLight
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (selected) SquadOrange else SquadGrayLight,
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (selected) Color.White else SquadTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(format.labelRes),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (selected) SquadOrange else SquadTextPrimary
                )

                Text(
                    text = stringResource(format.descRes),
                    fontSize = 12.sp,
                    color = SquadTextSecondary
                )
            }

            if (selected) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = SquadOrange,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ParticipationToggleRow(
    icon: ImageVector,
    label: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = if (checked) SquadOrangeLight else SquadGrayLight,
                    shape = RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) SquadOrange else SquadTextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadTextPrimary
            )

            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = SquadTextSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SquadOrange
            )
        )
    }
}

@Composable
private fun ParticipationBadge(
    icon: ImageVector,
    label: String
) {
    Surface(
        color = SquadOrangeLight,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SquadOrange,
                modifier = Modifier.size(14.dp)
            )

            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadOrange
            )
        }
    }
}

@Composable
private fun StepHeaderImage(
    heading: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    imageRes: Int? = null,
    colors: List<Color> = listOf(Color(0xFFD4611A), Color(0xFFFF8C42))
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                brush = Brush.linearGradient(colors),
                shape = RoundedCornerShape(12.dp)
            )
            .clip(RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.BottomStart
    ) {
        if (imageRes != null) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
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
                                Color.Black.copy(alpha = 0.62f)
                            )
                        )
                    )
            )
        }

        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .background(
                            color = Color.White.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))
            }

            Text(
                text = heading,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SectionLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = SquadTextSecondary,
        letterSpacing = 0.6.sp,
        modifier = modifier
    )
}

@Composable
private fun PrivacyButton(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        color = if (selected) SquadOrangeLight else Color.White,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            width = if (selected) 1.5.dp else 1.dp,
            color = if (selected) SquadOrange else SquadGrayLight
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) SquadOrange else SquadTextSecondary,
                modifier = Modifier.size(16.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) SquadOrange else SquadTextPrimary
            )
        }
    }
}

@Composable
private fun SportChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (selected) SquadOrange else Color.White,
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) SquadOrange else SquadGray
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color.White else SquadTextSecondary,
                modifier = Modifier.size(14.dp)
            )

            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) Color.White else SquadTextPrimary
            )
        }
    }
}

@Composable
private fun ReviewCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color(0xFFF8F8F8),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, SquadGrayLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            content = content
        )
    }
}

@Composable
private fun LocationPreviewCard(
    latitude: Double?,
    longitude: Double?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
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
                    color = Color.White
                )

                Text(
                    text = "Toca para alterar",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0xFF2D5A27), Color(0xFF4A7C59))
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Text(
                    text = stringResource(R.string.createEvent_step3_heading),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
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

