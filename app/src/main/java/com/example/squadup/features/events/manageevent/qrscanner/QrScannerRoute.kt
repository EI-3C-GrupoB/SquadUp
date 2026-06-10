package com.example.squadup.features.events.manageevent.qrscanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun QrScannerRoute(
    eventId: String,
    onBackClick: () -> Unit,
    viewModel: QrScannerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val parsedEventId = eventId.toIntOrNull() ?: 0

    QrScannerScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onQrDetected = { code -> viewModel.onQrDetected(code, parsedEventId) },
        onReset = viewModel::reset
    )
}
