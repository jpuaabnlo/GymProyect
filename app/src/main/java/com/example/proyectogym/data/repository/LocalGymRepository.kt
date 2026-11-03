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
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio Local implementado con SQLite - Room.
 * Toda la aplicación lee y escribe en esta fuente de datos local.
 */
class LocalGymRepository(
    private val userDao: UserDao,
    private val membershipDao: MembershipDao,
    private val routineDao: RoutineDao,
    private val storeDao: StoreDao,
    private val notificationDao: NotificationDao,
    private val mediaAssetDao: MediaAssetDao
) : GymRepository {

    override suspend fun seedInitialDataIfEmpty() {
        if (userDao.countUsers() > 0) return

        // 1. Usuarios base para probar los 3 roles fácilmente
        val adminId = userDao.insertUser(
            UserEntity(
                nombre = "Sofía Hernández",
                email = "admin@gym.com",
                password = "1234",
                telefono = "555-100-2000",
                rol = "ADMIN",
                objetivo = "Administración general",
                horarioHabitual = "08:00",
                fechaAlta = "2025-01-10"
            )
        ).toInt()

        val coachId = userDao.insertUser(
            UserEntity(
                nombre = "Carlos Ramírez",
                email = "coach@gym.com",
                password = "1234",
                telefono = "555-300-4000",
                rol = "ENTRENADOR",
                objetivo = "Especialista en Fuerza e Hipertrofia",
                horarioHabitual = "07:00",
                fechaAlta = "2025-02-15"
            )
        ).toInt()

        val client1Id = userDao.insertUser(
            UserEntity(
                nombre = "Juan Pérez",
                email = "cliente@gym.com",
                password = "1234",
                telefono = "555-500-6000",
                rol = "CLIENTE",
                objetivo = "Ganar masa muscular",
                horarioHabitual = "18:30",
                fechaAlta = "2026-03-01",
                coachId = coachId
            )
        ).toInt()

        val client2Id = userDao.insertUser(
            UserEntity(
                nombre = "Ana López",
                email = "ana@gym.com",
                password = "1234",
                telefono = "555-700-8000",
                rol = "CLIENTE",
                objetivo = "Mejorar condición física",
                horarioHabitual = "19:00",
                fechaAlta = "2026-06-12",
                coachId = coachId
            )
        ).toInt()

        // 2. Catálogo de servicios del gimnasio
        val servMensualId = membershipDao.insertService(
            ServiceEntity(
                nombre = "Mensualidad General",
                tipo = "MENSUAL",
                precio = 550.0,
                duracionDias = 30,
                horario = "06:00 - 22:00"
            )
        ).toInt()

        val servPersonalizadoId = membershipDao.insertService(
            ServiceEntity(
                nombre = "Mensualidad Personalizada",
                tipo = "PERSONALIZADO",
                precio = 1200.0,
                duracionDias = 30,
                horario = "07:00 - 21:00"
            )
        ).toInt()

        val servSpinningId = membershipDao.insertService(
            ServiceEntity(
                nombre = "Clase Bicicleta / Spinning",
                tipo = "CLASE",
                precio = 450.0,
                duracionDias = 30,
                horario = "Lun-Vie 19:00"
            )
        ).toInt()

        membershipDao.insertService(
            ServiceEntity(
                nombre = "Visita por 1 Día",
                tipo = "VISITA",
                precio = 80.0,
                duracionDias = 1,
                horario = "Horario libre"
            )
        )

        // 3. Membresías iniciales (una activa y una por vencer para mostrar en Tablero)
        membershipDao.insertMembership(
            MembershipEntity(
                userId = client1Id,
                clienteNombre = "Juan Pérez",
                serviceId = servPersonalizadoId,
                servicioNombre = "Mensualidad Personalizada",
                montoPagado = 1200.0,
                metodoPago = "TARJETA",
                fechaInicio = "2026-10-15",
                fechaVencimiento = "2026-11-14",
                diasRestantes = 17,
                estado = "ACTIVA"
            )
        )

        membershipDao.insertMembership(
            MembershipEntity(
                userId = client2Id,
                clienteNombre = "Ana López",
                serviceId = servSpinningId,
                servicioNombre = "Clase Bicicleta / Spinning",
                montoPagado = 450.0,
                metodoPago = "EFECTIVO",
                fechaInicio = "2026-10-02",
                fechaVencimiento = "2026-11-01",
                diasRestantes = 4,
                estado = "POR_VENCER"
            )
        )

        // 4. Asistencias iniciales
        membershipDao.insertAttendance(
            AttendanceEntity(
                userId = client1Id,
                usuarioNombre = "Juan Pérez",
                servicioNombre = "Mensualidad Personalizada",
                fecha = "2026-10-25",
                hora = "18:30",
                origen = "CHECKIN_APP"
            )
        )
        membershipDao.insertAttendance(
            AttendanceEntity(
                userId = client1Id,
                usuarioNombre = "Juan Pérez",
                servicioNombre = "Mensualidad Personalizada",
                fecha = "2026-10-27",
                hora = "18:35",
                origen = "CHECKIN_APP"
            )
        )
        membershipDao.insertAttendance(
            AttendanceEntity(
                userId = client2Id,
                usuarioNombre = "Ana López",
                servicioNombre = "Clase Bicicleta / Spinning",
                fecha = "2026-10-27",
                hora = "19:00",
                origen = "MANUAL"
            )
        )
        membershipDao.insertAttendance(
            AttendanceEntity(
                userId = coachId,
                usuarioNombre = "Carlos Ramírez",
                servicioNombre = "Mensualidad General",
                fecha = "2026-10-28",
                hora = "07:05",
                origen = "MANUAL"
            )
        )

        // 5. Rutina asignada a Juan Pérez con ejercicios
        val routine1Id = routineDao.insertRoutine(
            RoutineEntity(
                coachId = coachId,
                clienteId = client1Id,
                clienteNombre = "Juan Pérez",
                nombre = "Fuerza Tren Superior",
                objetivo = "Hipertrofia",
                diasSemana = "Lun, Mié, Vie"
            )
        ).toInt()

        routineDao.insertExercise(
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
        routineDao.insertExercise(
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
        routineDao.insertExercise(
            RoutineExerciseEntity(
                routineId = routine1Id,
                nombre = "Press militar con mancuernas",
                grupoMuscular = "Hombro",
                series = 3,
                repeticiones = 12,
                pesoSugeridoKg = 18.0,
                completado = false
            )
        )
        routineDao.insertExercise(
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

        // Segunda rutina asignada a Ana López
        val routine2Id = routineDao.insertRoutine(
            RoutineEntity(
                coachId = coachId,
                clienteId = client2Id,
                clienteNombre = "Ana López",
                nombre = "Pierna y Resistencia",
                objetivo = "Resistencia",
                diasSemana = "Mar, Jue"
            )
        ).toInt()

        routineDao.insertExercise(
            RoutineExerciseEntity(
                routineId = routine2Id,
                nombre = "Sentadilla libre",
                grupoMuscular = "Pierna",
                series = 4,
                repeticiones = 12,
                pesoSugeridoKg = 40.0
            )
        )
        routineDao.insertExercise(
            RoutineExerciseEntity(
                routineId = routine2Id,
                nombre = "Desplantes con mancuerna",
                grupoMuscular = "Pierna",
                series = 3,
                repeticiones = 12,
                pesoSugeridoKg = 12.0
            )
        )

        // 6. Notas de progreso iniciales del cliente
        routineDao.insertProgressNote(
            ProgressNoteEntity(
                userId = client1Id,
                titulo = "Semana 1 - Inicio de rutina",
                pesoCorporalKg = 74.5,
                pesoLevantadoKg = 55.0,
                repeticionesLogradas = 10,
                comentario = "Buena energía en press de banca, cuidar técnica.",
                fecha = "2026-10-18"
            )
        )
        routineDao.insertProgressNote(
            ProgressNoteEntity(
                userId = client1Id,
                titulo = "Semana 2 - Subida de carga",
                pesoCorporalKg = 75.0,
                pesoLevantadoKg = 60.0,
                repeticionesLogradas = 10,
                comentario = "Logré completar las 4 series con 60 kg.",
                fecha = "2026-10-27"
            )
        )

        // 7. Productos para Punto de Venta (Tienda)
        storeDao.insertProduct(
            ProductEntity(
                nombre = "Proteína Whey 2 lb",
                categoria = "Suplementos",
                precio = 780.0,
                stock = 8
            )
        )
        storeDao.insertProduct(
            ProductEntity(
                nombre = "Creatina Monohidratada 300g",
                categoria = "Suplementos",
                precio = 450.0,
                stock = 12
            )
        )
        storeDao.insertProduct(
            ProductEntity(
                nombre = "Pre-Entreno Scoop Individual",
                categoria = "Suplementos",
                precio = 45.0,
                stock = 25
            )
        )
        storeDao.insertProduct(
            ProductEntity(
                nombre = "Agua Natural 1 Litro",
                categoria = "Bebidas",
                precio = 20.0,
                stock = 30
            )
        )
        storeDao.insertProduct(
            ProductEntity(
                nombre = "Guantes de Entrenamiento",
                categoria = "Accesorios",
                precio = 220.0,
                stock = 6
            )
        )

        // 8. Venta inicial registrada
        storeDao.insertSale(
            SaleEntity(
                adminId = adminId,
                resumenProductos = "1x Creatina Monohidratada 300g, 1x Agua Natural 1 Litro",
                total = 470.0,
                metodoPago = "EFECTIVO",
                fecha = "2026-10-28"
            )
        )

        // 9. Notificación y Multimedia semilla (modelos listos en BD)
        notificationDao.insertNotification(
            NotificationEntity(
                userId = client1Id,
                titulo = "Recordatorio de entrenamiento",
                mensaje = "Te toca Fuerza Tren Superior a las 18:30",
                horaProgramada = "18:30",
                tipo = "RECORDATORIO_ENTRENO",
                activa = true,
                leida = false,
                fechaCreacion = "2026-10-28"
            )
        )

        mediaAssetDao.insertMediaAsset(
            MediaAssetEntity(
                ownerId = client1Id,
                ownerType = "PERFIL",
                uriLocal = "content://com.example.proyectogym/avatar_default.png",
                mimeType = "image/png",
                descripcion = "Foto de perfil inicial de Juan Pérez",
                fechaRegistro = "2026-10-28"
            )
        )
    }

    // Usuarios
    override suspend fun login(email: String, password: String): UserEntity? =
        userDao.login(email.trim(), password.trim())

    override fun observeUserById(id: Int): Flow<UserEntity?> = userDao.observeUserById(id)
    override suspend fun getUserById(id: Int): UserEntity? = userDao.getUserById(id)
    override fun observeAllUsers(): Flow<List<UserEntity>> = userDao.observeAllUsers()
    override fun observeUsersByRole(rol: String): Flow<List<UserEntity>> = userDao.observeUsersByRole(rol)
    override fun observeClientsByCoach(coachId: Int): Flow<List<UserEntity>> = userDao.observeClientsByCoach(coachId)
    override suspend fun insertUser(user: UserEntity): Long = userDao.insertUser(user)
    override suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    override suspend fun deleteUser(user: UserEntity) = userDao.deleteUser(user)

    // Servicios, Membresías y Asistencias
    override fun observeAllServices(): Flow<List<ServiceEntity>> = membershipDao.observeAllServices()
    override suspend fun insertService(service: ServiceEntity): Long = membershipDao.insertService(service)
    override suspend fun updateService(service: ServiceEntity) = membershipDao.updateService(service)
    override suspend fun deleteService(service: ServiceEntity) = membershipDao.deleteService(service)

    override fun observeAllMemberships(): Flow<List<MembershipEntity>> = membershipDao.observeAllMemberships()
    override fun observeLatestMembershipForUser(userId: Int): Flow<MembershipEntity?> =
        membershipDao.observeLatestMembershipForUser(userId)
    override suspend fun insertMembership(membership: MembershipEntity): Long =
        membershipDao.insertMembership(membership)
    override suspend fun updateMembership(membership: MembershipEntity) =
        membershipDao.updateMembership(membership)
    override suspend fun deleteMembership(membership: MembershipEntity) =
        membershipDao.deleteMembership(membership)

    override fun observeAttendancesByUser(userId: Int): Flow<List<AttendanceEntity>> =
        membershipDao.observeAttendancesByUser(userId)
    override fun observeAllAttendances(): Flow<List<AttendanceEntity>> =
        membershipDao.observeAllAttendances()
    override suspend fun insertAttendance(attendance: AttendanceEntity): Long =
        membershipDao.insertAttendance(attendance)

    // Rutinas, Ejercicios y Progreso
    override fun observeAllRoutines(): Flow<List<RoutineEntity>> = routineDao.observeAllRoutines()
    override fun observeRoutinesByCoach(coachId: Int): Flow<List<RoutineEntity>> =
        routineDao.observeRoutinesByCoach(coachId)
    override fun observeActiveRoutineForClient(clienteId: Int): Flow<RoutineEntity?> =
        routineDao.observeActiveRoutineForClient(clienteId)
    override suspend fun getRoutineById(routineId: Int): RoutineEntity? =
        routineDao.getRoutineById(routineId)
    override suspend fun insertRoutine(routine: RoutineEntity): Long = routineDao.insertRoutine(routine)
    override suspend fun updateRoutine(routine: RoutineEntity) = routineDao.updateRoutine(routine)
    override suspend fun deleteRoutine(routine: RoutineEntity) {
        routineDao.deleteExercisesByRoutineId(routine.id)
        routineDao.deleteRoutine(routine)
    }

    override fun observeExercisesForRoutine(routineId: Int): Flow<List<RoutineExerciseEntity>> =
        routineDao.observeExercisesForRoutine(routineId)
    override suspend fun getExercisesForRoutineOnce(routineId: Int): List<RoutineExerciseEntity> =
        routineDao.getExercisesForRoutineOnce(routineId)
    override suspend fun insertExercise(exercise: RoutineExerciseEntity): Long =
        routineDao.insertExercise(exercise)
    override suspend fun updateExercise(exercise: RoutineExerciseEntity) =
        routineDao.updateExercise(exercise)
    override suspend fun deleteExercise(exercise: RoutineExerciseEntity) =
        routineDao.deleteExercise(exercise)

    override suspend fun replaceRoutineExercises(
        routineId: Int,
        exercises: List<RoutineExerciseEntity>
    ) {
        routineDao.deleteExercisesByRoutineId(routineId)
        exercises.forEach { exercise ->
            routineDao.insertExercise(exercise.copy(id = 0, routineId = routineId))
        }
    }

    override fun observeProgressNotesByUser(userId: Int): Flow<List<ProgressNoteEntity>> =
        routineDao.observeProgressNotesByUser(userId)
    override suspend fun insertProgressNote(note: ProgressNoteEntity): Long =
        routineDao.insertProgressNote(note)
    override suspend fun updateProgressNote(note: ProgressNoteEntity) =
        routineDao.updateProgressNote(note)
    override suspend fun deleteProgressNote(note: ProgressNoteEntity) =
        routineDao.deleteProgressNote(note)

    // Tienda
    override fun observeAllProducts(): Flow<List<ProductEntity>> = storeDao.observeAllProducts()
    override suspend fun insertProduct(product: ProductEntity): Long = storeDao.insertProduct(product)
    override suspend fun updateProduct(product: ProductEntity) = storeDao.updateProduct(product)
    override suspend fun deleteProduct(product: ProductEntity) = storeDao.deleteProduct(product)

    override fun observeAllSales(): Flow<List<SaleEntity>> = storeDao.observeAllSales()
    override suspend fun insertSale(sale: SaleEntity): Long = storeDao.insertSale(sale)

    // Notificaciones y Multimedia
    override fun observeNotificationsByUser(userId: Int): Flow<List<NotificationEntity>> =
        notificationDao.observeNotificationsByUser(userId)
    override suspend fun insertNotification(notification: NotificationEntity): Long =
        notificationDao.insertNotification(notification)
    override suspend fun updateNotification(notification: NotificationEntity) =
        notificationDao.updateNotification(notification)
    override suspend fun deleteNotification(notification: NotificationEntity) =
        notificationDao.deleteNotification(notification)

    override fun observeMediaByOwner(ownerId: Int, ownerType: String): Flow<List<MediaAssetEntity>> =
        mediaAssetDao.observeMediaByOwner(ownerId, ownerType)
    override suspend fun insertMediaAsset(mediaAsset: MediaAssetEntity): Long =
        mediaAssetDao.insertMediaAsset(mediaAsset)
    override suspend fun deleteMediaAsset(mediaAsset: MediaAssetEntity) =
        mediaAssetDao.deleteMediaAsset(mediaAsset)
}
