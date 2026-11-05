package com.example.proyectogym.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyectogym.ui.AppViewModelProvider
import com.example.proyectogym.ui.admin.AdminBillingScreen
import com.example.proyectogym.ui.admin.AdminDashboardScreen
import com.example.proyectogym.ui.admin.AdminManagementScreen
import com.example.proyectogym.ui.admin.AdminStoreScreen
import com.example.proyectogym.ui.admin.AdminViewModel
import com.example.proyectogym.ui.auth.LoginScreen
import com.example.proyectogym.ui.client.ClientHomeScreen
import com.example.proyectogym.ui.client.ClientProgressScreen
import com.example.proyectogym.ui.client.ClientViewModel
import com.example.proyectogym.ui.client.RoutineScreen
import com.example.proyectogym.ui.coach.CoachClientsScreen
import com.example.proyectogym.ui.coach.CoachRoutinesScreen
import com.example.proyectogym.ui.coach.CoachViewModel
import com.example.proyectogym.ui.coach.RoutineEditorScreen
import com.example.proyectogym.ui.profile.ProfileScreen

@Composable
fun GymApp() {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { usuario ->
                    rootNavController.navigate("main/${usuario.id}/${usuario.rol}") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = "main/{userId}/{rol}",
            arguments = listOf(
                navArgument("userId") { type = NavType.IntType },
                navArgument("rol") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 1
            val rol = backStackEntry.arguments?.getString("rol") ?: "CLIENTE"

            RoleMainScreen(
                userId = userId,
                rol = rol,
                onLogout = {
                    rootNavController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun RoleMainScreen(
    userId: Int,
    rol: String,
    onLogout: () -> Unit
) {
    val tabNavController = rememberNavController()
    val itemsMenu = BottomNavItem.menuPorRol(rol)
    val startDestination = itemsMenu.first().route

    val clientViewModel: ClientViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val coachViewModel: CoachViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val adminViewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                itemsMenu.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            tabNavController.navigate(item.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
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
            navController = tabNavController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.ClienteInicio.route) {
                ClientHomeScreen(
                    userId = userId,
                    viewModel = clientViewModel,
                    onIrARutina = {
                        tabNavController.navigate(BottomNavItem.ClienteRutina.route) {
                            popUpTo(tabNavController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomNavItem.ClienteRutina.route) {
                RoutineScreen(
                    userId = userId,
                    viewModel = clientViewModel
                )
            }
            composable(BottomNavItem.ClienteProgreso.route) {
                ClientProgressScreen(
                    userId = userId,
                    viewModel = clientViewModel
                )
            }

            composable(BottomNavItem.CoachClientes.route) {
                CoachClientsScreen(
                    coachId = userId,
                    viewModel = coachViewModel
                )
            }
            composable(BottomNavItem.CoachRutinas.route) {
                CoachRoutinesScreen(
                    coachId = userId,
                    viewModel = coachViewModel,
                    onCrearRutina = {
                        tabNavController.navigate(BottomNavItem.ROUTE_EDITOR_RUTINA)
                    },
                    onEditarRutina = {
                        tabNavController.navigate(BottomNavItem.ROUTE_EDITOR_RUTINA)
                    }
                )
            }
            composable(BottomNavItem.ROUTE_EDITOR_RUTINA) {
                RoutineEditorScreen(
                    viewModel = coachViewModel,
                    onBack = { tabNavController.popBackStack() }
                )
            }

            composable(BottomNavItem.AdminTablero.route) {
                AdminDashboardScreen(viewModel = adminViewModel)
            }
            composable(BottomNavItem.AdminCobrar.route) {
                AdminBillingScreen(viewModel = adminViewModel)
            }
            composable(BottomNavItem.AdminTienda.route) {
                AdminStoreScreen(
                    adminId = userId,
                    viewModel = adminViewModel
                )
            }
            composable(BottomNavItem.AdminGestion.route) {
                AdminManagementScreen(viewModel = adminViewModel)
            }

            composable(BottomNavItem.Perfil.route) {
                ProfileScreen(
                    userId = userId,
                    onLogout = onLogout
                )
            }
        }
    }
}
