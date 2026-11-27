package com.example.proyectogym.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.MediaAssetEntity
import com.example.proyectogym.data.local.entity.MembershipEntity
import com.example.proyectogym.data.local.entity.ProductEntity
import com.example.proyectogym.data.local.entity.SaleEntity
import com.example.proyectogym.data.local.entity.ServiceEntity
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.data.repository.AttendanceRegistrationResult
import com.example.proyectogym.data.repository.GymRepository
import com.example.proyectogym.ui.components.UiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class AdminUiState(
    val usuarios: List<UserEntity> = emptyList(),
    val servicios: List<ServiceEntity> = emptyList(),
    val membresias: List<MembershipEntity> = emptyList(),
    val asistencias: List<AttendanceEntity> = emptyList(),
    val productos: List<ProductEntity> = emptyList(),
    val ventas: List<SaleEntity> = emptyList(),
    val carrito: Map<Int, Int> = emptyMap(),
    val metodoPagoTienda: String = "EFECTIVO",
    val mensajeFeedback: UiMessage? = null
)

class AdminViewModel(
    private val gymRepository: GymRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val grupo1 = combine(
                gymRepository.observeAllUsers(),
                gymRepository.observeAllServices(),
                gymRepository.observeAllMemberships()
            ) { users, services, memberships ->
                Triple(users, services, memberships)
            }

            val grupo2 = combine(
                gymRepository.observeAllAttendances(),
                gymRepository.observeAllProducts(),
                gymRepository.observeAllSales()
            ) { attendances, products, sales ->
                Triple(attendances, products, sales)
            }

            combine(grupo1, grupo2) { g1, g2 ->
                Pair(g1, g2)
            }.collect { (g1, g2) ->
                _uiState.update { actual ->
                    actual.copy(
                        usuarios = g1.first,
                        servicios = g1.second,
                        membresias = g1.third,
                        asistencias = g2.first,
                        productos = g2.second,
                        ventas = g2.third
                    )
                }
            }
        }
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeFeedback = null) }
    }

    fun registrarAsistenciaManual(usuario: UserEntity, servicioNombre: String) {
        viewModelScope.launch {
            val resultado = gymRepository.registerAttendanceWithCooldown(
                AttendanceEntity(
                    userId = usuario.id,
                    usuarioNombre = usuario.nombre,
                    servicioNombre = servicioNombre,
                    fecha = "",
                    hora = "",
                    origen = "MANUAL"
                )
            )
            when (resultado) {
                is AttendanceRegistrationResult.Success -> {
                    _uiState.update {
                        it.copy(mensajeFeedback = UiMessage(R.string.admin_checkin_msg, listOf(usuario.nombre)))
                    }
                }
                is AttendanceRegistrationResult.CooldownActive -> {
                    val horas = resultado.remainingMinutes / 60
                    val mins = resultado.remainingMinutes % 60
                    _uiState.update {
                        it.copy(
                            mensajeFeedback = UiMessage(
                                R.string.admin_checkin_cooldown,
                                listOf(usuario.nombre, horas, mins)
                            )
                        )
                    }
                }
            }
        }
    }

    fun cobrarServicio(
        cliente: UserEntity,
        servicio: ServiceEntity,
        metodoPago: String
    ) {
        cobrarServicios(
            cliente = cliente,
            servicios = listOf(servicio),
            metodoPago = metodoPago,
            coachSeleccionado = null
        )
    }

    fun cobrarServicios(
        cliente: UserEntity,
        servicios: List<ServiceEntity>,
        metodoPago: String,
        coachSeleccionado: UserEntity? = null
    ) {
        if (servicios.isEmpty()) return
        val formato = SimpleDateFormat("yyyy-MM-dd", Locale.US)

        viewModelScope.launch {
            var totalCobrado = 0.0
            servicios.forEach { servicio ->
                val calendario = Calendar.getInstance()
                val fechaInicio = formato.format(calendario.time)
                calendario.add(Calendar.DAY_OF_YEAR, servicio.duracionDias)
                val fechaVencimiento = formato.format(calendario.time)
                val estado = if (servicio.duracionDias <= 7) "POR_VENCER" else "ACTIVA"

                gymRepository.insertMembership(
                    MembershipEntity(
                        userId = cliente.id,
                        clienteNombre = cliente.nombre,
                        serviceId = servicio.id,
                        servicioNombre = servicio.nombre,
                        montoPagado = servicio.precio,
                        metodoPago = metodoPago,
                        fechaInicio = fechaInicio,
                        fechaVencimiento = fechaVencimiento,
                        diasRestantes = servicio.duracionDias,
                        estado = estado
                    )
                )
                totalCobrado += servicio.precio
            }

            if (coachSeleccionado != null && cliente.coachId != coachSeleccionado.id) {
                gymRepository.updateUser(cliente.copy(coachId = coachSeleccionado.id))
            }

            val resumenServicios = servicios.joinToString(", ") { it.nombre }
            _uiState.update {
                it.copy(
                    mensajeFeedback = UiMessage(
                        R.string.admin_billing_msg,
                        listOf(cliente.nombre, resumenServicios, totalCobrado)
                    )
                )
            }
        }
    }

    fun onMetodoPagoTiendaChange(metodo: String) {
        _uiState.update { it.copy(metodoPagoTienda = metodo) }
    }

    fun agregarProductoCarrito(producto: ProductEntity) {
        val actual = _uiState.value.carrito[producto.id] ?: 0
        if (actual >= producto.stock) {
            _uiState.update {
                it.copy(mensajeFeedback = UiMessage(R.string.admin_store_no_stock, listOf(producto.nombre)))
            }
            return
        }
        val nuevoMapa = _uiState.value.carrito.toMutableMap()
        nuevoMapa[producto.id] = actual + 1
        _uiState.update { it.copy(carrito = nuevoMapa) }
    }

    fun quitarProductoCarrito(producto: ProductEntity) {
        val actual = _uiState.value.carrito[producto.id] ?: 0
        if (actual <= 0) return
        val nuevoMapa = _uiState.value.carrito.toMutableMap()
        if (actual == 1) nuevoMapa.remove(producto.id) else nuevoMapa[producto.id] = actual - 1
        _uiState.update { it.copy(carrito = nuevoMapa) }
    }

    fun confirmarVentaTienda(adminId: Int) {
        val state = _uiState.value
        if (state.carrito.isEmpty()) {
            _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_store_empty_cart)) }
            return
        }

        val detalles = mutableListOf<String>()
        var totalVenta = 0.0

        viewModelScope.launch {
            state.carrito.forEach { (prodId, cantidad) ->
                val prod = state.productos.firstOrNull { it.id == prodId }
                if (prod != null && cantidad > 0) {
                    detalles.add("${cantidad}x ${prod.nombre}")
                    totalVenta += prod.precio * cantidad
                    val nuevoStock = (prod.stock - cantidad).coerceAtLeast(0)
                    gymRepository.updateProduct(prod.copy(stock = nuevoStock))
                }
            }

            val fechaHoy = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
            gymRepository.insertSale(
                SaleEntity(
                    adminId = adminId,
                    resumenProductos = detalles.joinToString(", "),
                    total = totalVenta,
                    metodoPago = state.metodoPagoTienda,
                    fecha = fechaHoy
                )
            )

            _uiState.update {
                it.copy(
                    carrito = emptyMap(),
                    mensajeFeedback = UiMessage(
                        R.string.admin_store_sale_ok,
                        listOf(totalVenta, state.metodoPagoTienda)
                    )
                )
            }
        }
    }

    fun guardarUsuario(
        idExistente: Int?,
        nombre: String,
        email: String,
        password: String,
        telefono: String,
        rol: String,
        coachId: Int? = null,
        fotoUri: String? = null
    ) {
        if (nombre.isBlank() || email.isBlank()) {
            _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_msg_user_required)) }
            return
        }
        val coachFinal = if (rol == "CLIENTE") coachId else null
        val hoy = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        viewModelScope.launch {
            val savedUserId: Int
            if (idExistente == null || idExistente == 0) {
                savedUserId = gymRepository.insertUser(
                    UserEntity(
                        nombre = nombre.trim(),
                        email = email.trim(),
                        password = password.ifBlank { "1234" },
                        telefono = telefono.trim(),
                        rol = rol,
                        fechaAlta = hoy,
                        coachId = coachFinal
                    )
                ).toInt()
                _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_msg_user_added)) }
            } else {
                savedUserId = idExistente
                val previo = gymRepository.getUserById(idExistente)
                gymRepository.updateUser(
                    UserEntity(
                        id = idExistente,
                        nombre = nombre.trim(),
                        email = email.trim(),
                        password = password.ifBlank { previo?.password ?: "1234" },
                        telefono = telefono.trim(),
                        rol = rol,
                        objetivo = previo?.objetivo ?: "",
                        horarioHabitual = previo?.horarioHabitual ?: "No definido",
                        fechaAlta = previo?.fechaAlta ?: hoy,
                        coachId = coachFinal
                    )
                )
                _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_msg_user_updated)) }
            }

            if (!fotoUri.isNullOrBlank()) {
                gymRepository.insertMediaAsset(
                    MediaAssetEntity(
                        ownerId = savedUserId,
                        ownerType = "PERFIL",
                        uriLocal = fotoUri,
                        mimeType = "image/*",
                        descripcion = nombre.trim(),
                        fechaRegistro = hoy
                    )
                )
            }
        }
    }

    fun eliminarUsuario(usuario: UserEntity) {
        viewModelScope.launch {
            gymRepository.deleteUser(usuario)
            _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_msg_user_deleted)) }
        }
    }

    fun guardarServicio(
        idExistente: Int?,
        nombre: String,
        tipo: String,
        precio: String,
        duracionDias: String,
        horario: String
    ) {
        if (nombre.isBlank()) return
        val entidad = ServiceEntity(
            id = idExistente ?: 0,
            nombre = nombre.trim(),
            tipo = tipo,
            precio = precio.toDoubleOrNull() ?: 0.0,
            duracionDias = duracionDias.toIntOrNull() ?: 30,
            horario = horario.ifBlank { "Horario general" }
        )
        viewModelScope.launch {
            if (idExistente == null || idExistente == 0) {
                gymRepository.insertService(entidad)
                _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_msg_service_added)) }
            } else {
                gymRepository.updateService(entidad)
                _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_msg_service_updated)) }
            }
        }
    }

    fun eliminarServicio(servicio: ServiceEntity) {
        viewModelScope.launch {
            gymRepository.deleteService(servicio)
            _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_msg_service_deleted)) }
        }
    }

    fun guardarProducto(
        idExistente: Int?,
        nombre: String,
        categoria: String,
        precio: String,
        stock: String
    ) {
        if (nombre.isBlank()) return
        val entidad = ProductEntity(
            id = idExistente ?: 0,
            nombre = nombre.trim(),
            categoria = categoria.ifBlank { "Suplementos" },
            precio = precio.toDoubleOrNull() ?: 0.0,
            stock = stock.toIntOrNull() ?: 0
        )
        viewModelScope.launch {
            if (idExistente == null || idExistente == 0) {
                gymRepository.insertProduct(entidad)
                _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_msg_product_added)) }
            } else {
                gymRepository.updateProduct(entidad)
                _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_msg_product_updated)) }
            }
        }
    }

    fun eliminarProducto(producto: ProductEntity) {
        viewModelScope.launch {
            gymRepository.deleteProduct(producto)
            _uiState.update { it.copy(mensajeFeedback = UiMessage(R.string.admin_msg_product_deleted)) }
        }
    }
}
