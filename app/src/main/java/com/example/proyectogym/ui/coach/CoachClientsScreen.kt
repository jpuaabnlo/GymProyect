package com.example.proyectogym.ui.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.ui.AppViewModelProvider
import com.example.proyectogym.ui.utils.LocalWindowWidthSizeClass
import com.example.proyectogym.ui.utils.isCompact
import com.example.proyectogym.ui.utils.isExpanded

@Composable
fun CoachClientsScreen(
    coachId: Int,
    windowSize: WindowWidthSizeClass = LocalWindowWidthSizeClass.current,
    viewModel: CoachViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(coachId) {
        viewModel.cargarDatosCoach(coachId)
    }

    val uiState by viewModel.uiState.collectAsState()
    var busqueda by rememberSaveable { mutableStateOf("") }

    val clientesFiltrados = uiState.clientes.filter {
        busqueda.isBlank() ||
            it.nombre.contains(busqueda, ignoreCase = true) ||
            it.objetivo.contains(busqueda, ignoreCase = true)
    }

    if (!windowSize.isCompact) {
        val listWeight = if (windowSize.isExpanded) 0.45f else 1f
        val detailWeight = if (windowSize.isExpanded) 0.55f else 1f

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ClientsListSection(
                clientesFiltrados = clientesFiltrados,
                busqueda = busqueda,
                onBusquedaChange = { busqueda = it },
                seleccionadoId = uiState.clienteSeleccionado?.id,
                onSelectCliente = { viewModel.seleccionarClienteParaDetalle(it) },
                modifier = Modifier.weight(listWeight)
            )

            Card(
                modifier = Modifier
                    .weight(detailWeight)
                    .fillMaxSize(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                val seleccionado = uiState.clienteSeleccionado
                if (seleccionado == null) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(R.string.coach_detail_select_hint),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(R.string.coach_detail_title, seleccionado.nombre),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stringResource(
                                    R.string.coach_client_schedule,
                                    seleccionado.horarioHabitual,
                                    seleccionado.telefono
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                            Text(
                                text = stringResource(
                                    R.string.coach_detail_attendances,
                                    uiState.asistenciasClienteSeleccionado.size
                                ),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(R.string.coach_detail_recent_progress),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                        if (uiState.progresoClienteSeleccionado.isEmpty()) {
                            item {
                                Text(stringResource(R.string.coach_detail_no_progress))
                            }
                        } else {
                            items(uiState.progresoClienteSeleccionado, key = { it.id }) { nota ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "${nota.fecha} · ${nota.titulo}",
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = stringResource(
                                                R.string.progress_body_stats,
                                                nota.pesoCorporalKg,
                                                nota.indiceGrasaPct,
                                                nota.masaMuscularKg,
                                                nota.imc
                                            ),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        ClientsListSection(
            clientesFiltrados = clientesFiltrados,
            busqueda = busqueda,
            onBusquedaChange = { busqueda = it },
            seleccionadoId = uiState.clienteSeleccionado?.id,
            onSelectCliente = { viewModel.seleccionarClienteParaDetalle(it) },
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        )

        val seleccionado = uiState.clienteSeleccionado
        if (seleccionado != null) {
            AlertDialog(
                onDismissRequest = { viewModel.seleccionarClienteParaDetalle(null) },
                title = { Text(stringResource(R.string.coach_detail_title, seleccionado.nombre)) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = stringResource(
                                R.string.coach_detail_attendances,
                                uiState.asistenciasClienteSeleccionado.size
                            ),
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = stringResource(R.string.coach_detail_recent_progress),
                            fontWeight = FontWeight.SemiBold
                        )
                        if (uiState.progresoClienteSeleccionado.isEmpty()) {
                            Text(stringResource(R.string.coach_detail_no_progress))
                        } else {
                            uiState.progresoClienteSeleccionado.take(4).forEach { nota ->
                                Text(
                                    text = "• ${nota.fecha} - Peso: ${nota.pesoCorporalKg}kg · Grasa: ${nota.indiceGrasaPct}% · Músculo: ${nota.masaMuscularKg}kg · IMC: ${nota.imc}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.seleccionarClienteParaDetalle(null) }) {
                        Text(stringResource(R.string.btn_close))
                    }
                }
            )
        }
    }
}

@Composable
private fun ClientsListSection(
    clientesFiltrados: List<UserEntity>,
    busqueda: String,
    onBusquedaChange: (String) -> Unit,
    seleccionadoId: Int?,
    onSelectCliente: (UserEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.coach_clients_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.coach_clients_subtitle),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = onBusquedaChange,
                label = { Text(stringResource(R.string.coach_search_label)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (clientesFiltrados.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = stringResource(R.string.coach_no_clients_found),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(clientesFiltrados, key = { it.id }) { cliente ->
                val isSelected = seleccionadoId == cliente.id
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = cliente.nombre, fontWeight = FontWeight.Bold)
                            Text(
                                text = stringResource(
                                    R.string.coach_client_goal,
                                    cliente.objetivo.ifBlank { "General" }
                                ),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = stringResource(
                                    R.string.coach_client_schedule,
                                    cliente.horarioHabitual,
                                    cliente.telefono
                                ),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Button(onClick = { onSelectCliente(cliente) }) {
                            Text(stringResource(R.string.coach_btn_results))
                        }
                    }
                }
            }
        }
    }
}
