package com.example.proyectogym.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.MediaAssetEntity
import com.example.proyectogym.data.local.entity.NotificationEntity
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

data class ProfileUiState(
    val usuario: UserEntity? = null,
    val nombre: String = "",
    val telefono: String = "",
    val objetivo: String = "",
    val horarioHabitual: String = "No definido",
    val asistencias: List<AttendanceEntity> = emptyList(),
    val notificaciones: List<NotificationEntity> = emptyList(),
    val archivosMultimedia: List<MediaAssetEntity> = emptyList(),
    val mensajeGuardado: String? = null
)

class ProfileViewModel(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var collectJob: Job? = null
    private var usuarioInicializadoId: Int? = null

    fun cargarPerfil(userId: Int) {
        if (usuarioInicializadoId == userId) return
        usuarioInicializadoId = userId

        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            combine(
                gymRepository.observeUserById(userId),
                gymRepository.observeAttendancesByUser(userId),
                gymRepository.observeNotificationsByUser(userId),
                gymRepository.observeMediaByOwner(userId, "PERFIL")
            ) { user, attendances, notifications, media ->
                Triple(user, attendances, Pair(notifications, media))
            }.collect { (user, attendances, extra) ->
                val (notifications, media) = extra
                _uiState.update { current ->
                    current.copy(
                        usuario = user,
                        nombre = if (current.usuario == null && user != null) user.nombre else current.nombre,
                        telefono = if (current.usuario == null && user != null) user.telefono else current.telefono,
                        objetivo = if (current.usuario == null && user != null) user.objetivo else current.objetivo,
                        horarioHabitual = if (current.usuario == null && user != null) user.horarioHabitual else current.horarioHabitual,
                        asistencias = attendances,
                        notificaciones = notifications,
                        archivosMultimedia = media
                    )
                }
            }
        }
    }

    fun onNombreChange(valor: String) {
        _uiState.update { it.copy(nombre = valor, mensajeGuardado = null) }
    }

    fun onTelefonoChange(valor: String) {
        _uiState.update { it.copy(telefono = valor, mensajeGuardado = null) }
    }

    fun onObjetivoChange(valor: String) {
        _uiState.update { it.copy(objetivo = valor, mensajeGuardado = null) }
    }

    fun guardarCambiosPerfil() {
        val actual = _uiState.value.usuario ?: return
        viewModelScope.launch {
            val actualizado = actual.copy(
                nombre = _uiState.value.nombre.trim().ifBlank { actual.nombre },
                telefono = _uiState.value.telefono.trim(),
                objetivo = _uiState.value.objetivo.trim(),
                horarioHabitual = _uiState.value.horarioHabitual
            )
            gymRepository.updateUser(actualizado)
            _uiState.update { it.copy(usuario = actualizado, mensajeGuardado = "Perfil guardado en la base de datos local") }
        }
    }

    fun guardarHoraRecordatorio(hora: String) {
        val actual = _uiState.value.usuario ?: return
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            val actualizado = actual.copy(horarioHabitual = hora)
            gymRepository.updateUser(actualizado)

            gymRepository.insertNotification(
                NotificationEntity(
                    userId = actual.id,
                    titulo = "Recordatorio de entrenamiento",
                    mensaje = "Entrenamiento programado a las $hora",
                    horaProgramada = hora,
                    tipo = "RECORDATORIO_ENTRENO",
                    activa = true,
                    leida = false,
                    fechaCreacion = hoy
                )
            )
            _uiState.update {
                it.copy(
                    horarioHabitual = hora,
                    mensajeGuardado = "Recordatorio ($hora) guardado en tabla de Notificaciones"
                )
            }
        }
    }

    fun eliminarNotificacion(notification: NotificationEntity) {
        viewModelScope.launch {
            gymRepository.deleteNotification(notification)
        }
    }

    fun agregarFotoMultimediaReferencia(descripcion: String) {
        val actual = _uiState.value.usuario ?: return
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            gymRepository.insertMediaAsset(
                MediaAssetEntity(
                    ownerId = actual.id,
                    ownerType = "PERFIL",
                    uriLocal = "content://com.example.proyectogym/perfil_${System.currentTimeMillis()}.jpg",
                    mimeType = "image/jpeg",
                    descripcion = descripcion.ifBlank { "Foto de perfil actualizada" },
                    fechaRegistro = hoy
                )
            )
            _uiState.update { it.copy(mensajeGuardado = "Referencia multimedia registrada en Room") }
        }
    }
}
