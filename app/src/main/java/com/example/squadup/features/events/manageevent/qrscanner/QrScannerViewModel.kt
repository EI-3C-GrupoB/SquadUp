package com.example.squadup.features.events.manageevent.qrscanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QrScannerViewModel : ViewModel() {
    private val repository = QrScannerRepository()

    private val _uiState = MutableStateFlow(QrScannerUiState())
    val uiState: StateFlow<QrScannerUiState> = _uiState.asStateFlow()

    fun onQrDetected(code: String, eventId: Int) {
        val state = _uiState.value
        if (state.isVerifying || code == state.lastScannedCode) return
        _uiState.value = state.copy(isVerifying = true, lastScannedCode = code)
        viewModelScope.launch {
            val result = repository.verifyTicket(code, eventId)
            _uiState.value = _uiState.value.copy(isVerifying = false, result = result)
        }
    }

    fun reset() {
        _uiState.value = QrScannerUiState()
    }
}
