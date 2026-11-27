package com.example.proyectogym.data.repository

import com.example.proyectogym.data.local.entity.AttendanceEntity
import java.text.Normalizer
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

const val ATTENDANCE_COOLDOWN_MILLIS = 5L * 60L * 60L * 1000L

sealed class AttendanceRegistrationResult {
    data class Success(val hora: String) : AttendanceRegistrationResult()
    data class CooldownActive(val remainingMinutes: Long) : AttendanceRegistrationResult()
}

object AttendanceCooldownHelper {

    fun remainingCooldownMinutes(
        asistencias: List<AttendanceEntity>,
        nowMillis: Long = System.currentTimeMillis()
    ): Long {
        if (asistencias.isEmpty()) return 0L
        val fmtWithSeconds = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        val fmtMinutes = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)

        val validMillis = asistencias.mapNotNull { item ->
            val raw = "${item.fecha.trim()} ${item.hora.trim()}"
            val parsed = runCatching { fmtWithSeconds.parse(raw)?.time }.getOrNull()
                ?: runCatching { fmtMinutes.parse(raw)?.time }.getOrNull()
            parsed?.takeIf { it <= nowMillis + 120_000L }
        }

        val lastMillis = validMillis.maxOrNull() ?: return 0L
        val elapsed = (nowMillis - lastMillis).coerceAtLeast(0L)
        if (elapsed >= ATTENDANCE_COOLDOWN_MILLIS) return 0L

        val remainingMillis = ATTENDANCE_COOLDOWN_MILLIS - elapsed
        return ceil(remainingMillis / 60_000.0).toLong().coerceAtLeast(1L)
    }

    fun getTodayDayOfWeek(): Int = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    fun getTodaySpanishName(dayOfWeek: Int = getTodayDayOfWeek()): String = when (dayOfWeek) {
        Calendar.MONDAY -> "Lunes"
        Calendar.TUESDAY -> "Martes"
        Calendar.WEDNESDAY -> "Miércoles"
        Calendar.THURSDAY -> "Jueves"
        Calendar.FRIDAY -> "Viernes"
        Calendar.SATURDAY -> "Sábado"
        Calendar.SUNDAY -> "Domingo"
        else -> "Lunes"
    }

    fun routineMatchesDay(
        diasSemana: String,
        dayOfWeek: Int = getTodayDayOfWeek()
    ): Boolean {
        val tokens = diasSemana.split(",", "·", "-", "/", ";")
            .map { normalizeText(it) }
            .filter { it.isNotBlank() }

        if (tokens.isEmpty()) return false

        val expectedPairs = when (dayOfWeek) {
            Calendar.MONDAY -> setOf("lunes", "lun", "monday", "mon")
            Calendar.TUESDAY -> setOf("martes", "mar", "tuesday", "tue")
            Calendar.WEDNESDAY -> setOf("miercoles", "mie", "wednesday", "wed")
            Calendar.THURSDAY -> setOf("jueves", "jue", "thursday", "thu")
            Calendar.FRIDAY -> setOf("viernes", "vie", "friday", "fri")
            Calendar.SATURDAY -> setOf("sabado", "sab", "saturday", "sat")
            Calendar.SUNDAY -> setOf("domingo", "dom", "sunday", "sun")
            else -> emptySet()
        }

        return tokens.any { token ->
            expectedPairs.any { expected ->
                token == expected || token.startsWith(expected)
            }
        }
    }

    private fun normalizeText(value: String): String {
        val normalized = Normalizer.normalize(value.trim().lowercase(Locale.US), Normalizer.Form.NFD)
        return normalized.replace(Regex("\\p{InCombiningDiacriticalMarks}+"), "")
    }
}
