package com.icescream.voltianix.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icescream.voltianix.data.model.Vehicle
import com.icescream.voltianix.data.repository.FleetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Estado de la flota para la pantalla del mapa. */
@HiltViewModel
class FleetViewModel @Inject constructor(
    repository: FleetRepository
) : ViewModel() {

    val uiState: StateFlow<UiState<List<Vehicle>>> = repository.observeVehicles()
        .map<List<Vehicle>, UiState<List<Vehicle>>> { UiState.Success(it) }
        .catch { error ->
            Log.e(TAG, "Error al escuchar la colección de vehículos", error)
            emit(UiState.Error(error.message ?: "No se pudo conectar con el servidor"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    private companion object {
        const val TAG = "FleetViewModel"
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
