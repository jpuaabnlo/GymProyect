package com.example.proyectogym.data.repository

import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.MediaAssetEntity
import com.example.proyectogym.data.local.entity.MembershipEntity
import com.example.proyectogym.data.local.entity.NotificationEntity
import com.example.proyectogym.data.local.entity.ProductEntity
import com.example.proyectogym.data.local.entity.ProgressNoteEntity
import com.example.proyectogym.data.local.entity.RoutineEntity
import com.example.proyectogym.data.local.entity.RoutineExerciseEntity
import com.example.proyectogym.data.local.entity.SaleEntity
import com.example.proyectogym.data.local.entity.ServiceEntity
import com.example.proyectogym.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface GymRepository {
    suspend fun syncFromRemote()

    suspend fun login(email: String, password: String): UserEntity?
    fun observeUserById(id: Int): Flow<UserEntity?>
    suspend fun getUserById(id: Int): UserEntity?
    fun observeAllUsers(): Flow<List<UserEntity>>
    fun observeUsersByRole(rol: String): Flow<List<UserEntity>>
    fun observeClientsByCoach(coachId: Int): Flow<List<UserEntity>>
    suspend fun insertUser(user: UserEntity): Long
    suspend fun updateUser(user: UserEntity)
    suspend fun deleteUser(user: UserEntity)

    fun observeAllServices(): Flow<List<ServiceEntity>>
    suspend fun insertService(service: ServiceEntity): Long
    suspend fun updateService(service: ServiceEntity)
    suspend fun deleteService(service: ServiceEntity)

    fun observeAllMemberships(): Flow<List<MembershipEntity>>
    fun observeLatestMembershipForUser(userId: Int): Flow<MembershipEntity?>
    suspend fun insertMembership(membership: MembershipEntity): Long
    suspend fun updateMembership(membership: MembershipEntity)
    suspend fun deleteMembership(membership: MembershipEntity)

    fun observeAttendancesByUser(userId: Int): Flow<List<AttendanceEntity>>
    fun observeAllAttendances(): Flow<List<AttendanceEntity>>
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    fun observeAllRoutines(): Flow<List<RoutineEntity>>
    fun observeRoutinesByCoach(coachId: Int): Flow<List<RoutineEntity>>
    fun observeActiveRoutineForClient(clienteId: Int): Flow<RoutineEntity?>
    suspend fun getRoutineById(routineId: Int): RoutineEntity?
    suspend fun insertRoutine(routine: RoutineEntity): Long
    suspend fun updateRoutine(routine: RoutineEntity)
    suspend fun deleteRoutine(routine: RoutineEntity)

    fun observeExercisesForRoutine(routineId: Int): Flow<List<RoutineExerciseEntity>>
    suspend fun getExercisesForRoutineOnce(routineId: Int): List<RoutineExerciseEntity>
    suspend fun insertExercise(exercise: RoutineExerciseEntity): Long
    suspend fun updateExercise(exercise: RoutineExerciseEntity)
    suspend fun deleteExercise(exercise: RoutineExerciseEntity)
    suspend fun replaceRoutineExercises(routineId: Int, exercises: List<RoutineExerciseEntity>)

    fun observeProgressNotesByUser(userId: Int): Flow<List<ProgressNoteEntity>>
    suspend fun insertProgressNote(note: ProgressNoteEntity): Long
    suspend fun updateProgressNote(note: ProgressNoteEntity)
    suspend fun deleteProgressNote(note: ProgressNoteEntity)

    fun observeAllProducts(): Flow<List<ProductEntity>>
    suspend fun insertProduct(product: ProductEntity): Long
    suspend fun updateProduct(product: ProductEntity)
    suspend fun deleteProduct(product: ProductEntity)

    fun observeAllSales(): Flow<List<SaleEntity>>
    suspend fun insertSale(sale: SaleEntity): Long

    fun observeNotificationsByUser(userId: Int): Flow<List<NotificationEntity>>
    suspend fun insertNotification(notification: NotificationEntity): Long
    suspend fun updateNotification(notification: NotificationEntity)
    suspend fun deleteNotification(notification: NotificationEntity)

    fun observeMediaByOwner(ownerId: Int, ownerType: String): Flow<List<MediaAssetEntity>>
    suspend fun insertMediaAsset(mediaAsset: MediaAssetEntity): Long
    suspend fun deleteMediaAsset(mediaAsset: MediaAssetEntity)
}
