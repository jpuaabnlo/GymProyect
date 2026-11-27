package com.example.proyectogym.ui.coach

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.R
import com.example.proyectogym.ui.AppViewModelProvider
import com.example.proyectogym.ui.components.asString
import com.example.proyectogym.ui.utils.LocalWindowWidthSizeClass

@Composable
fun CoachRoutinesScreen(
    coachId: Int,
    onCrearRutina: () -> Unit,
    onEditarRutina: (Int) -> Unit,
    windowSize: WindowWidthSizeClass = LocalWindowWidthSizeClass.current,
    viewModel: CoachViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(coachId) {
        viewModel.cargarDatosCoach(coachId)
    }

    val uiState by viewModel.uiState.collectAsState()

    val gridColumns = when (windowSize) {
        WindowWidthSizeClass.Compact -> GridCells.Fixed(1)
        WindowWidthSizeClass.Medium -> GridCells.Fixed(2)
        else -> GridCells.Adaptive(minSize = 300.dp)
    }

    LazyVerticalGrid(
        columns = gridColumns,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Text(
                    text = stringResource(R.string.coach_routines_title),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.coach_routines_select_client_subtitle),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        if (uiState.mensajeFeedback != null) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = uiState.mensajeFeedback!!.asString(),
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (uiState.clientes.isEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Text(
                        text = stringResource(R.string.coach_routines_no_assigned_clients),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            items(uiState.clientes, key = { it.id }) { cliente ->
                val rutinasCliente = uiState.rutinas.filter { it.clienteId == cliente.id }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.prepararNuevaRutinaParaCliente(cliente)
                            onCrearRutina()
                        },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = cliente.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = stringResource(
                                        R.string.coach_client_goal,
                                        cliente.objetivo.ifBlank { "General" }
                                    ),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.prepararNuevaRutinaParaCliente(cliente)
                                    onCrearRutina()
                                }
                            ) {
                                Text(stringResource(R.string.coach_new_routine_btn))
                            }
                        }

                        if (rutinasCliente.isNotEmpty()) {
                            HorizontalDivider()
                            Text(
                                text = stringResource(R.string.coach_client_existing_routines, rutinasCliente.size),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            rutinasCliente.forEach { rutina ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = rutina.nombre,
                                            fontWeight = FontWeight.SemiBold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${rutina.grupoMuscular.ifBlank { "General" }} · ${rutina.objetivo} · ${rutina.diasSemana}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Row {
                                        TextButton(
                                            onClick = {
                                                viewModel.cargarRutinaParaEditar(rutina.id)
                                                onEditarRutina(rutina.id)
                                            }
                                        ) {
                                            Text(stringResource(R.string.btn_edit))
                                        }
                                        TextButton(onClick = { viewModel.eliminarRutina(rutina) }) {
                                            Text(
                                                text = stringResource(R.string.btn_delete),
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
