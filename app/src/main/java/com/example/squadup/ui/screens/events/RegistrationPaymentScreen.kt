package com.example.squadup.ui.screens.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.QrCode2
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadBackground
import com.example.squadup.ui.theme.SquadGray
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadOrangeDark
import com.example.squadup.ui.theme.SquadOrangeLight
import com.example.squadup.ui.theme.SquadSurface
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadWhite

enum class PaymentMethod {
    CreditCard,
    Pix,
    ApplePay
}

@Composable
fun RegistrationPaymentScreen(
    selectedPaymentMethod: PaymentMethod,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onBackClick: () -> Unit,
    onChangePaymentClick: () -> Unit,
    onPayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            RegistrationPaymentHeader(
                title = "Registration Payment",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Summary",
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            PaymentSummaryCard()

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Payment Method",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "Change",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrangeDark,
                    modifier = Modifier.clickable(onClick = onChangePaymentClick)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            PaymentMethodCard(
                title = "Credit Card",
                subtitle = "**** **** **** 4242",
                icon = Icons.Outlined.CreditCard,
                iconBackground = Color(0xFFFFE1D3),
                selected = selectedPaymentMethod == PaymentMethod.CreditCard,
                onClick = {
                    onPaymentMethodSelected(PaymentMethod.CreditCard)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            PaymentMethodCard(
                title = "Pix",
                subtitle = "Instant transfer",
                icon = Icons.Outlined.QrCode2,
                iconBackground = Color(0xFFF3F0EF),
                selected = selectedPaymentMethod == PaymentMethod.Pix,
                onClick = {
                    onPaymentMethodSelected(PaymentMethod.Pix)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            PaymentMethodCard(
                title = "Apple Pay",
                subtitle = "Pay with Touch ID",
                icon = Icons.Outlined.Wallet,
                iconBackground = Color.Black,
                selected = selectedPaymentMethod == PaymentMethod.ApplePay,
                onClick = {
                    onPaymentMethodSelected(PaymentMethod.ApplePay)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                color = SquadGrayLight
            )

            Spacer(modifier = Modifier.height(14.dp))

            PaymentTotalRow(
                label = "Subtotal",
                value = "$25.00"
            )

            Spacer(modifier = Modifier.height(8.dp))

            PaymentTotalRow(
                label = "Fees",
                value = "FREE",
                valueColor = Color(0xFF10B981)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Total",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "$25.00",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = SquadOrange
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onPayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(9.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = SquadWhite
                )
            ) {
                Text(
                    text = "Pay $25.00",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(6.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RegistrationPaymentHeader(
    title: String,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(SquadGrayLight, RoundedCornerShape(2.dp))
                    .clickable(onClick = onBackClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Voltar",
                    tint = SquadTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrangeDark
            )
        }
    }
}

@Composable
private fun PaymentSummaryCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFFFFF3F0),
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(1.dp, SquadOrangeLight)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Sand Pro Tour",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadOrangeDark
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Beach Open 2024",
                        fontSize = 10.sp,
                        color = SquadTextSecondary
                    )
                }

                Text(
                    text = "CONFIRMED",
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Black,
                    color = SquadWhite,
                    modifier = Modifier
                        .background(
                            color = Color(0xFF00A884),
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = SquadOrangeLight.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(9.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Individual Registration",
                    fontSize = 11.sp,
                    color = SquadTextSecondary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "$25.00",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBackground: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = SquadSurface,
        shape = RoundedCornerShape(9.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) SquadOrange else SquadOrangeLight
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        color = iconBackground,
                        shape = RoundedCornerShape(7.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (iconBackground == Color.Black) SquadWhite else SquadOrangeDark,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadTextPrimary
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = subtitle,
                    fontSize = 9.sp,
                    color = SquadTextSecondary
                )
            }

            RadioButton(
                selected = selected,
                onClick = onClick,
                modifier = Modifier.size(28.dp),
                colors = RadioButtonDefaults.colors(
                    selectedColor = SquadOrange,
                    unselectedColor = SquadGray
                )
            )
        }
    }
}

@Composable
private fun PaymentTotalRow(
    label: String,
    value: String,
    valueColor: Color = SquadTextPrimary
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = SquadTextSecondary,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=235dp,height=520dp,dpi=440"
)
@Composable
fun RegistrationPaymentScreenPreview() {
    var selectedPaymentMethod by remember {
        mutableStateOf(PaymentMethod.CreditCard)
    }

    RegistrationPaymentScreen(
        selectedPaymentMethod = selectedPaymentMethod,
        onPaymentMethodSelected = { selectedPaymentMethod = it },
        onBackClick = {},
        onChangePaymentClick = {},
        onPayClick = {}
    )
}