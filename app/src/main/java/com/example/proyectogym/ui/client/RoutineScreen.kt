package com.example.proyectogym.ui.client

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.proyectogym.data.local.entity.RoutineEntity
import com.example.proyectogym.data.local.entity.RoutineExerciseEntity
import com.example.proyectogym.data.repository.AttendanceCooldownHelper
import com.example.proyectogym.ui.AppViewModelProvider
import com.example.proyectogym.ui.components.ExerciseVisualCard
import com.example.proyectogym.ui.theme.GymGreenDark
import com.example.proyectogym.ui.theme.GymGreenLight
import com.example.proyectogym.ui.theme.NavSelectedLightGray
import com.example.proyectogym.ui.theme.NavUnselectedGray
import com.example.proyectogym.ui.utils.LocalWindowWidthSizeClass
import com.example.proyectogym.ui.utils.isExpanded
import com.example.proyectogym.ui.utils.isMedium

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoutineScreen(
    userId: Int,
    windowSize: WindowWidthSizeClass = LocalWindowWidthSizeClass.current,
    viewModel: ClientViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(userId) {
        viewModel.cargarCliente(userId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val rutina = uiState.rutinaActiva

    when {
        windowSize.isExpanded -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(0.38f)
                        .fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RoutineHeaderText(rutina = rutina)
                        if (rutina != null) {
                            RoutineStatusBadge(
                                esRutinaDeHoy = uiState.esRutinaDeHoy,
                                diaActualNombre = uiState.diaActualNombre,
                                diasSemana = rutina.diasSemana
                            )
                        }
                        if (uiState.rutinaDeHoy == null && uiState.rutinasCliente.isNotEmpty()) {
                            RoutineNoneTodayHintCard(diaActualNombre = uiState.diaActualNombre)
                        }
                        if (uiState.rutinasCliente.size > 1) {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                uiState.rutinasCliente.forEach { r ->
                                    RoutineSelectorChip(
                                        rutina = r,
                                        isSelected = r.id == rutina?.id,
                                        onClick = { viewModel.seleccionarRutina(r.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 260.dp),
                    modifier = Modifier
                        .weight(0.62f)
                        .fillMaxHeight(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (uiState.ejerciciosRutina.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            RoutineEmptyExercisesCard()
                        }
                    } else {
                        items(uiState.ejerciciosRutina, key = { it.id }) { ejercicio ->
                            RoutineExerciseCard(
                                ejercicio = ejercicio,
                                esRutinaDeHoy = uiState.esRutinaDeHoy,
                                onToggle = { marcado ->
                                    viewModel.cambiarEstadoEjercicio(ejercicio, marcado)
                                }
                            )
                        }
                    }
                }
            }
        }

        windowSize.isMedium -> {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    RoutineHeaderText(rutina = rutina)
                }
                if (rutina != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        RoutineStatusBadge(
                            esRutinaDeHoy = uiState.esRutinaDeHoy,
                            diaActualNombre = uiState.diaActualNombre,
                            diasSemana = rutina.diasSemana
                        )
                    }
                }
                if (uiState.rutinaDeHoy == null && uiState.rutinasCliente.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        RoutineNoneTodayHintCard(diaActualNombre = uiState.diaActualNombre)
                    }
                }
                if (uiState.rutinasCliente.size > 1) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.rutinasCliente.forEach { r ->
                                RoutineSelectorChip(
                                    rutina = r,
                                    isSelected = r.id == rutina?.id,
                                    onClick = { viewModel.seleccionarRutina(r.id) }
                                )
                            }
                        }
                    }
                }
                if (uiState.ejerciciosRutina.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        RoutineEmptyExercisesCard()
                    }
                } else {
                    items(uiState.ejerciciosRutina, key = { it.id }) { ejercicio ->
                        RoutineExerciseCard(
                            ejercicio = ejercicio,
                            esRutinaDeHoy = uiState.esRutinaDeHoy,
                            onToggle = { marcado ->
                                viewModel.cambiarEstadoEjercicio(ejercicio, marcado)
                            }
                        )
                    }
                }
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    RoutineHeaderText(rutina = rutina)
                }

                if (rutina != null) {
                    item {
                        RoutineStatusBadge(
                            esRutinaDeHoy = uiState.esRutinaDeHoy,
                            diaActualNombre = uiState.diaActualNombre,
                            diasSemana = rutina.diasSemana
                        )
                    }
                }

                if (uiState.rutinaDeHoy == null && uiState.rutinasCliente.isNotEmpty()) {
                    item {
                        RoutineNoneTodayHintCard(diaActualNombre = uiState.diaActualNombre)
                    }
                }

                if (uiState.rutinasCliente.size > 1) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.rutinasCliente.forEach { r ->
                                RoutineSelectorChip(
                                    rutina = r,
                                    isSelected = r.id == rutina?.id,
                                    onClick = { viewModel.seleccionarRutina(r.id) }
                                )
                            }
                        }
                    }
                }

                if (uiState.ejerciciosRutina.isEmpty()) {
                    item {
                        RoutineEmptyExercisesCard()
                    }
                } else {
                    items(uiState.ejerciciosRutina, key = { it.id }) { ejercicio ->
                        RoutineExerciseCard(
                            ejercicio = ejercicio,
                            esRutinaDeHoy = uiState.esRutinaDeHoy,
                            onToggle = { marcado ->
                                viewModel.cambiarEstadoEjercicio(ejercicio, marcado)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineHeaderText(rutina: RoutineEntity?) {
    Column {
        Text(
            text = rutina?.nombre ?: stringResource(R.string.routine_today_default),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        val subtitulo = if (rutina != null) {
            val musculo = rutina.grupoMuscular.ifBlank { "General" }
            stringResource(R.string.routine_subtitle_full, musculo, rutina.objetivo, rutina.diasSemana)
        } else {
            stringResource(R.string.routine_none_assigned)
        }
        Text(
            text = subtitulo,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun RoutineStatusBadge(
    esRutinaDeHoy: Boolean,
    diaActualNombre: String,
    diasSemana: String
) {
    if (esRutinaDeHoy) {
        Surface(
            color = GymGreenLight,
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = null,
                    tint = GymGreenDark,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.routine_today_badge, diaActualNombre),
                    color = GymGreenDark,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else {
        Surface(
            color = NavSelectedLightGray,
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = NavUnselectedGray,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = stringResource(R.string.routine_readonly_badge, diasSemana),
                    color = NavUnselectedGray,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun RoutineNoneTodayHintCard(diaActualNombre: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = stringResource(R.string.routine_none_today_hint, diaActualNombre),
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun RoutineSelectorChip(
    rutina: RoutineEntity,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val isToday = AttendanceCooldownHelper.routineMatchesDay(rutina.diasSemana)
    val chipLabel = if (isToday) {
        stringResource(R.string.routine_chip_today_prefix, rutina.nombre)
    } else {
        "${rutina.nombre} (${rutina.diasSemana})"
    }
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(chipLabel) }
    )
}

@Composable
private fun RoutineEmptyExercisesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = stringResource(R.string.routine_empty_exercises),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun RoutineExerciseCard(
    ejercicio: RoutineExerciseEntity,
    esRutinaDeHoy: Boolean,
    onToggle: (Boolean) -> Unit
) {
    ExerciseVisualCard(
        nombre = ejercicio.nombre,
        grupoMuscular = ejercicio.grupoMuscular,
        series = ejercicio.series,
        repeticiones = ejercicio.repeticiones,
        pesoSugeridoKg = ejercicio.pesoSugeridoKg,
        completado = ejercicio.completado,
        trailingContent = {
            if (esRutinaDeHoy) {
                Checkbox(
                    checked = ejercicio.completado,
                    onCheckedChange = onToggle
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(R.string.routine_locked_desc),
                        tint = NavUnselectedGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Checkbox(
                        checked = ejercicio.completado,
                        enabled = false,
                        onCheckedChange = null
                    )
                }
            }
        }
    )
}
