package com.example.squadup.features.events.createevent

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import com.example.squadup.R
import com.example.squadup.core.enums.SportType
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.PrimaryButton
import com.example.squadup.core.ui.components.ProfileDropdownField
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.utils.toDisplayName
import com.example.squadup.core.utils.toIcon
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

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
    formatOptions: List<String>,
    // Step 2
    onFormatChange: (String) -> Unit,
    onMaxTeamsChange: (Int) -> Unit,
    onGeneralRulesChange: (String) -> Unit,
    onPublicEventToggle: (Boolean) -> Unit,
    onEntryFeeChange: (String) -> Unit,
    // Step 3
    onVenueChange: (String) -> Unit,
    onEventDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
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
        CreateEventStep.BASIC_INFO     -> R.string.createEvent_step1_title
        CreateEventStep.FORMAT_PLAYERS -> R.string.createEvent_step2_title
        CreateEventStep.LOCATION_TIME  -> R.string.createEvent_step3_title
        CreateEventStep.REVIEW         -> R.string.createEvent_review_title
    }

    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = stringResource(titleRes),
                showBackButton = true,
                onBackClick = if (uiState.currentStep == CreateEventStep.BASIC_INFO) onBackClick else onPreviousStep,
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
                // Progress bar
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
                        onFormatChange = onFormatChange,
                        onMaxTeamsChange = onMaxTeamsChange,
                        onGeneralRulesChange = onGeneralRulesChange,
                        onPublicEventToggle = onPublicEventToggle,
                        onEntryFeeChange = onEntryFeeChange,
                        onNextStep = onNextStep
                    )
                    CreateEventStep.LOCATION_TIME -> LocationTimeStep(
                        uiState = uiState,
                        onVenueChange = onVenueChange,
                        onEventDateChange = onEventDateChange,
                        onStartTimeChange = onStartTimeChange,
                        onEndTimeChange = onEndTimeChange,
                        onRecurringToggle = onRecurringToggle,
                        onNextStep = onNextStep
                    )
                    CreateEventStep.REVIEW -> ReviewStep(
                        uiState = uiState,
                        onEditSteps = { onGoToStep(CreateEventStep.BASIC_INFO) },
                        onTeamNotifyToggle = onTeamNotifyToggle,
                        onCreateEvent = onCreateEvent
                    )
                }
            }

            // Recurrence dialog
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

// ─── Progress Bar ─────────────────────────────────────────────────────────────

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

