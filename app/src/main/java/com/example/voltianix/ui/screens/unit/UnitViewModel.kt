package com.icescream.voltianix.ui.screens.unit

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class UnitViewModel @Inject constructor() : ViewModel() {

    private val _expandedSections = MutableStateFlow(
        mapOf(
            "vehicle_info" to false,
            "battery_status" to false,
            "diagnostics" to false,
            "technical_info" to false
        )
    )
    val expandedSections: StateFlow<Map<String, Boolean>> = _expandedSections.asStateFlow()

    fun toggleSection(sectionKey: String) {
        val current = _expandedSections.value
        _expandedSections.value = current.toMutableMap().apply {
            this[sectionKey] = !(this[sectionKey] ?: false)
        }
    }
}
