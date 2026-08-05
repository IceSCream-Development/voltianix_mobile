package com.icescream.voltianix.ui.navigation

import androidx.compose.material. icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Map
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavDestination(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Map : NavDestination("map", "Inicio", Icons.Default.Map)
    object Unit : NavDestination("unit", "Unidad", Icons.Default.DirectionsCar)
    object Alerts : NavDestination("alerts", "Alertas", Icons.Default.Notifications)
}

val bottomNavDestinations = listOf(
    NavDestination.Map,
    NavDestination.Unit,
    NavDestination.Alerts
)
