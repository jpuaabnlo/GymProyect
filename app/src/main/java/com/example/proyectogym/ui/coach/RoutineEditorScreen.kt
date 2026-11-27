package com.example.proyectogym.ui.coach

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.ui.components.ExerciseVisualCard
import com.example.proyectogym.ui.components.GymNotificationHelper
import com.example.proyectogym.ui.components.asString
import com.example.proyectogym.ui.theme.GymGreenDark
import com.example.proyectogym.ui.theme.GymGreenLight
import com.example.proyectogym.ui.utils.LocalWindowWidthSizeClass
import com.example.proyectogym.ui.utils.isExpanded
import com.example.proyectogym.ui.utils.isMedium

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RoutineEditorScreen(
    viewModel: CoachViewModel,
    onAbrirCatalogoEjercicios: () -> Unit,
    onBack: () -> Unit,
    windowSize: WindowWidthSizeClass = LocalWindowWidthSizeClass.current
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val objetivosDisponibles = listOf("Fuerza", "Hipertrofia", "Resistencia", "Pérdida de grasa")
    val diasSemana = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val clienteActual = uiState.clientes.firstOrNull { it.id == uiState.editClienteAsignadoId }

    val onSaveRoutine: () -> Unit = {
        viewModel.guardarRutina { routineName, clientName ->
            if (clientName != null) {
                GymNotificationHelper.mostrarNotificacion(
                    context = context,
                    titulo = context.getString(R.string.notif_routine_title),
                    mensaje = context.getString(
                        R.string.notif_routine_body,
                        routineName,
                        clientName
                    )
                )
            }
            onBack()
        }
    }

    if (windowSize.isExpanded) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.48f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EditorHeaderRow(
                    isNew = uiState.editRoutineId == null,
                    onBack = onBack
                )
                if (clienteActual != null) {
                    EditorClientCard(clienteActual = clienteActual)
                }
                if (uiState.mensajeFeedback != null) {
                    Text(
                        text = uiState.mensajeFeedback!!.asString(),
                        color = MaterialTheme.colorScheme.error
                    )
                }
                EditorConfigFields(
                    uiState = uiState,
                    objetivosDisponibles = objetivosDisponibles,
                    diasSemana = diasSemana,
                    viewModel = viewModel
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.52f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EditorExercisesHeader(
                    count = uiState.editEjercicios.size,
                    onAbrirCatalogoEjercicios = onAbrirCatalogoEjercicios
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (uiState.editEjercicios.isEmpty()) {
                        item {
                            EditorEmptyExercisesCard()
                        }
                    } else {
                        itemsIndexed(uiState.editEjercicios) { index, ej ->
                            ExerciseVisualCard(
                                nombre = ej.nombre,
                                grupoMuscular = ej.grupoMuscular,
                                series = ej.series,
                                repeticiones = ej.repeticiones,
                                pesoSugeridoKg = ej.pesoSugeridoKg,
                                trailingContent = {
                                    TextButton(onClick = { viewModel.quitarEjercicioTemporal(index) }) {
                                        Text(
                                            text = stringResource(R.string.editor_remove_btn),
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = onSaveRoutine,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.editor_save_assign_btn))
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .then(if (windowSize.isMedium) Modifier.widthIn(max = 680.dp) else Modifier)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    EditorHeaderRow(
                        isNew = uiState.editRoutineId == null,
                        onBack = onBack
                    )
                }

                if (clienteActual != null) {
                    item {
                        EditorClientCard(clienteActual = clienteActual)
                    }
                }

                if (uiState.mensajeFeedback != null) {
                    item {
                        Text(
                            text = uiState.mensajeFeedback!!.asString(),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                item {
                    EditorConfigFields(
                        uiState = uiState,
                        objetivosDisponibles = objetivosDisponibles,
                        diasSemana = diasSemana,
                        viewModel = viewModel
                    )
                }

                item {
                    EditorExercisesHeader(
                        count = uiState.editEjercicios.size,
                        onAbrirCatalogoEjercicios = onAbrirCatalogoEjercicios
                    )
                }

                if (uiState.editEjercicios.isEmpty()) {
                    item {
                        EditorEmptyExercisesCard()
                    }
                } else {
                    itemsIndexed(uiState.editEjercicios) { index, ej ->
                        ExerciseVisualCard(
                            nombre = ej.nombre,
                            grupoMuscular = ej.grupoMuscular,
                            series = ej.series,
                            repeticiones = ej.repeticiones,
                            pesoSugeridoKg = ej.pesoSugeridoKg,
                            trailingContent = {
                                TextButton(onClick = { viewModel.quitarEjercicioTemporal(index) }) {
                                    Text(
                                        text = stringResource(R.string.editor_remove_btn),
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    }
                }

                item {
                    Button(
                        onClick = onSaveRoutine,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(stringResource(R.string.editor_save_assign_btn))
                    }
                }
            }
        }
    }
}

@Composable
private fun EditorHeaderRow(
    isNew: Boolean,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isNew) {
                stringResource(R.string.editor_new_title)
            } else {
                stringResource(R.string.editor_edit_title)
            },
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        OutlinedButton(onClick = onBack) {
            Text(stringResource(R.string.btn_back))
        }
    }
}

@Composable
private fun EditorClientCard(clienteActual: UserEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GymGreenLight)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = stringResource(R.string.editor_selected_client_label, clienteActual.nombre),
                fontWeight = FontWeight.Bold,
                color = GymGreenDark
            )
            Text(
                text = stringResource(
                    R.string.coach_client_goal,
                    clienteActual.objetivo.ifBlank { "General" }
                ),
                style = MaterialTheme.typography.bodySmall,
                color = GymGreenDark
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditorConfigFields(
    uiState: CoachUiState,
    objetivosDisponibles: List<String>,
    diasSemana: List<String>,
    viewModel: CoachViewModel
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column {
            Text(
                text = stringResource(R.string.editor_muscle_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MUSCLE_GROUPS.forEach { musculo ->
                    FilterChip(
                        selected = uiState.editGrupoMuscular == musculo,
                        onClick = { viewModel.onEditGrupoMuscularChange(musculo) },
                        label = { Text(musculo) }
                    )
                }
            }
        }

        Column {
            Text(
                text = stringResource(R.string.editor_goal_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                objetivosDisponibles.forEach { obj ->
                    FilterChip(
                        selected = uiState.editObjetivo == obj,
                        onClick = { viewModel.onEditObjetivoChange(obj) },
                        label = { Text(obj) }
                    )
                }
            }
        }

        Column {
            Text(
                text = stringResource(R.string.editor_day_single_label),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                diasSemana.forEach { dia ->
                    FilterChip(
                        selected = uiState.editDiaUnico == dia,
                        onClick = { viewModel.onEditDiaUnicoChange(dia) },
                        label = { Text(dia) }
                    )
                }
            }
        }

        OutlinedTextField(
            value = uiState.editNombreRutina,
            onValueChange = viewModel::onEditNombreChange,
            label = { Text(stringResource(R.string.editor_routine_name_optional)) },
            placeholder = { Text("${uiState.editGrupoMuscular} · ${uiState.editDiaUnico}") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EditorExercisesHeader(
    count: Int,
    onAbrirCatalogoEjercicios: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.editor_exercises_count, count),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedButton(onClick = onAbrirCatalogoEjercicios) {
            Text(stringResource(R.string.editor_add_exercise_btn))
        }
    }
}

@Composable
private fun EditorEmptyExercisesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = stringResource(R.string.editor_no_exercises_hint),
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
