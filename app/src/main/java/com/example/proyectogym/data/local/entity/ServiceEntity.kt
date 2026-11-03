package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val tipo: String, // "MENSUAL", "PERSONALIZADO", "CLASE", "VISITA"
    val precio: Double,
    val duracionDias: Int,
    val horario: String,
    val activo: Boolean = true
)
