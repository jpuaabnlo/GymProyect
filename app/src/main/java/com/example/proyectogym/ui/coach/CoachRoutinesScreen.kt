package com.example.proyectogym.ui.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.ui.AppViewModelProvider

@Composable
fun CoachRoutinesScreen(
    coachId: Int,
    onCrearRutina: () -> Unit,
    onEditarRutina: (Int) -> Unit,
    viewModel: CoachViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(coachId) {
        viewModel.cargarDatosCoach(coachId)
    }

    val uiState by viewModel.uiState.collectAsState()

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
                    text = "Mis rutinas",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = {
                        viewModel.prepararNuevaRutina()
                        onCrearRutina()
                    }
                ) {
                    Text("+ Nueva rutina")
                }
            }
            Text(
                text = "Crea, edita, elimina y asigna rutinas personalizadas a tus clientes.",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (uiState.rutinas.isEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No hay rutinas creadas todavía. Presiona '+ Nueva rutina'.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(uiState.rutinas, key = { it.id }) { rutina ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = rutina.nombre, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Objetivo: ${rutina.objetivo} · Días: ${rutina.diasSemana}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Cliente asignado: ${rutina.clienteNombre}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            TextButton(
                                onClick = {
                                    viewModel.cargarRutinaParaEditar(rutina.id)
                                    onEditarRutina(rutina.id)
                                }
                            ) {
                                Text("Editar")
                            }
                            TextButton(onClick = { viewModel.eliminarRutina(rutina) }) {
                                Text("Eliminar", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}
