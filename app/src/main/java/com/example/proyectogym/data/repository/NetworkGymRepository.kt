package com.example.proyectogym.data.repository

import com.example.proyectogym.data.remote.AttendanceDto
import com.example.proyectogym.data.remote.GymApiService
import com.example.proyectogym.data.remote.MediaAssetDto
import com.example.proyectogym.data.remote.MembershipDto
import com.example.proyectogym.data.remote.NotificationDto
import com.example.proyectogym.data.remote.ProductDto
import com.example.proyectogym.data.remote.ProgressNoteDto
import com.example.proyectogym.data.remote.RoutineDto
import com.example.proyectogym.data.remote.RoutineExerciseDto
import com.example.proyectogym.data.remote.SaleDto
import com.example.proyectogym.data.remote.ServiceDto
import com.example.proyectogym.data.remote.UserDto

interface RemoteGymRepository {
    suspend fun findUserByEmail(email: String): Result<UserDto?>

    suspend fun getUsers(): Result<List<UserDto>>
    suspend fun saveUser(user: UserDto): Result<UserDto>
    suspend fun deleteUser(id: Int): Result<Unit>

    suspend fun getServices(): Result<List<ServiceDto>>
    suspend fun saveService(service: ServiceDto): Result<ServiceDto>
    suspend fun deleteService(id: Int): Result<Unit>

    suspend fun getMemberships(): Result<List<MembershipDto>>
    suspend fun saveMembership(membership: MembershipDto): Result<MembershipDto>
    suspend fun deleteMembership(id: Int): Result<Unit>

    suspend fun getAttendances(): Result<List<AttendanceDto>>
    suspend fun saveAttendance(attendance: AttendanceDto): Result<AttendanceDto>

    suspend fun getRoutines(): Result<List<RoutineDto>>
    suspend fun saveRoutine(routine: RoutineDto): Result<RoutineDto>
    suspend fun deleteRoutine(id: Int): Result<Unit>

    suspend fun getRoutineExercises(): Result<List<RoutineExerciseDto>>
    suspend fun saveRoutineExercise(exercise: RoutineExerciseDto): Result<RoutineExerciseDto>
    suspend fun deleteRoutineExercise(id: Int): Result<Unit>

    suspend fun getProgressNotes(): Result<List<ProgressNoteDto>>
    suspend fun saveProgressNote(note: ProgressNoteDto): Result<ProgressNoteDto>
    suspend fun deleteProgressNote(id: Int): Result<Unit>

    suspend fun getProducts(): Result<List<ProductDto>>
    suspend fun saveProduct(product: ProductDto): Result<ProductDto>
    suspend fun deleteProduct(id: Int): Result<Unit>

    suspend fun getSales(): Result<List<SaleDto>>
    suspend fun saveSale(sale: SaleDto): Result<SaleDto>

    suspend fun getNotifications(): Result<List<NotificationDto>>
    suspend fun saveNotification(notification: NotificationDto): Result<NotificationDto>
    suspend fun deleteNotification(id: Int): Result<Unit>

    suspend fun getMediaAssets(): Result<List<MediaAssetDto>>
    suspend fun saveMediaAsset(mediaAsset: MediaAssetDto): Result<MediaAssetDto>
    suspend fun deleteMediaAsset(id: Int): Result<Unit>
}

