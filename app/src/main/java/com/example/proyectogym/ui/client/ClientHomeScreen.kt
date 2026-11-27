package com.example.proyectogym.ui.client

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectogym.R
import com.example.proyectogym.ui.AppViewModelProvider
import com.example.proyectogym.ui.components.QrCodeGraphic
import com.example.proyectogym.ui.components.asString
import com.example.proyectogym.ui.utils.LocalWindowWidthSizeClass
import com.example.proyectogym.ui.utils.isExpanded
import com.example.proyectogym.ui.utils.isMedium

@Composable
fun ClientHomeScreen(
    userId: Int,
    onIrARutina: () -> Unit,
    windowSize: WindowWidthSizeClass = LocalWindowWidthSizeClass.current,
    viewModel: ClientViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(userId) {
        viewModel.cargarCliente(userId)
    }

    val uiState by viewModel.uiState.collectAsState()
    var mostrarQrDialog by rememberSaveable { mutableStateOf(false) }

    val nombreSocio = uiState.cliente?.nombre ?: stringResource(R.string.client_default_member)
    val objetivo = uiState.cliente?.objetivo?.ifBlank { stringResource(R.string.client_default_goal) }
        ?: stringResource(R.string.client_default_goal)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .then(if (windowSize.isExpanded) Modifier.widthIn(max = 1200.dp) else Modifier)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.client_hello, nombreSocio),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.client_current_goal, objetivo),
                    style = MaterialTheme.typography.bodyMedium
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

            when {
                windowSize.isExpanded -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                MembershipStatusCard(
                                    uiState = uiState,
                                    onShowQr = { mostrarQrDialog = true }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                AssignedRoutineCard(
                                    uiState = uiState,
                                    onIrARutina = onIrARutina
                                )
                            }
                        }
                    }
                    item {
                        ActivitySummaryCard(uiState = uiState)
                    }
                }

                windowSize.isMedium -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                MembershipStatusCard(
                                    uiState = uiState,
                                    onShowQr = { mostrarQrDialog = true }
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                AssignedRoutineCard(uiState = uiState, onIrARutina = onIrARutina)
                                ActivitySummaryCard(uiState = uiState)
                            }
                        }
                    }
                }

                else -> {
                    item {
                        MembershipStatusCard(
                            uiState = uiState,
                            onShowQr = { mostrarQrDialog = true }
                        )
                    }
                    item {
                        AssignedRoutineCard(uiState = uiState, onIrARutina = onIrARutina)
                    }
                    item {
                        ActivitySummaryCard(uiState = uiState)
                    }
                }
            }
        }
    }

    if (mostrarQrDialog) {
        val qrCodeValue = "GYM-SOCIO-${uiState.cliente?.id ?: 0}"
        val cooldownHoras = uiState.minutosRestantesCooldown / 60
        val cooldownMins = uiState.minutosRestantesCooldown % 60
        AlertDialog(
            onDismissRequest = { mostrarQrDialog = false },
            title = { Text(stringResource(R.string.client_qr_dialog_title)) },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.client_qr_dialog_msg))
                    QrCodeGraphic(code = qrCodeValue)
                    Text(
                        text = qrCodeValue,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        stringResource(
                            R.string.client_qr_member_label,
                            uiState.cliente?.nombre ?: "-"
                        )
                    )
                    if (!uiState.puedeRegistrarAsistencia) {
                        Text(
                            text = stringResource(
                                R.string.client_checkin_cooldown,
                                cooldownHoras,
                                cooldownMins
                            ),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.registrarCheckInHoy()
                        mostrarQrDialog = false
                    },
                    enabled = uiState.puedeRegistrarAsistencia
                ) {
                    Text(stringResource(R.string.client_qr_confirm_btn))
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarQrDialog = false }) {
                    Text(stringResource(R.string.btn_close))
                }
            }
        )
    }
}

@Composable
private fun MembershipStatusCard(
    uiState: ClientUiState,
    onShowQr: () -> Unit
) {
    val membresia = uiState.membresiaActual
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.client_membership_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (membresia != null) {
                Text(text = stringResource(R.string.client_service_label, membresia.servicioNombre))
                Text(
                    text = stringResource(
                        R.string.client_expires_label,
                        membresia.fechaVencimiento,
                        membresia.estado
                    )
                )
                Text(text = stringResource(R.string.client_days_left, membresia.diasRestantes))
                LinearProgressIndicator(
                    progress = { (membresia.diasRestantes / 30f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(stringResource(R.string.client_no_membership))
            }

            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onShowQr,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(20.dp)
                )
                Text(stringResource(R.string.client_qr_confirm_btn))
            }
            if (!uiState.puedeRegistrarAsistencia) {
                val horas = uiState.minutosRestantesCooldown / 60
                val mins = uiState.minutosRestantesCooldown % 60
                Text(
                    text = stringResource(R.string.client_checkin_cooldown, horas, mins),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AssignedRoutineCard(
    uiState: ClientUiState,
    onIrARutina: () -> Unit
) {
    val rutina = uiState.rutinaDeHoy ?: uiState.rutinaActiva
    val completados = uiState.ejerciciosRutina.count { it.completado }
    val totalEjercicios = uiState.ejerciciosRutina.size

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.client_my_routine_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (rutina != null) {
                Text(text = stringResource(R.string.client_routine_summary, rutina.nombre, rutina.objetivo))
                Text(text = stringResource(R.string.client_routine_days, rutina.diasSemana))
                if (uiState.rutinaDeHoy != null) {
                    Text(text = stringResource(R.string.client_routine_progress, completados, totalEjercicios))
                } else {
                    Text(
                        text = stringResource(R.string.routine_none_today_hint, uiState.diaActualNombre),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Button(onClick = onIrARutina) {
                    Text(stringResource(R.string.client_go_to_routine))
                }
            } else {
                Text(stringResource(R.string.client_no_routine))
            }
        }
    }
}

@Composable
private fun ActivitySummaryCard(uiState: ClientUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.client_activity_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(text = stringResource(R.string.client_total_attendances, uiState.asistencias.size))
            Text(text = stringResource(R.string.client_total_notes, uiState.notasProgreso.size))
        }
    }
}
