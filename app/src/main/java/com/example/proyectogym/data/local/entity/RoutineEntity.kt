package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val coachId: Int,
    val clienteId: Int? = null,
    val clienteNombre: String = "Sin asignar",
    val nombre: String,
    val objetivo: String,
    val diasSemana: String
)
