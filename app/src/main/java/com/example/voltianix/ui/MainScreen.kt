package com.icescream.voltianix.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.icescream.voltianix.ui.components.CustomHeader
import com.icescream.voltianix.ui.navigation.NavDestination
import com.icescream.voltianix.ui.navigation.bottomNavDestinations
import com.icescream.voltianix.ui.screens.alerts.AlertsScreen
import com.icescream.voltianix.ui.screens.map.MapScreen
import com.icescream.voltianix.ui.screens.unit.UnitScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            when (currentRoute) {
                NavDestination.Map.route -> {
                    CustomHeader(
                        title = "Buenos días, Miguel",
                        subtitle = "EV-001",
                        showProfileIcon = true
                    )
                }
                NavDestination.Unit.route -> {
                    CustomHeader(
                        title = "Unidad",
                        subtitle = "Consulta el estado completo de tu Unidad",
                        showProfileIcon = false // Oculta el avatar
                    )
                }
                NavDestination.Alerts.route -> {
                    CustomHeader(
                        title = "Alertas",
                        showProfileIcon = false // Oculta el avatar
                    )
                }
            }
        },

        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .shadow(
                        elevation = 32.dp,
                        spotColor = Color.Transparent,
                        ambientColor = MaterialTheme.colorScheme.onBackground
                    ),
                containerColor = MaterialTheme.colorScheme.background,
            ){
                val currentDestination = navBackStackEntry?.destination

                bottomNavDestinations.forEach { destination ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = destination.iconRes),
                                contentDescription = destination.title
                            )
                        },
                        label = { Text(destination.title) },
                        colors = NavigationBarItemColors(
                            selectedIconColor = MaterialTheme.colorScheme.tertiary,
                            selectedTextColor = MaterialTheme.colorScheme.tertiary,
                            selectedIndicatorColor = MaterialTheme.colorScheme.tertiary.copy(0.1f),
                            unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                            unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                            disabledIconColor = MaterialTheme.colorScheme.primary,
                            disabledTextColor = MaterialTheme.colorScheme.primary

                        ),
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
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
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavDestination.Map.route) { MapScreen() }
            composable(NavDestination.Unit.route) { UnitScreen() }
            composable(NavDestination.Alerts.route) { AlertsScreen() }
        }
    }
}
