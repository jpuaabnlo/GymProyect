package com.example.proyectogym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyectogym.R
import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.ui.theme.GymGreen
import com.example.proyectogym.ui.theme.GymGreenDark
import com.example.proyectogym.ui.theme.HeatmapActiveGreen
import com.example.proyectogym.ui.theme.HeatmapEmptyGray
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private data class HeatmapDayCell(
    val dateKey: String,
    val dayNumber: Int,
    val isToday: Boolean,
    val isFuture: Boolean
)

private data class HeatmapMonthData(
    val title: String,
    val leadingEmptySlots: Int,
    val days: List<HeatmapDayCell>
)

@Composable
fun AttendanceHeatmapCalendar(
    asistencias: List<AttendanceEntity>,
    modifier: Modifier = Modifier
) {
    val asistenciasPorFecha = remember(asistencias) {
        asistencias.groupBy { it.fecha }
    }

    val months = remember { buildLastTwoMonths() }
    val todayKey = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().time)
    }
    var selectedDateKey by remember { mutableStateOf<String?>(todayKey) }

    val weekDayLabels = listOf("L", "M", "X", "J", "V", "S", "D")

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                months.forEach { month ->
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = month.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GymGreenDark
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            weekDayLabels.forEach { dayLabel ->
                                Text(
                                    text = dayLabel,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        val totalSlots = month.leadingEmptySlots + month.days.size
                        val rowCount = (totalSlots + 6) / 7
                        for (row in 0 until rowCount) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                for (col in 0 until 7) {
                                    val slotIndex = row * 7 + col
                                    val dayIndex = slotIndex - month.leadingEmptySlots
                                    if (dayIndex in month.days.indices) {
                                        val cell = month.days[dayIndex]
                                        val attended = asistenciasPorFecha.containsKey(cell.dateKey)
                                        val isSelected = selectedDateKey == cell.dateKey

                                        val bgColor = when {
                                            attended -> HeatmapActiveGreen
                                            else -> HeatmapEmptyGray
                                        }
                                        val textColor = if (attended) Color.White else Color(0xFF374151)

                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (cell.isFuture && !attended) HeatmapEmptyGray.copy(alpha = 0.45f) else bgColor)
                                                .then(
                                                    when {
                                                        isSelected -> Modifier.border(2.dp, GymGreenDark, RoundedCornerShape(4.dp))
                                                        cell.isToday -> Modifier.border(1.5.dp, GymGreen, RoundedCornerShape(4.dp))
                                                        else -> Modifier
                                                    }
                                                )
                                                .clickable { selectedDateKey = cell.dateKey },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = cell.dayNumber.toString(),
                                                fontSize = 9.sp,
                                                fontWeight = if (attended || cell.isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = textColor
                                            )
                                        }
                                    } else {
                                        Spacer(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val dateToShow = selectedDateKey
            if (dateToShow != null) {
                val registros = asistenciasPorFecha[dateToShow].orEmpty()
                val detalle = if (registros.isEmpty()) {
                    stringResource(R.string.profile_heatmap_day_absent, dateToShow)
                } else {
                    val horas = registros.joinToString(", ") { "${it.hora} (${it.servicioNombre})" }
                    stringResource(R.string.profile_heatmap_day_present, dateToShow, horas)
                }
                Text(
                    text = detalle,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (registros.isNotEmpty()) GymGreenDark else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(2.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HeatmapLegendItem(
                    color = HeatmapEmptyGray,
                    label = stringResource(R.string.profile_heatmap_legend_absent)
                )
                HeatmapLegendItem(
                    color = HeatmapActiveGreen,
                    label = stringResource(R.string.profile_heatmap_legend_present)
                )
            }
        }
    }
}

@Composable
private fun HeatmapLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun buildLastTwoMonths(): List<HeatmapMonthData> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val monthTitleFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val todayCal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val todayMillis = todayCal.timeInMillis
    val todayKey = dateFormat.format(todayCal.time)

    return listOf(-1, 0).map { offset ->
        val monthCal = (todayCal.clone() as Calendar).apply {
            set(Calendar.DAY_OF_MONTH, 1)
            add(Calendar.MONTH, offset)
        }
        val firstDayOfWeek = monthCal.get(Calendar.DAY_OF_WEEK)
        // Convertir Calendar.SUNDAY(1)..SATURDAY(7) -> Lunes(0)..Domingo(6)
        val leadingEmpty = (firstDayOfWeek + 5) % 7
        val maxDay = monthCal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val days = (1..maxDay).map { day ->
            val dayCal = (monthCal.clone() as Calendar).apply {
                set(Calendar.DAY_OF_MONTH, day)
            }
            val key = dateFormat.format(dayCal.time)
            HeatmapDayCell(
                dateKey = key,
                dayNumber = day,
                isToday = key == todayKey,
                isFuture = dayCal.timeInMillis > todayMillis
            )
        }

        val rawTitle = monthTitleFormat.format(monthCal.time)
        val formattedTitle = rawTitle.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        HeatmapMonthData(
            title = formattedTitle,
            leadingEmptySlots = leadingEmpty,
            days = days
        )
    }
}
