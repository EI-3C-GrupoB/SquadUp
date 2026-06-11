package com.example.squadup.core.ui.components

import androidx.compose.material3.MaterialTheme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.squadup.R
import com.example.squadup.core.ui.theme.SquadOrange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

data class SelectedLocation(
    val lat: Double,
    val lng: Double,
    val address: String
)

/**
 * Shared Location picker dialog.
 *
 * Uses the shared [MapLibreLocationPicker] component.
 * On tap the marker appears; once the user hits Confirm the
 * address is reverse-geocoded via Nominatim and returned as a [SelectedLocation].
 */
@Composable
fun LocationPickerDialog(
    onLocationSelected: (SelectedLocation) -> Unit,
    onDismiss: () -> Unit
) {
    val isLandscape = rememberIsLandscape()
    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    var pickedAddress by remember { mutableStateOf("") }
    var isGeocoding by remember { mutableStateOf(false) }

    // Reverse-geocode via Nominatim every time the user taps a new point
    LaunchedEffect(selectedLat, selectedLng) {
        val lat = selectedLat ?: return@LaunchedEffect
        val lng = selectedLng ?: return@LaunchedEffect
        isGeocoding = true
        pickedAddress = withContext(Dispatchers.IO) {
            try {
                val conn = URL(
                    "https://nominatim.openstreetmap.org/reverse" +
                        "?lat=$lat&lon=$lng&format=json&accept-language=pt"
                ).openConnection() as HttpURLConnection
                conn.setRequestProperty("User-Agent", "SquadUpApp/1.0")
                conn.connectTimeout = 5_000
                conn.readTimeout = 5_000
                val text = conn.inputStream.bufferedReader().readText()
                conn.disconnect()
                Regex(""""display_name"\s*:\s*"([^"]+)"""")
                    .find(text)?.groupValues?.get(1)
                    ?: "%.4f, %.4f".format(lat, lng)
            } catch (_: Exception) {
                "%.4f, %.4f".format(lat, lng)
            }
        }
        isGeocoding = false
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isLandscape) 420.dp else 560.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(18.dp),
            shadowElevation = 10.dp
        ) {
            Column {
                // ── Header ────────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.register_location_pick_title),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = stringResource(R.string.register_location_close),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = SquadOrange,
                        modifier = Modifier.clickable(onClick = onDismiss)
                    )
                }

                // ── Map ───────────────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    MapLibreLocationPicker(
                        initialLatitude = null,
                        initialLongitude = null,
                        onLocationSelected = { lat, lng ->
                            selectedLat = lat
                            selectedLng = lng
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // Hint pill
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.dp),
                        color = Color.White.copy(alpha = 0.95f),
                        shape = RoundedCornerShape(999.dp),
                        shadowElevation = 4.dp
                    ) {
                        Text(
                            text = stringResource(R.string.register_location_tap_hint),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }

                // ── Address display + Confirm button ──────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedLat != null || isGeocoding) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                tint = SquadOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = when {
                                    isGeocoding -> stringResource(R.string.register_location_getting)
                                    pickedAddress.isNotBlank() -> pickedAddress
                                    else -> ""
                                },
                                color = if (!isGeocoding) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    PrimaryButton(
                        text = stringResource(R.string.register_location_confirm),
                        onClick = {
                            val lat = selectedLat ?: return@PrimaryButton
                            val lng = selectedLng ?: return@PrimaryButton
                            onLocationSelected(
                                SelectedLocation(
                                    lat = lat,
                                    lng = lng,
                                    address = pickedAddress.ifBlank { "%.4f, %.4f".format(lat, lng) }
                                )
                            )
                        },
                        enabled = selectedLat != null && !isGeocoding
                    )
                }
            }
        }
    }
}
