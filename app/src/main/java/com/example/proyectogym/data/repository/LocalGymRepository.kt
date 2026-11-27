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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class LocalGymRepository(
    private val userDao: UserDao,
    private val membershipDao: MembershipDao,
    private val routineDao: RoutineDao,
    private val storeDao: StoreDao,
    private val notificationDao: NotificationDao,
    private val mediaAssetDao: MediaAssetDao,
    private val networkRepository: RemoteGymRepository
) : GymRepository {

    private val attendanceMutex = Mutex()
    private val syncMutex = Mutex()

    override suspend fun syncFromRemote() {
        if (!syncMutex.tryLock()) return
        try {
            runCatching {
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

                // Si Firebase y Room están vacíos, cargamos datos iniciales de respaldo.
                seedInitialDataIfEmpty()
            }
        } finally {
            syncMutex.unlock()
        }
    }

    private fun relativeDateStr(daysOffset: Int): String {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, daysOffset) }
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    override suspend fun seedInitialDataIfEmpty() {
        val hayUsuarios = userDao.countUsers() > 0

        if (!hayUsuarios) {
            val hoy = relativeDateStr(0)
            val haceDosDias = relativeDateStr(-2)
            val ayer = relativeDateStr(-1)
            val haceQuinceDias = relativeDateStr(-15)
            val enQuinceDias = relativeDateStr(15)
            val enCuatroDias = relativeDateStr(4)
            val haceTreintaDias = relativeDateStr(-30)

            val adminId = insertUser(
                UserEntity(
                    nombre = "Sofía Hernández",
                    email = "admin@gym.com",
                    password = "1234",
                    telefono = "5551002000",
                    rol = "ADMIN",
                    objetivo = "Administración general",
                    horarioHabitual = "08:00",
                    fechaAlta = haceTreintaDias
                )
            ).toInt()

            val coachId = insertUser(
                UserEntity(
                    nombre = "Carlos Ramírez",
                    email = "coach@gym.com",
                    password = "1234",
                    telefono = "5553004000",
                    rol = "ENTRENADOR",
                    objetivo = "Fuerza e Hipertrofia",
                    horarioHabitual = "07:00",
                    fechaAlta = haceTreintaDias
                )
            ).toInt()

            val client1Id = insertUser(
                UserEntity(
                    nombre = "Juan Pérez",
                    email = "cliente@gym.com",
                    password = "1234",
                    telefono = "5555006000",
                    rol = "CLIENTE",
                    objetivo = "Ganar masa muscular",
                    horarioHabitual = "18:30",
                    fechaAlta = haceTreintaDias,
                    coachId = coachId
                )
            ).toInt()

            val client2Id = insertUser(
                UserEntity(
                    nombre = "Ana López",
                    email = "ana@gym.com",
                    password = "1234",
                    telefono = "5557008000",
                    rol = "CLIENTE",
                    objetivo = "Mejorar condición física",
                    horarioHabitual = "19:00",
                    fechaAlta = haceQuinceDias,
                    coachId = coachId
                )
            ).toInt()

            val servPersonalizadoId = seedBaseServicesAndGetPersonalizedId()

            insertMembership(
                MembershipEntity(
                    userId = client1Id,
                    clienteNombre = "Juan Pérez",
                    serviceId = servPersonalizadoId,
                    servicioNombre = "Mensualidad con Entrenador",
                    montoPagado = 1200.0,
                    metodoPago = "TARJETA",
                    fechaInicio = haceQuinceDias,
                    fechaVencimiento = enQuinceDias,
                    diasRestantes = 15,
                    estado = "ACTIVA"
                )
            )

            insertMembership(
                MembershipEntity(
                    userId = client2Id,
                    clienteNombre = "Ana López",
                    serviceId = servPersonalizadoId,
                    servicioNombre = "Mensualidad General",
                    montoPagado = 550.0,
                    metodoPago = "EFECTIVO",
                    fechaInicio = haceTreintaDias,
                    fechaVencimiento = enCuatroDias,
                    diasRestantes = 4,
                    estado = "POR_VENCER"
                )
            )

            insertAttendance(
                AttendanceEntity(
                    userId = client1Id,
                    usuarioNombre = "Juan Pérez",
                    servicioNombre = "Mensualidad con Entrenador",
                    fecha = haceDosDias,
                    hora = "18:30",
                    origen = "QR"
                )
            )
            insertAttendance(
                AttendanceEntity(
                    userId = client2Id,
                    usuarioNombre = "Ana López",
                    servicioNombre = "Mensualidad General",
                    fecha = ayer,
                    hora = "19:00",
                    origen = "MANUAL"
                )
            )

            val todayName = AttendanceCooldownHelper.getTodaySpanishName()
            val routine1Id = insertRoutine(
                RoutineEntity(
                    coachId = coachId,
                    clienteId = client1Id,
                    clienteNombre = "Juan Pérez",
                    nombre = "Pecho · $todayName",
                    grupoMuscular = "Pecho",
                    objetivo = "Hipertrofia",
                    diasSemana = todayName
                )
            ).toInt()

            insertExercise(
                RoutineExerciseEntity(
                    routineId = routine1Id,
                    nombre = "Press de Banca plana",
                    grupoMuscular = "Pecho",
                    series = 4,
                    repeticiones = 10,
                    pesoSugeridoKg = 60.0,
                    completado = true
                )
            )
            insertExercise(
                RoutineExerciseEntity(
                    routineId = routine1Id,
                    nombre = "Remo con barra",
                    grupoMuscular = "Espalda",
                    series = 4,
                    repeticiones = 12,
                    pesoSugeridoKg = 45.0,
                    completado = false
                )
            )
            insertExercise(
                RoutineExerciseEntity(
                    routineId = routine1Id,
                    nombre = "Curl de bíceps",
                    grupoMuscular = "Brazo",
                    series = 3,
                    repeticiones = 15,
                    pesoSugeridoKg = 14.0,
                    completado = false
                )
            )

            insertProgressNote(
                ProgressNoteEntity(
                    userId = client1Id,
                    titulo = "Evaluación inicial",
                    pesoCorporalKg = 76.0,
                    indiceGrasaPct = 21.5,
                    masaMuscularKg = 33.2,
                    imc = 24.8,
                    comentario = "Inicio de etapa de recomposición.",
                    fecha = haceTreintaDias
                )
            )
            insertProgressNote(
                ProgressNoteEntity(
                    userId = client1Id,
                    titulo = "Control quincenal",
                    pesoCorporalKg = 75.2,
                    indiceGrasaPct = 20.1,
                    masaMuscularKg = 34.0,
                    imc = 24.5,
                    comentario = "Mejoría en masa magra y energía.",
                    fecha = haceQuinceDias
                )
            )
            insertProgressNote(
                ProgressNoteEntity(
                    userId = client1Id,
                    titulo = "Medición reciente",
                    pesoCorporalKg = 74.5,
                    indiceGrasaPct = 18.8,
                    masaMuscularKg = 34.9,
                    imc = 24.2,
                    comentario = "Buena adherencia a rutina y dieta.",
                    fecha = haceDosDias
                )
            )

            insertProduct(
                ProductEntity(
                    nombre = "Proteína Whey 2 lb",
                    categoria = "Suplementos",
                    precio = 780.0,
                    stock = 8
                )
            )
            insertProduct(
                ProductEntity(
                    nombre = "Creatina Monohidratada 300g",
                    categoria = "Suplementos",
                    precio = 450.0,
                    stock = 12
                )
            )
            insertProduct(
                ProductEntity(
                    nombre = "Agua Natural 1 Litro",
                    categoria = "Bebidas",
                    precio = 20.0,
                    stock = 30
                )
            )
            insertProduct(
                ProductEntity(
                    nombre = "Guantes de Entrenamiento",
                    categoria = "Accesorios",
                    precio = 220.0,
                    stock = 6
                )
            )

            insertSale(
                SaleEntity(
                    adminId = adminId,
                    resumenProductos = "1x Creatina Monohidratada 300g, 1x Agua Natural 1 Litro",
                    total = 470.0,
                    metodoPago = "EFECTIVO",
                    fecha = hoy
                )
            )
        } else if (membershipDao.countServices() == 0) {
            seedBaseServicesAndGetPersonalizedId()
        }
    }

    private suspend fun seedBaseServicesAndGetPersonalizedId(): Int {
        insertService(
            ServiceEntity(
                nombre = "Mensualidad General",
                tipo = "MENSUAL",
                precio = 550.0,
                duracionDias = 30,
                horario = "06:00 - 22:00"
            )
        )

        val servPersonalizadoId = insertService(
            ServiceEntity(
                nombre = "Mensualidad con Entrenador",
                tipo = "PERSONALIZADO",
                precio = 1200.0,
                duracionDias = 30,
                horario = "07:00 - 21:00"
            )
        ).toInt()

        insertService(
            ServiceEntity(
                nombre = "Clase Bicicleta / Spinning",
                tipo = "CLASE",
                precio = 450.0,
                duracionDias = 30,
                horario = "Lun-Vie 19:00"
            )
        )

        insertService(
            ServiceEntity(
                nombre = "Visita por 1 Día",
                tipo = "VISITA",
                precio = 80.0,
                duracionDias = 1,
                horario = "Horario libre"
            )
        )

        return servPersonalizadoId
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

    override suspend fun registerAttendanceWithCooldown(
        attendance: AttendanceEntity
    ): AttendanceRegistrationResult = attendanceMutex.withLock {
        val existing = membershipDao.getAttendancesByUserOnce(attendance.userId)
        val remaining = AttendanceCooldownHelper.remainingCooldownMinutes(existing)
        if (remaining > 0L) {
            return@withLock AttendanceRegistrationResult.CooldownActive(remaining)
        }

        val now = Date()
        val fechaStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(now)
        val horaFull = SimpleDateFormat("HH:mm:ss", Locale.US).format(now)
        val nuevaAsistencia = attendance.copy(fecha = fechaStr, hora = horaFull)

        val id = membershipDao.insertAttendance(nuevaAsistencia)
        val saved = if (nuevaAsistencia.id == 0) nuevaAsistencia.copy(id = id.toInt()) else nuevaAsistencia
        networkRepository.saveAttendance(saved.toDto())

        AttendanceRegistrationResult.Success(horaFull.take(5))
    }

    override fun observeAllRoutines(): Flow<List<RoutineEntity>> = routineDao.observeAllRoutines()
    override fun observeRoutinesByCoach(coachId: Int): Flow<List<RoutineEntity>> =
        routineDao.observeRoutinesByCoach(coachId)
    override fun observeRoutinesForClient(clienteId: Int): Flow<List<RoutineEntity>> =
        routineDao.observeRoutinesForClient(clienteId)
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
