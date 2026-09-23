package com.icescream.voltianix.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.icescream.voltianix.data.FleetConfig
import com.icescream.voltianix.data.mapper.toAlert
import com.icescream.voltianix.data.mapper.toVehicle
import com.icescream.voltianix.data.model.Alert
import com.icescream.voltianix.data.model.Vehicle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Único punto de acceso a Firestore.
 *
 * Cada consulta es un callbackFlow: el awaitClose quita el listener cuando nadie está
 * escuchando. Antes los listeners se registraban y no se quitaban nunca, así que seguían
 * vivos después de cerrar la pantalla.
 *
 * Los errores se propagan como excepción del Flow para que el ViewModel los pueda mostrar,
 * en lugar de quedarse callado como antes.
 */
@Singleton
class FleetRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    /** Toda la flota, en tiempo real. */
    fun observeVehicles(): Flow<List<Vehicle>> = callbackFlow {
        val registration = firestore.collection(FleetConfig.VEHICLES_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.map { it.toVehicle() }.orEmpty())
            }

        awaitClose { registration.remove() }
    }

    /** Una sola unidad. Emite null si el documento no existe. */
    fun observeVehicle(vehicleId: String): Flow<Vehicle?> = callbackFlow {
        val registration = firestore.collection(FleetConfig.VEHICLES_COLLECTION)
            .document(vehicleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.takeIf { it.exists() }?.toVehicle())
            }

        awaitClose { registration.remove() }
    }

    /** Historial de alertas, en tiempo real. */
    fun observeAlerts(): Flow<List<Alert>> = callbackFlow {
        val registration = firestore.collection(FleetConfig.ALERTS_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.map { it.toAlert() }.orEmpty())
            }

        awaitClose { registration.remove() }
    }
}
