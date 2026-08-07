package com.icescream.voltianix.data.model

import com.google.firebase.firestore.GeoPoint

data class Vehicle(
    val id: String = "",
    val name: String = "",
    val battery: Int = 0,
    val status: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    // Campos extendidos para la vista de Unidad y Alertas
    val brand: String = "Ford",
    val model: String = "Ford E-Transit",
    val year: String = "2025",
    val plates: String = "ABC-123-D",
    val color: String = "Blanco",
    val batteryHealth: Int = 96,
    val lastCharge: String = "[03/08/2026] 08:00 PM",
    val nextChargeKm: Int = 35,
    val batteryTemp: String = "31°C",
    val electricalSystem: String = "Correcto",
    val brakes: String = "Correcto",
    val tires: String = "Correcto",
    val gpsStatus: String = "Conectado",
    val batteryCapacity: String = "75 kWh",
    val chargeType: String = "CCS Combo",
    val fastCharge: String = "Compatible",
    val maxPower: String = "250 kW",
    val lastInspection: String = "15/07/2026"
)