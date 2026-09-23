package com.icescream.voltianix.ui.screens.unit

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.icescream.voltianix.data.model.Vehicle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UnitViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _selectedVehicle = MutableStateFlow<Vehicle?>(null)
    val selectedVehicle: StateFlow<Vehicle?> = _selectedVehicle.asStateFlow()

    private val _expandedSections = MutableStateFlow<Map<String, Boolean>>(
        mapOf(
            "vehicle_info" to true,
            "battery_status" to false,
            "diagnostics" to false,
            "technical_info" to false
        )
    )
    val expandedSections: StateFlow<Map<String, Boolean>> = _expandedSections.asStateFlow()

    init {
        loadVehicleData("EV-01")
    }

    private fun loadVehicleData(vehicleId: String) {
        firestore.collection("vehicles").document(vehicleId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    return@addSnapshotListener
                }
                val vehicle = snapshot.toObject(Vehicle::class.java)?.copy(id = snapshot.id)
                _selectedVehicle.value = vehicle
            }
    }

    fun toggleSection(sectionKey: String) {
        val currentMap = _expandedSections.value.toMutableMap()
        currentMap[sectionKey] = !(currentMap[sectionKey] ?: false)
        _expandedSections.value = currentMap
    }
}