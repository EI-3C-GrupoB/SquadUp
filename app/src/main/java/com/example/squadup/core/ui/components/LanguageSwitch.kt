package com.example.squadup.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.squadup.core.ui.theme.SquadOrange
import com.example.squadup.core.utils.AppLanguage

@Composable
fun LanguageSwitch(
    selectedLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    // Estado local para a animação
    var localLanguage by remember(selectedLanguage) { mutableStateOf(selectedLanguage) }
    val scope = rememberCoroutineScope()

    val switchWidth = 96.dp
    val switchHeight = 34.dp
    val innerPadding = 3.dp

    val contentWidth = switchWidth - innerPadding * 2
    val contentHeight = switchHeight - innerPadding * 2

    val optionWidth = contentWidth / 2
    val selectedHeight = contentHeight * 0.85f

    val horizontalInset = (contentHeight - selectedHeight) / 2
    val selectedWidth = optionWidth - horizontalInset * 2

    val outerShape = RoundedCornerShape(50)
    val selectedShape = RoundedCornerShape(44)

    val density = LocalDensity.current

    val selectedOffset by animateDpAsState(
        targetValue = if (localLanguage == AppLanguage.EN) {
            horizontalInset
        } else {
            optionWidth + horizontalInset
        },
        animationSpec = tween(durationMillis = 280),
        label = "LanguageSwitchOffset"
    )

    Box(
        modifier = modifier
            .width(switchWidth)
            .height(switchHeight)
            .clip(outerShape)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = outerShape
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = outerShape
            )
            .padding(innerPadding)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset {
                    IntOffset(
                        x = with(density) { selectedOffset.roundToPx() },
                        y = 0
                    )
                }
                .width(selectedWidth)
                .height(selectedHeight)
                .shadow(
                    elevation = 1.dp,
                    shape = selectedShape,
                    clip = false
                )
                .clip(selectedShape)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = selectedShape
                )
        )

        Row(
            modifier = Modifier
                .width(contentWidth)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LanguageTextButton(
                text = "EN",
                selected = localLanguage == AppLanguage.EN,
                onClick = {
                    if (localLanguage != AppLanguage.EN) {
                        localLanguage = AppLanguage.EN
                        scope.launch {
                            delay(250)
                            onLanguageChange(AppLanguage.EN)
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )

            LanguageTextButton(
                text = "PT",
                selected = localLanguage == AppLanguage.PT,
                onClick = {
                    if (localLanguage != AppLanguage.PT) {
                        localLanguage = AppLanguage.PT
                        scope.launch {
                            delay(250)
                            onLanguageChange(AppLanguage.PT)
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LanguageTextButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(44))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) {
                SquadOrange
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontSize = 14.sp,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
            letterSpacing = 0.5.sp
        )
    }
}
