package com.example.proyectogym.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.MembershipEntity
import com.example.proyectogym.data.local.entity.ProgressNoteEntity
import com.example.proyectogym.data.local.entity.RoutineEntity
import com.example.proyectogym.data.local.entity.RoutineExerciseEntity
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.data.repository.AttendanceCooldownHelper
import com.example.proyectogym.data.repository.AttendanceRegistrationResult
import com.example.proyectogym.data.repository.GymRepository
import com.example.proyectogym.ui.components.UiMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ClientUiState(
    val cliente: UserEntity? = null,
    val membresiaActual: MembershipEntity? = null,
    val rutinasCliente: List<RoutineEntity> = emptyList(),
    val rutinaSeleccionadaId: Int? = null,
    val rutinaDeHoy: RoutineEntity? = null,
    val rutinaActiva: RoutineEntity? = null,
    val esRutinaDeHoy: Boolean = false,
    val diaActualNombre: String = AttendanceCooldownHelper.getTodaySpanishName(),
    val ejerciciosRutina: List<RoutineExerciseEntity> = emptyList(),
    val asistencias: List<AttendanceEntity> = emptyList(),
    val notasProgreso: List<ProgressNoteEntity> = emptyList(),
    val puedeRegistrarAsistencia: Boolean = true,
    val minutosRestantesCooldown: Long = 0L,
    val mensajeFeedback: UiMessage? = null
)

