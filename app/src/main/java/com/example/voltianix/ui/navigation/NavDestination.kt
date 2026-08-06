package com.icescream.voltianix.ui.navigation

import com.icescream.voltianix.R


sealed class NavDestination(
    val route: String,
    val title: String,
    val iconRes: Int
) {
    object Map : NavDestination("map", "Inicio", R.drawable.home_icon)
    object Unit : NavDestination("unit", "Unidad", R.drawable.unit_icon)
    object Alerts : NavDestination("alerts", "Alertas", R.drawable.alert_icon)
}

val bottomNavDestinations = listOf(
    NavDestination.Map,
    NavDestination.Unit,
    NavDestination.Alerts
)
