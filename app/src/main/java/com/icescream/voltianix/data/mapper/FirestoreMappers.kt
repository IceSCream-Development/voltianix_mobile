package com.icescream.voltianix.data.mapper

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import com.icescream.voltianix.data.model.Alert
import com.icescream.voltianix.data.model.Vehicle
import java.util.Date

/**
 * Lectura manual de los documentos de Firestore.
 *
 * A propósito NO se usa toObject(): lanza una excepción si un campo viene con otro tipo, y
 * como eso ocurre dentro del listener, se lleva la app entera. Aquí un campo con el tipo
 * equivocado simplemente se ignora y se queda el valor por defecto.
 *
 * Además se aceptan los dos formatos que ha tenido la base de datos: el viejo
 * (latitude, longitude, batteryLevel, vehicleId) y el nuevo (location como GeoPoint,
 * battery, name).
 */
fun vehicleFrom(id: String, data: Map<String, Any?>): Vehicle {
    // Los valores por defecto son los datos de demo; ver DemoVehicleData.
    val demo = Vehicle()

    return Vehicle(
        id = id,
        name = data.string("name", "vehicleId") ?: id,
        battery = data.number("battery", "batteryLevel")?.toInt()?.coerceIn(0, 100) ?: demo.battery,
        status = data.string("status") ?: demo.status,
        location = data.location(),
        brand = data.string("brand") ?: demo.brand,
        model = data.string("model") ?: demo.model,
        year = data.string("year") ?: demo.year,
        plates = data.string("plates") ?: demo.plates,
        color = data.string("color") ?: demo.color,
        batteryHealth = data.number("batteryHealth")?.toInt()?.coerceIn(0, 100) ?: demo.batteryHealth,
        lastCharge = data.string("lastCharge") ?: demo.lastCharge,
        batteryTemp = data.string("batteryTemp") ?: demo.batteryTemp,
        electricalSystem = data.string("electricalSystem") ?: demo.electricalSystem,
        brakes = data.string("brakes") ?: demo.brakes,
        tires = data.string("tires") ?: demo.tires,
        gpsStatus = data.string("gpsStatus") ?: demo.gpsStatus,
        batteryCapacity = data.string("batteryCapacity") ?: demo.batteryCapacity,
        chargeType = data.string("chargeType") ?: demo.chargeType,
        fastCharge = data.string("fastCharge") ?: demo.fastCharge,
        maxPower = data.string("maxPower") ?: demo.maxPower,
        lastInspection = data.string("lastInspection") ?: demo.lastInspection
    )
}

fun alertFrom(id: String, data: Map<String, Any?>): Alert {
    val demo = Alert()

    return Alert(
        id = id,
        title = data.string("title") ?: demo.title,
        description = data.string("description") ?: demo.description,
        time = data.string("time") ?: demo.time,
        date = data.string("date") ?: demo.date,
        section = data.string("section") ?: demo.section,
        colorStatusType = data.string("colorStatusType", "color") ?: demo.colorStatusType,
        createdAt = data.date("createdAt", "timestamp")
    )
}

fun DocumentSnapshot.toVehicle(): Vehicle = vehicleFrom(id, data.orEmpty())

fun DocumentSnapshot.toAlert(): Alert = alertFrom(id, data.orEmpty())

// --- Lectores tolerantes ---

/**
 * Primer valor de texto utilizable entre varias llaves.
 *
 * El descarte del texto vacío va dentro del lambda a propósito: si se hace después,
 * `firstNotNullOfOrNull` se queda con la primera llave que sea String aunque venga en blanco y
 * ya no revisa las demás, así que un documento con `{"colorStatusType": "", "color": "RED"}`
 * terminaba en el valor por defecto en vez de caer al formato viejo.
 */
private fun Map<String, Any?>.string(vararg keys: String): String? =
    keys.firstNotNullOfOrNull { (this[it] as? String)?.takeIf(String::isNotBlank) }

private fun Map<String, Any?>.number(vararg keys: String): Double? =
    keys.firstNotNullOfOrNull { this[it] as? Number }?.toDouble()

private fun Map<String, Any?>.date(vararg keys: String): Date? =
    keys.firstNotNullOfOrNull { key ->
        when (val value = this[key]) {
            is Timestamp -> value.toDate()
            is Date -> value
            else -> null
        }
    }

/**
 * Coordenadas de la unidad. Primero el formato nuevo (location como GeoPoint) y, si no está,
 * el viejo (latitude y longitude sueltos). Devuelve null si no hay coordenadas usables, para
 * no dibujar la unidad en (0,0).
 */
private fun Map<String, Any?>.location(): GeoPoint? {
    (this["location"] as? GeoPoint)?.let { return it }

    val latitude = number("latitude", "lat") ?: return null
    val longitude = number("longitude", "lng", "lon") ?: return null
    if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) return null

    return GeoPoint(latitude, longitude)
}
