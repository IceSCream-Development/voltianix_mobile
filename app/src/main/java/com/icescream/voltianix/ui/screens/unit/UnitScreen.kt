package com.icescream.voltianix.ui.screens.unit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.icescream.voltianix.data.model.Vehicle
import com.icescream.voltianix.ui.components.Accordion
import com.icescream.voltianix.ui.components.InformationRow

val CustomGreen = Color(0xFF38C172)

@Composable
fun UnitScreen(
    modifier: Modifier = Modifier,
    viewModel: UnitViewModel = hiltViewModel()
) {
    val expandedSections by viewModel.expandedSections.collectAsState()
    val selectedVehicle by viewModel.selectedVehicle.collectAsState()

    if (selectedVehicle == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = CustomGreen)
        }
    } else {
        UnitScreenContent(
            modifier = modifier,
            vehicle = selectedVehicle!!,
            expandedSections = expandedSections,
            onToggleSection = { viewModel.toggleSection(it) }
        )
    }
}

@Composable
fun UnitScreenContent(
    modifier: Modifier = Modifier,
    vehicle: Vehicle,
    expandedSections: Map<String, Boolean>,
    onToggleSection: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 32.dp, horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Accordion(
            title = "Información del Vehículo",
            expanded = expandedSections["vehicle_info"] ?: false,
            onExpandedChange = { onToggleSection("vehicle_info") }
        ) {
            InformationRow("Marca", vehicle.brand)
            InformationRow("Modelo", vehicle.model)
            InformationRow("Año", vehicle.year)
            InformationRow("Placas", vehicle.plates)
            InformationRow("Color", vehicle.color)
            InformationRow("Número de Unidad", vehicle.name.ifEmpty { "EV-001" })
        }

        Accordion(
            title = "Estado de la Batería",
            expanded = expandedSections["battery_status"] ?: false,
            onExpandedChange = { onToggleSection("battery_status") }
        ) {
            val rangeKm = (vehicle.battery * 2.92).toInt()

            InformationRow("Carga Actual", "${vehicle.battery}%")
            InformationRow("Autonomía", "$rangeKm km")
            InformationRow("Salud de Batería", "${vehicle.batteryHealth}%")
            InformationRow("Última Carga", vehicle.lastCharge)
            InformationRow("Próxima Carga", "${vehicle.nextChargeKm} km")
        }

        Accordion(
            title = "Diagnóstico",
            expanded = expandedSections["diagnostics"] ?: false,
            onExpandedChange = { onToggleSection("diagnostics") }
        ) {
            InformationRow("Temperatura de Batería", vehicle.batteryTemp, CustomGreen)
            InformationRow("Sistema Eléctrico", vehicle.electricalSystem, CustomGreen)
            InformationRow("Frenos", vehicle.brakes, CustomGreen)
            InformationRow("Neumáticos", vehicle.tires, CustomGreen)
            InformationRow("GPS", vehicle.gpsStatus, CustomGreen)
        }

        Accordion(
            title = "Información Técnica",
            expanded = expandedSections["technical_info"] ?: false,
            onExpandedChange = { onToggleSection("technical_info") }
        ) {
            InformationRow("Capacidad de Batería", vehicle.batteryCapacity)
            InformationRow("Tipo de Carga", vehicle.chargeType)
            InformationRow("Carga Rápida", vehicle.fastCharge)
            InformationRow("Potencia Máxima", vehicle.maxPower)
            InformationRow("Última Inspección", vehicle.lastInspection)
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewUnitScreen() {
    UnitScreenContent(
        vehicle = Vehicle(
            id = "EV-01",
            name = "EV-001",
            battery = 85,
            status = "en_ruta"
        ),
        expandedSections = mapOf("vehicle_info" to true),
        onToggleSection = {}
    )
}