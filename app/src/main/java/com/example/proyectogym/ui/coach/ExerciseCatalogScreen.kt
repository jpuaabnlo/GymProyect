package com.example.proyectogym.ui.coach

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.proyectogym.R
import com.example.proyectogym.ui.components.ExerciseVisualCard
import com.example.proyectogym.ui.theme.GymGreen
import com.example.proyectogym.ui.theme.GymGreenDark
import com.example.proyectogym.ui.utils.LocalWindowWidthSizeClass

@Composable
fun ExerciseCatalogScreen(
    viewModel: CoachViewModel,
    onBack: () -> Unit,
    windowSize: WindowWidthSizeClass = LocalWindowWidthSizeClass.current
) {
    val uiState by viewModel.uiState.collectAsState()
    val allLabel = stringResource(R.string.catalog_filter_all)

    var musculoFiltro by rememberSaveable(uiState.editGrupoMuscular) {
        mutableStateOf(
            if (uiState.editGrupoMuscular in MUSCLE_GROUPS) uiState.editGrupoMuscular else allLabel
        )
    }
    var busqueda by rememberSaveable { mutableStateOf("") }

    var ejercicioSeleccionado by remember { mutableStateOf<CatalogExercise?>(null) }
    var seriesInput by rememberSaveable { mutableStateOf("4") }
    var repsInput by rememberSaveable { mutableStateOf("10") }
    var pesoInput by rememberSaveable { mutableStateOf("20") }

    val nombresAgregados = remember(uiState.editEjercicios) {
        uiState.editEjercicios.map { it.nombre.trim().lowercase() }.toSet()
    }

    val ejerciciosFiltrados = remember(musculoFiltro, busqueda, allLabel) {
        EXERCISE_CATALOG.filter { ej ->
            val matchMusculo = musculoFiltro == allLabel || ej.grupoMuscular == musculoFiltro
            val matchTexto = busqueda.isBlank() ||
                ej.nombre.contains(busqueda, ignoreCase = true) ||
                ej.equipo.contains(busqueda, ignoreCase = true)
            matchMusculo && matchTexto
        }
    }

    val gridColumns = when (windowSize) {
        WindowWidthSizeClass.Compact -> GridCells.Fixed(1)
        WindowWidthSizeClass.Medium -> GridCells.Fixed(2)
        else -> GridCells.Adaptive(minSize = 260.dp)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.catalog_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            OutlinedButton(onClick = onBack) {
                Text(stringResource(R.string.btn_back))
            }
        }

        Text(
            text = stringResource(R.string.catalog_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = busqueda,
            onValueChange = { busqueda = it },
            placeholder = { Text(stringResource(R.string.catalog_search_hint)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { busqueda = "" }) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.btn_clear))
                    }
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = musculoFiltro == allLabel,
                onClick = { musculoFiltro = allLabel },
                label = { Text(allLabel) }
            )
            MUSCLE_GROUPS.forEach { musculo ->
                FilterChip(
                    selected = musculoFiltro == musculo,
                    onClick = { musculoFiltro = musculo },
                    label = { Text(musculo) }
                )
            }
        }

        LazyVerticalGrid(
            columns = gridColumns,
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(ejerciciosFiltrados, key = { it.id }) { ej ->
                val yaAgregado = ej.nombre.trim().lowercase() in nombresAgregados
                ExerciseVisualCard(
                    nombre = ej.nombre,
                    grupoMuscular = ej.grupoMuscular,
                    descripcion = "${ej.equipo} · ${ej.descripcion}",
                    completado = yaAgregado,
                    onClick = if (yaAgregado) {
                        null
                    } else {
                        {
                            ejercicioSeleccionado = ej
                            seriesInput = "4"
                            repsInput = "10"
                            pesoInput = "20"
                        }
                    },
                    trailingContent = if (yaAgregado) {
                        {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = GymGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = stringResource(R.string.catalog_already_added),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = GymGreenDark
                                )
                            }
                        }
                    } else {
                        null
                    }
                )
            }
        }
    }

    val seleccionado = ejercicioSeleccionado
    if (seleccionado != null) {
        AlertDialog(
            onDismissRequest = { ejercicioSeleccionado = null },
            title = { Text(seleccionado.nombre) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "${seleccionado.grupoMuscular} · ${seleccionado.equipo}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    OutlinedTextField(
                        value = seriesInput,
                        onValueChange = { seriesInput = it.filter(Char::isDigit).take(2) },
                        label = { Text(stringResource(R.string.editor_exercise_sets)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = repsInput,
                        onValueChange = { repsInput = it.filter(Char::isDigit).take(3) },
                        label = { Text(stringResource(R.string.editor_exercise_reps)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = pesoInput,
                        onValueChange = { pesoInput = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text(stringResource(R.string.editor_exercise_weight)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val agregado = viewModel.agregarEjercicioTemporal(
                            nombre = seleccionado.nombre,
                            grupoMuscular = seleccionado.grupoMuscular,
                            series = seriesInput,
                            repeticiones = repsInput,
                            pesoKg = pesoInput
                        )
                        ejercicioSeleccionado = null
                        if (agregado) {
                            onBack()
                        }
                    }
                ) {
                    Text(stringResource(R.string.editor_btn_add))
                }
            },
            dismissButton = {
                TextButton(onClick = { ejercicioSeleccionado = null }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}
