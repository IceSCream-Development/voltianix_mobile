package com.icescream.voltianix.ui.screens.alerts

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.icescream.voltianix.data.model.Alert
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AlertsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow<AlertsUiState>(AlertsUiState.Loading)
    val uiState: StateFlow<AlertsUiState> = _uiState.asStateFlow()

    init {
        fetchAlertsFromFirestore()
    }

    private fun fetchAlertsFromFirestore() {
        firestore.collection("alerts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("AlertsViewModel", "Error al escuchar Firestore: ${error.message}", error)
                    _uiState.value = AlertsUiState.Error(error.message ?: "Error al conectar con el servidor")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val alertList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Alert::class.java)?.copy(id = doc.id)
                    }
                    _uiState.value = AlertsUiState.Success(alertList)
                }
            }
    }
}