// ─── Step 1: Basic Info ───────────────────────────────────────────────────────

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
        // Header image
        StepHeaderImage(
            heading = stringResource(R.string.createEvent_step1_heading),
            subtitle = stringResource(R.string.createEvent_step1_subtitle),
            imageRes = R.drawable.create_event_1
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Event Name
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
                Icon(Icons.Outlined.Edit, null, tint = SquadTextSecondary, modifier = Modifier.size(18.dp))
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

        // Privacy
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

        // Sport — single select
        SectionLabel(stringResource(R.string.createEvent_sports_label))
        Spacer(modifier = Modifier.height(10.dp))

        @OptIn(ExperimentalLayoutApi::class)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SportType.entries.forEach { sport ->
                val selected = uiState.selectedSport == sport
                SportChip(
                    label = sport.toDisplayName(context),
                    icon = sport.toIcon(),
                    selected = selected,
                    onClick = { onSportSelect(sport) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton(text = stringResource(R.string.createEvent_next_step), onClick = onNextStep)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─── Step 2: Format & Players ─────────────────────────────────────────────────

@Composable
private fun FormatPlayersStep(
    uiState: CreateEventUiState,
    formatOptions: List<String>,
    onFormatChange: (String) -> Unit,
    onMaxTeamsChange: (Int) -> Unit,
    onGeneralRulesChange: (String) -> Unit,
    onPublicEventToggle: (Boolean) -> Unit,
    onEntryFeeChange: (String) -> Unit,
    onNextStep: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        StepHeaderImage(
            heading = stringResource(R.string.createEvent_step2_heading),
            colors = listOf(Color(0xFF1A1A2E), Color(0xFF16213E))
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Format dropdown
        ProfileDropdownField(
            label = stringResource(R.string.createEvent_format_label),
            selectedValue = uiState.format,
            options = formatOptions,
            onValueSelected = onFormatChange
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Max Teams
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
                Icon(Icons.Default.Remove, null, tint = SquadTextPrimary, modifier = Modifier.size(20.dp))
            }
            Slider(
                value = uiState.maxTeams.toFloat(),
                onValueChange = { onMaxTeamsChange(it.toInt() - uiState.maxTeams) },
                valueRange = 2f..64f,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
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
            text = stringResource(R.string.createEvent_max_teams_hint, uiState.maxTeams * 11, uiState.maxTeams * 5),
            fontSize = 12.sp,
            color = SquadTextSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        // General Rules
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
                    modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                    textStyle = TextStyle(fontSize = 14.sp, color = SquadTextPrimary),
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
                    text = stringResource(R.string.createEvent_rules_counter, uiState.generalRules.length),
                    fontSize = 11.sp,
                    color = SquadTextSecondary,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Entry & Privacy
        SectionLabel(stringResource(R.string.createEvent_entry_privacy_label))
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Public, null, tint = SquadOrange, modifier = Modifier.size(20.dp))
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
            placeholder = { Text("$ 0.00", color = SquadTextSecondary) },
            prefix = { Text("$ ", color = SquadTextPrimary, fontWeight = FontWeight.SemiBold) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SquadOrange,
                unfocusedBorderColor = SquadGrayLight
            )
        )

        Spacer(modifier = Modifier.height(32.dp))
        PrimaryButton(text = stringResource(R.string.createEvent_next_step), onClick = onNextStep)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─── Step 3: Location & Time ──────────────────────────────────────────────────

@Composable
private fun LocationTimeStep(
    uiState: CreateEventUiState,
    onVenueChange: (String) -> Unit,
    onEventDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onRecurringToggle: (Boolean) -> Unit,
    onNextStep: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        StepHeaderImage(
            heading = stringResource(R.string.createEvent_step3_heading),
            colors = listOf(Color(0xFF2D5A27), Color(0xFF4A7C59))
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Select on Map
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SquadGray),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SquadTextPrimary)
        ) {
            Icon(Icons.Outlined.Map, null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.createEvent_select_on_map),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Schedule
        Text(
            text = stringResource(R.string.createEvent_schedule_label),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = SquadTextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Event Date
        SectionLabel(stringResource(R.string.createEvent_event_date_label))
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = uiState.eventDate,
            onValueChange = onEventDateChange,
            placeholder = { Text("DD / MM / YYYY", color = SquadTextSecondary) },
            leadingIcon = { Icon(Icons.Outlined.CalendarMonth, null, tint = SquadTextSecondary, modifier = Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = SquadOrange,
                unfocusedBorderColor = SquadGrayLight
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Start & End Time
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel(stringResource(R.string.createEvent_start_time_label))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.startTime,
                    onValueChange = onStartTimeChange,
                    placeholder = { Text("09:00 AM", color = SquadTextSecondary, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Schedule, null, tint = SquadTextSecondary, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SquadOrange,
                        unfocusedBorderColor = SquadGrayLight
                    )
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel(stringResource(R.string.createEvent_end_time_label))
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = uiState.endTime,
                    onValueChange = onEndTimeChange,
                    placeholder = { Text("11:30 AM", color = SquadTextSecondary, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Outlined.Schedule, null, tint = SquadTextSecondary, modifier = Modifier.size(16.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SquadOrange,
                        unfocusedBorderColor = SquadGrayLight
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Recurring
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
                Icon(Icons.Outlined.Autorenew, null, tint = SquadOrange, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.createEvent_recurring_label), fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SquadTextPrimary)
                Text(stringResource(R.string.createEvent_recurring_subtitle), fontSize = 12.sp, color = SquadTextSecondary)
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
        PrimaryButton(text = stringResource(R.string.createEvent_review_event), onClick = onNextStep)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─── Review Step ─────────────────────────────────────────────────────────────

@Composable
private fun ReviewStep(
    uiState: CreateEventUiState,
    onEditSteps: () -> Unit,
    onTeamNotifyToggle: (String) -> Unit,
    onCreateEvent: () -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(16.dp))

        // Cover image area
        Text(stringResource(R.string.createEvent_review_cover_label), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.linearGradient(listOf(Color(0xFF2F9D73), Color(0xFF1A6B3C))),
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.AddPhotoAlternate, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(stringResource(R.string.createEvent_review_add_cover), fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Medium)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Summary Review header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.createEvent_review_summary_label), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary, modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.createEvent_review_edit_steps),
                fontSize = 13.sp,
                color = SquadOrange,
                modifier = Modifier.clickable(onClick = onEditSteps)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Branding card
        ReviewCard {
            Text(stringResource(R.string.createEvent_review_branding_label), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, letterSpacing = 0.8.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (uiState.eventName.isNotBlank()) uiState.eventName else "—",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            uiState.selectedSport?.let { sport ->
                Surface(color = sport.color.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
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

        // Date & Venue
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ReviewCard(modifier = Modifier.weight(1f)) {
                Icon(Icons.Outlined.CalendarMonth, null, tint = SquadOrange, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text(stringResource(R.string.createEvent_review_date_label), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(if (uiState.eventDate.isNotBlank()) uiState.eventDate else "—", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary)
                if (uiState.startTime.isNotBlank()) {
                    Text("${uiState.startTime} EST", fontSize = 12.sp, color = SquadTextSecondary)
                }
            }
            ReviewCard(modifier = Modifier.weight(1f)) {
                Icon(Icons.Outlined.LocationOn, null, tint = SquadOrange, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text(stringResource(R.string.createEvent_review_venue_label), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(if (uiState.venue.isNotBlank()) uiState.venue else "—", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Format & Capacity
        ReviewCard {
            Text(stringResource(R.string.createEvent_review_format_label), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, letterSpacing = 0.8.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("${uiState.format} • ${uiState.maxTeams} Max Teams", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = SquadTextPrimary)
        }

        // Notify Teams — só para PLAYER_ORGANIZER
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
                                colors = CheckboxDefaults.colors(checkedColor = SquadOrange)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(team.name, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = SquadTextPrimary)
                                Text("${team.nMembers} members", fontSize = 12.sp, color = SquadTextSecondary)
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
        PrimaryButton(text = stringResource(R.string.createEvent_create_button), onClick = onCreateEvent)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─── Recurrence Dialog ────────────────────────────────────────────────────────

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
                Text(stringResource(R.string.createEvent_recurrence_title), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = SquadTextPrimary)
                Spacer(modifier = Modifier.height(20.dp))

                listOf(
                    RecurrenceType.DAILY to stringResource(R.string.createEvent_recurrence_daily),
                    RecurrenceType.WEEKLY to stringResource(R.string.createEvent_recurrence_weekly),
                    RecurrenceType.MONTHLY to stringResource(R.string.createEvent_recurrence_monthly)
                ).forEach { (type, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onRecurrenceTypeChange(type) }.padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 15.sp,
                            fontWeight = if (uiState.recurrenceType == type) FontWeight.Bold else FontWeight.Normal,
                            color = if (uiState.recurrenceType == type) SquadOrange else SquadTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        RadioButton(
                            selected = uiState.recurrenceType == type,
                            onClick = { onRecurrenceTypeChange(type) },
                            colors = RadioButtonDefaults.colors(selectedColor = SquadOrange)
                        )
                    }

                    // Day selector for weekly
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
                                            if (selected) SquadOrange else SquadGrayLight,
                                            CircleShape
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
                PrimaryButton(text = stringResource(R.string.createEvent_recurrence_save), onClick = onSave)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.createEvent_recurrence_cancel),
                    fontSize = 14.sp,
                    color = SquadTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onCancel).padding(vertical = 6.dp)
                )
            }
        }
    }
}

// ─── Private helpers ─────────────────────────────────────────────────────────

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
                            Color.White.copy(alpha = 0.18f),
                            RoundedCornerShape(999.dp)
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
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SquadTextSecondary, letterSpacing = 0.6.sp, modifier = modifier)
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
            Icon(icon, null, tint = if (selected) SquadOrange else SquadTextSecondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, fontSize = 14.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, color = if (selected) SquadOrange else SquadTextPrimary)
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
        border = BorderStroke(1.dp, if (selected) SquadOrange else SquadGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, null, tint = if (selected) Color.White else SquadTextSecondary, modifier = Modifier.size(14.dp))
            Text(label, fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal, color = if (selected) Color.White else SquadTextPrimary)
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
        Column(modifier = Modifier.padding(14.dp), content = content)
    }
}

