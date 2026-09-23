package com.icescream.voltianix.data.mapper

import com.google.firebase.firestore.GeoPoint
import com.icescream.voltianix.data.model.DemoVehicleData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * El lector de Firestore tiene que aguantar los dos formatos de la base de datos y, sobre
 * todo, no tronar cuando un campo viene con el tipo equivocado: antes eso cerraba la app.
 */
class FirestoreMappersTest {

    @Test
    fun `lee el formato nuevo con location y battery`() {
        val vehicle = vehicleFrom(
            id = "EV-01",
            data = mapOf(
                "name" to "EV-001",
                "battery" to 85L,
                "status" to "en_ruta",
                "location" to GeoPoint(21.88234, -102.28259)
            )
        )

        assertEquals("EV-001", vehicle.name)
        assertEquals(85, vehicle.battery)
        assertEquals("en_ruta", vehicle.status)
        assertEquals(21.88234, vehicle.location?.latitude ?: 0.0, 0.00001)
        assertEquals(-102.28259, vehicle.location?.longitude ?: 0.0, 0.00001)
    }

    @Test
    fun `lee el formato viejo con latitude longitude y batteryLevel`() {
        val vehicle = vehicleFrom(
            id = "abc123",
            data = mapOf(
                "vehicleId" to "EV-01",
                "batteryLevel" to 42.0,
                "latitude" to 21.9,
                "longitude" to -102.3,
                "speedKmh" to 35.5
            )
        )

        assertEquals("EV-01", vehicle.name)
        assertEquals(42, vehicle.battery)
        assertEquals(21.9, vehicle.location?.latitude ?: 0.0, 0.00001)
        assertEquals(-102.3, vehicle.location?.longitude ?: 0.0, 0.00001)
    }

    @Test
    fun `sin coordenadas la ubicacion queda nula en vez de cero cero`() {
        val vehicle = vehicleFrom(id = "EV-01", data = mapOf("battery" to 10L))

        assertNull(vehicle.location)
    }

    @Test
    fun `un campo con el tipo equivocado no revienta y usa el valor por defecto`() {
        val vehicle = vehicleFrom(
            id = "EV-01",
            data = mapOf(
                "battery" to "ochenta y cinco",
                "location" to mapOf("lat" to 21.9, "lng" to -102.3),
                "brand" to 12345
            )
        )

        assertEquals(0, vehicle.battery)
        assertNull(vehicle.location)
        assertEquals(DemoVehicleData.BRAND, vehicle.brand)
    }

    @Test
    fun `coordenadas fuera de rango se descartan`() {
        val vehicle = vehicleFrom(
            id = "EV-01",
            data = mapOf("latitude" to 999.0, "longitude" to -102.3)
        )

        assertNull(vehicle.location)
    }

    @Test
    fun `la bateria se recorta al rango valido`() {
        assertEquals(100, vehicleFrom("EV-01", mapOf("battery" to 140L)).battery)
        assertEquals(0, vehicleFrom("EV-01", mapOf("battery" to -5L)).battery)
    }

    @Test
    fun `sin nombre se usa el id del documento`() {
        assertEquals("EV-01", vehicleFrom("EV-01", emptyMap()).name)
    }

    @Test
    fun `la ficha tecnica cae en los datos de demo cuando Firestore no la trae`() {
        val vehicle = vehicleFrom("EV-01", emptyMap())

        assertEquals(DemoVehicleData.MODEL, vehicle.model)
        assertEquals(DemoVehicleData.PLATES, vehicle.plates)
        assertEquals(DemoVehicleData.BRAKES, vehicle.brakes)
    }

    @Test
    fun `lee una alerta completa`() {
        val alert = alertFrom(
            id = "a1",
            data = mapOf(
                "title" to "Batería baja",
                "description" to "La carga bajó del 30%",
                "time" to "8:45 PM",
                "date" to "Agosto 04, 2026",
                "section" to "Nuevo",
                "colorStatusType" to "YELLOW"
            )
        )

        assertEquals("a1", alert.id)
        assertEquals("Batería baja", alert.title)
        assertEquals("YELLOW", alert.colorStatusType)
        assertNull(alert.createdAt)
    }
}
