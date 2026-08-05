package com.icescream.voltianix.data.model

import com.google.firebase.firestore.GeoPoint

data class Vehicle(
    val id: String = "",
    val name: String = "EV-01",
    val location: GeoPoint = GeoPoint(21.88234, -102.28259),
    val battery: Int = 100,
    val speed: Double = 0.0,
    val status: String = "In Transit"
)