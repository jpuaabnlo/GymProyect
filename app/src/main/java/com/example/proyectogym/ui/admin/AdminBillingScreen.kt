package com.example.proyectogym.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun AdminBillingScreen(
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val clientes = uiState.usuarios.filter { it.rol == "CLIENTE" }
    val servicios = uiState.servicios
    val metodosPago = listOf("EFECTIVO", "TARJETA", "TRANSFERENCIA")

    var clienteSeleccionadoId by remember(clientes) {
        mutableStateOf(clientes.firstOrNull()?.id)
    }
    var servicioSeleccionadoId by remember(servicios) {
        mutableStateOf(servicios.firstOrNull()?.id)
    }
    var metodoSeleccionado by remember { mutableStateOf("EFECTIVO") }

    val clienteActual = clientes.firstOrNull { it.id == clienteSeleccionadoId }
    val servicioActual = servicios.firstOrNull { it.id == servicioSeleccionadoId }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Cobrar Membresía / Visita",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Registra pagos de mensualidad, clases o visitas de un día.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.mensajeFeedback != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.mensajeFeedback!!,
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 1. Seleccionar socio
        item {
            Text(
                text = "1. Selecciona el cliente:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Column {
                clientes.forEach { cliente ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = clienteSeleccionadoId == cliente.id,
                            onClick = { clienteSeleccionadoId = cliente.id }
                        )
                        Text("${cliente.nombre} (${cliente.email})")
                    }
                }
            }
        }

        // 2. Seleccionar servicio
        item {
            HorizontalDivider()
            Text(
                text = "2. Selecciona el servicio:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Column {
                servicios.forEach { serv ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = servicioSeleccionadoId == serv.id,
                            onClick = { servicioSeleccionadoId = serv.id }
                        )
                        Text("${serv.nombre} - $${serv.precio} (${serv.duracionDias} días)")
                    }
                }
            }
        }

        // 3. Seleccionar método de pago (RadioButton requerido)
        item {
            HorizontalDivider()
            Text(
                text = "3. Método de pago:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                metodosPago.forEach { metodo ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = metodoSeleccionado == metodo,
                            onClick = { metodoSeleccionado = metodo }
                        )
                        Text(metodo, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Resumen y botón de cobro
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Total a cobrar: $${servicioActual?.precio ?: 0.0}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            if (clienteActual != null && servicioActual != null) {
                                viewModel.cobrarServicio(
                                    cliente = clienteActual,
                                    servicio = servicioActual,
                                    metodoPago = metodoSeleccionado
                                )
                            }
                        },
                        enabled = clienteActual != null && servicioActual != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Registrar cobro y renovar membresía")
                    }
                }
            }
        }
    }
}
