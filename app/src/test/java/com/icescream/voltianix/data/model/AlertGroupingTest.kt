package com.icescream.voltianix.data.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date
import java.util.concurrent.TimeUnit

class AlertGroupingTest {

    private val now = Date(1_800_000_000_000L)

    private fun daysAgo(days: Long) = Date(now.time - TimeUnit.DAYS.toMillis(days))

    @Test
    fun `la seccion sale de la fecha cuando el documento la trae`() {
        assertEquals(Alert.SECTION_NEW, sectionFor(alert(createdAt = daysAgo(0)), now))
        assertEquals(Alert.SECTION_WEEK, sectionFor(alert(createdAt = daysAgo(3)), now))
        assertEquals(Alert.SECTION_MONTH, sectionFor(alert(createdAt = daysAgo(20)), now))
        assertEquals(Alert.SECTION_LAST_MONTH, sectionFor(alert(createdAt = daysAgo(45)), now))
    }

    @Test
    fun `mas de un ano cae en Mas Antiguas y no en Mes Pasado`() {
        assertEquals(Alert.SECTION_LAST_MONTH, sectionFor(alert(createdAt = daysAgo(200)), now))
        assertEquals(Alert.SECTION_OLDER, sectionFor(alert(createdAt = daysAgo(400)), now))
    }

    @Test
    fun `una fecha adelantada al reloj del celular se queda en Nuevo`() {
        // El servidor puede ir unos segundos adelante: la resta sale negativa y antes eso
        // mandaba la alerta a la sección equivocada.
        val future = Date(now.time + TimeUnit.MINUTES.toMillis(5))

        assertEquals(Alert.SECTION_NEW, sectionFor(alert(createdAt = future), now))
    }

    @Test
    fun `sin fecha se respeta la seccion guardada en Firestore`() {
        val stored = alert(section = Alert.SECTION_MONTH)

        assertEquals(Alert.SECTION_MONTH, sectionFor(stored, now))
    }

    @Test
    fun `las secciones salen en orden y las desconocidas van al final`() {
        val sections = groupAlertsBySection(
            listOf(
                alert(id = "vieja", section = Alert.SECTION_LAST_MONTH),
                alert(id = "rara", section = "La semana pasada"),
                alert(id = "nueva", section = Alert.SECTION_NEW)
            ),
            now
        )

        assertEquals(
            listOf(Alert.SECTION_NEW, Alert.SECTION_LAST_MONTH, Alert.SECTION_OTHER),
            sections.map { it.title }
        )
        assertEquals("rara", sections.last().alerts.single().id)
    }

    @Test
    fun `dentro de una seccion se ordena de la mas nueva a la mas vieja`() {
        val sections = groupAlertsBySection(
            listOf(
                alert(id = "hace 5 horas", createdAt = Date(now.time - TimeUnit.HOURS.toMillis(5))),
                alert(id = "hace 1 hora", createdAt = Date(now.time - TimeUnit.HOURS.toMillis(1))),
                alert(id = "sin fecha", section = Alert.SECTION_NEW)
            ),
            now
        )

        assertEquals(
            listOf("hace 1 hora", "hace 5 horas", "sin fecha"),
            sections.single().alerts.map { it.id }
        )
    }

    private fun alert(
        id: String = "id",
        section: String = Alert.SECTION_NEW,
        createdAt: Date? = null
    ) = Alert(id = id, section = section, createdAt = createdAt)
}
