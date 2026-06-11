package com.example.squadup.core.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.squadup.R
import com.example.squadup.core.utils.AppLanguage
import com.example.squadup.core.ui.theme.*
import com.example.squadup.core.utils.clickableNoRipple

@Composable
fun SettingsDialog(
    selectedLanguage: AppLanguage,
    isDarkMode: Boolean,
    onLanguageChange: (AppLanguage) -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    isAdmin: Boolean = false,
    isAdminView: Boolean = false,
    onAdminViewChange: (Boolean) -> Unit = {},
    onAdminPageClick: () -> Unit = {}
) {
    var showContent by rememberSaveable { mutableStateOf(false) }
    val isLandscape = rememberIsLandscape()
    val headerHeight = if (isLandscape) 48.dp else 56.dp
    val dialogTopPadding = if (isLandscape) 8.dp else 16.dp

    LaunchedEffect(Unit) {
        showContent = true
    }

    Popup(
        alignment = Alignment.TopCenter,
        onDismissRequest = onDismiss,
        properties = PopupProperties(
            focusable = true,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            // Scrim — começa abaixo da status bar + header
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(top = headerHeight)
                    .background(Color.Black.copy(alpha = 0.45f))
                    .clickableNoRipple(onClick = onDismiss)
            )

            AnimatedVisibility(
                visible = showContent,
                enter = expandVertically(
                    animationSpec = tween(400),
                    expandFrom = Alignment.Top
                ) + fadeIn(animationSpec = tween(400)),
                exit = shrinkVertically(
                    animationSpec = tween(300),
                    shrinkTowards = Alignment.Top
                ) + fadeOut(animationSpec = tween(300)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = headerHeight + dialogTopPadding)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .then(if (isLandscape) Modifier.widthIn(max = 560.dp) else Modifier)
                        .clickableNoRipple { },
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 12.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .padding(if (isLandscape) 18.dp else 24.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.settings_title),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.5.sp
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        SettingsRow(
                            icon = Icons.Outlined.Language,
                            label = stringResource(R.string.settings_language)
                        ) {
                            SettingsToggle(
                                options = listOf("EN", "PT"),
                                selectedIndex = if (selectedLanguage == AppLanguage.EN) 0 else 1,
                                onOptionSelected = { index ->
                                    onLanguageChange(
                                        if (index == 0) AppLanguage.EN else AppLanguage.PT
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        SettingsRow(
                            icon = Icons.Outlined.DarkMode,
                            label = stringResource(R.string.settings_dark_mode)
                        ) {
                            SettingsToggle(
                                options = listOf(
                                    stringResource(R.string.settings_off),
                                    stringResource(R.string.settings_on)
                                ),
                                selectedIndex = if (isDarkMode) 1 else 0,
                                onOptionSelected = { index ->
                                    onDarkModeChange(index == 1)
                                }
                            )
                        }

                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(20.dp))
                            SettingsRow(
                                icon = Icons.Outlined.AdminPanelSettings,
                                label = "Admin page"
                            ) {
                                AdminPageButton(
                                    onClick = {
                                        onAdminViewChange(true)
                                        onAdminPageClick()
                                        onDismiss()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    label: String,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        content()
    }
}

@Composable
private fun SettingsToggle(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    var localSelectedIndex by remember(selectedIndex) { mutableStateOf(selectedIndex) }
    val scope = rememberCoroutineScope()

    val horizontalBias by animateFloatAsState(
        targetValue = if (localSelectedIndex == 0) -1f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "ToggleAnimation"
    )

    Box(
        modifier = Modifier
            .width(110.dp)
            .height(36.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .align(BiasAlignment(horizontalBias, 0f))
                .shadow(2.dp, RoundedCornerShape(999.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(999.dp))
        )

        Row(modifier = Modifier.fillMaxSize()) {
            options.forEachIndexed { index, option ->
                val isSelected = index == localSelectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickableNoRipple {
                            if (localSelectedIndex != index) {
                                localSelectedIndex = index
                                scope.launch {
                                    delay(200)
                                    onOptionSelected(index)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) SquadOrange else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
@Composable
private fun AdminPageButton(
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = SquadOrange,
        shape = RoundedCornerShape(999.dp),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Admin page",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SquadWhite
            )

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = null,
                tint = SquadWhite,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