class ClientViewModel(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientUiState())
    val uiState: StateFlow<ClientUiState> = _uiState.asStateFlow()

    private val selectedRoutineIdFlow = MutableStateFlow<Int?>(null)
    private var collectJob: Job? = null
    private var clienteActualId: Int? = null

    private data class RoutineBundle(
        val rutinas: List<RoutineEntity>,
        val rutinaDeHoy: RoutineEntity?,
        val rutinaMostrada: RoutineEntity?,
        val esDeHoy: Boolean,
        val ejercicios: List<RoutineExerciseEntity>
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cargarCliente(userId: Int) {
        if (clienteActualId == userId) return
        clienteActualId = userId
        selectedRoutineIdFlow.value = null

        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            val rutinaConEjerciciosFlow = combine(
                gymRepository.observeRoutinesForClient(userId),
                selectedRoutineIdFlow
            ) { rutinas, selectedId ->
                val today = AttendanceCooldownHelper.getTodayDayOfWeek()
                val rutinaHoy = rutinas.firstOrNull {
                    AttendanceCooldownHelper.routineMatchesDay(it.diasSemana, today)
                }
                val mostrada = if (selectedId != null) {
                    rutinas.firstOrNull { it.id == selectedId } ?: rutinaHoy ?: rutinas.firstOrNull()
                } else {
                    rutinaHoy ?: rutinas.firstOrNull()
                }
                val esDeHoy = mostrada != null &&
                    AttendanceCooldownHelper.routineMatchesDay(mostrada.diasSemana, today)
                Triple(rutinas, rutinaHoy, Pair(mostrada, esDeHoy))
            }.flatMapLatest { (rutinas, rutinaHoy, pair) ->
                val (mostrada, esDeHoy) = pair
                if (mostrada == null) {
                    flowOf(RoutineBundle(rutinas, rutinaHoy, null, false, emptyList()))
                } else {
                    gymRepository.observeExercisesForRoutine(mostrada.id).map { list ->
                        RoutineBundle(rutinas, rutinaHoy, mostrada, esDeHoy, list)
                    }
                }
            }

            combine(
                gymRepository.observeUserById(userId),
                gymRepository.observeLatestMembershipForUser(userId),
                rutinaConEjerciciosFlow,
                gymRepository.observeAttendancesByUser(userId),
                gymRepository.observeProgressNotesByUser(userId)
            ) { user, membership, bundle, attendances, notes ->
                val cooldownMin = AttendanceCooldownHelper.remainingCooldownMinutes(attendances)
                ClientUiState(
                    cliente = user,
                    membresiaActual = membership,
                    rutinasCliente = bundle.rutinas,
                    rutinaSeleccionadaId = bundle.rutinaMostrada?.id,
                    rutinaDeHoy = bundle.rutinaDeHoy,
                    rutinaActiva = bundle.rutinaMostrada,
                    esRutinaDeHoy = bundle.esDeHoy,
                    diaActualNombre = AttendanceCooldownHelper.getTodaySpanishName(),
                    ejerciciosRutina = bundle.ejercicios,
                    asistencias = attendances,
                    notasProgreso = notes,
                    puedeRegistrarAsistencia = cooldownMin == 0L,
                    minutosRestantesCooldown = cooldownMin,
                    mensajeFeedback = _uiState.value.mensajeFeedback
                )
            }.collect { nuevoEstado ->
                _uiState.value = nuevoEstado
            }
        }
    }

    fun seleccionarRutina(routineId: Int) {
        selectedRoutineIdFlow.value = routineId
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeFeedback = null) }
    }

    fun registrarCheckInHoy() {
        val state = _uiState.value
        val cliente = state.cliente ?: return
        val servicio = state.membresiaActual?.servicioNombre ?: "Acceso General"

        viewModelScope.launch {
            val resultado = gymRepository.registerAttendanceWithCooldown(
                AttendanceEntity(
                    userId = cliente.id,
                    usuarioNombre = cliente.nombre,
                    servicioNombre = servicio,
                    fecha = "",
                    hora = "",
                    origen = "QR"
                )
            )
            when (resultado) {
                is AttendanceRegistrationResult.Success -> {
                    _uiState.update {
                        it.copy(
                            puedeRegistrarAsistencia = false,
                            minutosRestantesCooldown = 5 * 60L,
                            mensajeFeedback = UiMessage(R.string.client_checkin_success, listOf(resultado.hora))
                        )
                    }
                }
                is AttendanceRegistrationResult.CooldownActive -> {
                    val horas = resultado.remainingMinutes / 60
                    val mins = resultado.remainingMinutes % 60
                    _uiState.update {
                        it.copy(
                            puedeRegistrarAsistencia = false,
                            minutosRestantesCooldown = resultado.remainingMinutes,
                            mensajeFeedback = UiMessage(R.string.client_checkin_cooldown, listOf(horas, mins))
                        )
                    }
                }
            }
        }
    }

    fun cambiarEstadoEjercicio(ejercicio: RoutineExerciseEntity, completado: Boolean) {
        if (!_uiState.value.esRutinaDeHoy) return
        viewModelScope.launch {
            gymRepository.updateExercise(ejercicio.copy(completado = completado))
        }
    }

    fun guardarMedicionCorporal(
        idExistente: Int?,
        pesoCorporal: String,
        indiceGrasa: String,
        masaMuscular: String,
        imc: String,
        comentario: String
    ) {
        val cliente = _uiState.value.cliente ?: return
        val pesoVal = pesoCorporal.toDoubleOrNull() ?: 0.0
        val grasaVal = indiceGrasa.toDoubleOrNull() ?: 0.0
        val masaVal = masaMuscular.toDoubleOrNull() ?: 0.0
        val imcVal = imc.toDoubleOrNull() ?: 0.0

        if (pesoVal <= 0.0 && grasaVal <= 0.0 && masaVal <= 0.0 && imcVal <= 0.0) {
            _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.progress_error_metrics_required)) }
            return
        }

        val fechaStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

        val nota = ProgressNoteEntity(
            id = idExistente ?: 0,
            userId = cliente.id,
            titulo = comentario.trim().ifBlank { "Control corporal" },
            pesoCorporalKg = pesoVal,
            pesoLevantadoKg = 0.0,
            repeticionesLogradas = 0,
            indiceGrasaPct = grasaVal,
            masaMuscularKg = masaVal,
            imc = imcVal,
            comentario = comentario.trim(),
            fecha = fechaStr
        )

        viewModelScope.launch {
            if (idExistente == null || idExistente == 0) {
                gymRepository.insertProgressNote(nota)
                _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.progress_msg_added)) }
            } else {
                gymRepository.updateProgressNote(nota)
                _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.progress_msg_updated)) }
            }
        }
    }

    fun eliminarNotaProgreso(nota: ProgressNoteEntity) {
        viewModelScope.launch {
            gymRepository.deleteProgressNote(nota)
            _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.progress_msg_deleted)) }
        }
    }
}
