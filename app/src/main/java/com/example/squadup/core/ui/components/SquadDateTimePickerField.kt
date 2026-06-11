package com.example.squadup.core.ui.components

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadWhite
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

enum class DateTimePickerMode {
    DATE,
    TIME
}

@Composable
fun SquadDateTimePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    mode: DateTimePickerMode,
    modifier: Modifier = Modifier,
    labelColor: Color = MaterialTheme.colorScheme.onSurface,
    leadingIcon: ImageVector = when (mode) {
        DateTimePickerMode.DATE -> Icons.Outlined.CalendarMonth
        DateTimePickerMode.TIME -> Icons.Outlined.AccessTime
    }
) {
    var showPicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = labelColor,
            letterSpacing = 0.6.sp,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { showPicker = true }
                ),
            color = SquadWhite,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, SquadGrayLight)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = value.ifBlank { placeholder },
                    fontSize = 14.sp,
                    color = if (value.isBlank()) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    if (showPicker) {
        when (mode) {
            DateTimePickerMode.DATE -> {
                SquadDatePickerDialog(
                    initialValue = value,
                    onDismiss = { showPicker = false },
                    onDateSelected = { selectedDate ->
                        onValueChange(selectedDate)
                        showPicker = false
                    }
                )
            }

            DateTimePickerMode.TIME -> {
                SquadTimePickerDialog(
                    initialValue = value,
                    onDismiss = { showPicker = false },
                    onTimeSelected = { selectedTime ->
                        onValueChange(selectedTime)
                        showPicker = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SquadDatePickerDialog(
    initialValue: String,
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    val initialDate = remember(initialValue) {
        runCatching {
            LocalDate.parse(initialValue, formatter)
        }.getOrElse {
            LocalDate.now()
        }
    }

    val initialMillis = remember(initialDate) {
        initialDate
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis
                    if (selectedMillis != null) {
                        val selectedDate = Instant
                            .ofEpochMilli(selectedMillis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .format(formatter)

                        onDateSelected(selectedDate)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = SquadOrange
                )
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SquadTimePickerDialog(
    initialValue: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    val initialTime = remember(initialValue) {
        runCatching {
            LocalTime.parse(initialValue, formatter)
        }.getOrElse {
            LocalTime.now()
        }
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    val selectedTime = LocalTime
                        .of(timePickerState.hour, timePickerState.minute)
                        .format(formatter)

                    onTimeSelected(selectedTime)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancelar",
                    color = SquadOrange
                )
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}