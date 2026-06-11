package com.example.squadup.features.events.editevent

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.AppNavBar
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.AppLanguage

@Composable
fun EditEventScreen(
    uiState: EditEventUiState,
    selectedRoute: String,
    onNavItemClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onStartDateChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndDateChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onEntryFeeChange: (String) -> Unit,
    onMaxTeamsChange: (String) -> Unit,
    onParticipationLimitChange: (String) -> Unit,
    onIsPublicChange: (Boolean) -> Unit,
    onSave: () -> Unit,
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = "Editar Evento",
                showBackButton = true,
                onBackClick = onBackClick,
                showSettingsButton = true,
                selectedLanguage = selectedLanguage,
                isDarkMode = isDarkMode,
                onLanguageChange = onLanguageChange,
                onDarkModeChange = onDarkModeChange
            )
        },
        bottomBar = { AppNavBar(selectedRoute = selectedRoute, onItemClick = onNavItemClick) }
    ) { innerPadding ->
        when {
            uiState.isLoading -> Box(
                Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator(color = SquadOrange) }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Title
                FieldLabel("Nome do Evento")
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = onTitleChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors()
                )
                Spacer(Modifier.height(14.dp))

                // Description
                FieldLabel("Descrição")
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChange,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp),
                    maxLines = 5,
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors()
                )
                Spacer(Modifier.height(14.dp))

                // Address
                FieldLabel("Local")
                OutlinedTextField(
                    value = uiState.address,
                    onValueChange = onAddressChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = SquadGray, modifier = Modifier.size(18.dp)) },
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors()
                )
                Spacer(Modifier.height(14.dp))

                // Start date + time
                FieldLabel("Data/Hora de Início")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.startDate,
                        onValueChange = onStartDateChange,
                        placeholder = { Text("AAAA-MM-DD", fontSize = 13.sp, color = SquadGray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )
                    OutlinedTextField(
                        value = uiState.startTime,
                        onValueChange = onStartTimeChange,
                        placeholder = { Text("HH:MM", fontSize = 13.sp, color = SquadGray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )
                }
                Spacer(Modifier.height(14.dp))

                // End date + time
                FieldLabel("Data/Hora de Fim")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.endDate,
                        onValueChange = onEndDateChange,
                        placeholder = { Text("AAAA-MM-DD", fontSize = 13.sp, color = SquadGray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )
                    OutlinedTextField(
                        value = uiState.endTime,
                        onValueChange = onEndTimeChange,
                        placeholder = { Text("HH:MM", fontSize = 13.sp, color = SquadGray) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = fieldColors()
                    )
                }
                Spacer(Modifier.height(14.dp))

                // Entry fee
                FieldLabel("Taxa de Entrada (€)")
                OutlinedTextField(
                    value = uiState.entryFee,
                    onValueChange = onEntryFeeChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Outlined.Euro, null, tint = SquadGray, modifier = Modifier.size(18.dp)) },
                    shape = RoundedCornerShape(10.dp),
                    colors = fieldColors()
                )
                Spacer(Modifier.height(14.dp))

                // Max teams / participation limit
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(Modifier.weight(1f)) {
                        FieldLabel("Máx. Equipas")
                        OutlinedTextField(
                            value = uiState.maxTeams,
                            onValueChange = onMaxTeamsChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = fieldColors()
                        )
                    }
                    Column(Modifier.weight(1f)) {
                        FieldLabel("Limite de Inscrições")
                        OutlinedTextField(
                            value = uiState.participationLimit,
                            onValueChange = onParticipationLimitChange,
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = fieldColors()
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))

                // Public / Private toggle
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (uiState.isPublic) Icons.Outlined.Public else Icons.Outlined.Lock,
                            null, tint = SquadOrange, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text("Evento Público", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                if (uiState.isPublic) "Visível para todos" else "Apenas por convite",
                                fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = uiState.isPublic,
                            onCheckedChange = onIsPublicChange,
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SquadOrange)
                        )
                    }
                }

                // Error
                uiState.errorMessage?.let { err ->
                    Spacer(Modifier.height(12.dp))
                    Text(err, fontSize = 13.sp, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onSave,
                    enabled = uiState.canSave,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SquadOrange, contentColor = Color.White)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Text("Guardar Alterações", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = SquadOrange,
    unfocusedBorderColor = Color(0xFFE0E0E0),
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface
)