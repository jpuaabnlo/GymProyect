package com.example.proyectogym.ui.client

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.ProgressNoteEntity
import com.example.proyectogym.ui.AppViewModelProvider
import com.example.proyectogym.ui.components.asString
import com.example.proyectogym.ui.utils.LocalWindowWidthSizeClass
import com.example.proyectogym.ui.utils.isExpanded
import com.example.proyectogym.ui.utils.isMedium
import java.util.Locale

enum class BodyMetric(
    val labelRes: Int,
    val unit: String,
    val color: Color,
    val extract: (ProgressNoteEntity) -> Double
) {
    PESO(R.string.progress_metric_weight, "kg", Color(0xFF15803D), { it.pesoCorporalKg }),
    GRASA(R.string.progress_metric_fat, "%", Color(0xFFEA580C), { it.indiceGrasaPct }),
    MASA_MUSCULAR(R.string.progress_metric_muscle, "kg", Color(0xFF2563EB), { it.masaMuscularKg }),
    IMC(R.string.progress_metric_bmi, "", Color(0xFF7C3AED), { it.imc })
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClientProgressScreen(
    userId: Int,
    windowSize: WindowWidthSizeClass = LocalWindowWidthSizeClass.current,
    viewModel: ClientViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(userId) {
        viewModel.cargarCliente(userId)
    }

    val uiState by viewModel.uiState.collectAsState()

    var metricasActivas by remember { mutableStateOf(BodyMetric.entries.toSet()) }
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }
    var notaEditando by remember { mutableStateOf<ProgressNoteEntity?>(null) }

    var pesoCorporal by rememberSaveable { mutableStateOf("") }
    var indiceGrasa by rememberSaveable { mutableStateOf("") }
    var masaMuscular by rememberSaveable { mutableStateOf("") }
    var imc by rememberSaveable { mutableStateOf("") }
    var comentario by rememberSaveable { mutableStateOf("") }

    fun abrirFormulario(nota: ProgressNoteEntity?) {
        notaEditando = nota
        pesoCorporal = nota?.pesoCorporalKg?.takeIf { it > 0 }?.toString() ?: ""
        indiceGrasa = nota?.indiceGrasaPct?.takeIf { it > 0 }?.toString() ?: ""
        masaMuscular = nota?.masaMuscularKg?.takeIf { it > 0 }?.toString() ?: ""
        imc = nota?.imc?.takeIf { it > 0 }?.toString() ?: ""
        comentario = nota?.comentario ?: ""
        mostrarFormulario = true
    }

    val medicionesOrdenadas = remember(uiState.notasProgreso) {
        uiState.notasProgreso.sortedWith(compareBy<ProgressNoteEntity> { it.fecha }.thenBy { it.id })
    }

    val onToggleMetric: (BodyMetric) -> Unit = { metric ->
        val next = metricasActivas.toMutableSet()
        if (metric in metricasActivas && next.size > 1) {
            next.remove(metric)
        } else {
            next.add(metric)
        }
        metricasActivas = next
    }

    when {
        windowSize.isExpanded -> {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProgressHeaderSection(onAddClick = { abrirFormulario(null) })
                    if (uiState.mensajeFeedback != null) {
                        ProgressFeedbackCard(message = uiState.mensajeFeedback!!.asString())
                    }
                    ProgressMetricsFilterSection(
                        metricasActivas = metricasActivas,
                        onToggleMetric = onToggleMetric
                    )
                    BodyMetricsChartCard(
                        mediciones = medicionesOrdenadas,
                        metricasActivas = metricasActivas,
                        chartHeight = 220.dp
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (uiState.notasProgreso.isEmpty()) {
                        item {
                            ProgressEmptyCard()
                        }
                    } else {
                        item {
                            Text(
                                text = stringResource(R.string.progress_history_title, uiState.notasProgreso.size),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(uiState.notasProgreso, key = { it.id }) { nota ->
                            ProgressNoteCard(
                                nota = nota,
                                onEdit = { abrirFormulario(nota) },
                                onDelete = { viewModel.eliminarNotaProgreso(nota) }
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
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ProgressHeaderSection(onAddClick = { abrirFormulario(null) })
                }
                if (uiState.mensajeFeedback != null) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ProgressFeedbackCard(message = uiState.mensajeFeedback!!.asString())
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    ProgressMetricsFilterSection(
                        metricasActivas = metricasActivas,
                        onToggleMetric = onToggleMetric
                    )
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    BodyMetricsChartCard(
                        mediciones = medicionesOrdenadas,
                        metricasActivas = metricasActivas
                    )
                }
                if (uiState.notasProgreso.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ProgressEmptyCard()
                    }
                } else {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = stringResource(R.string.progress_history_title, uiState.notasProgreso.size),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(uiState.notasProgreso, key = { it.id }) { nota ->
                        ProgressNoteCard(
                            nota = nota,
                            onEdit = { abrirFormulario(nota) },
                            onDelete = { viewModel.eliminarNotaProgreso(nota) }
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    ProgressHeaderSection(onAddClick = { abrirFormulario(null) })
                }
                if (uiState.mensajeFeedback != null) {
                    item {
                        ProgressFeedbackCard(message = uiState.mensajeFeedback!!.asString())
                    }
                }
                item {
                    ProgressMetricsFilterSection(
                        metricasActivas = metricasActivas,
                        onToggleMetric = onToggleMetric
                    )
                }
                item {
                    BodyMetricsChartCard(
                        mediciones = medicionesOrdenadas,
                        metricasActivas = metricasActivas
                    )
                }
                if (uiState.notasProgreso.isEmpty()) {
                    item {
                        ProgressEmptyCard()
                    }
                } else {
                    item {
                        Text(
                            text = stringResource(R.string.progress_history_title, uiState.notasProgreso.size),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(uiState.notasProgreso, key = { it.id }) { nota ->
                        ProgressNoteCard(
                            nota = nota,
                            onEdit = { abrirFormulario(nota) },
                            onDelete = { viewModel.eliminarNotaProgreso(nota) }
                        )
                    }
                }
            }
        }
    }

    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = { mostrarFormulario = false },
            title = {
                Text(
                    if (notaEditando == null) {
                        stringResource(R.string.progress_dialog_new)
                    } else {
                        stringResource(R.string.progress_dialog_edit)
                    }
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = pesoCorporal,
                        onValueChange = { pesoCorporal = filterDecimalInput(it) },
                        label = { Text(stringResource(R.string.progress_field_body_weight)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = indiceGrasa,
                        onValueChange = { indiceGrasa = filterDecimalInput(it) },
                        label = { Text(stringResource(R.string.progress_field_fat_pct)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = masaMuscular,
                        onValueChange = { masaMuscular = filterDecimalInput(it) },
                        label = { Text(stringResource(R.string.progress_field_muscle_mass)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = imc,
                        onValueChange = { imc = filterDecimalInput(it) },
                        label = { Text(stringResource(R.string.progress_field_bmi)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text(stringResource(R.string.progress_field_comment)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarFormulario = false
                        viewModel.guardarMedicionCorporal(
                            idExistente = notaEditando?.id,
                            pesoCorporal = pesoCorporal,
                            indiceGrasa = indiceGrasa,
                            masaMuscular = masaMuscular,
                            imc = imc,
                            comentario = comentario
                        )
                    }
                ) {
                    Text(stringResource(R.string.btn_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFormulario = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

@Composable
private fun ProgressHeaderSection(onAddClick: () -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.progress_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onAddClick) {
                Text(stringResource(R.string.progress_add_btn))
            }
        }
        Text(
            text = stringResource(R.string.progress_body_subtitle),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ProgressFeedbackCard(message: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            fontWeight = FontWeight.SemiBold
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProgressMetricsFilterSection(
    metricasActivas: Set<BodyMetric>,
    onToggleMetric: (BodyMetric) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = stringResource(R.string.progress_select_metrics_label),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BodyMetric.entries.forEach { metric ->
                val selected = metric in metricasActivas
                FilterChip(
                    selected = selected,
                    onClick = { onToggleMetric(metric) },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(metric.color)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = metric.color.copy(alpha = 0.16f),
                        selectedLabelColor = metric.color
                    ),
                    label = { Text(stringResource(metric.labelRes)) }
                )
            }
        }
    }
}

@Composable
private fun ProgressEmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Text(
            text = stringResource(R.string.progress_body_empty),
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun ProgressNoteCard(
    nota: ProgressNoteEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = nota.fecha, fontWeight = FontWeight.Bold)
                if (nota.titulo.isNotBlank()) {
                    Text(
                        text = nota.titulo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = stringResource(
                    R.string.progress_body_stats,
                    nota.pesoCorporalKg,
                    nota.indiceGrasaPct,
                    nota.masaMuscularKg,
                    nota.imc
                ),
                style = MaterialTheme.typography.bodyMedium
            )
            if (nota.comentario.isNotBlank()) {
                Text(
                    text = stringResource(R.string.progress_note_prefix, nota.comentario),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onEdit) {
                    Text(stringResource(R.string.btn_edit))
                }
                TextButton(onClick = onDelete) {
                    Text(
                        text = stringResource(R.string.btn_delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun BodyMetricsChartCard(
    mediciones: List<ProgressNoteEntity>,
    metricasActivas: Set<BodyMetric>,
    chartHeight: Dp = 190.dp
) {
    val activeList = BodyMetric.entries.filter { it in metricasActivas }
    val multiMetricMode = activeList.size > 1

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.progress_chart_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (multiMetricMode) {
                    Text(
                        text = stringResource(R.string.progress_chart_normalized_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (mediciones.isEmpty()) {
                Text(
                    text = stringResource(R.string.progress_chart_no_data),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(chartHeight)
                        .padding(vertical = 6.dp)
                ) {
                    val padH = 16.dp.toPx()
                    val padV = 14.dp.toPx()
                    val plotW = (size.width - padH * 2).coerceAtLeast(1f)
                    val plotH = (size.height - padV * 2).coerceAtLeast(1f)

                    val gridColor = Color(0xFFE5E7EB)
                    for (i in 0..4) {
                        val y = padV + (plotH * i / 4f)
                        drawLine(
                            color = gridColor,
                            start = Offset(padH, y),
                            end = Offset(padH + plotW, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    activeList.forEach { metric ->
                        val points = mediciones.mapIndexedNotNull { idx, note ->
                            val value = metric.extract(note)
                            if (value > 0.0) idx to value else null
                        }
                        if (points.isEmpty()) return@forEach

                        val minVal = points.minOf { it.second }
                        val maxVal = points.maxOf { it.second }
                        val span = (maxVal - minVal).takeIf { it > 0.0001 } ?: 1.0

                        val offsets = points.map { (idx, value) ->
                            val xRatio = if (mediciones.size <= 1) {
                                0.5f
                            } else {
                                idx.toFloat() / (mediciones.size - 1).toFloat()
                            }
                            val yNorm = if (maxVal == minVal) {
                                0.5f
                            } else {
                                ((value - minVal) / span).toFloat()
                            }
                            Offset(
                                x = padH + xRatio * plotW,
                                y = padV + plotH * (1f - yNorm)
                            )
                        }

                        if (offsets.size >= 2) {
                            val path = Path().apply {
                                moveTo(offsets.first().x, offsets.first().y)
                                for (i in 1 until offsets.size) {
                                    lineTo(offsets[i].x, offsets[i].y)
                                }
                            }
                            drawPath(
                                path = path,
                                color = metric.color,
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }

                        offsets.forEach { pt ->
                            drawCircle(
                                color = Color.White,
                                radius = 5.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = metric.color,
                                radius = 3.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = mediciones.first().fecha,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (mediciones.size > 2) {
                        Text(
                            text = mediciones[mediciones.size / 2].fecha,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (mediciones.size > 1) {
                        Text(
                            text = mediciones.last().fecha,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    activeList.forEach { metric ->
                        val values = mediciones.map { metric.extract(it) }.filter { it > 0.0 }
                        if (values.isNotEmpty()) {
                            val latest = values.last()
                            val diff = if (values.size >= 2) latest - values.first() else 0.0
                            val diffSign = if (diff >= 0) "+" else ""
                            val unitSuffix = if (metric.unit.isNotBlank()) " ${metric.unit}" else ""
                            val summaryText = if (values.size >= 2) {
                                "${String.format(Locale.US, "%.1f", latest)}$unitSuffix ($diffSign${String.format(Locale.US, "%.1f", diff)}$unitSuffix)"
                            } else {
                                "${String.format(Locale.US, "%.1f", latest)}$unitSuffix"
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(metric.color)
                                    )
                                    Text(
                                        text = stringResource(metric.labelRes),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Text(
                                    text = summaryText,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = metric.color
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun filterDecimalInput(input: String): String {
    val clean = input.replace(',', '.')
    val sb = StringBuilder()
    var hasDot = false
    var decimals = 0
    for (ch in clean) {
        if (ch.isDigit()) {
            if (!hasDot || decimals < 2) {
                sb.append(ch)
                if (hasDot) decimals++
            }
        } else if (ch == '.' && !hasDot) {
            if (sb.isEmpty()) sb.append('0')
            sb.append('.')
            hasDot = true
        }
    }
    return sb.toString()
}
