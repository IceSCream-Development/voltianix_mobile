package com.icescream.voltianix.data.model

import com.google.firebase.firestore.GeoPoint

/**
 * Una unidad de la flota.
 *
 * Los primeros campos son telemetría real que llega de Firestore. La ficha técnica
 * (marca, placas, diagnóstico…) todavía es relleno de demo: ver [DemoVehicleData].
 *
 * Los documentos se leen con `vehicleFrom`, no con `toObject()`, para que un campo
 * faltante o con el tipo equivocado no tire la app.
 */
data class Vehicle(
    val id: String = "",
    val name: String = "",
    val battery: Int = 0,
    val status: String = "",
    /**
     * `null` cuando el documento no trae coordenadas usables.
     * El mapa omite esas unidades en vez de dibujarlas en (0,0), en medio del océano.
     */
    val location: GeoPoint? = null,

    // --- Ficha técnica: datos de demo mientras no existan en Firestore ---
    val brand: String = DemoVehicleData.BRAND,
    val model: String = DemoVehicleData.MODEL,
    val year: String = DemoVehicleData.YEAR,
    val plates: String = DemoVehicleData.PLATES,
    val color: String = DemoVehicleData.COLOR,
    val batteryHealth: Int = DemoVehicleData.BATTERY_HEALTH,
    val lastCharge: String = DemoVehicleData.LAST_CHARGE,
    val batteryTemp: String = DemoVehicleData.BATTERY_TEMP,
    val electricalSystem: String = DemoVehicleData.ELECTRICAL_SYSTEM,
    val brakes: String = DemoVehicleData.BRAKES,
    val tires: String = DemoVehicleData.TIRES,
    val gpsStatus: String = DemoVehicleData.GPS_STATUS,
    val batteryCapacity: String = DemoVehicleData.BATTERY_CAPACITY,
    val chargeType: String = DemoVehicleData.CHARGE_TYPE,
    val fastCharge: String = DemoVehicleData.FAST_CHARGE,
    val maxPower: String = DemoVehicleData.MAX_POWER,
    val lastInspection: String = DemoVehicleData.LAST_INSPECTION
) {

    /** Nombre a mostrar. Si el documento no trae `name`, se usa el ID del documento. */
    val displayName: String
        get() = name.ifBlank { id }

    /** Estado legible. Antes esta traducción estaba suelta dentro del mapa. */
    val statusLabel: String
        get() = when {
            status.equals("en_ruta", ignoreCase = true) -> "En Ruta"
            status.isBlank() -> "Sin datos"
            else -> status
        }

    /** Autonomía estimada: 100 % de batería ≈ 292 km. */
    val remainingRangeKm: Int
        get() = (battery * KM_PER_BATTERY_PERCENT).toInt()

    /** Próxima recarga recomendada según la carga actual. */
    val nextRechargeKm: Int
        get() = if (battery > LOW_BATTERY_PERCENT) (battery * 0.8).toInt() else MIN_RECHARGE_KM

    companion object {
        const val KM_PER_BATTERY_PERCENT = 2.92
        private const val LOW_BATTERY_PERCENT = 20
        private const val MIN_RECHARGE_KM = 5
    }
}
