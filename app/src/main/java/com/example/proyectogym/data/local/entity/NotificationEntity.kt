package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val titulo: String,
    val mensaje: String,
    val horaProgramada: String,
    val tipo: String,
    val activa: Boolean = true,
    val leida: Boolean = false,
    val fechaCreacion: String
)
