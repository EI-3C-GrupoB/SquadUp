package com.example.squadup.core.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.squadup.core.ui.theme.SquadGrayLight
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.ui.theme.SquadTextPrimary
import com.example.squadup.core.ui.theme.SquadTextSecondary
import com.example.squadup.core.ui.theme.SquadWhite

@OptIn(ExperimentalMaterial3Api::class)
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
    singleLine: Boolean = true,
    enabled: Boolean = true,
    labelColor: Color = SquadTextPrimary
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = labelColor,
                modifier = Modifier.padding(start = 4.dp)
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

        Spacer(modifier = Modifier.height(6.dp))

        val interactionSource = remember { MutableInteractionSource() }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            enabled = enabled,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 15.sp,
                color = SquadTextPrimary,
                fontWeight = FontWeight.Medium
            ),
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
                        Text(text = placeholder, color = SquadTextSecondary, fontSize = 15.sp)
                    },
                    leadingIcon = leadingIcon?.let {
                        { Icon(imageVector = it, contentDescription = null, tint = SquadTextSecondary) }
                    },
                    trailingIcon = when {
                        isPassword -> {
                            {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = null,
                                        tint = SquadTextSecondary
                                    )
                                }
                            }
                        }
                        trailingIcon != null -> {
                            { Icon(imageVector = trailingIcon, contentDescription = null, tint = SquadTextSecondary) }
                        }
                        else -> null
                    },
                    singleLine = singleLine,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    container = {
                        OutlinedTextFieldDefaults.Container(
                            enabled = enabled,
                            isError = false,
                            interactionSource = interactionSource,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SquadOrange,
                                unfocusedBorderColor = SquadGrayLight,
                                cursorColor = SquadOrange,
                                focusedTextColor = SquadTextPrimary,
                                unfocusedTextColor = SquadTextPrimary,
                                focusedContainerColor = SquadWhite,
                                unfocusedContainerColor = SquadWhite,
                                disabledBorderColor = SquadGrayLight,
                                disabledTextColor = SquadTextPrimary,
                                disabledContainerColor = SquadWhite,
                                disabledPlaceholderColor = SquadTextSecondary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                )
            }
        )
    }
}
