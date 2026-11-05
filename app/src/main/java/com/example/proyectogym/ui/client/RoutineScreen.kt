package com.example.proyectogym.ui.client

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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.data.local.entity.RoutineExerciseEntity
import com.example.proyectogym.ui.AppViewModelProvider

@Composable
fun RoutineScreen(
    userId: Int,
    viewModel: ClientViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(userId) {
        viewModel.cargarCliente(userId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val rutina = uiState.rutinaActiva

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = rutina?.nombre ?: "Rutina de hoy",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (rutina != null) {
                    "Objetivo: ${rutina.objetivo} · Días: ${rutina.diasSemana}"
                } else {
                    "Sin rutina asignada actualmente"
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (uiState.ejerciciosRutina.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Tu entrenador aún no ha agregado ejercicios a tu rutina.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(uiState.ejerciciosRutina, key = { it.id }) { ejercicio ->
                EjercicioItem(
                    ejercicio = ejercicio,
                    onCheckedChange = { marcado ->
                        viewModel.cambiarEstadoEjercicio(ejercicio, marcado)
                    }
                )
            }
        }
    }
}

@Composable
fun EjercicioItem(
    ejercicio: RoutineExerciseEntity,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (ejercicio.completado) {
                MaterialTheme.colorScheme.surfaceVariant
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
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = ejercicio.nombre, fontWeight = FontWeight.Bold)
                Text(
                    text = "${ejercicio.grupoMuscular} · ${ejercicio.series} series x ${ejercicio.repeticiones} reps · ${ejercicio.pesoSugeridoKg} kg",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Checkbox(
                checked = ejercicio.completado,
                onCheckedChange = onCheckedChange
            )
        }
    }
}
