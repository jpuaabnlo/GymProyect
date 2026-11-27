package com.example.proyectogym.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.data.remote.toEntity
import com.example.proyectogym.data.repository.GymRepository
import com.example.proyectogym.data.repository.RemoteGymRepository
import com.example.proyectogym.ui.components.UiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val recordarSesion: Boolean = true,
    val mensajeError: UiMessage? = null,
    val mensajeInfo: UiMessage? = null,
    val usuariosRegistrados: List<UserEntity> = emptyList()
)

class LoginViewModel(
    private val gymRepository: GymRepository,
    private val networkRepository: RemoteGymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            gymRepository.syncFromRemote()
        }
        viewModelScope.launch {
            gymRepository.observeAllUsers().collect { users ->
                _uiState.update { it.copy(usuariosRegistrados = users) }
            }
        }
    }

    fun onEmailChange(nuevoEmail: String) {
        _uiState.update { it.copy(email = nuevoEmail, mensajeError = null, mensajeInfo = null) }
    }

    fun onPasswordChange(nuevoPassword: String) {
        _uiState.update { it.copy(password = nuevoPassword, mensajeError = null, mensajeInfo = null) }
    }

    fun onRecordarChange(valor: Boolean) {
        _uiState.update { it.copy(recordarSesion = valor) }
    }

    fun iniciarSesion(onLoginExitoso: (UserEntity) -> Unit) {
        val correo = _uiState.value.email.trim()
        val clave = _uiState.value.password.trim()

        if (correo.isBlank() || clave.isBlank()) {
            _uiState.update { it.copy(mensajeError = UiMessage(R.string.login_error_empty)) }
            return
        }

        viewModelScope.launch {
            var usuario = gymRepository.login(correo, clave)
            if (usuario == null) {
                gymRepository.syncFromRemote()
                usuario = gymRepository.login(correo, clave)
            }
            if (usuario != null) {
                _uiState.update { it.copy(mensajeError = null) }
                onLoginExitoso(usuario)
            } else {
                _uiState.update { it.copy(mensajeError = UiMessage(R.string.login_error_invalid)) }
            }
        }
    }

    fun registrarCuenta(
        nombre: String,
        email: String,
        telefono: String,
        password: String,
        rol: String
    ) {
        val nombreLimpio = nombre.trim()
        val correoLimpio = email.trim()
        val claveLimpia = password.trim()

        if (nombreLimpio.isBlank() || correoLimpio.isBlank() || claveLimpia.isBlank()) {
            _uiState.update { it.copy(mensajeError = UiMessage(R.string.login_error_register_fields)) }
            return
        }

        viewModelScope.launch {
            gymRepository.syncFromRemote()
            val existeRemoto = networkRepository.findUserByEmail(correoLimpio).getOrNull() != null
            val existeLocal = _uiState.value.usuariosRegistrados.any {
                it.email.equals(correoLimpio, ignoreCase = true)
            }
            if (existeRemoto || existeLocal) {
                _uiState.update { it.copy(mensajeError = UiMessage(R.string.login_error_email_exists)) }
                return@launch
            }

            val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            gymRepository.insertUser(
                UserEntity(
                    nombre = nombreLimpio,
                    email = correoLimpio,
                    password = claveLimpia,
                    telefono = telefono.trim(),
                    rol = rol,
                    fechaAlta = hoy
                )
            )
            _uiState.update {
                it.copy(
                    email = correoLimpio,
                    password = claveLimpia,
                    mensajeError = null,
                    mensajeInfo = UiMessage(R.string.login_info_registered, listOf(nombreLimpio))
                )
            }
        }
    }

    fun recuperarContrasena(correoRecuperacion: String) {
        if (correoRecuperacion.isBlank()) {
            _uiState.update { it.copy(mensajeError = UiMessage(R.string.login_error_recover_empty)) }
            return
        }
        viewModelScope.launch {
            val remoto = networkRepository.findUserByEmail(correoRecuperacion).getOrNull()?.toEntity()
            val encontrado = remoto ?: _uiState.value.usuariosRegistrados.firstOrNull {
                it.email.equals(correoRecuperacion.trim(), ignoreCase = true)
            }
            if (encontrado != null) {
                _uiState.update {
                    it.copy(
                        mensajeInfo = UiMessage(
                            R.string.login_info_recover_found,
                            listOf(encontrado.nombre, encontrado.password)
                        ),
                        mensajeError = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        mensajeError = UiMessage(
                            R.string.login_error_recover_not_found,
                            listOf(correoRecuperacion)
                        )
                    )
                }
            }
        }
    }
}
