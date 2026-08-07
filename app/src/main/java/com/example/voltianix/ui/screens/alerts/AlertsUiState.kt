package com.icescream.voltianix.ui.screens.alerts

sealed interface AlertsUiState {
    object Loading : AlertsUiState
    data class Success(val alerts: List<com.icescream.voltianix.data.model.Alert>) : AlertsUiState
    data class Error(val message: String) : AlertsUiState
}