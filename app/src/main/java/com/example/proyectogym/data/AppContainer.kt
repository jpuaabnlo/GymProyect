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

interface AppContainer {
    val gymRepository: GymRepository
    val networkGymRepository: RemoteGymRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    private val baseUrl = "https://appfb-25327.firebaseio.com/"

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

    override val networkGymRepository: RemoteGymRepository by lazy {
        NetworkGymRepository(retrofitService)
    }

    override val gymRepository: GymRepository by lazy {
        LocalGymRepository(
            userDao = database.userDao(),
            membershipDao = database.membershipDao(),
            routineDao = database.routineDao(),
            storeDao = database.storeDao(),
            notificationDao = database.notificationDao(),
            mediaAssetDao = database.mediaAssetDao(),
            networkRepository = networkGymRepository
        )
    }
}
