package com.example.proyectogym.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Definición de endpoints REST con Retrofit (Agregar, Editar, Ver, Eliminar, Listar y Recuperar).
 * Preparada para conectarse al backend propio en el siguiente entregable.
 */
interface GymApiService {

    // Autenticación y recuperación
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): UserDto

    @POST("auth/recover")
    suspend fun recoverPassword(@Body request: PasswordRecoveryDto): ApiMessageDto

    // Usuarios (Listar, Ver, Agregar, Editar, Eliminar)
    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): UserDto

    @POST("users")
    suspend fun createUser(@Body user: UserDto): UserDto

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserDto): UserDto

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): ApiMessageDto

    // Servicios
    @GET("services")
    suspend fun getServices(): List<ServiceDto>

    // Rutinas (Listar, Ver, Agregar, Editar, Eliminar)
    @GET("routines")
    suspend fun getRoutines(): List<RoutineDto>

    @GET("routines/{id}")
    suspend fun getRoutineById(@Path("id") id: Int): RoutineDto

    @POST("routines")
    suspend fun createRoutine(@Body routine: RoutineDto): RoutineDto

    @PUT("routines/{id}")
    suspend fun updateRoutine(@Path("id") id: Int, @Body routine: RoutineDto): RoutineDto

    @DELETE("routines/{id}")
    suspend fun deleteRoutine(@Path("id") id: Int): ApiMessageDto

    // Productos (Listar, Agregar, Editar, Eliminar)
    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @POST("products")
    suspend fun createProduct(@Body product: ProductDto): ProductDto

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: ProductDto): ProductDto

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): ApiMessageDto

    // Notas de progreso (Listar, Agregar, Eliminar)
    @GET("users/{userId}/progress")
    suspend fun getProgressNotes(@Path("userId") userId: Int): List<ProgressNoteDto>

    @POST("progress")
    suspend fun createProgressNote(@Body note: ProgressNoteDto): ProgressNoteDto

    @DELETE("progress/{id}")
    suspend fun deleteProgressNote(@Path("id") id: Int): ApiMessageDto
}
