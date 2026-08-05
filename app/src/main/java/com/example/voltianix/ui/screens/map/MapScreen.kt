package com.icescream.voltianix.ui.screens.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.icescream.voltianix.ui.FleetViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun MapScreen(viewModel: FleetViewModel = viewModel()) {
    val context = LocalContext.current
    val vehicles by viewModel.vehicles.collectAsState()

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(15.0)
                controller.setCenter(GeoPoint(21.88234, -102.28259))
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            vehicles.forEach { vehicle ->
                val vehiclePoint = GeoPoint(vehicle.location.latitude, vehicle.location.longitude)

                val marker = Marker(mapView).apply {
                    position = vehiclePoint
                    title = "${vehicle.name} | Batería: ${vehicle.battery}%"
                    snippet = "Velocidad: ${vehicle.speed} km/h"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(marker)

                if (vehicle.id == "EV-01") {
                    mapView.controller.animateTo(vehiclePoint)
                }
            }
            mapView.invalidate()
        }
    )
}