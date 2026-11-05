package com.example.proyectogym.data.repository

import com.example.proyectogym.data.remote.ApiMessageDto
import com.example.proyectogym.data.remote.GymApiService
import com.example.proyectogym.data.remote.LoginRequestDto
import com.example.proyectogym.data.remote.PasswordRecoveryDto
import com.example.proyectogym.data.remote.ProductDto
import com.example.proyectogym.data.remote.ProgressNoteDto
import com.example.proyectogym.data.remote.RoutineDto
import com.example.proyectogym.data.remote.ServiceDto
import com.example.proyectogym.data.remote.UserDto

/**
 * Contrato del Repositorio Remoto (Network Repository).
 */
interface RemoteGymRepository {
    suspend fun login(email: String, password: String): Result<UserDto>
    suspend fun recoverPassword(email: String): Result<ApiMessageDto>

    suspend fun getUsers(): Result<List<UserDto>>
    suspend fun getUserById(id: Int): Result<UserDto>
    suspend fun createUser(user: UserDto): Result<UserDto>
    suspend fun updateUser(id: Int, user: UserDto): Result<UserDto>
    suspend fun deleteUser(id: Int): Result<ApiMessageDto>

    suspend fun getServices(): Result<List<ServiceDto>>

    suspend fun getRoutines(): Result<List<RoutineDto>>
    suspend fun getRoutineById(id: Int): Result<RoutineDto>
    suspend fun createRoutine(routine: RoutineDto): Result<RoutineDto>
    suspend fun updateRoutine(id: Int, routine: RoutineDto): Result<RoutineDto>
    suspend fun deleteRoutine(id: Int): Result<ApiMessageDto>

    suspend fun getProducts(): Result<List<ProductDto>>
    suspend fun createProduct(product: ProductDto): Result<ProductDto>
    suspend fun updateProduct(id: Int, product: ProductDto): Result<ProductDto>
    suspend fun deleteProduct(id: Int): Result<ApiMessageDto>

    suspend fun getProgressNotes(userId: Int): Result<List<ProgressNoteDto>>
    suspend fun createProgressNote(note: ProgressNoteDto): Result<ProgressNoteDto>
    suspend fun deleteProgressNote(id: Int): Result<ApiMessageDto>
}

/**
 * Implementación de Network Repository con Retrofit (estilo Codelab Mars Photos).
 * Envuelve las llamadas con runCatching para que no provoquen cierres inesperados
 * mientras el proyecto opera en modo 100% local sin backend desplegado.
 */
class NetworkGymRepository(
    private val gymApiService: GymApiService
) : RemoteGymRepository {

    override suspend fun login(email: String, password: String): Result<UserDto> =
        runCatching { gymApiService.login(LoginRequestDto(email, password)) }

    override suspend fun recoverPassword(email: String): Result<ApiMessageDto> =
        runCatching { gymApiService.recoverPassword(PasswordRecoveryDto(email)) }

    override suspend fun getUsers(): Result<List<UserDto>> =
        runCatching { gymApiService.getUsers() }

    override suspend fun getUserById(id: Int): Result<UserDto> =
        runCatching { gymApiService.getUserById(id) }

    override suspend fun createUser(user: UserDto): Result<UserDto> =
        runCatching { gymApiService.createUser(user) }

    override suspend fun updateUser(id: Int, user: UserDto): Result<UserDto> =
        runCatching { gymApiService.updateUser(id, user) }

    override suspend fun deleteUser(id: Int): Result<ApiMessageDto> =
        runCatching { gymApiService.deleteUser(id) }

    override suspend fun getServices(): Result<List<ServiceDto>> =
        runCatching { gymApiService.getServices() }

    override suspend fun getRoutines(): Result<List<RoutineDto>> =
        runCatching { gymApiService.getRoutines() }

    override suspend fun getRoutineById(id: Int): Result<RoutineDto> =
        runCatching { gymApiService.getRoutineById(id) }

    override suspend fun createRoutine(routine: RoutineDto): Result<RoutineDto> =
        runCatching { gymApiService.createRoutine(routine) }

    override suspend fun updateRoutine(id: Int, routine: RoutineDto): Result<RoutineDto> =
        runCatching { gymApiService.updateRoutine(id, routine) }

    override suspend fun deleteRoutine(id: Int): Result<ApiMessageDto> =
        runCatching { gymApiService.deleteRoutine(id) }

    override suspend fun getProducts(): Result<List<ProductDto>> =
        runCatching { gymApiService.getProducts() }

    override suspend fun createProduct(product: ProductDto): Result<ProductDto> =
        runCatching { gymApiService.createProduct(product) }

    override suspend fun updateProduct(id: Int, product: ProductDto): Result<ProductDto> =
        runCatching { gymApiService.updateProduct(id, product) }

    override suspend fun deleteProduct(id: Int): Result<ApiMessageDto> =
        runCatching { gymApiService.deleteProduct(id) }

    override suspend fun getProgressNotes(userId: Int): Result<List<ProgressNoteDto>> =
        runCatching { gymApiService.getProgressNotes(userId) }

    override suspend fun createProgressNote(note: ProgressNoteDto): Result<ProgressNoteDto> =
        runCatching { gymApiService.createProgressNote(note) }

    override suspend fun deleteProgressNote(id: Int): Result<ApiMessageDto> =
        runCatching { gymApiService.deleteProgressNote(id) }
}
