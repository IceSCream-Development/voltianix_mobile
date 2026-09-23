package com.icescream.voltianix.ui.screens.unit

import androidx.compose.ui.graphics.Color
import com.icescream.voltianix.ui.theme.Green40
import com.icescream.voltianix.ui.theme.Grey40
import com.icescream.voltianix.ui.theme.Red40
import com.icescream.voltianix.ui.theme.Yellow40

private val FAILURE_WORDS = listOf("falla", "fallo", "error", "crítico", "critico", "desconectado", "mal")
private val WARNING_WORDS = listOf("revisar", "advertencia", "pendiente", "bajo", "precaución", "precaucion")

/**
 * Color del punto de diagnóstico según el valor.
 *
 * Antes el punto siempre se pintaba verde, aunque el dato dijera "Falla": con los datos de
 * demo se sigue viendo igual, pero un problema real ya se nota.
 */
fun diagnosticColor(value: String): Color = when {
    value.isBlank() -> Grey40
    FAILURE_WORDS.any { value.contains(it, ignoreCase = true) } -> Red40
    WARNING_WORDS.any { value.contains(it, ignoreCase = true) } -> Yellow40
    else -> Green40
}

/** Igual que [diagnosticColor], pero para un texto de temperatura tipo "31°C". */
fun temperatureColor(value: String): Color {
    val degrees = Regex("-?[0-9]+([.,][0-9]+)?")
        .find(value)
        ?.value
        ?.replace(",", ".")
        ?.toDoubleOrNull()
        ?: return diagnosticColor(value)

    return when {
        degrees >= 50 -> Red40
        degrees >= 40 -> Yellow40
        else -> Green40
    }
}
