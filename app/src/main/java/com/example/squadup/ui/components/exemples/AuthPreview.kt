package com.example.squadup.ui.components.examples

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.squadup.ui.components.AuthCard
import com.example.squadup.ui.components.AuthTextField
import com.example.squadup.ui.components.PrimaryButton
import com.example.squadup.ui.components.SectionDivider
import com.example.squadup.ui.components.SocialLoginButton
import androidx.compose.foundation.layout.Row
import com.example.squadup.R

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AuthCard {
        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            placeholder = "Pedro@example.com",
            trailingIcon = Icons.Outlined.Email
        )

        Spacer(modifier = Modifier.height(18.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            placeholder = "••••••••",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(28.dp))

        PrimaryButton(
            text = "SIGN IN",
            onClick = { }
        )

        Spacer(modifier = Modifier.height(28.dp))

        SectionDivider(text = "OR CONTINUE WITH")
    }
}