package com.icescream.voltianix.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.icescream.voltianix.ui.navigation.NavDestination
import com.icescream.voltianix.ui.navigation.bottomNavDestinations
import com.icescream.voltianix.ui.screens.alerts.AlertsScreen
import com.icescream.voltianix.ui.screens.map.MapScreen
import com.icescream.voltianix.ui.screens.unit.UnitScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                bottomNavDestinations.forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.icon, contentDescription = null) },
                        label = { Text(destination.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavDestination.Map.route,
            modifier = Modifier
                // ⚠️ SOLO conservamos el padding inferior (NavigationBar)
                // Esto permite que el Header suba hasta el borde superior real de la pantalla
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(NavDestination.Map.route) { MapScreen() }
            composable(NavDestination.Unit.route) { UnitScreen() }
            composable(NavDestination.Alerts.route) { AlertsScreen() }
        }
    }
}