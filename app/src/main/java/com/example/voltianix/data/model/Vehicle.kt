package com.icescream.voltianix.data.model

import com.google.firebase.firestore.GeoPoint

data class Vehicle(
    val id: String = "",
    val name: String = "EV-001",
    val location: GeoPoint = GeoPoint(21.88234, -102.28259),
    val battery: Int = 100,
    val speed: Double = 0.0,
    val status: String = "en_ruta"
) {
    // 🔋 Autonomía estimada (Ejemplo: 100% de batería ≈ 290 km de rango)
    val remainingRangeKm: Int
        get() = (battery * 2.92).toInt()

    // ⚡ Próxima recarga recomendada según el nivel de batería actual
    val nextRechargeKm: Int
        get() = if (battery > 20) (battery * 0.8).toInt() else 5
}