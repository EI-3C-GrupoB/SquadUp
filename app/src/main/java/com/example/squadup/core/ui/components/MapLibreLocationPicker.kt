package com.example.squadup.core.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView

/**
 * Reusable MapLibre map composable.
 * The user taps on the map to select a location; the marker is placed at the tapped point
 * and [onLocationSelected] is called with the chosen (latitude, longitude).
 *
 * @param initialLatitude  Optional pre-selected latitude (e.g. when editing).
 * @param initialLongitude Optional pre-selected longitude.
 * @param onLocationSelected Callback invoked every time the user taps the map.
 */
@Composable
fun MapLibreLocationPicker(
    initialLatitude: Double?,
    initialLongitude: Double?,
    onLocationSelected: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val defaultLocation = LatLng(
        initialLatitude ?: 41.6932,
        initialLongitude ?: -8.8329
    )

    val mapView = remember {
        MapLibre.getInstance(context.applicationContext)
        MapView(context).apply { onCreate(null) }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START   -> mapView.onStart()
                Lifecycle.Event.ON_RESUME  -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE   -> mapView.onPause()
                Lifecycle.Event.ON_STOP    -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else                       -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(initialLatitude, initialLongitude) {
        mapView.getMapAsync { map ->
            map.setStyle("https://tiles.openfreemap.org/styles/liberty") {
                map.uiSettings.apply {
                    isCompassEnabled    = false
                    isLogoEnabled       = false
                    isAttributionEnabled = true
                    isZoomGesturesEnabled   = true
                    isScrollGesturesEnabled = true
                    isRotateGesturesEnabled = true
                    isTiltGesturesEnabled   = true
                }

                map.cameraPosition = CameraPosition.Builder()
                    .target(defaultLocation)
                    .zoom(if (initialLatitude != null && initialLongitude != null) 15.0 else 12.0)
                    .build()

                // Show pre-existing marker if coordinates are already set
                if (initialLatitude != null && initialLongitude != null) {
                    map.clear()
                    map.addMarker(MarkerOptions().position(defaultLocation).title("Localização selecionada"))
                }

                map.addOnMapClickListener { point ->
                    map.clear()
                    map.addMarker(MarkerOptions().position(point).title("Localização selecionada"))
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15.0), 400)
                    onLocationSelected(point.latitude, point.longitude)
                    true
                }
            }
        }
    }

    AndroidView(modifier = modifier, factory = { mapView })
}
