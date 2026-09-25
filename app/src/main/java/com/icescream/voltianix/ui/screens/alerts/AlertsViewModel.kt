package com.icescream.voltianix.ui.screens.alerts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.icescream.voltianix.data.model.Alert
import com.icescream.voltianix.data.repository.FleetRepository
import com.icescream.voltianix.ui.STOP_TIMEOUT_MILLIS
import com.icescream.voltianix.ui.UiState
import com.icescream.voltianix.ui.asUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

/** Estado del historial de alertas. */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AlertsViewModel @Inject constructor(
    repository: FleetRepository
) : ViewModel() {

    private val retryTrigger = MutableStateFlow(0)

    val uiState: StateFlow<UiState<List<Alert>>> = retryTrigger
        .flatMapLatest { repository.observeAlerts().asUiState(TAG) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = UiState.Loading
        )

    fun retry() {
        retryTrigger.update { it + 1 }
    }

    private companion object {
        const val TAG = "AlertsViewModel"
    }
}
