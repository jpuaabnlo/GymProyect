package com.example.proyectogym.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectogym.navigation.BottomNavItem

@Composable
fun MainScreen(){
    val navigationBarController = rememberNavController()

    val items = listOf(
        BottomNavItem.Inicio,
        BottomNavItem.Rutina,
        BottomNavItem.Perfil
    )

    Scaffold(
        bottomBar = {
            NavigationBar{
                val navBackStackEntry by navigationBarController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route} == true,
                        onClick = {
                            navigationBarController.navigate(item.route){
                                popUpTo(navigationBarController.graph.findStartDestination().id){
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
            navController = navigationBarController,
            startDestination = BottomNavItem.Inicio.route,
            modifier = Modifier.padding(innerPadding)
        ){
            composable(BottomNavItem.Inicio.route){
                Box(modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center){
                    Text("Pantalla de Inicio")
                }
            }
            composable(BottomNavItem.Rutina.route){
                RoutineScreen()
            }
            composable(BottomNavItem.Perfil.route){
                ProfileScreen()
            }
        }
    }
}