package com.example.squadup.features.payment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PaymentRoute(
    inscricaoId: String,
    eventId: String,
    onBackClick: () -> Unit,
    onPaymentSuccess: (ticketId: String) -> Unit,
    viewModel: PaymentViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val parsedInscricao = inscricaoId.toIntOrNull()
    val parsedEvent = eventId.toIntOrNull()

    LaunchedEffect(inscricaoId, eventId) {
        if (parsedInscricao != null && parsedEvent != null) {
            viewModel.load(inscricaoId = parsedInscricao, eventId = parsedEvent)
        }
    }

    PaymentScreen(
        uiState = uiState,
        onBackClick = onBackClick,
        onPaymentMethodChange = viewModel::onPaymentMethodChange,
        onConfirmPayment = {
            viewModel.confirmPayment { ticketId ->
                onPaymentSuccess(ticketId.toString())
            }
        }
    )
}
