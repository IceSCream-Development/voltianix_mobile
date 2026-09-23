package com.icescream.voltianix.ui.screens.map

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.EvStation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.icescream.voltianix.data.FleetConfig
import com.icescream.voltianix.data.model.Vehicle
import com.icescream.voltianix.ui.FleetViewModel
import com.icescream.voltianix.ui.UiState
import com.icescream.voltianix.ui.theme.Green40
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

/** Centro del mapa mientras no hay coordenadas de la unidad: Aguascalientes. */
private val DEFAULT_CENTER = GeoPoint(21.88234, -102.28259)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: FleetViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val vehicles = when (val state = uiState) {
        is UiState.Success -> state.data
        else -> emptyList()
    }
    // Solo se pueden dibujar las unidades que traen coordenadas usables.
    val locatedVehicles = vehicles.filter { it.location != null }
    val selectedVehicle = vehicles.find { it.id == FleetConfig.DEFAULT_VEHICLE_ID }
        ?: vehicles.firstOrNull()

    var searchQuery by remember { mutableStateOf("") }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // Controla si la cámara persigue automáticamente al vehículo o no
    var isTrackingVehicle by remember { mutableStateOf(true) }

    val scaffoldState = rememberBottomSheetScaffoldState()

    // osmdroid necesita saber cuándo la pantalla se pausa para soltar sus recursos.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapViewRef?.onResume()
                Lifecycle.Event.ON_PAUSE -> mapViewRef?.onPause()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 90.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetContent = {
            // La tarjeta dice qué está pasando en lugar de inventar datos mientras carga.
            when (val state = uiState) {
                is UiState.Loading -> SheetMessage(title = "Cargando unidad...")

                is UiState.Error -> SheetMessage(
                    title = "Sin conexión con el servidor",
                    detail = state.message,
                    isError = true
                )

                is UiState.Success -> selectedVehicle?.let { vehicle ->
                    VehicleSummary(vehicle = vehicle)
                } ?: SheetMessage(
                    title = "Sin datos de la unidad",
                    detail = "No se encontró ${FleetConfig.DEFAULT_VEHICLE_ID} en la base de datos"
                )
            }
        }
    ) { innerPadding ->
        // --- CONTENIDO PRINCIPAL (MAPA + UI OVERLAY) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // MAPA DE BACKGROUND
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(15.0)
                        controller.setCenter(DEFAULT_CENTER)

                        // Overlay para capturar cuando el usuario arrastra libremente el mapa
                        val touchOverlay = object : Overlay() {
                            override fun onTouchEvent(event: MotionEvent?, mapView: MapView?): Boolean {
                                if (event?.action == MotionEvent.ACTION_MOVE) {
                                    isTrackingVehicle = false
                                }
                                return false
                            }
                        }
                        overlays.add(0, touchOverlay)

                        mapViewRef = this
                    }
                },
                update = { mapView ->
                    // Mantenemos el touchOverlay en la posición 0 y actualizamos los demás
                    val touchOverlay = mapView.overlays.firstOrNull()
                    mapView.overlays.clear()
                    touchOverlay?.let { mapView.overlays.add(it) }

                    locatedVehicles.forEach { vehicle ->
                        val location = vehicle.location ?: return@forEach
                        val vehiclePoint = GeoPoint(location.latitude, location.longitude)
                        val marker = Marker(mapView).apply {
                            position = vehiclePoint
                            title = vehicle.displayName
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        mapView.overlays.add(marker)

                        // Solo persigue al vehículo si el seguimiento sigue activo.
                        if (isTrackingVehicle && vehicle.id == selectedVehicle?.id) {
                            mapView.controller.animateTo(vehiclePoint)
                        }
                    }
                    mapView.invalidate()
                },
                onRelease = { mapView ->
                    // Sin esto osmdroid deja hilos y caché de teselas vivos al salir de la pantalla.
                    mapView.onDetach()
                    mapViewRef = null
                }
            )

            // BARRA DE BÚSQUEDA FLOTANTE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(48.dp)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            // TODO: abrir el menú lateral cuando exista.
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    "Buscar",
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            singleLine = true
                        )
                        // TODO: la búsqueda todavía no filtra nada.
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Buscar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // BOTONES FLOTANTES DE ACCIÓN
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloatingActionButton(
                    // TODO: mostrar las estaciones de carga.
                    onClick = { },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.EvStation, contentDescription = "Estaciones")
                }

                FloatingActionButton(
                    onClick = {
                        val location = selectedVehicle?.location ?: return@FloatingActionButton
                        isTrackingVehicle = true
                        mapViewRef?.controller?.animateTo(
                            GeoPoint(location.latitude, location.longitude)
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = if (isTrackingVehicle) Green40 else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Mi Ubicación")
                }
            }
        }
    }
}

/** Resumen de la unidad dentro de la tarjeta deslizable. */
@Composable
private fun VehicleSummary(vehicle: Vehicle) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = vehicle.displayName,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
        Spacer(modifier = Modifier.height(16.dp))

        // 1. ESTADO
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
            Spacer(modifier = Modifier.width(10.dp))
            SummaryTexts(title = "Estado", value = vehicle.statusLabel)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. BATERÍA CON BARRA DE PROGRESO VERDE
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.BatteryChargingFull,
                contentDescription = "Batería",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Batería",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { vehicle.battery / 100f },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Green40,
                        trackColor = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${vehicle.battery}%",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. AUTONOMÍA RESTANTE
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = "Autonomía",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            SummaryTexts(
                title = "Autonomía Restante",
                value = "${vehicle.remainingRangeKm} km"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. PRÓXIMA RECARGA RECOMENDADA
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Recarga",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            SummaryTexts(
                title = "Próxima Recarga Recomendada",
                value = "${vehicle.nextRechargeKm} km"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun SummaryTexts(title: String, value: String) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Mensaje dentro de la tarjeta cuando no hay datos que mostrar. */
@Composable
private fun SheetMessage(title: String, detail: String? = null, isError: Boolean = false) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isError) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
        if (detail != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = detail,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}
