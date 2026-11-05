package com.example.proyectogym.data.repository

import com.example.proyectogym.data.local.dao.MediaAssetDao
import com.example.proyectogym.data.local.dao.MembershipDao
import com.example.proyectogym.data.local.dao.NotificationDao
import com.example.proyectogym.data.local.dao.RoutineDao
import com.example.proyectogym.data.local.dao.StoreDao
import com.example.proyectogym.data.local.dao.UserDao
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
import com.example.proyectogym.data.remote.toDto
import com.example.proyectogym.data.remote.toEntity
import kotlinx.coroutines.flow.Flow

class LocalGymRepository(
    private val userDao: UserDao,
    private val membershipDao: MembershipDao,
    private val routineDao: RoutineDao,
    private val storeDao: StoreDao,
    private val notificationDao: NotificationDao,
    private val mediaAssetDao: MediaAssetDao,
    private val networkRepository: RemoteGymRepository
) : GymRepository {

    override suspend fun syncFromRemote() {
        networkRepository.getUsers().onSuccess { list ->
            list.forEach { userDao.insertUser(it.toEntity()) }
        }
        networkRepository.getServices().onSuccess { list ->
            list.forEach { membershipDao.insertService(it.toEntity()) }
        }
        networkRepository.getMemberships().onSuccess { list ->
            list.forEach { membershipDao.insertMembership(it.toEntity()) }
        }
        networkRepository.getAttendances().onSuccess { list ->
            list.forEach { membershipDao.insertAttendance(it.toEntity()) }
        }
        networkRepository.getRoutines().onSuccess { list ->
            list.forEach { routineDao.insertRoutine(it.toEntity()) }
        }
        networkRepository.getRoutineExercises().onSuccess { list ->
            list.forEach { routineDao.insertExercise(it.toEntity()) }
        }
        networkRepository.getProgressNotes().onSuccess { list ->
            list.forEach { routineDao.insertProgressNote(it.toEntity()) }
        }
        networkRepository.getProducts().onSuccess { list ->
            list.forEach { storeDao.insertProduct(it.toEntity()) }
        }
        networkRepository.getSales().onSuccess { list ->
            list.forEach { storeDao.insertSale(it.toEntity()) }
        }
        networkRepository.getNotifications().onSuccess { list ->
            list.forEach { notificationDao.insertNotification(it.toEntity()) }
        }
        networkRepository.getMediaAssets().onSuccess { list ->
            list.forEach { mediaAssetDao.insertMediaAsset(it.toEntity()) }
        }
    }

    override suspend fun login(email: String, password: String): UserEntity? =
        userDao.login(email.trim(), password.trim())

    override fun observeUserById(id: Int): Flow<UserEntity?> = userDao.observeUserById(id)
    override suspend fun getUserById(id: Int): UserEntity? = userDao.getUserById(id)
    override fun observeAllUsers(): Flow<List<UserEntity>> = userDao.observeAllUsers()
    override fun observeUsersByRole(rol: String): Flow<List<UserEntity>> = userDao.observeUsersByRole(rol)
    override fun observeClientsByCoach(coachId: Int): Flow<List<UserEntity>> = userDao.observeClientsByCoach(coachId)

    override suspend fun insertUser(user: UserEntity): Long {
        val id = userDao.insertUser(user)
        val saved = if (user.id == 0) user.copy(id = id.toInt()) else user
        networkRepository.saveUser(saved.toDto())
        return id
    }

    override suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
        networkRepository.saveUser(user.toDto())
    }

    override suspend fun deleteUser(user: UserEntity) {
        userDao.deleteUser(user)
        networkRepository.deleteUser(user.id)
    }

    override fun observeAllServices(): Flow<List<ServiceEntity>> = membershipDao.observeAllServices()

    override suspend fun insertService(service: ServiceEntity): Long {
        val id = membershipDao.insertService(service)
        val saved = if (service.id == 0) service.copy(id = id.toInt()) else service
        networkRepository.saveService(saved.toDto())
        return id
    }

    override suspend fun updateService(service: ServiceEntity) {
        membershipDao.updateService(service)
        networkRepository.saveService(service.toDto())
    }

    override suspend fun deleteService(service: ServiceEntity) {
        membershipDao.deleteService(service)
        networkRepository.deleteService(service.id)
    }

    override fun observeAllMemberships(): Flow<List<MembershipEntity>> = membershipDao.observeAllMemberships()
    override fun observeLatestMembershipForUser(userId: Int): Flow<MembershipEntity?> =
        membershipDao.observeLatestMembershipForUser(userId)

    override suspend fun insertMembership(membership: MembershipEntity): Long {
        val id = membershipDao.insertMembership(membership)
        val saved = if (membership.id == 0) membership.copy(id = id.toInt()) else membership
        networkRepository.saveMembership(saved.toDto())
        return id
    }

    override suspend fun updateMembership(membership: MembershipEntity) {
        membershipDao.updateMembership(membership)
        networkRepository.saveMembership(membership.toDto())
    }

    override suspend fun deleteMembership(membership: MembershipEntity) {
        membershipDao.deleteMembership(membership)
        networkRepository.deleteMembership(membership.id)
    }

    override fun observeAttendancesByUser(userId: Int): Flow<List<AttendanceEntity>> =
        membershipDao.observeAttendancesByUser(userId)
    override fun observeAllAttendances(): Flow<List<AttendanceEntity>> =
        membershipDao.observeAllAttendances()

    override suspend fun insertAttendance(attendance: AttendanceEntity): Long {
        val id = membershipDao.insertAttendance(attendance)
        val saved = if (attendance.id == 0) attendance.copy(id = id.toInt()) else attendance
        networkRepository.saveAttendance(saved.toDto())
        return id
    }

    override fun observeAllRoutines(): Flow<List<RoutineEntity>> = routineDao.observeAllRoutines()
    override fun observeRoutinesByCoach(coachId: Int): Flow<List<RoutineEntity>> =
        routineDao.observeRoutinesByCoach(coachId)
    override fun observeActiveRoutineForClient(clienteId: Int): Flow<RoutineEntity?> =
        routineDao.observeActiveRoutineForClient(clienteId)
    override suspend fun getRoutineById(routineId: Int): RoutineEntity? =
        routineDao.getRoutineById(routineId)

    override suspend fun insertRoutine(routine: RoutineEntity): Long {
        val id = routineDao.insertRoutine(routine)
        val saved = if (routine.id == 0) routine.copy(id = id.toInt()) else routine
        networkRepository.saveRoutine(saved.toDto())
        return id
    }

    override suspend fun updateRoutine(routine: RoutineEntity) {
        routineDao.updateRoutine(routine)
        networkRepository.saveRoutine(routine.toDto())
    }

    override suspend fun deleteRoutine(routine: RoutineEntity) {
        val ejerciciosPrevios = routineDao.getExercisesForRoutineOnce(routine.id)
        routineDao.deleteExercisesByRoutineId(routine.id)
        routineDao.deleteRoutine(routine)
        ejerciciosPrevios.forEach { networkRepository.deleteRoutineExercise(it.id) }
        networkRepository.deleteRoutine(routine.id)
    }

    override fun observeExercisesForRoutine(routineId: Int): Flow<List<RoutineExerciseEntity>> =
        routineDao.observeExercisesForRoutine(routineId)
    override suspend fun getExercisesForRoutineOnce(routineId: Int): List<RoutineExerciseEntity> =
        routineDao.getExercisesForRoutineOnce(routineId)

    override suspend fun insertExercise(exercise: RoutineExerciseEntity): Long {
        val id = routineDao.insertExercise(exercise)
        val saved = if (exercise.id == 0) exercise.copy(id = id.toInt()) else exercise
        networkRepository.saveRoutineExercise(saved.toDto())
        return id
    }

    override suspend fun updateExercise(exercise: RoutineExerciseEntity) {
        routineDao.updateExercise(exercise)
        networkRepository.saveRoutineExercise(exercise.toDto())
    }

    override suspend fun deleteExercise(exercise: RoutineExerciseEntity) {
        routineDao.deleteExercise(exercise)
        networkRepository.deleteRoutineExercise(exercise.id)
    }

    override suspend fun replaceRoutineExercises(
        routineId: Int,
        exercises: List<RoutineExerciseEntity>
    ) {
        val previos = routineDao.getExercisesForRoutineOnce(routineId)
        routineDao.deleteExercisesByRoutineId(routineId)
        previos.forEach { networkRepository.deleteRoutineExercise(it.id) }

        exercises.forEach { exercise ->
            val cleanExercise = exercise.copy(id = 0, routineId = routineId)
            val newId = routineDao.insertExercise(cleanExercise).toInt()
            networkRepository.saveRoutineExercise(cleanExercise.copy(id = newId).toDto())
        }
    }

    override fun observeProgressNotesByUser(userId: Int): Flow<List<ProgressNoteEntity>> =
        routineDao.observeProgressNotesByUser(userId)

    override suspend fun insertProgressNote(note: ProgressNoteEntity): Long {
        val id = routineDao.insertProgressNote(note)
        val saved = if (note.id == 0) note.copy(id = id.toInt()) else note
        networkRepository.saveProgressNote(saved.toDto())
        return id
    }

    override suspend fun updateProgressNote(note: ProgressNoteEntity) {
        routineDao.updateProgressNote(note)
        networkRepository.saveProgressNote(note.toDto())
    }

    override suspend fun deleteProgressNote(note: ProgressNoteEntity) {
        routineDao.deleteProgressNote(note)
        networkRepository.deleteProgressNote(note.id)
    }

    override fun observeAllProducts(): Flow<List<ProductEntity>> = storeDao.observeAllProducts()

    override suspend fun insertProduct(product: ProductEntity): Long {
        val id = storeDao.insertProduct(product)
        val saved = if (product.id == 0) product.copy(id = id.toInt()) else product
        networkRepository.saveProduct(saved.toDto())
        return id
    }

    override suspend fun updateProduct(product: ProductEntity) {
        storeDao.updateProduct(product)
        networkRepository.saveProduct(product.toDto())
    }

    override suspend fun deleteProduct(product: ProductEntity) {
        storeDao.deleteProduct(product)
        networkRepository.deleteProduct(product.id)
    }

    override fun observeAllSales(): Flow<List<SaleEntity>> = storeDao.observeAllSales()

    override suspend fun insertSale(sale: SaleEntity): Long {
        val id = storeDao.insertSale(sale)
        val saved = if (sale.id == 0) sale.copy(id = id.toInt()) else sale
        networkRepository.saveSale(saved.toDto())
        return id
    }

    override fun observeNotificationsByUser(userId: Int): Flow<List<NotificationEntity>> =
        notificationDao.observeNotificationsByUser(userId)

    override suspend fun insertNotification(notification: NotificationEntity): Long {
        val id = notificationDao.insertNotification(notification)
        val saved = if (notification.id == 0) notification.copy(id = id.toInt()) else notification
        networkRepository.saveNotification(saved.toDto())
        return id
    }

    override suspend fun updateNotification(notification: NotificationEntity) {
        notificationDao.updateNotification(notification)
        networkRepository.saveNotification(notification.toDto())
    }

    override suspend fun deleteNotification(notification: NotificationEntity) {
        notificationDao.deleteNotification(notification)
        networkRepository.deleteNotification(notification.id)
    }

    override fun observeMediaByOwner(ownerId: Int, ownerType: String): Flow<List<MediaAssetEntity>> =
        mediaAssetDao.observeMediaByOwner(ownerId, ownerType)

    override suspend fun insertMediaAsset(mediaAsset: MediaAssetEntity): Long {
        val id = mediaAssetDao.insertMediaAsset(mediaAsset)
        val saved = if (mediaAsset.id == 0) mediaAsset.copy(id = id.toInt()) else mediaAsset
        networkRepository.saveMediaAsset(saved.toDto())
        return id
    }

    override suspend fun deleteMediaAsset(mediaAsset: MediaAssetEntity) {
        mediaAssetDao.deleteMediaAsset(mediaAsset)
        networkRepository.deleteMediaAsset(mediaAsset.id)
    }
}
