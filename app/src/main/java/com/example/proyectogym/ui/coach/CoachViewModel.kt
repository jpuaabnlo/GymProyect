package com.example.proyectogym.ui.coach

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.NotificationEntity
import com.example.proyectogym.data.local.entity.ProgressNoteEntity
import com.example.proyectogym.data.local.entity.RoutineEntity
import com.example.proyectogym.data.local.entity.RoutineExerciseEntity
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.data.repository.GymRepository
import com.example.proyectogym.ui.components.UiMessage
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
    val editGrupoMuscular: String = "Pecho",
    val editObjetivo: String = "Fuerza",
    val editDiaUnico: String = "Lunes",
    val editDiasSeleccionados: Set<String> = setOf("Lunes"),
    val editClienteAsignadoId: Int? = null,
    val editEjercicios: List<RoutineExerciseEntity> = emptyList(),
    val mensajeFeedback: UiMessage? = null
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
                gymRepository.observeClientsByCoach(coachId),
                gymRepository.observeRoutinesByCoach(coachId)
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
                editGrupoMuscular = "Pecho",
                editObjetivo = "Fuerza",
                editDiaUnico = "Lunes",
                editDiasSeleccionados = setOf("Lunes"),
                editClienteAsignadoId = primerCliente?.id,
                editEjercicios = emptyList(),
                mensajeFeedback = null
            )
        }
    }

    fun prepararNuevaRutinaParaCliente(cliente: UserEntity) {
        _uiState.update {
            it.copy(
                editRoutineId = null,
                editNombreRutina = "",
                editGrupoMuscular = "Pecho",
                editObjetivo = "Fuerza",
                editDiaUnico = "Lunes",
                editDiasSeleccionados = setOf("Lunes"),
                editClienteAsignadoId = cliente.id,
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
            val diaPrincipal = dias.firstOrNull() ?: "Lunes"
            val musculo = rutina.grupoMuscular.ifBlank {
                ejercicios.firstOrNull()?.grupoMuscular?.ifBlank { "Pecho" } ?: "Pecho"
            }

            _uiState.update {
                it.copy(
                    editRoutineId = rutina.id,
                    editNombreRutina = rutina.nombre,
                    editGrupoMuscular = musculo,
                    editObjetivo = rutina.objetivo,
                    editDiaUnico = diaPrincipal,
                    editDiasSeleccionados = if (dias.isEmpty()) setOf(diaPrincipal) else dias,
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

    fun onEditGrupoMuscularChange(valor: String) {
        _uiState.update { it.copy(editGrupoMuscular = valor) }
    }

    fun onEditObjetivoChange(valor: String) {
        _uiState.update { it.copy(editObjetivo = valor) }
    }

    fun onEditDiaUnicoChange(dia: String) {
        _uiState.update { it.copy(editDiaUnico = dia, editDiasSeleccionados = setOf(dia)) }
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
    ): Boolean {
        val nombreLimpio = nombre.trim()
        if (nombreLimpio.isBlank()) return false
        val yaExiste = _uiState.value.editEjercicios.any {
            it.nombre.equals(nombreLimpio, ignoreCase = true)
        }
        if (yaExiste) {
            _uiState.update {
                it.copy(mensajeFeedback = UiMessage(R.string.coach_error_duplicate_exercise, listOf(nombreLimpio)))
            }
            return false
        }
        val nuevo = RoutineExerciseEntity(
            routineId = _uiState.value.editRoutineId ?: 0,
            nombre = nombreLimpio,
            grupoMuscular = grupoMuscular.trim().ifBlank { "General" },
            series = series.toIntOrNull() ?: 3,
            repeticiones = repeticiones.toIntOrNull() ?: 12,
            pesoSugeridoKg = pesoKg.toDoubleOrNull() ?: 20.0
        )
        _uiState.update { it.copy(editEjercicios = it.editEjercicios + nuevo, mensajeFeedback = null) }
        return true
    }

    fun quitarEjercicioTemporal(index: Int) {
        _uiState.update { actual ->
            val copia = actual.editEjercicios.toMutableList()
            if (index in copia.indices) copia.removeAt(index)
            actual.copy(editEjercicios = copia)
        }
    }

    fun guardarRutina(onGuardado: (routineName: String, clientName: String?) -> Unit) {
        val state = _uiState.value
        val cliente = state.clientes.firstOrNull { it.id == state.editClienteAsignadoId }
        val diaFinal = state.editDiaUnico.ifBlank { "Lunes" }
        val nombreFinal = state.editNombreRutina.trim()
            .ifBlank { "${state.editGrupoMuscular} · $diaFinal" }

        val entidadRutina = RoutineEntity(
            id = state.editRoutineId ?: 0,
            coachId = state.coachId,
            clienteId = cliente?.id,
            clienteNombre = cliente?.nombre ?: "Sin asignar",
            nombre = nombreFinal,
            grupoMuscular = state.editGrupoMuscular,
            objetivo = state.editObjetivo,
            diasSemana = diaFinal
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
                val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
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

            _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.coach_msg_routine_saved)) }
            onGuardado(entidadRutina.nombre, cliente?.nombre)
        }
    }

    fun eliminarRutina(rutina: RoutineEntity) {
        viewModelScope.launch {
            gymRepository.deleteRoutine(rutina)
            _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.coach_msg_routine_deleted)) }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeFeedback = null) }
    }
}
