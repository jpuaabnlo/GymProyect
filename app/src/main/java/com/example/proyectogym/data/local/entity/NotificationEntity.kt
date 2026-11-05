package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo preparado para Notificaciones locales / push (recordatorios de entrenamiento,
 * avisos de vencimiento de membresía y asignación de nueva rutina).
 */
@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val titulo: String,
    val mensaje: String,
    val horaProgramada: String, // Ej. "17:30"
    val tipo: String, // "RECORDATORIO_ENTRENO", "VENCIMIENTO_MEMBRESIA", "NUEVA_RUTINA"
    val activa: Boolean = true,
    val leida: Boolean = false,
    val fechaCreacion: String
)
