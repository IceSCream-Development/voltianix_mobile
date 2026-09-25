package com.icescream.voltianix.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icescream.voltianix.data.model.Vehicle
import com.icescream.voltianix.data.repository.FleetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** Estado de la flota para la pantalla del mapa. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FleetViewModel @Inject constructor(
    repository: FleetRepository
) : ViewModel() {

    /** Cada cambio vuelve a abrir la consulta: es el reintento manual de la pantalla. */
    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<UiState<List<Vehicle>>> = retryTrigger
        .flatMapLatest {
            repository.observeFleet()
                .map { snapshot -> snapshot.vehicles }
                .asUiState(TAG)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    fun retry() {
        retryTrigger.update { it + 1 }
    }

    private companion object {
        const val TAG = "FleetViewModel"
    }
}
