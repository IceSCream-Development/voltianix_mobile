package com.icescream.voltianix.ui

/**
 * Estado de una pantalla que depende de datos remotos.
 *
 * Reemplaza al antiguo AlertsUiState para que las tres pantallas se comporten igual:
 * antes el mapa y la pantalla de Unidad se tragaban los errores en silencio y se quedaban
 * cargando para siempre.
 */
sealed interface UiState<out T> {

    data object Loading : UiState<Nothing>

    data class Success<T>(val data: T) : UiState<T>

    data class Error(val message: String) : UiState<Nothing>
}
