package com.example.squadup.features.payment

enum class PaymentMethod {
    CARD, MBWAY, GOOGLE_PAY
}

data class PaymentUiState(
    val isLoading: Boolean = false,
    val isConfirming: Boolean = false,
    val errorMessage: String? = null,

    val eventTitle: String = "",
    val eventDate: String = "",
    val eventVenue: String = "",

    val price: Double = 0.0,
    val currency: String = "EUR",

    val selectedPaymentMethod: PaymentMethod = PaymentMethod.CARD,

    val inscricaoId: Int? = null,
    val eventId: Int? = null,
    val paymentId: Int? = null
) {
    val total: Double get() = price

    val currencySymbol: String get() = when (currency.uppercase()) {
        "EUR" -> "€"
        "USD" -> "$"
        "GBP" -> "£"
        else -> "${currency.uppercase()} "
    }
}
