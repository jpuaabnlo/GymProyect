package com.example.proyectogym.ui.client

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
import com.example.proyectogym.data.local.entity.ProgressNoteEntity
import com.example.proyectogym.ui.AppViewModelProvider

@Composable
fun ClientProgressScreen(
    userId: Int,
    viewModel: ClientViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(userId) {
        viewModel.cargarCliente(userId)
    }

    val uiState by viewModel.uiState.collectAsState()

    var mostrarFormulario by remember { mutableStateOf(false) }
    var notaEditando by remember { mutableStateOf<ProgressNoteEntity?>(null) }

    var titulo by remember { mutableStateOf("") }
    var pesoCorporal by remember { mutableStateOf("") }
    var pesoLevantado by remember { mutableStateOf("") }
    var repeticiones by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }

    fun abrirFormulario(nota: ProgressNoteEntity?) {
        notaEditando = nota
        titulo = nota?.titulo ?: ""
        pesoCorporal = nota?.pesoCorporalKg?.toString() ?: ""
        pesoLevantado = nota?.pesoLevantadoKg?.toString() ?: ""
        repeticiones = nota?.repeticionesLogradas?.toString() ?: ""
        comentario = nota?.comentario ?: ""
        mostrarFormulario = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mi progreso",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(onClick = { abrirFormulario(null) }) {
                    Text("+ Registrar")
                }
            }
            Text(
                text = "Registra tus pesos, repeticiones y notas de entrenamiento.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.notasProgreso.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Todavía no hay registros de progreso. Presiona '+ Registrar' para agregar uno.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(uiState.notasProgreso, key = { it.id }) { nota ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = nota.titulo, fontWeight = FontWeight.Bold)
                            Text(text = nota.fecha, style = MaterialTheme.typography.bodySmall)
                        }
                        Text(
                            text = "Peso corporal: ${nota.pesoCorporalKg} kg · Carga: ${nota.pesoLevantadoKg} kg x ${nota.repeticionesLogradas} reps",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (nota.comentario.isNotBlank()) {
                            Text(
                                text = "Nota: ${nota.comentario}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(onClick = { abrirFormulario(nota) }) {
                                Text("Editar")
                            }
                            TextButton(onClick = { viewModel.eliminarNotaProgreso(nota) }) {
                                Text("Eliminar", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = { mostrarFormulario = false },
            title = {
                Text(if (notaEditando == null) "Nuevo registro de progreso" else "Editar registro")
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título / Ejercicio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pesoCorporal,
                        onValueChange = { pesoCorporal = it },
                        label = { Text("Peso corporal (kg)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pesoLevantado,
                        onValueChange = { pesoLevantado = it },
                        label = { Text("Peso levantado (kg)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = repeticiones,
                        onValueChange = { repeticiones = it },
                        label = { Text("Repeticiones logradas") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Comentario / observaciones") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarFormulario = false
                        viewModel.guardarNotaProgreso(
                            idExistente = notaEditando?.id,
                            titulo = titulo,
                            pesoCorporal = pesoCorporal,
                            pesoLevantado = pesoLevantado,
                            repeticiones = repeticiones,
                            comentario = comentario
                        )
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFormulario = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
