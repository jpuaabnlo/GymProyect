package com.example.proyectogym.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.MediaAssetEntity
import com.example.proyectogym.data.local.entity.NotificationEntity
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

data class ProfileUiState(
    val usuario: UserEntity? = null,
    val nombre: String = "",
    val telefono: String = "",
    val objetivo: String = "",
    val horarioHabitual: String = "No definido",
    val asistencias: List<AttendanceEntity> = emptyList(),
    val notificaciones: List<NotificationEntity> = emptyList(),
    val archivosMultimedia: List<MediaAssetEntity> = emptyList(),
    val errorNombre: UiMessage? = null,
    val errorTelefono: UiMessage? = null,
    val mensajeGuardado: UiMessage? = null
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
                    val primerCarga = current.usuario == null && user != null
                    current.copy(
                        usuario = user,
                        nombre = if (primerCarga) user!!.nombre else current.nombre,
                        telefono = if (primerCarga) user!!.telefono.filter(Char::isDigit).take(10) else current.telefono,
                        objetivo = if (primerCarga) user!!.objetivo else current.objetivo,
                        horarioHabitual = if (primerCarga) user!!.horarioHabitual else current.horarioHabitual,
                        asistencias = attendances,
                        notificaciones = notifications,
                        archivosMultimedia = media
                    )
                }
            }
        }
    }

    fun onNombreChange(valor: String) {
        val limpio = valor.filter { !it.isDigit() }
        _uiState.update { it.copy(nombre = limpio, errorNombre = null, mensajeGuardado = null) }
    }

    fun onTelefonoChange(valor: String) {
        val digitos = valor.filter(Char::isDigit).take(10)
        _uiState.update { it.copy(telefono = digitos, errorTelefono = null, mensajeGuardado = null) }
    }

    fun onObjetivoChange(valor: String) {
        _uiState.update { it.copy(objetivo = valor, mensajeGuardado = null) }
    }

    fun guardarCambiosPerfil() {
        val actual = _uiState.value.usuario ?: return
        val nombreLimpio = _uiState.value.nombre.trim()
        val telefonoLimpio = _uiState.value.telefono.trim()

        if (nombreLimpio.isBlank()) {
            _uiState.update { it.copy(errorNombre = UiMessage(R.string.profile_error_name_required)) }
            return
        }
        if (telefonoLimpio.length != 10 || !telefonoLimpio.all(Char::isDigit)) {
            _uiState.update { it.copy(errorTelefono = UiMessage(R.string.profile_error_phone_10)) }
            return
        }

        viewModelScope.launch {
            val actualizado = actual.copy(
                nombre = nombreLimpio,
                telefono = telefonoLimpio,
                objetivo = _uiState.value.objetivo.trim(),
                horarioHabitual = _uiState.value.horarioHabitual
            )
            gymRepository.updateUser(actualizado)
            _uiState.update {
                it.copy(
                    usuario = actualizado,
                    errorNombre = null,
                    errorTelefono = null,
                    mensajeGuardado = UiMessage(R.string.profile_updated_msg)
                )
            }
        }
    }

    fun guardarHoraRecordatorio(
        hora: String,
        tituloNotificacion: String,
        mensajeNotificacion: String
    ) {
        val actual = _uiState.value.usuario ?: return
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        viewModelScope.launch {
            val actualizado = actual.copy(horarioHabitual = hora)
            gymRepository.updateUser(actualizado)

            gymRepository.insertNotification(
                NotificationEntity(
                    userId = actual.id,
                    titulo = tituloNotificacion,
                    mensaje = mensajeNotificacion,
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
                    mensajeGuardado = UiMessage(R.string.profile_reminder_saved_msg, listOf(hora))
                )
            }
        }
    }

    fun eliminarNotificacion(notification: NotificationEntity) {
        viewModelScope.launch {
            gymRepository.deleteNotification(notification)
        }
    }

    fun guardarFotoPerfilUri(uriString: String, descripcion: String) {
        val actual = _uiState.value.usuario ?: return
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            gymRepository.insertMediaAsset(
                MediaAssetEntity(
                    ownerId = actual.id,
                    ownerType = "PERFIL",
                    uriLocal = uriString,
                    mimeType = "image/*",
                    descripcion = descripcion.ifBlank { "Foto de perfil" },
                    fechaRegistro = hoy
                )
            )
            _uiState.update {
                it.copy(mensajeGuardado = UiMessage(R.string.profile_photo_saved_msg))
            }
        }
    }
}
