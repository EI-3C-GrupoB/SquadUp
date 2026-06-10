package com.example.squadup.features.payment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentEventRow(
    val id: Int,

    @SerialName("titulo")
    val title: String,

    @SerialName("morada")
    val address: String? = null,

    @SerialName("data_inicio")
    val startDate: String? = null,

    @SerialName("preco")
    val price: Double? = null,

    @SerialName("taxa_inscricao")
    val entryFee: Double? = null,

    @SerialName("moeda")
    val currency: String? = null
)

@Serializable
data class PaymentRow(
    val id: Int,

    @SerialName("estado_pagamento")
    val status: String? = null,

    @SerialName("valor")
    val amount: Double? = null
)

@Serializable
data class PaymentStatusUpdateRow(
    @SerialName("estado_pagamento")
    val status: String
)

@Serializable
data class PaymentTicketInsertRow(
    @SerialName("user_id")
    val userId: Int,

    @SerialName("evento_id")
    val eventId: Int,

    @SerialName("codigo_qr")
    val codigoQr: String,

    @SerialName("estado")
    val status: String = "ativo"
)

@Serializable
data class PaymentTicketCreatedRow(
    val id: Int
)

@Serializable
data class PaymentUserRow(
    val id: Int,

    @SerialName("auth_user_id")
    val authUserId: String? = null,

    @SerialName("nome")
    val name: String? = null
)

@Serializable
data class PaymentEventOrganizerRow(
    @SerialName("titulo")
    val title: String,

    @SerialName("criador_id")
    val creatorId: Int? = null
)

@Serializable
data class PaymentNotificationInsertRow(
    @SerialName("user_id")
    val userId: Int,

    @SerialName("titulo")
    val title: String,

    @SerialName("descricao")
    val description: String,

    @SerialName("tipo")
    val type: String,

    @SerialName("referencia_id")
    val referenceId: Int? = null,

    @SerialName("referencia_tipo")
    val referenceType: String? = null
)
