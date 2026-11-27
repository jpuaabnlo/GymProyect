package com.example.proyectogym.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.MembershipEntity
import com.example.proyectogym.data.local.entity.ServiceEntity
import com.example.proyectogym.data.local.entity.UserEntity
import com.example.proyectogym.data.repository.AttendanceCooldownHelper
import com.example.proyectogym.ui.AppViewModelProvider
import com.example.proyectogym.ui.components.asString
import com.example.proyectogym.ui.theme.GymGreenDark
import com.example.proyectogym.ui.theme.GymGreenLight
import com.example.proyectogym.ui.theme.NavSelectedLightGray
import com.example.proyectogym.ui.utils.LocalWindowWidthSizeClass
import com.example.proyectogym.ui.utils.isExpanded
import com.example.proyectogym.ui.utils.isMedium
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

enum class AttendanceRangeFilter(val labelRes: Int) {
    TODAY(R.string.admin_range_today),
    DAYS_7(R.string.admin_range_7d),
    DAYS_31(R.string.admin_range_31d),
    MONTHS_3(R.string.admin_range_3m),
    YEAR_1(R.string.admin_range_1y)
}

@Composable
fun AdminDashboardScreen(
    windowSize: WindowWidthSizeClass = LocalWindowWidthSizeClass.current,
    viewModel: AdminViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    var rangoSeleccionado by rememberSaveable { mutableStateOf(AttendanceRangeFilter.DAYS_31) }

    val totalVentasMonto = uiState.ventas.sumOf { it.total }
    val porVencer = uiState.membresias.filter { it.diasRestantes <= 7 }
    val asistenciasFiltradas = remember(uiState.asistencias, rangoSeleccionado) {
        filterAttendancesByRange(uiState.asistencias, rangoSeleccionado)
    }
    val asistenciasPorServicio = remember(asistenciasFiltradas, uiState.servicios) {
        buildServiceAttendanceRows(asistenciasFiltradas, uiState.servicios)
    }
    val clientes = uiState.usuarios.filter { it.rol == "CLIENTE" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.admin_dashboard_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
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
                        text = uiState.mensajeFeedback!!.asString(),
                        modifier = Modifier.padding(12.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IndicadorCard(
                    titulo = stringResource(R.string.admin_kpi_attendances),
                    valor = "${uiState.asistencias.size}",
                    modifier = Modifier.weight(1f)
                )
                IndicadorCard(
                    titulo = stringResource(R.string.admin_kpi_sales),
                    valor = "$${totalVentasMonto.toInt()}",
                    modifier = Modifier.weight(1f)
                )
                IndicadorCard(
                    titulo = stringResource(R.string.admin_kpi_expiring),
                    valor = "${porVencer.size}",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        when {
            windowSize.isExpanded -> {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MembershipsStatusColumn(membresias = uiState.membresias)
                        }

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AttendanceByServiceSection(
                                asistenciasPorServicio = asistenciasPorServicio,
                                rangoSeleccionado = rangoSeleccionado,
                                onSelectRango = { rangoSeleccionado = it }
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                            QuickCheckinColumn(
                                clientes = clientes,
                                membresias = uiState.membresias,
                                asistencias = uiState.asistencias,
                                onCheckIn = { cliente, servicio ->
                                    viewModel.registrarAsistenciaManual(
                                        usuario = cliente,
                                        servicioNombre = servicio
                                    )
                                }
                            )
                        }
                    }
                }
            }

            windowSize.isMedium -> {
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    AttendanceByServiceSection(
                        asistenciasPorServicio = asistenciasPorServicio,
                        rangoSeleccionado = rangoSeleccionado,
                        onSelectRango = { rangoSeleccionado = it }
                    )
                }

                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MembershipsStatusColumn(membresias = uiState.membresias)
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            QuickCheckinColumn(
                                clientes = clientes,
                                membresias = uiState.membresias,
                                asistencias = uiState.asistencias,
                                onCheckIn = { cliente, servicio ->
                                    viewModel.registrarAsistenciaManual(
                                        usuario = cliente,
                                        servicioNombre = servicio
                                    )
                                }
                            )
                        }
                    }
                }
            }

            else -> {
                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = stringResource(R.string.admin_memberships_section),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (uiState.membresias.isEmpty()) {
                    item {
                        Text(stringResource(R.string.admin_no_memberships))
                    }
                } else {
                    items(uiState.membresias, key = { "memb_${it.id}" }) { m ->
                        MembershipStatusRowCard(m)
                    }
                }

                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    AttendanceByServiceSection(
                        asistenciasPorServicio = asistenciasPorServicio,
                        rangoSeleccionado = rangoSeleccionado,
                        onSelectRango = { rangoSeleccionado = it }
                    )
                }

                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    Text(
                        text = stringResource(R.string.admin_quick_checkin),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(clientes, key = { "checkin_${it.id}" }) { cliente ->
                    val memb = uiState.membresias.firstOrNull { it.userId == cliente.id }
                    val cooldownMin = AttendanceCooldownHelper.remainingCooldownMinutes(
                        uiState.asistencias.filter { it.userId == cliente.id }
                    )
                    QuickCheckinCard(
                        cliente = cliente,
                        servicioNombre = memb?.servicioNombre
                            ?: stringResource(R.string.admin_general_visit),
                        cooldownMinutos = cooldownMin,
                        onCheckIn = {
                            viewModel.registrarAsistenciaManual(
                                usuario = cliente,
                                servicioNombre = memb?.servicioNombre ?: "Visita General"
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MembershipsStatusColumn(membresias: List<MembershipEntity>) {
    Text(
        text = stringResource(R.string.admin_memberships_section),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    if (membresias.isEmpty()) {
        Text(stringResource(R.string.admin_no_memberships))
    } else {
        membresias.forEach { m ->
            MembershipStatusRowCard(m)
        }
    }
}

@Composable
private fun QuickCheckinColumn(
    clientes: List<UserEntity>,
    membresias: List<MembershipEntity>,
    asistencias: List<AttendanceEntity>,
    onCheckIn: (UserEntity, String) -> Unit
) {
    Text(
        text = stringResource(R.string.admin_quick_checkin),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
    clientes.forEach { cliente ->
        val memb = membresias.firstOrNull { it.userId == cliente.id }
        val cooldownMin = AttendanceCooldownHelper.remainingCooldownMinutes(
            asistencias.filter { it.userId == cliente.id }
        )
        QuickCheckinCard(
            cliente = cliente,
            servicioNombre = memb?.servicioNombre
                ?: stringResource(R.string.admin_general_visit),
            cooldownMinutos = cooldownMin,
            onCheckIn = {
                onCheckIn(cliente, memb?.servicioNombre ?: "Visita General")
            }
        )
    }
}

@Composable
private fun MembershipStatusRowCard(m: MembershipEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (m.diasRestantes <= 7) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = m.clienteNombre, fontWeight = FontWeight.Bold)
                Text(
                    text = stringResource(
                        R.string.admin_membership_expires,
                        m.servicioNombre,
                        m.fechaVencimiento
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(
                text = stringResource(R.string.admin_days_badge, m.diasRestantes),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AttendanceByServiceSection(
    asistenciasPorServicio: List<Pair<String, Int>>,
    rangoSeleccionado: AttendanceRangeFilter,
    onSelectRango: (AttendanceRangeFilter) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.admin_attendance_by_service),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AttendanceRangeFilter.entries.forEach { range ->
                FilterChip(
                    selected = rangoSeleccionado == range,
                    onClick = { onSelectRango(range) },
                    label = { Text(stringResource(range.labelRes)) }
                )
            }
        }

        if (asistenciasPorServicio.isEmpty()) {
            Text(stringResource(R.string.admin_no_attendances))
        } else {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, NavSelectedLightGray, RoundedCornerShape(10.dp)),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .background(GymGreenLight),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.admin_table_col_service),
                            fontWeight = FontWeight.Bold,
                            color = GymGreenDark,
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier
                                .weight(0.68f)
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                        VerticalDivider(
                            color = NavSelectedLightGray,
                            modifier = Modifier.fillMaxHeight()
                        )
                        Text(
                            text = stringResource(R.string.admin_table_col_count),
                            fontWeight = FontWeight.Bold,
                            color = GymGreenDark,
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(0.32f)
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        )
                    }

                    asistenciasPorServicio.forEach { (servicio, conteo) ->
                        HorizontalDivider(color = NavSelectedLightGray)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = servicio,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .weight(0.68f)
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                            VerticalDivider(
                                color = NavSelectedLightGray,
                                modifier = Modifier.fillMaxHeight()
                            )
                            Text(
                                text = conteo.toString(),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .weight(0.32f)
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun filterAttendancesByRange(
    asistencias: List<AttendanceEntity>,
    range: AttendanceRangeFilter
): List<AttendanceEntity> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val startCal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        when (range) {
            AttendanceRangeFilter.TODAY -> Unit
            AttendanceRangeFilter.DAYS_7 -> add(Calendar.DAY_OF_YEAR, -6)
            AttendanceRangeFilter.DAYS_31 -> add(Calendar.DAY_OF_YEAR, -30)
            AttendanceRangeFilter.MONTHS_3 -> add(Calendar.MONTH, -3)
            AttendanceRangeFilter.YEAR_1 -> add(Calendar.YEAR, -1)
        }
    }
    val startMillis = startCal.timeInMillis

    return asistencias.filter { item ->
        val millis = runCatching { dateFormat.parse(item.fecha)?.time }.getOrNull() ?: return@filter false
        millis >= startMillis
    }
}

private fun buildServiceAttendanceRows(
    asistenciasFiltradas: List<AttendanceEntity>,
    servicios: List<ServiceEntity>
): List<Pair<String, Int>> {
    val conteos = linkedMapOf<String, Int>()
    servicios.forEach { serv ->
        conteos[serv.nombre] = 0
    }
    asistenciasFiltradas.forEach { att ->
        conteos[att.servicioNombre] = (conteos[att.servicioNombre] ?: 0) + 1
    }
    return conteos.entries
        .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
        .map { it.key to it.value }
}

@Composable
private fun QuickCheckinCard(
    cliente: UserEntity,
    servicioNombre: String,
    cooldownMinutos: Long,
    onCheckIn: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = cliente.nombre, fontWeight = FontWeight.SemiBold)
                Text(
                    text = servicioNombre,
                    style = MaterialTheme.typography.bodySmall
                )
                if (cooldownMinutos > 0L) {
                    val horas = cooldownMinutos / 60
                    val mins = cooldownMinutos % 60
                    Text(
                        text = stringResource(R.string.admin_checkin_available_in, horas, mins),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            OutlinedButton(
                onClick = onCheckIn,
                enabled = cooldownMinutos == 0L
            ) {
                Text(stringResource(R.string.admin_checkin_btn))
            }
        }
    }
}

@Composable
private fun IndicadorCard(
    titulo: String,
    valor: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
