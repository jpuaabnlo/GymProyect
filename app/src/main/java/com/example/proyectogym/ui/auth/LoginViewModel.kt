package com.example.proyectogym.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.data.repository.GymRepository
import com.example.proyectogym.data.repository.RemoteGymRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val recordarSesion: Boolean = true,
    val mensajeError: String? = null,
    val mensajeInfo: String? = null,
    val cargando: Boolean = false,
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
            _uiState.update { it.copy(cargando = true) }
            gymRepository.seedInitialDataIfEmpty()
            _uiState.update { it.copy(cargando = false) }

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
            _uiState.update { it.copy(mensajeError = "Ingresa tu correo y contraseña") }
            return
        }

        viewModelScope.launch {
            val usuario = gymRepository.login(correo, clave)
            if (usuario != null) {
                _uiState.update { it.copy(mensajeError = null) }
                onLoginExitoso(usuario)
            } else {
                _uiState.update { it.copy(mensajeError = "Credenciales incorrectas. Verifica correo y contraseña.") }
            }
        }
    }

    fun accesoRapidoDemo(rolBuscado: String, onLoginExitoso: (UserEntity) -> Unit) {
        viewModelScope.launch {
            gymRepository.seedInitialDataIfEmpty()
            val demoEmail = when (rolBuscado) {
                "ADMIN" -> "admin@gym.com"
                "ENTRENADOR" -> "coach@gym.com"
                else -> "cliente@gym.com"
            }
            val usuario = gymRepository.login(demoEmail, "1234")
                ?: _uiState.value.usuariosRegistrados.firstOrNull { it.rol == rolBuscado }

            if (usuario != null) {
                onLoginExitoso(usuario)
            } else {
                _uiState.update { it.copy(mensajeError = "No se encontró usuario demo para $rolBuscado") }
            }
        }
    }

    fun recuperarContrasena(correoRecuperacion: String) {
        if (correoRecuperacion.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Escribe tu correo para recuperar tu contraseña") }
            return
        }
        viewModelScope.launch {
            // Intentamos notificar por capa remota (falla silenciosa sin backend) y validamos en BD local
            networkRepository.recoverPassword(correoRecuperacion)
            val encontrado = _uiState.value.usuariosRegistrados.firstOrNull {
                it.email.equals(correoRecuperacion.trim(), ignoreCase = true)
            }
            if (encontrado != null) {
                _uiState.update {
                    it.copy(
                        mensajeInfo = "Cuenta encontrada (${encontrado.nombre}). Contraseña local registrada: ${encontrado.password}",
                        mensajeError = null
                    )
                }
            } else {
                _uiState.update {
                    it.copy(mensajeError = "No existe ninguna cuenta con el correo $correoRecuperacion")
                }
            }
        }
    }
}
