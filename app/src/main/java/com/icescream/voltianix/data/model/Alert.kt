package com.icescream.voltianix.data.model

data class Alert(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: String = "",
    val date: String = "",
    val section: String = "Nuevo", // "Nuevo", "Esta Semana", "Este Mes", "Mes Pasado"
    val colorStatusType: String = "GREEN" // "GREEN", "YELLOW", "BLUE", "RED"
)