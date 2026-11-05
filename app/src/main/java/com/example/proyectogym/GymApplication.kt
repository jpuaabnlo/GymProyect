package com.example.proyectogym

import android.app.Application
import com.example.proyectogym.data.AppContainer
import com.example.proyectogym.data.AppDataContainer

class GymApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
