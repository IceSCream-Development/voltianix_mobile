package com.icescream.voltianix.ui.screens.map

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.icescream.voltianix.ui.FleetViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

val SearchBarGray = Color(0xFF5E5E5E)
val CustomGreen = Color(0xFF38C172)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: FleetViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val vehicles by viewModel.vehicles.collectAsState()

    val selectedVehicle = vehicles.find { it.id == "EV-01" } ?: vehicles.firstOrNull()
    var searchQuery by remember { mutableStateOf("") }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // Controla si la cámara persigue automáticamente al vehículo o no
    var isTrackingVehicle by remember { mutableStateOf(true) }

    val scaffoldState = rememberBottomSheetScaffoldState()

    // --- MODO OSCURO DINÁMICO (Para UI de Compose) ---
    val isDark = isSystemInDarkTheme()
    val cardBackground = if (isDark) Color(0xFF2D2D2D) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF2B2B2B)
    val subtitleColor = if (isDark) Color(0xFFA0A0A0) else Color.Gray
    val searchBarBg = if (isDark) Color(0xFF2D2D2D) else Color.White
    val searchIconBg = if (isDark) Color(0xFF3E3E3E) else SearchBarGray
    val dividerColor = if (isDark) Color(0xFF3E3E3E) else Color(0xFFE0E0E0)

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 90.dp,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetContainerColor = cardBackground,
        sheetContent = {
            // --- CONTENIDO DESPLEGABLE DE LA TARJETA ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text(
                    text = selectedVehicle?.name ?: "EV-001",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = dividerColor, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))

                // 1. ESTADO
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(textColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Estado", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                        Text(
                            text = if (selectedVehicle?.status == "en_ruta") "En Ruta" else selectedVehicle?.status ?: "En Ruta",
                            fontSize = 13.sp,
                            color = subtitleColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 2. BATERÍA CON BARRA DE PROGRESO VERDE
                val batteryVal = selectedVehicle?.battery ?: 85
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BatteryChargingFull,
                        contentDescription = "Batería",
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Batería", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { batteryVal / 100f },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = CustomGreen,
                                trackColor = dividerColor
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "$batteryVal%",
                                fontSize = 13.sp,
                                color = subtitleColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. AUTONOMÍA RESTANTE
                val rangeKm = (batteryVal * 2.92).toInt()
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = "Autonomía",
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Autonomía Restante", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                        Text("$rangeKm km", fontSize = 13.sp, color = subtitleColor)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 4. PRÓXIMA RECARGA RECOMENDADA
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Recarga",
                        tint = textColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Próxima Recarga Recomendada", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                        Text("${selectedVehicle?.nextChargeKm ?: 18} km", fontSize = 13.sp, color = subtitleColor)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
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
                        controller.setCenter(GeoPoint(21.88234, -102.28259))

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

                    vehicles.forEach { vehicle ->
                        val vehiclePoint = GeoPoint(vehicle.location.latitude, vehicle.location.longitude)
                        val marker = Marker(mapView).apply {
                            position = vehiclePoint
                            title = vehicle.name
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        mapView.overlays.add(marker)

                        // Solo persigue automáticamente al vehículo si 'isTrackingVehicle' está en true
                        if (isTrackingVehicle && vehicle.id == selectedVehicle?.id) {
                            mapView.controller.animateTo(vehiclePoint)
                        }
                    }
                    mapView.invalidate()
                }
            )

            // BARRA DE BÚSQUEDA FLOTANTE
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = searchBarBg,
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
                                .background(searchIconBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Buscar", color = subtitleColor, fontSize = 14.sp) },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = textColor,
                                unfocusedTextColor = textColor
                            ),
                            singleLine = true
                        )
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Search, contentDescription = "Buscar", tint = subtitleColor)
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
                    onClick = { /* Estaciones */ },
                    containerColor = searchBarBg,
                    contentColor = if (isDark) Color.White else Color(0xFF4A4A4A),
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Outlined.EvStation, contentDescription = "Estaciones")
                }

                FloatingActionButton(
                    onClick = {
                        selectedVehicle?.let { vehicle ->
                            isTrackingVehicle = true
                            mapViewRef?.controller?.animateTo(
                                GeoPoint(vehicle.location.latitude, vehicle.location.longitude)
                            )
                        }
                    },
                    containerColor = searchBarBg,
                    contentColor = if (isTrackingVehicle) CustomGreen else if (isDark) Color.White else Color(0xFF4A4A4A),
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Mi Ubicación")
                }
            }
        }
    }
}