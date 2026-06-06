package com.example.squadup.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquadDateTimePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    mode: DateTimePickerMode,
    modifier: Modifier = Modifier,
    labelColor: Color = SquadTextPrimary,
    leadingIcon: ImageVector = when (mode) {
        DateTimePickerMode.DATE -> Icons.Outlined.CalendarMonth
        DateTimePickerMode.TIME -> Icons.Outlined.AccessTime
    },
    trailingIcon: ImageVector = when (mode) {
        DateTimePickerMode.DATE -> Icons.Outlined.CalendarMonth
        DateTimePickerMode.TIME -> Icons.Outlined.AccessTime
    }
) {
    var showPicker by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        BasicTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    showPicker = true
                },
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    innerTextField = innerTextField,
                    enabled = true,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    interactionSource = interactionSource,
                    placeholder = {
                        Text(
                            text = placeholder,
                            color = SquadTextSecondary,
                            fontSize = 15.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = leadingIcon,
                            contentDescription = null,
                            tint = SquadTextSecondary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPicker = true }) {
                            Icon(
                                imageVector = trailingIcon,
                                contentDescription = when (mode) {
                                    DateTimePickerMode.DATE -> "Abrir calendário"
                                    DateTimePickerMode.TIME -> "Abrir seletor de hora"
                                },
                                tint = SquadTextSecondary
                            )
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = false,
                            interactionSource = interactionSource,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SquadOrange,
                                unfocusedBorderColor = SquadGrayLight,
                                cursorColor = SquadOrange,
                                focusedTextColor = SquadTextPrimary,
                                unfocusedTextColor = SquadTextPrimary,
                                focusedContainerColor = SquadWhite,
                                unfocusedContainerColor = SquadWhite
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                )
            }
        )
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
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
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
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}