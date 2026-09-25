package com.icescream.voltianix.ui.screens.unit

import androidx.compose.ui.graphics.Color
import com.icescream.voltianix.ui.theme.Green40
import com.icescream.voltianix.ui.theme.Grey40
import com.icescream.voltianix.ui.theme.Red40
import com.icescream.voltianix.ui.theme.Yellow40
import java.text.Normalizer

private val FAILURE_WORDS = setOf(
    "falla", "fallas", "fallo", "fallos", "error", "errores", "averia", "averias",
    "critico", "critica", "criticos", "criticas",
    "desconectado", "desconectada", "mal", "malo", "mala", "urgente"
)

private val WARNING_WORDS = setOf(
    "revisar", "revision", "advertencia", "pendiente", "alerta",
    "bajo", "baja", "precaucion"
)

/** "Sin fallas" o "no hay errores" no son una falla: la negación cancela la palabra que sigue. */
private val NEGATIONS = setOf("sin", "no")

/**
 * Frases completas que sí son una falla aunque empiecen con una negación.
 * Sin esto "Sin conexión" se leería como lo contrario de lo que dice.
 */
private val FAILURE_PHRASES = setOf(
    "sin conexion", "sin senal", "sin datos", "sin respuesta",
    "no disponible", "fuera de servicio"
)

private val ACCENT_MARKS = Regex("\\p{Mn}+")
private val NOT_A_LETTER_OR_DIGIT = Regex("[^a-z0-9]+")
private val DEGREES = Regex("-?[0-9]+([.,][0-9]+)?")

/**
 * Color del punto de diagnóstico según el valor.
 *
 * Se comparan palabras completas, no subcadenas: con `contains` el valor "Normal" contenía "mal"
 * y una unidad sana se pintaba en rojo.
 */
fun diagnosticColor(value: String): Color {
    val tokens = tokensOf(value)
    if (tokens.isEmpty()) return Grey40

    if (tokens.joinToString(" ") in FAILURE_PHRASES) return Red40

    val negationIndex = tokens.indexOfFirst { it in NEGATIONS }
    var hasFailure = false
    var hasWarning = false

    tokens.forEachIndexed { index, token ->
        val negated = negationIndex >= 0 && index > negationIndex
        if (negated) return@forEachIndexed

        when (token) {
            in FAILURE_WORDS -> hasFailure = true
            in WARNING_WORDS -> hasWarning = true
        }
    }

    return when {
        hasFailure -> Red40
        hasWarning -> Yellow40
        else -> Green40
    }
}

/** Igual que [diagnosticColor], pero para un texto de temperatura tipo "31°C". */
fun temperatureColor(value: String): Color {
    val degrees = DEGREES
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

/** Palabras del texto en minúsculas y sin acentos, para poder compararlas completas. */
private fun tokensOf(value: String): List<String> =
    Normalizer.normalize(value.lowercase(), Normalizer.Form.NFD)
        .replace(ACCENT_MARKS, "")
        .split(NOT_A_LETTER_OR_DIGIT)
        .filter { it.isNotEmpty() }
