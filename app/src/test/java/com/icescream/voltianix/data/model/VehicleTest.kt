package com.icescream.voltianix.data.model

import org.junit.Assert.assertEquals
import org.junit.Test

class VehicleTest {

    @Test
    fun `el estado se muestra legible y sin guiones bajos`() {
        assertEquals("En Ruta", Vehicle(status = "en_ruta").statusLabel)
        assertEquals("Fuera de Servicio", Vehicle(status = "fuera_de_servicio").statusLabel)
        assertEquals("Cargando", Vehicle(status = "en_carga").statusLabel)
        assertEquals("Sin datos", Vehicle(status = "").statusLabel)
    }

    @Test
    fun `un estado desconocido al menos se limpia`() {
        assertEquals("Estado Raro", Vehicle(status = "estado_raro").statusLabel)
    }

    @Test
    fun `la autonomia sale de la carga actual`() {
        assertEquals(248, Vehicle(battery = 85).remainingRangeKm)
        assertEquals(0, Vehicle(battery = 0).remainingRangeKm)
    }

    @Test
    fun `la proxima recarga son los kilometros antes de tocar la reserva`() {
        // (85 - 20) * 2.92 = 189.8 km, coherente con los 248 km de autonomía.
        assertEquals(189, Vehicle(battery = 85).nextRechargeKm)
    }

    @Test
    fun `debajo de la reserva ya no quedan kilometros que recomendar`() {
        assertEquals(0, Vehicle(battery = 10).nextRechargeKm)
        assertEquals(0, Vehicle(battery = 20).nextRechargeKm)
    }

    @Test
    fun `sin nombre se usa el id del documento`() {
        assertEquals("EV-01", Vehicle(id = "EV-01").displayName)
        assertEquals("Camioneta 1", Vehicle(id = "EV-01", name = "Camioneta 1").displayName)
    }
}
