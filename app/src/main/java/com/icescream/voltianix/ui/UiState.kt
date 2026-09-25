package com.icescream.voltianix.ui

import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen

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

/** Cuánto sigue viva la consulta después de que la pantalla deja de mirarla. */
const val STOP_TIMEOUT_MILLIS = 5_000L

private const val MAX_RETRIES = 3L
private const val RETRY_DELAY_MILLIS = 2_000L

/**
 * Convierte el flujo de datos en estados de pantalla, con reintentos.
 *
 * Los tres ViewModels hacían esto por su cuenta y con `catch` a secas, que **termina** el flujo:
 * un error de red al abrir la app dejaba la pantalla clavada en el mensaje de error mientras la
 * app siguiera abierta, porque `WhileSubscribed` solo reinicia cuando nadie está mirando.
 * Aquí se reintenta solo unas cuantas veces antes de rendirse, y el reintento manual lo
 * dispara cada ViewModel con su `retry()`.
 */
fun <T, R> Flow<T>.asUiState(
    tag: String,
    fallbackMessage: String = DEFAULT_ERROR_MESSAGE,
    transform: (T) -> UiState<R>
): Flow<UiState<R>> =
    retryWhen { cause, attempt ->
        val shouldRetry = attempt < MAX_RETRIES
        if (shouldRetry) {
            Log.w(tag, "Reintento ${attempt + 1} después de un error de Firestore", cause)
            delay(RETRY_DELAY_MILLIS * (attempt + 1))
        }
        shouldRetry
    }
        .map(transform)
        .catch { error ->
            Log.e(tag, fallbackMessage, error)
            emit(UiState.Error(error.message ?: fallbackMessage))
        }

/** Versión corta para las pantallas donde cualquier dato que llegue ya es un éxito. */
fun <T> Flow<T>.asUiState(
    tag: String,
    fallbackMessage: String = DEFAULT_ERROR_MESSAGE
): Flow<UiState<T>> = asUiState(tag, fallbackMessage) { UiState.Success(it) }

private const val DEFAULT_ERROR_MESSAGE = "No se pudo conectar con el servidor"
