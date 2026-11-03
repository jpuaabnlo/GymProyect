package com.example.proyectogym.data

import android.content.Context
import com.example.proyectogym.data.local.GymDatabase
import com.example.proyectogym.data.remote.GymApiService
import com.example.proyectogym.data.repository.GymRepository
import com.example.proyectogym.data.repository.LocalGymRepository
import com.example.proyectogym.data.repository.NetworkGymRepository
import com.example.proyectogym.data.repository.RemoteGymRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Contenedor de dependencias de la aplicación (patrón oficial de los Codelabs de Android Developers:
 * Inventory App + Mars Photos).
 *
 * Cadena de dependencias:
 * GymApplication -> AppContainer -> MainActivity -> Compose -> ViewModel -> Repository -> Local (Room) / Remote (Retrofit)
 */
interface AppContainer {
    val gymRepository: GymRepository
    val networkGymRepository: RemoteGymRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    // URL base preparada para un futuro backend local en emulador Android (10.0.2.2) o en la nube
    private val baseUrl = "http://10.0.2.2:8080/api/"

    private val database: GymDatabase by lazy {
        GymDatabase.getDatabase(context)
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val retrofitService: GymApiService by lazy {
        retrofit.create(GymApiService::class.java)
    }

    override val gymRepository: GymRepository by lazy {
        LocalGymRepository(
            userDao = database.userDao(),
            membershipDao = database.membershipDao(),
            routineDao = database.routineDao(),
            storeDao = database.storeDao(),
            notificationDao = database.notificationDao(),
            mediaAssetDao = database.mediaAssetDao()
        )
    }

    override val networkGymRepository: RemoteGymRepository by lazy {
        NetworkGymRepository(retrofitService)
    }
}
