package com.icescream.voltianix.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.icescream.voltianix.data.model.Vehicle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FleetViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _vehicles = MutableStateFlow<List<Vehicle>>(emptyList())
    val vehicles: StateFlow<List<Vehicle>> = _vehicles

    init {
        listenToTelemetry()
    }

    private fun listenToTelemetry() {
        db.collection("vehicles")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("VoltianixFirestore", "Error al escuchar Firestore", error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    Log.d("VoltianixFirestore", "Documentos encontrados: ${snapshot.size()}")

                    val vehicleList = snapshot.documents.mapNotNull { doc ->
                        // 1. Extraer latitud y longitud numéricas
                        val lat = doc.getDouble("latitude")
                        val lng = doc.getDouble("longitude")

                        // Si no hay coordenadas válidas, ignorar
                        if (lat == null || lng == null) return@mapNotNull null

                        // 2. Mapear claves exactas de tu Firestore
                        val id = doc.getString("vehicleId") ?: doc.id
                        val battery = doc.getDouble("batteryLevel")?.toInt()
                            ?: doc.getLong("batteryLevel")?.toInt() ?: 0
                        val speed = doc.getDouble("speedKmh")
                            ?: doc.getLong("speedKmh")?.toDouble() ?: 0.0
                        val status = doc.getString("status") ?: "en_ruta"

                        Vehicle(
                            id = id,
                            name = id, // O el nombre que prefieras mostrar
                            location = GeoPoint(lat, lng),
                            battery = battery,
                            speed = speed,
                            status = status
                        )
                    }
                    _vehicles.value = vehicleList
                }
            }
    }
}