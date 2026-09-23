package com.icescream.voltianix.ui.screens.alerts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icescream.voltianix.data.model.Alert
import com.icescream.voltianix.data.repository.FleetRepository
import com.icescream.voltianix.ui.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Estado del historial de alertas. */
@HiltViewModel
class AlertsViewModel @Inject constructor(
    repository: FleetRepository
) : ViewModel() {

    val uiState: StateFlow<UiState<List<Alert>>> = repository.observeAlerts()
        .map<List<Alert>, UiState<List<Alert>>> { UiState.Success(it) }
        .catch { error ->
            Log.e(TAG, "Error al escuchar la colección de alertas", error)
            emit(UiState.Error(error.message ?: "No se pudo conectar con el servidor"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    private companion object {
        const val TAG = "AlertsViewModel"
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
