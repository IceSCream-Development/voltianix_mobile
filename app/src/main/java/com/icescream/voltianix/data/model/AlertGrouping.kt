package com.icescream.voltianix.data.model

import java.util.Date
import java.util.concurrent.TimeUnit

/** Una sección del historial con sus alertas ya ordenadas. */
data class AlertSection(
    val title: String,
    val alerts: List<Alert>
)

private val DAY_MILLIS = TimeUnit.DAYS.toMillis(1)
private val WEEK_MILLIS = TimeUnit.DAYS.toMillis(7)
private val MONTH_MILLIS = TimeUnit.DAYS.toMillis(30)
private val YEAR_MILLIS = TimeUnit.DAYS.toMillis(365)

/**
 * Sección que le toca a una alerta según su antigüedad.
 * Si el documento no trae fecha, se respeta la sección escrita en Firestore.
 *
 * La antigüedad se recorta a cero: si el reloj del celular va atrasado respecto al servidor,
 * la resta sale negativa y la alerta caía en cualquier sección menos la que le toca.
 */
fun sectionFor(alert: Alert, now: Date = Date()): String {
    val createdAt = alert.createdAt ?: return alert.section
    return when ((now.time - createdAt.time).coerceAtLeast(0)) {
        in 0 until DAY_MILLIS -> Alert.SECTION_NEW
        in DAY_MILLIS until WEEK_MILLIS -> Alert.SECTION_WEEK
        in WEEK_MILLIS until MONTH_MILLIS -> Alert.SECTION_MONTH
        in MONTH_MILLIS until YEAR_MILLIS -> Alert.SECTION_LAST_MONTH
        else -> Alert.SECTION_OLDER
    }
}

/**
 * Agrupa las alertas en el orden en que se muestran: Nuevo, Esta Semana, Este Mes,
 * Mes Pasado, Más Antiguas y, al final, "Otras".
 *
 * Esa última sección existe para que una alerta con una sección desconocida no desaparezca
 * de la pantalla sin avisar, que es lo que pasaba antes. Dentro de cada sección se ordena de
 * la más nueva a la más vieja; las que no traen fecha quedan al final, como llegaron.
 */
fun groupAlertsBySection(alerts: List<Alert>, now: Date = Date()): List<AlertSection> {
    val bySection = alerts.groupBy { alert ->
        val section = sectionFor(alert, now)
        Alert.SECTION_ORDER.firstOrNull { it.equals(section, ignoreCase = true) }
            ?: Alert.SECTION_OTHER
    }

    return (Alert.SECTION_ORDER + Alert.SECTION_OTHER).mapNotNull { title ->
        val sectionAlerts = bySection[title]
        if (sectionAlerts.isNullOrEmpty()) return@mapNotNull null

        AlertSection(
            title = title,
            alerts = sectionAlerts.sortedWith(
                compareByDescending<Alert> { it.createdAt != null }
                    .thenByDescending { it.createdAt?.time ?: 0L }
            )
        )
    }
}
