package com.icescream.voltianix.ui.screens.unit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icescream.voltianix.data.FleetConfig
import com.icescream.voltianix.data.model.Vehicle
import com.icescream.voltianix.data.repository.FleetRepository
import com.icescream.voltianix.ui.STOP_TIMEOUT_MILLIS
import com.icescream.voltianix.ui.UiState
import com.icescream.voltianix.ui.asUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** Estado de la pantalla de Unidad. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UnitViewModel @Inject constructor(
    repository: FleetRepository
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<UiState<Vehicle>> = retryTrigger
        .flatMapLatest {
            // La misma consulta que usa el mapa: antes esta pantalla abría su propia escucha
            // sobre el mismo documento.
            repository.observeFleet().asUiState(TAG) { snapshot ->
                val vehicle = snapshot.vehicle(FleetConfig.DEFAULT_VEHICLE_ID)
                when {
                    vehicle != null -> UiState.Success(vehicle)

                    // Sin conexión, Firestore responde desde la caché en lugar de fallar. Con la
                    // caché vacía eso se veía igual que una unidad borrada de la base, y la
                    // pantalla acusaba a la base de datos de un problema de red.
                    snapshot.fromCache -> UiState.Error(OFFLINE_MESSAGE)

                    else -> UiState.Error(
                        "No se encontró la unidad ${FleetConfig.DEFAULT_VEHICLE_ID} en la base de datos"
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    fun retry() {
        retryTrigger.update { it + 1 }
    }

    private val _expandedSections = MutableStateFlow(
        mapOf(
            VEHICLE_INFO to true,
            BATTERY_STATUS to false,
            DIAGNOSTICS to false,
            TECHNICAL_INFO to false
        )
    )
    val expandedSections: StateFlow<Map<String, Boolean>> = _expandedSections.asStateFlow()

    fun toggleSection(sectionKey: String) {
        _expandedSections.value = _expandedSections.value.toMutableMap().apply {
            this[sectionKey] = !(this[sectionKey] ?: false)
        }
    }

    companion object {
        const val VEHICLE_INFO = "vehicle_info"
        const val BATTERY_STATUS = "battery_status"
        const val DIAGNOSTICS = "diagnostics"
        const val TECHNICAL_INFO = "technical_info"

        private const val TAG = "UnitViewModel"
        private const val OFFLINE_MESSAGE =
            "Sin conexión. No se pudieron cargar los datos de la unidad."
    }
}
