package com.example.proyectogym.ui.client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.MembershipEntity
import com.example.proyectogym.data.local.entity.ProgressNoteEntity
import com.example.proyectogym.data.local.entity.RoutineEntity
import com.example.proyectogym.data.local.entity.RoutineExerciseEntity
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.data.repository.GymRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ClientUiState(
    val cliente: UserEntity? = null,
    val membresiaActual: MembershipEntity? = null,
    val rutinaActiva: RoutineEntity? = null,
    val ejerciciosRutina: List<RoutineExerciseEntity> = emptyList(),
    val asistencias: List<AttendanceEntity> = emptyList(),
    val notasProgreso: List<ProgressNoteEntity> = emptyList(),
    val mensajeFeedback: String? = null
)

class ClientViewModel(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientUiState())
    val uiState: StateFlow<ClientUiState> = _uiState.asStateFlow()

    private var collectJob: Job? = null
    private var clienteActualId: Int? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    fun cargarCliente(userId: Int) {
        if (clienteActualId == userId) return
        clienteActualId = userId

        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            val rutinaConEjerciciosFlow = gymRepository.observeActiveRoutineForClient(userId)
                .flatMapLatest { routine ->
                    if (routine == null) {
                        flowOf(Pair<RoutineEntity?, List<RoutineExerciseEntity>>(null, emptyList()))
                    } else {
                        gymRepository.observeExercisesForRoutine(routine.id).let { exercisesFlow ->
                            combine(flowOf(routine), exercisesFlow) { r, list -> Pair(r, list) }
                        }
                    }
                }

            combine(
                gymRepository.observeUserById(userId),
                gymRepository.observeLatestMembershipForUser(userId),
                rutinaConEjerciciosFlow,
                gymRepository.observeAttendancesByUser(userId),
                gymRepository.observeProgressNotesByUser(userId)
            ) { user, membership, routinePair, attendances, notes ->
                ClientUiState(
                    cliente = user,
                    membresiaActual = membership,
                    rutinaActiva = routinePair.first,
                    ejerciciosRutina = routinePair.second,
                    asistencias = attendances,
                    notasProgreso = notes,
                    mensajeFeedback = _uiState.value.mensajeFeedback
                )
            }.collect { nuevoEstado ->
                _uiState.value = nuevoEstado
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeFeedback = null) }
    }

    fun registrarCheckInHoy() {
        val cliente = _uiState.value.cliente ?: return
        val servicio = _uiState.value.membresiaActual?.servicioNombre ?: "Acceso General"
        val ahora = Date()
        val fechaStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(ahora)
        val horaStr = SimpleDateFormat("HH:mm", Locale.getDefault()).format(ahora)

        viewModelScope.launch {
            gymRepository.insertAttendance(
                AttendanceEntity(
                    userId = cliente.id,
                    usuarioNombre = cliente.nombre,
                    servicioNombre = servicio,
                    fecha = fechaStr,
                    hora = horaStr,
                    origen = "CHECKIN_APP"
                )
            )
            _uiState.update { it.copy(mensajeFeedback = "Asistencia registrada hoy a las $horaStr") }
        }
    }

    fun cambiarEstadoEjercicio(ejercicio: RoutineExerciseEntity, completado: Boolean) {
        viewModelScope.launch {
            gymRepository.updateExercise(ejercicio.copy(completado = completado))
        }
    }

    fun guardarNotaProgreso(
        idExistente: Int?,
        titulo: String,
        pesoCorporal: String,
        pesoLevantado: String,
        repeticiones: String,
        comentario: String
    ) {
        val cliente = _uiState.value.cliente ?: return
        if (titulo.isBlank()) {
            _uiState.update { it.copy(mensajeFeedback = "Escribe un título para tu nota de progreso") }
            return
        }
        val fechaStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val nota = ProgressNoteEntity(
            id = idExistente ?: 0,
            userId = cliente.id,
            titulo = titulo.trim(),
            pesoCorporalKg = pesoCorporal.toDoubleOrNull() ?: 0.0,
            pesoLevantadoKg = pesoLevantado.toDoubleOrNull() ?: 0.0,
            repeticionesLogradas = repeticiones.toIntOrNull() ?: 0,
            comentario = comentario.trim(),
            fecha = fechaStr
        )

        viewModelScope.launch {
            if (idExistente == null || idExistente == 0) {
                gymRepository.insertProgressNote(nota)
                _uiState.update { it.copy(mensajeFeedback = "Registro de progreso agregado") }
            } else {
                gymRepository.updateProgressNote(nota)
                _uiState.update { it.copy(mensajeFeedback = "Registro de progreso actualizado") }
            }
        }
    }

    fun eliminarNotaProgreso(nota: ProgressNoteEntity) {
        viewModelScope.launch {
            gymRepository.deleteProgressNote(nota)
            _uiState.update { it.copy(mensajeFeedback = "Registro eliminado") }
        }
    }
}
