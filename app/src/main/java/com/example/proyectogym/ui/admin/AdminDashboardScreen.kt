package com.example.proyectogym.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.ui.AppViewModelProvider

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    val totalVentasMonto = uiState.ventas.sumOf { it.total }
    val porVencer = uiState.membresias.filter { it.diasRestantes <= 7 }
    val asistenciasPorServicio = uiState.asistencias.groupingBy { it.servicioNombre }.eachCount()
    val clientes = uiState.usuarios.filter { it.rol == "CLIENTE" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Tablero Administrativo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
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

        // Indicadores superiores
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IndicadorCard(
                    titulo = "Asistencias",
                    valor = "${uiState.asistencias.size}",
                    modifier = Modifier.weight(1f)
                )
                IndicadorCard(
                    titulo = "Tienda POS",
                    valor = "$${totalVentasMonto.toInt()}",
                    modifier = Modifier.weight(1f)
                )
                IndicadorCard(
                    titulo = "Por vencer",
                    valor = "${porVencer.size}",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Próximas reinscripciones
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = "Estado de membresías y próximas reinscripciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.membresias.isEmpty()) {
            item {
                Text("No hay membresías registradas.")
            }
        } else {
            items(uiState.membresias, key = { "memb_${it.id}" }) { m ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (m.diasRestantes <= 7) {
                            MaterialTheme.colorScheme.errorContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = m.clienteNombre, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${m.servicioNombre} · Vence: ${m.fechaVencimiento}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Text(
                            text = "${m.diasRestantes} días",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Acumulado de asistencias por servicio
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = "Acumulado de asistencias por servicio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Column(
                modifier = Modifier.padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (asistenciasPorServicio.isEmpty()) {
                    Text("Sin asistencias registradas aún.")
                } else {
                    asistenciasPorServicio.forEach { (servicio, conteo) ->
                        Text(text = "• $servicio: $conteo asistencias")
                    }
                }
            }
        }

        // Registro manual de asistencia en mostrador
        item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
            Text(
                text = "Registro rápido de asistencia en recepción",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(clientes, key = { "checkin_${it.id}" }) { cliente ->
            val memb = uiState.membresias.firstOrNull { it.userId == cliente.id }
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = cliente.nombre, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = memb?.servicioNombre ?: "Visita / Acceso general",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            viewModel.registrarAsistenciaManual(
                                usuario = cliente,
                                servicioNombre = memb?.servicioNombre ?: "Visita General"
                            )
                        }
                    ) {
                        Text("Check-in")
                    }
                }
            }
        }
    }
}

@Composable
private fun IndicadorCard(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
