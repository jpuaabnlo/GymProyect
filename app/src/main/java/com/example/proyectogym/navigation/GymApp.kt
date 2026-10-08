package com.example.proyectogym.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyectogym.screens.LoginScreen
import com.example.proyectogym.screens.MainScreen

@Composable
fun GymApp(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Login"
    ) {
        composable("Login"){
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("Login") {
                            inclusive = true;
                        }
                    }
                }
            )
        }

        composable("home") {
            MainScreen()
        }
    }
}