package com.example.proyectogym.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
    object Inicio: BottomNavItem("inicio", "Inicio", Icons.Default.Home)
    object Rutina: BottomNavItem("rutina", "Rutina", Icons.Default.List)
    object Perfil: BottomNavItem("perfil", "Perfil", Icons.Default.Person)
}