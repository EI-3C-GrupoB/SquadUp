package com.example.squadup.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.ui.theme.SquadGrayLight
import com.example.squadup.ui.theme.SquadOrange
import com.example.squadup.ui.theme.SquadTextPrimary
import com.example.squadup.ui.theme.SquadTextSecondary
import com.example.squadup.ui.theme.SquadWhite

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    labelActionText: String? = null,
    onLabelActionClick: (() -> Unit)? = null,
    isPassword: Boolean = false,
    singleLine: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = SquadTextPrimary,
                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            if (labelActionText != null && onLabelActionClick != null) {
                TextButton(
                    onClick = onLabelActionClick,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = labelActionText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SquadOrange
                    )
                }
            }
        }

        @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            visualTransformation = if (isPassword && !passwordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            decorationBox = { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    visualTransformation = if (isPassword && !passwordVisible) {
                        PasswordVisualTransformation()
                    } else {
                        VisualTransformation.None
                    },
                    innerTextField = innerTextField,
                    placeholder = {
                        Text(
                            text = placeholder,
                            color = SquadTextSecondary,
                            fontSize = 15.sp
                        )
                    },
                    leadingIcon = if (leadingIcon != null) {
                        {
                            Icon(
                                imageVector = leadingIcon,
                                contentDescription = null,
                                tint = SquadTextSecondary
                            )
                        }
                    } else null,
                    trailingIcon = if (isPassword) {
                        {
                            IconButton(
                                onClick = {
                                    passwordVisible = !passwordVisible
                                }
                            ) {
                                Icon(
                                    imageVector = if (passwordVisible) {
                                        Icons.Outlined.VisibilityOff
                                    } else {
                                        Icons.Outlined.Visibility
                                    },
                                    contentDescription = if (passwordVisible) {
                                        "Esconder palavra-passe"
                                    } else {
                                        "Mostrar palavra-passe"
                                    },
                                    tint = SquadTextSecondary
                                )
                            }
                        }
                    } else if (trailingIcon != null) {
                        {
                            Icon(
                                imageVector = trailingIcon,
                                contentDescription = null,
                                tint = SquadTextSecondary
                            )
                        }
                    } else null,
                    singleLine = singleLine,
                    enabled = true,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = true,
                            isError = false,
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SquadOrange,
                                unfocusedBorderColor = SquadGrayLight,
                                cursorColor = SquadOrange,
                                focusedTextColor = SquadTextPrimary,
                                unfocusedTextColor = SquadTextPrimary,
                                focusedContainerColor = SquadWhite,
                                unfocusedContainerColor = SquadWhite
                            ),
                            shape = RoundedCornerShape(14.dp),
                        )
                    }
                )
            }
        )
    }
}