package com.example.proyectogym.ui.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoutineEditorScreen(
    viewModel: CoachViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val objetivosDisponibles = listOf("Fuerza", "Hipertrofia", "Resistencia", "Pérdida de grasa")
    val diasSemana = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")

    var mostrarDialogoEjercicio by remember { mutableStateOf(false) }
    var ejNombre by remember { mutableStateOf("") }
    var ejGrupo by remember { mutableStateOf("") }
    var ejSeries by remember { mutableStateOf("4") }
    var ejReps by remember { mutableStateOf("10") }
    var ejPeso by remember { mutableStateOf("30") }

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
                    text = if (uiState.editRoutineId == null) "Nueva rutina" else "Editar rutina",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(onClick = onBack) {
                    Text("Volver")
                }
            }
        }

        if (uiState.mensajeFeedback != null) {
            item {
                Text(
                    text = uiState.mensajeFeedback!!,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        item {
            OutlinedTextField(
                value = uiState.editNombreRutina,
                onValueChange = viewModel::onEditNombreChange,
                label = { Text("Nombre de la rutina") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Selección de objetivo con RadioButton (Criterio de UI)
        item {
            Text(
                text = "Objetivo principal:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Column {
                objetivosDisponibles.forEach { obj ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.editObjetivo == obj,
                            onClick = { viewModel.onEditObjetivoChange(obj) }
                        )
                        Text(text = obj)
                    }
                }
            }
        }

        // Selección de días con FilterChip
        item {
            Text(
                text = "Días de entrenamiento:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                diasSemana.forEach { dia ->
                    FilterChip(
                        selected = uiState.editDiasSeleccionados.contains(dia),
                        onClick = { viewModel.toggleDiaSemana(dia) },
                        label = { Text(dia) }
                    )
                }
            }
        }

        // Asignar cliente con RadioButton
        item {
            Text(
                text = "Asignar a cliente:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Column {
                uiState.clientes.forEach { cliente ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.editClienteAsignadoId == cliente.id,
                            onClick = { viewModel.onClienteAsignadoChange(cliente.id) }
                        )
                        Text(text = "${cliente.nombre} (${cliente.email})")
                    }
                }
            }
        }

        // Lista de ejercicios de la rutina
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ejercicios (${uiState.editEjercicios.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                OutlinedButton(onClick = {
                    ejNombre = ""
                    ejGrupo = ""
                    ejSeries = "4"
                    ejReps = "10"
                    ejPeso = "30"
                    mostrarDialogoEjercicio = true
                }) {
                    Text("+ Ejercicio")
                }
            }
        }

        itemsIndexed(uiState.editEjercicios) { index, ej ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = ej.nombre, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${ej.grupoMuscular} · ${ej.series} x ${ej.repeticiones} · ${ej.pesoSugeridoKg} kg",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    TextButton(onClick = { viewModel.quitarEjercicioTemporal(index) }) {
                        Text("Quitar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        item {
            Button(
                onClick = { viewModel.guardarRutina(onGuardado = onBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Guardar y asignar rutina")
            }
        }
    }

    if (mostrarDialogoEjercicio) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoEjercicio = false },
            title = { Text("Agregar ejercicio a la rutina") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ejNombre,
                        onValueChange = { ejNombre = it },
                        label = { Text("Nombre del ejercicio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ejGrupo,
                        onValueChange = { ejGrupo = it },
                        label = { Text("Grupo muscular (Pecho, Pierna...)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ejSeries,
                        onValueChange = { ejSeries = it },
                        label = { Text("Series") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ejReps,
                        onValueChange = { ejReps = it },
                        label = { Text("Repeticiones") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = ejPeso,
                        onValueChange = { ejPeso = it },
                        label = { Text("Peso sugerido (kg)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogoEjercicio = false
                        viewModel.agregarEjercicioTemporal(
                            nombre = ejNombre,
                            grupoMuscular = ejGrupo,
                            series = ejSeries,
                            repeticiones = ejReps,
                            pesoKg = ejPeso
                        )
                    }
                ) {
                    Text("Agregar")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEjercicio = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
