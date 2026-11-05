package com.example.proyectogym.ui.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.ui.AppViewModelProvider

@Composable
fun ClientHomeScreen(
    userId: Int,
    onIrARutina: () -> Unit,
    viewModel: ClientViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(userId) {
        viewModel.cargarCliente(userId)
    }

    val uiState by viewModel.uiState.collectAsState()
    var mostrarQrDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Hola, ${uiState.cliente?.nombre ?: "Socio"}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Objetivo actual: ${uiState.cliente?.objetivo?.ifBlank { "Mantenerse activo" } ?: "-"}",
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

        item {
            val membresia = uiState.membresiaActual
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Estado de mi membresía",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (membresia != null) {
                        Text(text = "Servicio: ${membresia.servicioNombre}")
                        Text(text = "Vence el: ${membresia.fechaVencimiento} (${membresia.estado})")
                        Text(text = "Días restantes: ${membresia.diasRestantes} de 30")
                        LinearProgressIndicator(
                            progress = { (membresia.diasRestantes / 30f).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text("No tienes membresía activa registrada. Acude a recepción.")
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = viewModel::registrarCheckInHoy,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Registrar asistencia")
                        }
                        OutlinedButton(
                            onClick = { mostrarQrDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Mi código QR")
                        }
                    }
                }
            }
        }

        item {
            val rutina = uiState.rutinaActiva
            val completados = uiState.ejerciciosRutina.count { it.completado }
            val totalEjercicios = uiState.ejerciciosRutina.size

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
                        text = "Mi rutina asignada",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (rutina != null) {
                        Text(text = "${rutina.nombre} · Objetivo: ${rutina.objetivo}")
                        Text(text = "Días sugeridos: ${rutina.diasSemana}")
                        Text(text = "Avance de hoy: $completados / $totalEjercicios ejercicios completados")
                        Button(onClick = onIrARutina) {
                            Text("Ir a mi rutina")
                        }
                    } else {
                        Text("Aún no tienes una rutina personalizada asignada por un entrenador.")
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Resumen de actividad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = "Asistencias acumuladas: ${uiState.asistencias.size}")
                    Text(text = "Notas de progreso registradas: ${uiState.notasProgreso.size}")
                }
            }
        }
    }

    if (mostrarQrDialog) {
        AlertDialog(
            onDismissRequest = { mostrarQrDialog = false },
            title = { Text("Código de socio para Check-In") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Muestra este código en recepción para validar tu entrada:")
                    Text(
                        text = "GYM-SOCIO-${uiState.cliente?.id ?: 0}",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("Socio: ${uiState.cliente?.nombre ?: "-"}")
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarQrDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}
