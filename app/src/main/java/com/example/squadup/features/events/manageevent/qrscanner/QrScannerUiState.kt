package com.example.squadup.features.events.manageevent.qrscanner

sealed class QrVerificationResult {
    object Scanning : QrVerificationResult()
    data class Valid(val playerName: String, val eventName: String, val ticketId: Int) : QrVerificationResult()
    data class WrongEvent(val eventName: String) : QrVerificationResult()
    data class Expired(val eventName: String) : QrVerificationResult()
    object NotFound : QrVerificationResult()
    data class Error(val message: String) : QrVerificationResult()
}

data class QrScannerUiState(
    val result: QrVerificationResult = QrVerificationResult.Scanning,
    val isVerifying: Boolean = false,
    val lastScannedCode: String = ""
)
