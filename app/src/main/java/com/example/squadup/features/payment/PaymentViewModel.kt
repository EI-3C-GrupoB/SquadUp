package com.example.squadup.features.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PaymentViewModel : ViewModel() {

    private val repository = PaymentRepository()

    private val _uiState = MutableStateFlow(PaymentUiState(isLoading = true))
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    private var onPaymentConfirmed: ((Int) -> Unit)? = null

    fun load(inscricaoId: Int, eventId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            repository.loadPaymentDetails(inscricaoId, eventId)
                .onSuccess { state -> _uiState.value = state }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Não foi possível carregar o pagamento."
                    )
                }
        }
    }

    fun onPaymentMethodChange(method: PaymentMethod) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun confirmPayment(onSuccess: (ticketId: Int) -> Unit) {
        val state = _uiState.value
        val eventId = state.eventId

        if (eventId == null) {
            _uiState.value = state.copy(errorMessage = "Dados de pagamento inválidos.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isConfirming = true, errorMessage = null)
            repository.confirmPayment(
                paymentId = state.paymentId,
                eventId = eventId,
                inscricaoId = state.inscricaoId
            )
                .onSuccess { ticketId ->
                    _uiState.value = _uiState.value.copy(isConfirming = false)
                    onSuccess(ticketId)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        isConfirming = false,
                        errorMessage = e.message ?: "Não foi possível confirmar o pagamento."
                    )
                }
        }
    }
}
