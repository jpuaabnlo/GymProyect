package com.example.proyectogym.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Destinos de la barra de navegación inferior organizados para los 3 roles del negocio:
 * Cliente, Entrenador y Administrador.
 */
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    // --- Menú Cliente ---
    data object ClienteInicio : BottomNavItem("cliente_inicio", "Inicio", Icons.Default.Home)
    data object ClienteRutina : BottomNavItem("cliente_rutina", "Rutina", Icons.Default.FitnessCenter)
    data object ClienteProgreso : BottomNavItem("cliente_progreso", "Progreso", Icons.Default.Timeline)

    // --- Menú Entrenador ---
    data object CoachClientes : BottomNavItem("coach_clientes", "Clientes", Icons.Default.Group)
    data object CoachRutinas : BottomNavItem("coach_rutinas", "Rutinas", Icons.Default.FitnessCenter)

    // --- Menú Administrador ---
    data object AdminTablero : BottomNavItem("admin_tablero", "Tablero", Icons.Default.Dashboard)
    data object AdminCobrar : BottomNavItem("admin_cobrar", "Cobrar", Icons.Default.AttachMoney)
    data object AdminTienda : BottomNavItem("admin_tienda", "Tienda", Icons.Default.ShoppingCart)
    data object AdminGestion : BottomNavItem("admin_gestion", "Gestión", Icons.Default.Settings)

    // --- Común a todos los roles ---
    data object Perfil : BottomNavItem("perfil", "Perfil", Icons.Default.Person)

    companion object {
        const val ROUTE_EDITOR_RUTINA = "coach_editor_rutina"

        fun menuPorRol(rol: String): List<BottomNavItem> = when (rol) {
            "ENTRENADOR" -> listOf(CoachClientes, CoachRutinas, Perfil)
            "ADMIN" -> listOf(AdminTablero, AdminCobrar, AdminTienda, AdminGestion, Perfil)
            else -> listOf(ClienteInicio, ClienteRutina, ClienteProgreso, Perfil)
        }
    }
}
