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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.ui.AppViewModelProvider

@Composable
fun CoachClientsScreen(
    coachId: Int,
    viewModel: CoachViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(coachId) {
        viewModel.cargarDatosCoach(coachId)
    }

    val uiState by viewModel.uiState.collectAsState()
    var busqueda by remember { mutableStateOf("") }

    val clientesFiltrados = uiState.clientes.filter {
        busqueda.isBlank() ||
            it.nombre.contains(busqueda, ignoreCase = true) ||
            it.objetivo.contains(busqueda, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Mis clientes",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Consulta el seguimiento, asistencias y resultados de cada socio.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        item {
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                label = { Text("Buscar cliente por nombre u objetivo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (clientesFiltrados.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No se encontraron clientes con ese criterio.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(clientesFiltrados, key = { it.id }) { cliente ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                                text = "Objetivo: ${cliente.objetivo.ifBlank { "General" }}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "Horario habitual: ${cliente.horarioHabitual} · Tel: ${cliente.telefono}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Button(onClick = { viewModel.seleccionarClienteParaDetalle(cliente) }) {
                            Text("Resultados")
                        }
                    }
                }
            }
        }
    }

    val seleccionado = uiState.clienteSeleccionado
    if (seleccionado != null) {
        AlertDialog(
            onDismissRequest = { viewModel.seleccionarClienteParaDetalle(null) },
            title = { Text("Seguimiento de ${seleccionado.nombre}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Asistencias registradas: ${uiState.asistenciasClienteSeleccionado.size}",
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Últimos registros de progreso:",
                        fontWeight = FontWeight.SemiBold
                    )
                    if (uiState.progresoClienteSeleccionado.isEmpty()) {
                        Text("Este cliente aún no registra notas de progreso.")
                    } else {
                        uiState.progresoClienteSeleccionado.take(4).forEach { nota ->
                            Text(
                                text = "• ${nota.fecha} - ${nota.titulo}: ${nota.pesoLevantadoKg}kg x ${nota.repeticionesLogradas} reps (Peso corp: ${nota.pesoCorporalKg}kg)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.seleccionarClienteParaDetalle(null) }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
