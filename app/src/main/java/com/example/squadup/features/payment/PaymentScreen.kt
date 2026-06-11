package com.example.squadup.features.payment

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.components.AppHeader
import com.example.squadup.core.ui.components.responsiveHorizontalPadding
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange

@Composable
fun PaymentScreen(
    uiState: PaymentUiState,
    onBackClick: () -> Unit,
    onPaymentMethodChange: (PaymentMethod) -> Unit,
    onConfirmPayment: () -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                showLogo = false,
                title = "Pagamento",
                showBackButton = true,
                onBackClick = onBackClick,
                showNotificationsButton = false,
                showSettingsButton = false
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SquadOrange)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = responsiveHorizontalPadding(18.dp))
                .padding(top = 18.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EventSummaryCard(uiState)
            FeeBreakdownCard(uiState)
            PaymentMethodCard(
                selected = uiState.selectedPaymentMethod,
                onSelect = onPaymentMethodChange
            )

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage,
                    color = Color(0xFFE53935),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = responsiveHorizontalPadding(4.dp))
                )
            }

            Button(
                onClick = onConfirmPayment,
                enabled = !uiState.isConfirming,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                if (uiState.isConfirming) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text(
                        text = "Confirmar Pagamento",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EventSummaryCard(uiState: PaymentUiState) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SquadGrayLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Resumo do evento",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.eventTitle,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (uiState.eventDate.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                SummaryRow(icon = Icons.Outlined.CalendarMonth, text = uiState.eventDate)
            }
            if (uiState.eventVenue.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                SummaryRow(icon = Icons.Outlined.LocationOn, text = uiState.eventVenue, maxLines = 2)
            }
        }
    }
}

@Composable
private fun SummaryRow(icon: ImageVector, text: String, maxLines: Int = Int.MAX_VALUE) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = text,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FeeBreakdownCard(uiState: PaymentUiState) {
    val sym = uiState.currencySymbol
    fun Double.fmt() = if (this % 1.0 == 0.0) "$sym${toInt()}" else "$sym${"%.2f".format(this)}"

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SquadGrayLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Valores",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))
            FeeRow(label = "Total", value = uiState.total.fmt(), bold = true)
        }
    }
}

@Composable
private fun FeeRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = if (bold) SquadOrange else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PaymentMethodCard(
    selected: PaymentMethod,
    onSelect: (PaymentMethod) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, SquadGrayLight)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Método de pagamento",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            PaymentMethodOption(
                method = PaymentMethod.CARD,
                label = "Cartão de Crédito",
                icon = Icons.Outlined.CreditCard,
                selected = selected == PaymentMethod.CARD,
                onSelect = onSelect
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 6.dp),
                color = SquadGrayLight
            )
            PaymentMethodOption(
                method = PaymentMethod.MBWAY,
                label = "MB Way",
                icon = Icons.Outlined.PhoneAndroid,
                selected = selected == PaymentMethod.MBWAY,
                onSelect = onSelect
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 6.dp),
                color = SquadGrayLight
            )
            PaymentMethodOption(
                method = PaymentMethod.GOOGLE_PAY,
                label = "Google Pay",
                icon = Icons.Outlined.Smartphone,
                selected = selected == PaymentMethod.GOOGLE_PAY,
                onSelect = onSelect
            )
        }
    }
}

@Composable
private fun PaymentMethodOption(
    method: PaymentMethod,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onSelect: (PaymentMethod) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(method) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = { onSelect(method) },
            colors = RadioButtonDefaults.colors(selectedColor = SquadOrange)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) SquadOrange else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
