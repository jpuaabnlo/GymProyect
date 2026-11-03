package com.example.proyectogym

import android.app.Application
import com.example.proyectogym.data.AppContainer
import com.example.proyectogym.data.AppDataContainer

/**
 * Clase Application personalizada registrada en AndroidManifest.xml.
 * Mantiene una única instancia de AppContainer para toda la vida de la app.
 */
class GymApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