class NetworkGymRepository(
    private val gymApiService: GymApiService
) : RemoteGymRepository {

    override suspend fun findUserByEmail(email: String): Result<UserDto?> =
        runCatching {
            gymApiService.getUsers()?.values?.firstOrNull {
                it.email.equals(email.trim(), ignoreCase = true)
            }
        }

    override suspend fun getUsers(): Result<List<UserDto>> =
        runCatching { gymApiService.getUsers()?.values?.toList().orEmpty() }

    override suspend fun saveUser(user: UserDto): Result<UserDto> =
        runCatching { gymApiService.saveUser(user.id, user) }

    override suspend fun deleteUser(id: Int): Result<Unit> =
        runCatching { gymApiService.deleteUser(id); Unit }

    override suspend fun getServices(): Result<List<ServiceDto>> =
        runCatching { gymApiService.getServices()?.values?.toList().orEmpty() }

    override suspend fun saveService(service: ServiceDto): Result<ServiceDto> =
        runCatching { gymApiService.saveService(service.id, service) }

    override suspend fun deleteService(id: Int): Result<Unit> =
        runCatching { gymApiService.deleteService(id); Unit }

    override suspend fun getMemberships(): Result<List<MembershipDto>> =
        runCatching { gymApiService.getMemberships()?.values?.toList().orEmpty() }

    override suspend fun saveMembership(membership: MembershipDto): Result<MembershipDto> =
        runCatching { gymApiService.saveMembership(membership.id, membership) }

    override suspend fun deleteMembership(id: Int): Result<Unit> =
        runCatching { gymApiService.deleteMembership(id); Unit }

    override suspend fun getAttendances(): Result<List<AttendanceDto>> =
        runCatching { gymApiService.getAttendances()?.values?.toList().orEmpty() }

    override suspend fun saveAttendance(attendance: AttendanceDto): Result<AttendanceDto> =
        runCatching { gymApiService.saveAttendance(attendance.id, attendance) }

    override suspend fun getRoutines(): Result<List<RoutineDto>> =
        runCatching { gymApiService.getRoutines()?.values?.toList().orEmpty() }

    override suspend fun saveRoutine(routine: RoutineDto): Result<RoutineDto> =
        runCatching { gymApiService.saveRoutine(routine.id, routine) }

    override suspend fun deleteRoutine(id: Int): Result<Unit> =
        runCatching { gymApiService.deleteRoutine(id); Unit }

    override suspend fun getRoutineExercises(): Result<List<RoutineExerciseDto>> =
        runCatching { gymApiService.getRoutineExercises()?.values?.toList().orEmpty() }

    override suspend fun saveRoutineExercise(exercise: RoutineExerciseDto): Result<RoutineExerciseDto> =
        runCatching { gymApiService.saveRoutineExercise(exercise.id, exercise) }

    override suspend fun deleteRoutineExercise(id: Int): Result<Unit> =
        runCatching { gymApiService.deleteRoutineExercise(id); Unit }

    override suspend fun getProgressNotes(): Result<List<ProgressNoteDto>> =
        runCatching { gymApiService.getProgressNotes()?.values?.toList().orEmpty() }

    override suspend fun saveProgressNote(note: ProgressNoteDto): Result<ProgressNoteDto> =
        runCatching { gymApiService.saveProgressNote(note.id, note) }

    override suspend fun deleteProgressNote(id: Int): Result<Unit> =
        runCatching { gymApiService.deleteProgressNote(id); Unit }

    override suspend fun getProducts(): Result<List<ProductDto>> =
        runCatching { gymApiService.getProducts()?.values?.toList().orEmpty() }

    override suspend fun saveProduct(product: ProductDto): Result<ProductDto> =
        runCatching { gymApiService.saveProduct(product.id, product) }

    override suspend fun deleteProduct(id: Int): Result<Unit> =
        runCatching { gymApiService.deleteProduct(id); Unit }

    override suspend fun getSales(): Result<List<SaleDto>> =
        runCatching { gymApiService.getSales()?.values?.toList().orEmpty() }

    override suspend fun saveSale(sale: SaleDto): Result<SaleDto> =
        runCatching { gymApiService.saveSale(sale.id, sale) }

    override suspend fun getNotifications(): Result<List<NotificationDto>> =
        runCatching { gymApiService.getNotifications()?.values?.toList().orEmpty() }

    override suspend fun saveNotification(notification: NotificationDto): Result<NotificationDto> =
        runCatching { gymApiService.saveNotification(notification.id, notification) }

    override suspend fun deleteNotification(id: Int): Result<Unit> =
        runCatching { gymApiService.deleteNotification(id); Unit }

    override suspend fun getMediaAssets(): Result<List<MediaAssetDto>> =
        runCatching { gymApiService.getMediaAssets()?.values?.toList().orEmpty() }

    override suspend fun saveMediaAsset(mediaAsset: MediaAssetDto): Result<MediaAssetDto> =
        runCatching { gymApiService.saveMediaAsset(mediaAsset.id, mediaAsset) }

    override suspend fun deleteMediaAsset(id: Int): Result<Unit> =
        runCatching { gymApiService.deleteMediaAsset(id); Unit }
}
