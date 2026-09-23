package com.icescream.voltianix.ui.screens.unit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.icescream.voltianix.data.model.Vehicle
import com.icescream.voltianix.ui.UiState
import com.icescream.voltianix.ui.components.Accordion
import com.icescream.voltianix.ui.components.ErrorState
import com.icescream.voltianix.ui.components.InformationRow
import com.icescream.voltianix.ui.components.LoadingState
import com.icescream.voltianix.ui.theme.VoltianixTheme

@Composable
fun UnitScreen(
    modifier: Modifier = Modifier,
    viewModel: UnitViewModel = hiltViewModel()
) {
    val expandedSections by viewModel.expandedSections.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is UiState.Loading -> LoadingState(modifier = modifier)

        is UiState.Error -> ErrorState(message = state.message, modifier = modifier)

        is UiState.Success -> UnitScreenContent(
            modifier = modifier,
            vehicle = state.data,
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
            expanded = expandedSections[UnitViewModel.VEHICLE_INFO] ?: false,
            onExpandedChange = { onToggleSection(UnitViewModel.VEHICLE_INFO) }
        ) {
            InformationRow("Marca", vehicle.brand)
            InformationRow("Modelo", vehicle.model)
            InformationRow("Año", vehicle.year)
            InformationRow("Placas", vehicle.plates)
            InformationRow("Color", vehicle.color)
            InformationRow("Número de Unidad", vehicle.displayName)
        }

        Accordion(
            title = "Estado de la Batería",
            expanded = expandedSections[UnitViewModel.BATTERY_STATUS] ?: false,
            onExpandedChange = { onToggleSection(UnitViewModel.BATTERY_STATUS) }
        ) {
            InformationRow("Carga Actual", "${vehicle.battery}%")
            InformationRow("Autonomía", "${vehicle.remainingRangeKm} km")
            InformationRow("Salud de Batería", "${vehicle.batteryHealth}%")
            InformationRow("Última Carga", vehicle.lastCharge)
            InformationRow("Próxima Carga", "${vehicle.nextRechargeKm} km")
        }

        Accordion(
            title = "Diagnóstico",
            expanded = expandedSections[UnitViewModel.DIAGNOSTICS] ?: false,
            onExpandedChange = { onToggleSection(UnitViewModel.DIAGNOSTICS) }
        ) {
            InformationRow(
                "Temperatura de Batería",
                vehicle.batteryTemp,
                temperatureColor(vehicle.batteryTemp)
            )
            InformationRow(
                "Sistema Eléctrico",
                vehicle.electricalSystem,
                diagnosticColor(vehicle.electricalSystem)
            )
            InformationRow("Frenos", vehicle.brakes, diagnosticColor(vehicle.brakes))
            InformationRow("Neumáticos", vehicle.tires, diagnosticColor(vehicle.tires))
            InformationRow("GPS", vehicle.gpsStatus, diagnosticColor(vehicle.gpsStatus))
        }

        Accordion(
            title = "Información Técnica",
            expanded = expandedSections[UnitViewModel.TECHNICAL_INFO] ?: false,
            onExpandedChange = { onToggleSection(UnitViewModel.TECHNICAL_INFO) }
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
    VoltianixTheme {
        UnitScreenContent(
            vehicle = Vehicle(
                id = "EV-01",
                name = "EV-001",
                battery = 85,
                status = "en_ruta"
            ),
            expandedSections = mapOf(UnitViewModel.VEHICLE_INFO to true),
            onToggleSection = {}
        )
    }
}
