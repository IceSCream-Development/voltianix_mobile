package com.icescream.voltianix.ui.screens.unit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.icescream.voltianix.ui.components.Accordion
import com.icescream.voltianix.ui.components.InformationRow

@Composable
fun UnitScreen(
    modifier: Modifier = Modifier,
    viewModel: UnitViewModel = hiltViewModel(
        checkNotNull<ViewModelStoreOwner>(
            LocalViewModelStoreOwner.current
        ) {
                "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
            }, null
    )
) {
    val expandedSections by viewModel.expandedSections.collectAsState()

    UnitScreenContent(
        modifier = modifier,
        expandedSections = expandedSections,
        onToggleSection = { viewModel.toggleSection(it) }
    )
}

@Composable
fun UnitScreenContent(
    modifier: Modifier = Modifier,
    expandedSections: Map<String, Boolean>,
    onToggleSection: (String) -> Unit
) {
    Column (
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
            InformationRow("Marca", "Ford")
            InformationRow("Modelo", "Ford E-Transit")
            InformationRow("Año", "2025")
            InformationRow("Placas", "ABC-123-D")
            InformationRow("Color", "Blanco")
            InformationRow("Número de Unidad", "EV-001")
        }

        Accordion(
            title = "Estado de la Batería",
            expanded = expandedSections["battery_status"] ?: false,
            onExpandedChange = { onToggleSection("battery_status") }
        ) {
            InformationRow("Carga Actual", "85%")
            InformationRow("Autonomía", "248 km")
            InformationRow("Salud de Batería", "96%")
            InformationRow("Última Carga", "[03/08/2026] 08:00 PM")
            InformationRow("Próxima Carga", "35 km")
        }

        Accordion(
            title = "Diagnóstico",
            expanded = expandedSections["diagnostics"] ?: false,
            onExpandedChange = { onToggleSection("diagnostics") }
        ) {
            InformationRow("Temperatura de Batería", "31°C",  Color.Green)
            InformationRow("Sistema Eléctrico", "Correcto",  Color.Green)
            InformationRow("Frenos", "Correcto", Color.Green)
            InformationRow("Neumáticos", "Correcto", Color.Green)
            InformationRow("GPS", "Conectado", Color.Green)
        }

        Accordion(
            title = "Información Técnica",
            expanded = expandedSections["technical_info"] ?: false,
            onExpandedChange = { onToggleSection("technical_info") }
        ) {
            InformationRow("Capacidad de Batería", "75 kWh")
            InformationRow("Tipo de Carga", "CSS Combo")
            InformationRow("Carga Rápida", "Compatible")
            InformationRow("Potencia Máxima", "250 kW")
            InformationRow("Última Inspección", "75 kWh")
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewUnitScreen(){
    UnitScreenContent(
        expandedSections = emptyMap(),
        onToggleSection = {}
    )
}