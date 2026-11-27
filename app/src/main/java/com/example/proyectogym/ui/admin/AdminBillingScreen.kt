package com.example.proyectogym.ui.admin

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.ServiceEntity
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.ui.AppViewModelProvider
import com.example.proyectogym.ui.components.SearchableComboBox
import com.example.proyectogym.ui.components.asString
import com.example.proyectogym.ui.theme.NavBackgroundWhite
import com.example.proyectogym.ui.utils.LocalWindowWidthSizeClass
import com.example.proyectogym.ui.utils.isExpanded
import com.example.proyectogym.ui.utils.isMedium

private const val MAX_BILLING_SERVICES = 5

@Composable
fun AdminBillingScreen(
    windowSize: WindowWidthSizeClass = LocalWindowWidthSizeClass.current,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    val clientes = uiState.usuarios.filter { it.rol == "CLIENTE" }
    val entrenadores = uiState.usuarios.filter { it.rol == "ENTRENADOR" }
    val servicios = uiState.servicios
    val metodosPago = listOf("EFECTIVO", "TARJETA", "TRANSFERENCIA")

    var clienteSeleccionadoId by rememberSaveable { mutableStateOf<Int?>(null) }
    val servicioSlotsIds = remember { mutableStateListOf<Int?>(null) }
    var coachSeleccionadoId by rememberSaveable { mutableStateOf<Int?>(null) }
    var metodoSeleccionado by rememberSaveable { mutableStateOf("EFECTIVO") }

    val clienteActual = clientes.firstOrNull { it.id == clienteSeleccionadoId }
    val serviciosElegidos: List<ServiceEntity> = servicioSlotsIds.mapNotNull { id ->
        servicios.firstOrNull { it.id == id }
    }

    val requiereEntrenador = serviciosElegidos.any { serv ->
        serv.tipo.equals("PERSONALIZADO", ignoreCase = true) ||
            serv.nombre.contains("entrenador", ignoreCase = true) ||
            serv.nombre.contains("personalizad", ignoreCase = true)
    }

    LaunchedEffect(clienteActual?.id) {
        if (clienteActual?.coachId != null) {
            coachSeleccionadoId = clienteActual.coachId
        }
    }

    val coachActual = entrenadores.firstOrNull { it.id == coachSeleccionadoId }
    val totalCobro = serviciosElegidos.sumOf { it.precio }
    val puedeConfirmar = clienteActual != null &&
        serviciosElegidos.isNotEmpty() &&
        (!requiereEntrenador || coachActual != null)

    val onConfirmarCobro: () -> Unit = {
        if (clienteActual != null && serviciosElegidos.isNotEmpty()) {
            viewModel.cobrarServicios(
                cliente = clienteActual,
                servicios = serviciosElegidos,
                metodoPago = metodoSeleccionado,
                coachSeleccionado = if (requiereEntrenador) coachActual else null
            )
            servicioSlotsIds.clear()
            servicioSlotsIds.add(null)
        }
    }

    if (windowSize.isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.58f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BillingHeaderSection(feedback = uiState.mensajeFeedback?.asString())
                BillingSelectionCard(
                    clientes = clientes,
                    clienteActual = clienteActual,
                    onSelectCliente = { clienteSeleccionadoId = it },
                    servicios = servicios,
                    servicioSlotsIds = servicioSlotsIds,
                    requiereEntrenador = requiereEntrenador,
                    entrenadores = entrenadores,
                    coachActual = coachActual,
                    onSelectCoach = { coachSeleccionadoId = it }
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.42f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (clienteActual != null) {
                            Text(
                                text = clienteActual.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        if (serviciosElegidos.isNotEmpty()) {
                            serviciosElegidos.forEach { serv ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = serv.nombre,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "$${String.format(java.util.Locale.US, "%.2f", serv.precio)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                            HorizontalDivider()
                        }
                        BillingPaymentControls(
                            metodosPago = metodosPago,
                            metodoSeleccionado = metodoSeleccionado,
                            onSelectMetodo = { metodoSeleccionado = it },
                            totalCobro = totalCobro,
                            puedeConfirmar = puedeConfirmar,
                            onConfirmar = onConfirmarCobro
                        )
                    }
                }
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .then(if (windowSize.isMedium) Modifier.widthIn(max = 680.dp) else Modifier)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BillingHeaderSection(feedback = uiState.mensajeFeedback?.asString())
                    BillingSelectionCard(
                        clientes = clientes,
                        clienteActual = clienteActual,
                        onSelectCliente = { clienteSeleccionadoId = it },
                        servicios = servicios,
                        servicioSlotsIds = servicioSlotsIds,
                        requiereEntrenador = requiereEntrenador,
                        entrenadores = entrenadores,
                        coachActual = coachActual,
                        onSelectCoach = { coachSeleccionadoId = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Surface(
                color = NavBackgroundWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .then(if (windowSize.isMedium) Modifier.widthIn(max = 680.dp) else Modifier)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        HorizontalDivider()
                        BillingPaymentControls(
                            metodosPago = metodosPago,
                            metodoSeleccionado = metodoSeleccionado,
                            onSelectMetodo = { metodoSeleccionado = it },
                            totalCobro = totalCobro,
                            puedeConfirmar = puedeConfirmar,
                            onConfirmar = onConfirmarCobro
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BillingHeaderSection(feedback: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.admin_billing_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.admin_billing_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )
        if (feedback != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = feedback,
                    modifier = Modifier.padding(12.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun BillingSelectionCard(
    clientes: List<UserEntity>,
    clienteActual: UserEntity?,
    onSelectCliente: (Int?) -> Unit,
    servicios: List<ServiceEntity>,
    servicioSlotsIds: MutableList<Int?>,
    requiereEntrenador: Boolean,
    entrenadores: List<UserEntity>,
    coachActual: UserEntity?,
    onSelectCoach: (Int?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_billing_step1),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            SearchableComboBox(
                label = stringResource(R.string.admin_billing_client_combo_label),
                placeholder = stringResource(R.string.admin_billing_client_combo_placeholder),
                items = clientes,
                selectedItem = clienteActual,
                itemLabel = { it.nombre },
                itemSubLabel = { it.email },
                onItemSelected = { onSelectCliente(it.id) },
                onClearSelection = { onSelectCliente(null) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))

            Text(
                text = stringResource(R.string.admin_billing_step2),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (servicios.isEmpty()) {
                Text(
                    text = stringResource(R.string.admin_billing_no_services),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                servicioSlotsIds.forEachIndexed { index, selectedId ->
                    val selectedInSlot = servicios.firstOrNull { it.id == selectedId }
                    val selectedElsewhere = servicioSlotsIds
                        .mapIndexedNotNull { i, id -> if (i != index) id else null }
                        .toSet()
                    val availableForSlot = servicios.filter { it.id !in selectedElsewhere }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        SearchableComboBox(
                            label = stringResource(R.string.admin_billing_service_combo_label, index + 1),
                            placeholder = stringResource(R.string.admin_billing_service_combo_placeholder),
                            items = availableForSlot,
                            selectedItem = selectedInSlot,
                            itemLabel = { "${it.nombre} ($${it.precio.toInt()})" },
                            itemSubLabel = { "${it.tipo} · ${it.duracionDias}d · ${it.horario}" },
                            onItemSelected = { serv ->
                                servicioSlotsIds[index] = serv.id
                            },
                            onClearSelection = {
                                servicioSlotsIds[index] = null
                            },
                            modifier = Modifier.weight(1f)
                        )

                        if (servicioSlotsIds.size > 1) {
                            IconButton(onClick = { servicioSlotsIds.removeAt(index) }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(R.string.btn_delete)
                                )
                            }
                        }
                    }
                }

                val remainingCount = servicios.count { s -> s.id !in servicioSlotsIds.filterNotNull() }
                if (servicioSlotsIds.size < MAX_BILLING_SERVICES && remainingCount > 0) {
                    OutlinedButton(
                        onClick = { servicioSlotsIds.add(null) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text(
                            stringResource(
                                R.string.admin_billing_add_service,
                                servicioSlotsIds.size,
                                MAX_BILLING_SERVICES
                            )
                        )
                    }
                }
            }

            if (requiereEntrenador) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 2.dp))
                Text(
                    text = stringResource(R.string.admin_billing_coach_step),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                SearchableComboBox(
                    label = stringResource(R.string.admin_billing_coach_combo_label),
                    placeholder = stringResource(R.string.admin_billing_coach_combo_placeholder),
                    items = entrenadores,
                    selectedItem = coachActual,
                    itemLabel = { it.nombre },
                    itemSubLabel = { it.objetivo.ifBlank { it.email } },
                    onItemSelected = { onSelectCoach(it.id) },
                    onClearSelection = { onSelectCoach(null) }
                )
            }
        }
    }
}

@Composable
private fun BillingPaymentControls(
    metodosPago: List<String>,
    metodoSeleccionado: String,
    onSelectMetodo: (String) -> Unit,
    totalCobro: Double,
    puedeConfirmar: Boolean,
    onConfirmar: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.admin_billing_payment_short),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            metodosPago.forEach { metodo ->
                FilterChip(
                    selected = metodoSeleccionado == metodo,
                    onClick = { onSelectMetodo(metodo) },
                    label = { Text(metodo) }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.admin_billing_total, totalCobro),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Button(
            onClick = onConfirmar,
            enabled = puedeConfirmar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.admin_billing_confirm))
        }
    }
}
