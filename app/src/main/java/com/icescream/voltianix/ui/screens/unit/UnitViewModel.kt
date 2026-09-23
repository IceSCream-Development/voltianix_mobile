package com.icescream.voltianix.ui.screens.unit

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icescream.voltianix.data.FleetConfig
import com.icescream.voltianix.data.model.Vehicle
import com.icescream.voltianix.data.repository.FleetRepository
import com.icescream.voltianix.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Estado de la pantalla de Unidad. */
@HiltViewModel
class UnitViewModel @Inject constructor(
    repository: FleetRepository
) : ViewModel() {

    val uiState: StateFlow<UiState<Vehicle>> =
        repository.observeVehicle(FleetConfig.DEFAULT_VEHICLE_ID)
            .map<Vehicle?, UiState<Vehicle>> { vehicle ->
                // Si el documento no existe hay que decirlo. Antes se salía en silencio y la
                // pantalla se quedaba girando el indicador de carga para siempre.
                vehicle?.let { UiState.Success(it) }
                    ?: UiState.Error(
                        "No se encontró la unidad ${FleetConfig.DEFAULT_VEHICLE_ID} en la base de datos"
                    )
            }
            .catch { error ->
                Log.e(TAG, "Error al escuchar la unidad ${FleetConfig.DEFAULT_VEHICLE_ID}", error)
                emit(UiState.Error(error.message ?: "No se pudo conectar con el servidor"))
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
                initialValue = UiState.Loading
            )

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
        private const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
