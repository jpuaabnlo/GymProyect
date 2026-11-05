package com.example.proyectogym.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.NotificationEntity
import com.example.proyectogym.data.local.entity.ProgressNoteEntity
import com.example.proyectogym.data.local.entity.RoutineEntity
import com.example.proyectogym.data.local.entity.RoutineExerciseEntity
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.data.repository.GymRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class CoachUiState(
    val coachId: Int = 0,
    val clientes: List<UserEntity> = emptyList(),
    val rutinas: List<RoutineEntity> = emptyList(),
    val clienteSeleccionado: UserEntity? = null,
    val progresoClienteSeleccionado: List<ProgressNoteEntity> = emptyList(),
    val asistenciasClienteSeleccionado: List<AttendanceEntity> = emptyList(),
    val editRoutineId: Int? = null,
    val editNombreRutina: String = "",
    val editObjetivo: String = "Fuerza",
    val editDiasSeleccionados: Set<String> = setOf("Lun", "Mié", "Vie"),
    val editClienteAsignadoId: Int? = null,
    val editEjercicios: List<RoutineExerciseEntity> = emptyList(),
    val mensajeFeedback: String? = null
)

class CoachViewModel(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CoachUiState())
    val uiState: StateFlow<CoachUiState> = _uiState.asStateFlow()

    private var mainJob: Job? = null
    private var clienteDetalleJob: Job? = null
    private var coachInicializado: Int? = null

    fun cargarDatosCoach(coachId: Int) {
        if (coachInicializado == coachId) return
        coachInicializado = coachId
        _uiState.update { it.copy(coachId = coachId) }

        mainJob?.cancel()
        mainJob = viewModelScope.launch {
            combine(
                gymRepository.observeUsersByRole("CLIENTE"),
                gymRepository.observeAllRoutines()
            ) { clientes, rutinas ->
                Pair(clientes, rutinas)
            }.collect { (clientes, rutinas) ->
                _uiState.update {
                    it.copy(
                        clientes = clientes,
                        rutinas = rutinas
                    )
                }
            }
        }
    }

    fun seleccionarClienteParaDetalle(cliente: UserEntity?) {
        clienteDetalleJob?.cancel()
        if (cliente == null) {
            _uiState.update {
                it.copy(
                    clienteSeleccionado = null,
                    progresoClienteSeleccionado = emptyList(),
                    asistenciasClienteSeleccionado = emptyList()
                )
            }
            return
        }

        _uiState.update { it.copy(clienteSeleccionado = cliente) }
        clienteDetalleJob = viewModelScope.launch {
            combine(
                gymRepository.observeProgressNotesByUser(cliente.id),
                gymRepository.observeAttendancesByUser(cliente.id)
            ) { notas, asistencias ->
                Pair(notas, asistencias)
            }.collect { (notas, asistencias) ->
                _uiState.update {
                    it.copy(
                        progresoClienteSeleccionado = notas,
                        asistenciasClienteSeleccionado = asistencias
                    )
                }
            }
        }
    }

    fun prepararNuevaRutina() {
        val primerCliente = _uiState.value.clientes.firstOrNull()
        _uiState.update {
            it.copy(
                editRoutineId = null,
                editNombreRutina = "",
                editObjetivo = "Fuerza",
                editDiasSeleccionados = setOf("Lun", "Mié", "Vie"),
                editClienteAsignadoId = primerCliente?.id,
                editEjercicios = emptyList(),
                mensajeFeedback = null
            )
        }
    }

    fun cargarRutinaParaEditar(routineId: Int) {
        viewModelScope.launch {
            val rutina = gymRepository.getRoutineById(routineId) ?: return@launch
            val ejercicios = gymRepository.getExercisesForRoutineOnce(routineId)
            val dias = rutina.diasSemana.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .toSet()

            _uiState.update {
                it.copy(
                    editRoutineId = rutina.id,
                    editNombreRutina = rutina.nombre,
                    editObjetivo = rutina.objetivo,
                    editDiasSeleccionados = if (dias.isEmpty()) setOf("Lun") else dias,
                    editClienteAsignadoId = rutina.clienteId,
                    editEjercicios = ejercicios,
                    mensajeFeedback = null
                )
            }
        }
    }

    fun onEditNombreChange(valor: String) {
        _uiState.update { it.copy(editNombreRutina = valor) }
    }

    fun onEditObjetivoChange(valor: String) {
        _uiState.update { it.copy(editObjetivo = valor) }
    }

    fun toggleDiaSemana(dia: String) {
        _uiState.update { actual ->
            val nuevos = actual.editDiasSeleccionados.toMutableSet()
            if (nuevos.contains(dia)) nuevos.remove(dia) else nuevos.add(dia)
            actual.copy(editDiasSeleccionados = nuevos)
        }
    }

    fun onClienteAsignadoChange(clienteId: Int?) {
        _uiState.update { it.copy(editClienteAsignadoId = clienteId) }
    }

    fun agregarEjercicioTemporal(
        nombre: String,
        grupoMuscular: String,
        series: String,
        repeticiones: String,
        pesoKg: String
    ) {
        if (nombre.isBlank()) return
        val nuevo = RoutineExerciseEntity(
            routineId = _uiState.value.editRoutineId ?: 0,
            nombre = nombre.trim(),
            grupoMuscular = grupoMuscular.trim().ifBlank { "General" },
            series = series.toIntOrNull() ?: 3,
            repeticiones = repeticiones.toIntOrNull() ?: 12,
            pesoSugeridoKg = pesoKg.toDoubleOrNull() ?: 20.0
        )
        _uiState.update { it.copy(editEjercicios = it.editEjercicios + nuevo) }
    }

    fun quitarEjercicioTemporal(index: Int) {
        _uiState.update { actual ->
            val copia = actual.editEjercicios.toMutableList()
            if (index in copia.indices) copia.removeAt(index)
            actual.copy(editEjercicios = copia)
        }
    }

    fun guardarRutina(onGuardado: () -> Unit) {
        val state = _uiState.value
        if (state.editNombreRutina.isBlank()) {
            _uiState.update { it.copy(mensajeFeedback = "Escribe un nombre para la rutina") }
            return
        }

        val cliente = state.clientes.firstOrNull { it.id == state.editClienteAsignadoId }
        val diasTexto = state.editDiasSeleccionados.joinToString(", ").ifBlank { "Lun, Mié, Vie" }

        val entidadRutina = RoutineEntity(
            id = state.editRoutineId ?: 0,
            coachId = state.coachId,
            clienteId = cliente?.id,
            clienteNombre = cliente?.nombre ?: "Sin asignar",
            nombre = state.editNombreRutina.trim(),
            objetivo = state.editObjetivo,
            diasSemana = diasTexto
        )

        viewModelScope.launch {
            val idFinal = if (state.editRoutineId == null) {
                gymRepository.insertRoutine(entidadRutina).toInt()
            } else {
                gymRepository.updateRoutine(entidadRutina)
                state.editRoutineId
            }

            gymRepository.replaceRoutineExercises(idFinal, state.editEjercicios)

            if (cliente != null) {
                val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                gymRepository.insertNotification(
                    NotificationEntity(
                        userId = cliente.id,
                        titulo = "Nueva rutina asignada",
                        mensaje = "Tu entrenador te asignó la rutina: ${entidadRutina.nombre}",
                        horaProgramada = "09:00",
                        tipo = "NUEVA_RUTINA",
                        activa = true,
                        leida = false,
                        fechaCreacion = hoy
                    )
                )
            }

            _uiState.update { it.copy(mensajeFeedback = "Rutina guardada correctamente") }
            onGuardado()
        }
    }

    fun eliminarRutina(rutina: RoutineEntity) {
        viewModelScope.launch {
            gymRepository.deleteRoutine(rutina)
            _uiState.update { it.copy(mensajeFeedback = "Rutina eliminada") }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeFeedback = null) }
    }
}
