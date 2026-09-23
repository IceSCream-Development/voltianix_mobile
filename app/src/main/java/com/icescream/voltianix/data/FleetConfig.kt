package com.icescream.voltianix.data

/**
 * Configuración de la flota en un solo lugar.
 *
 * Antes el ID "EV-01" estaba escrito a mano en [com.icescream.voltianix.ui.screens.unit.UnitViewModel]
 * y en la pantalla del mapa, y el encabezado mostraba "EV-001", que no existe en la base de datos.
 */
object FleetConfig {

    /**
     * Unidad que se muestra en el mapa y en la pantalla de Unidad.
     *
     * TODO: cuando exista login, este ID debe venir del usuario que inició sesión.
     */
    const val DEFAULT_VEHICLE_ID = "EV-01"

    const val VEHICLES_COLLECTION = "vehicles"

    const val ALERTS_COLLECTION = "alerts"
}
