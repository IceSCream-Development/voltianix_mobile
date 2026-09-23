package com.icescream.voltianix.data.model

/**
 * DATOS DE DEMO.
 *
 * Estos valores NO vienen de Firestore: son relleno para que la demo se vea completa.
 * [Vehicle] los usa como valores por defecto, así que se siguen viendo igual que antes,
 * pero ya están identificados en un solo lugar.
 *
 * Cuando un campo empiece a existir de verdad en la colección `vehicles`, basta con
 * borrar su constante de aquí y quitar el valor por defecto en [Vehicle]: el lector
 * (`vehicleFrom`) ya lo toma de la base de datos si el documento lo trae.
 */
object DemoVehicleData {

    // Información del vehículo
    const val BRAND = "Ford"
    const val MODEL = "Ford E-Transit"
    const val YEAR = "2025"
    const val PLATES = "ABC-123-D"
    const val COLOR = "Blanco"

    // Estado de la batería
    const val BATTERY_HEALTH = 96
    const val LAST_CHARGE = "[03/08/2026] 08:00 PM"

    // Diagnóstico
    const val BATTERY_TEMP = "31°C"
    const val ELECTRICAL_SYSTEM = "Correcto"
    const val BRAKES = "Correcto"
    const val TIRES = "Correcto"
    const val GPS_STATUS = "Conectado"

    // Información técnica
    const val BATTERY_CAPACITY = "75 kWh"
    const val CHARGE_TYPE = "CCS Combo"
    const val FAST_CHARGE = "Compatible"
    const val MAX_POWER = "250 kW"
    const val LAST_INSPECTION = "15/07/2026"
}

/**
 * DATOS DE DEMO del usuario que ve la app.
 *
 * TODO: reemplazar por el perfil real cuando haya login.
 */
object DemoUserData {

    const val GREETING_NAME = "Miguel"

    const val AVATAR_INITIAL = "M"
}
