package com.example.squadup.ui.screens.events

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.SportsBasketball
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

enum class TournamentPaymentMethod {
    CreditCard,
    ApplePay,
    GooglePay
}

@Composable
fun TournamentPaymentScreen(
    selectedPaymentMethod: TournamentPaymentMethod,
    onPaymentMethodSelected: (TournamentPaymentMethod) -> Unit,
    onBackClick: () -> Unit,
    onConfirmPayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TournamentPaymentHeader(
                title = "Registration Payment",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            TournamentPaymentBottomBar(
                total = "$45.00",
                onConfirmPayClick = onConfirmPayClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SquadBackground)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            TournamentPaymentSummaryCard()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Select Payment Method",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = SquadTextPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            TournamentPaymentMethodCard(
                title = "Credit Card",
                subtitle = "**** **** **** 4242",
                icon = Icons.Outlined.CreditCard,
                iconBackground = Color(0xFFFFF4EF),
                selected = selectedPaymentMethod == TournamentPaymentMethod.CreditCard,
                onClick = {
                    onPaymentMethodSelected(TournamentPaymentMethod.CreditCard)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            TournamentPaymentMethodCard(
                title = "Apple Pay",
                subtitle = "",
                customIconText = "Apple",
                iconBackground = Color.Black,
                selected = selectedPaymentMethod == TournamentPaymentMethod.ApplePay,
                onClick = {
                    onPaymentMethodSelected(TournamentPaymentMethod.ApplePay)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            TournamentPaymentMethodCard(
                title = "Google Pay",
                subtitle = "",
                customIconText = "GPay",
                iconBackground = Color.White,
                selected = selectedPaymentMethod == TournamentPaymentMethod.GooglePay,
                onClick = {
                    onPaymentMethodSelected(TournamentPaymentMethod.GooglePay)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                color = SquadOrangeLight,
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(18.dp))

            SecurePaymentRow()

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun TournamentPaymentHeader(
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
                .height(64.dp)
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Voltar",
                tint = SquadTextSecondary,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onBackClick)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SquadOrangeDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TournamentPaymentSummaryCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, SquadOrangeLight)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                TournamentImagePreview()

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "City 3x3 Showdown",
                        fontSize = 21.sp,
                        lineHeight = 25.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(Color(0xFF00897B), RoundedCornerShape(999.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "★",
                                fontSize = 8.sp,
                                color = SquadWhite
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        Text(
                            text = "Elite Tier Tournament",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = SquadTextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Team: Slam Dunkers",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = SquadOrangeDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            HorizontalDivider(
                color = SquadGrayLight
            )

            Spacer(modifier = Modifier.height(18.dp))

            PaymentAmountLine(
                label = "Registration Fee",
                value = "$45.00",
                labelSize = 15.sp,
                valueSize = 23.sp,
                valueColor = SquadTextPrimary,
                valueBold = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            PaymentAmountLine(
                label = "Processing Fee",
                value = "$0.00",
                labelSize = 15.sp,
                valueSize = 15.sp,
                labelColor = Color(0xFF00796B),
                valueColor = Color(0xFF00796B),
                valueBold = true
            )
        }
    }
}

@Composable
private fun TournamentImagePreview() {
    Box(
        modifier = Modifier
            .size(width = 82.dp, height = 82.dp)
            .background(
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF111827),
                        Color(0xFF334155),
                        Color(0xFFE0A044)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.SportsBasketball,
            contentDescription = null,
            tint = SquadWhite.copy(alpha = 0.9f),
            modifier = Modifier.size(42.dp)
        )
    }
}

@Composable
private fun PaymentAmountLine(
    label: String,
    value: String,
    labelSize: androidx.compose.ui.unit.TextUnit,
    valueSize: androidx.compose.ui.unit.TextUnit,
    labelColor: Color = SquadTextSecondary,
    valueColor: Color = SquadTextPrimary,
    valueBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = label,
            fontSize = labelSize,
            fontWeight = FontWeight.Medium,
            color = labelColor,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            fontSize = valueSize,
            fontWeight = if (valueBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
private fun TournamentPaymentMethodCard(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    customIconText: String? = null,
    iconBackground: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = SquadSurface,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) SquadOrangeDark else SquadOrangeLight
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PaymentMethodIcon(
                icon = icon,
                customText = customIconText,
                backgroundColor = iconBackground
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadTextPrimary
                )

                if (subtitle.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = SquadTextSecondary
                    )
                }
            }

            RadioButton(
                selected = selected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(
                    selectedColor = SquadOrangeDark,
                    unselectedColor = SquadOrangeLight
                )
            )
        }
    }
}

@Composable
private fun PaymentMethodIcon(
    icon: ImageVector?,
    customText: String?,
    backgroundColor: Color
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(5.dp)
            )
            .then(
                if (backgroundColor == Color.White) {
                    Modifier.background(Color.White, RoundedCornerShape(5.dp))
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = SquadOrangeDark,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = customText.orEmpty(),
                fontSize = if (customText == "Apple") 12.sp else 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (backgroundColor == Color.Black) SquadWhite else Color(0xFF3367D6)
            )
        }
    }
}

@Composable
private fun SecurePaymentRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.Shield,
            contentDescription = null,
            tint = Color(0xFF00796B),
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "SECURE 256-BIT ENCRYPTED TRANSACTION",
            fontSize = 12.sp,
            letterSpacing = 2.sp,
            color = SquadTextPrimary
        )
    }
}

@Composable
private fun TournamentPaymentBottomBar(
    total: String,
    onConfirmPayClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SquadSurface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Total Amount",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = SquadTextSecondary,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = total,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = SquadOrange
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onConfirmPayClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SquadOrange,
                    contentColor = SquadWhite
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Confirm & Pay",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    device = "spec:width=402dp,height=880dp,dpi=440"
)
@Composable
fun TournamentPaymentScreenPreview() {
    var selectedPaymentMethod by remember {
        mutableStateOf(TournamentPaymentMethod.CreditCard)
    }

    TournamentPaymentScreen(
        selectedPaymentMethod = selectedPaymentMethod,
        onPaymentMethodSelected = { selectedPaymentMethod = it },
        onBackClick = {},
        onConfirmPayClick = {}
    )
}