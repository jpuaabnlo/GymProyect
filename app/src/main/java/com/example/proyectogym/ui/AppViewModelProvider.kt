package com.example.proyectogym.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.proyectogym.GymApplication
import com.example.proyectogym.ui.admin.AdminViewModel
import com.example.proyectogym.ui.auth.LoginViewModel
import com.example.proyectogym.ui.client.ClientViewModel
import com.example.proyectogym.ui.coach.CoachViewModel
import com.example.proyectogym.ui.profile.ProfileViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            LoginViewModel(
                gymRepository = gymApplication().container.gymRepository,
                networkRepository = gymApplication().container.networkGymRepository
            )
        }
        initializer {
            ProfileViewModel(
                gymRepository = gymApplication().container.gymRepository
            )
        }
        initializer {
            ClientViewModel(
                gymRepository = gymApplication().container.gymRepository
            )
        }
        initializer {
            CoachViewModel(
                gymRepository = gymApplication().container.gymRepository
            )
        }
        initializer {
            AdminViewModel(
                gymRepository = gymApplication().container.gymRepository
            )
        }
    }
}

fun CreationExtras.gymApplication(): GymApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as GymApplication)
