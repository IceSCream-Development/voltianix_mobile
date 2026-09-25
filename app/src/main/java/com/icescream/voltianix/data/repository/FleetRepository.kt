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
 * Lo que entrega la consulta de la flota.
 *
 * [fromCache] existe para no confundir "no hay conexión" con "la unidad no está en la base":
 * estando offline Firestore no lanza error, entrega un snapshot servido desde la caché local,
 * y con la caché vacía eso se veía exactamente igual que un documento borrado.
 */
data class FleetSnapshot(
    val vehicles: List<Vehicle> = emptyList(),
    val fromCache: Boolean = false
) {
    fun vehicle(id: String): Vehicle? = vehicles.find { it.id == id }
}

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

    /**
     * La flota completa, en tiempo real.
     *
     * Es la única consulta de vehículos: la usan el mapa y la pantalla de Unidad. Antes esa
     * segunda pantalla abría su propia escucha sobre el mismo documento; al compartir la
     * consulta, el SDK de Firestore comparte también el listen que va a la red.
     */
    fun observeFleet(): Flow<FleetSnapshot> = callbackFlow {
        val registration = firestore.collection(FleetConfig.VEHICLES_COLLECTION)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot == null) return@addSnapshotListener

                trySend(
                    FleetSnapshot(
                        vehicles = snapshot.documents.map { it.toVehicle() },
                        fromCache = snapshot.metadata.isFromCache
                    )
                )
            }

        awaitClose { registration.remove() }
    }

    /**
     * Historial de alertas, en tiempo real.
     *
     * El `limit` corta el peor caso: el historial solo crece y sin él se descargaba, se cobraba
     * y se guardaba en memoria la colección entera en cada arranque.
     *
     * TODO: cuando se confirme que todos los documentos de `alerts` traen `createdAt`, cambiar
     * esto por `.orderBy("createdAt", Query.Direction.DESCENDING).limit(...)` para que el
     * servidor mande las más nuevas ya ordenadas. Hoy no se puede: Firestore deja fuera de un
     * `orderBy` los documentos a los que les falte ese campo, y la pantalla se vaciaría.
     */
    fun observeAlerts(): Flow<List<Alert>> = callbackFlow {
        val registration = firestore.collection(FleetConfig.ALERTS_COLLECTION)
            .limit(FleetConfig.ALERTS_LIMIT)
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
