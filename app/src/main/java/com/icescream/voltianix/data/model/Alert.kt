package com.icescream.voltianix.data.model

import java.util.Date

/**
 * Una alerta del historial.
 *
 * [section] es el texto que hoy viene guardado en Firestore. Si el documento además trae
 * [createdAt], la sección y el orden se calculan solos a partir de la fecha, que es lo que
 * debería pasar: con la sección escrita a mano una alerta se queda en "Nuevo" para siempre.
 */
data class Alert(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: String = "",
    val date: String = "",
    val section: String = SECTION_NEW,
    val colorStatusType: String = COLOR_GREEN,
    /** Opcional: campo createdAt o timestamp en Firestore. */
    val createdAt: Date? = null
) {

    companion object {
        const val SECTION_NEW = "Nuevo"
        const val SECTION_WEEK = "Esta Semana"
        const val SECTION_MONTH = "Este Mes"
        const val SECTION_LAST_MONTH = "Mes Pasado"

        /** Para las alertas cuya sección no es ninguna de las conocidas. */
        const val SECTION_OTHER = "Otras"

        const val COLOR_GREEN = "GREEN"
        const val COLOR_YELLOW = "YELLOW"
        const val COLOR_BLUE = "BLUE"
        const val COLOR_RED = "RED"

        val SECTION_ORDER = listOf(SECTION_NEW, SECTION_WEEK, SECTION_MONTH, SECTION_LAST_MONTH)
    }
}
