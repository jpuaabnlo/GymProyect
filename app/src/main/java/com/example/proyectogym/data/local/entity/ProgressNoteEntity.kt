package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress_notes")
data class ProgressNoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val titulo: String,
    val pesoCorporalKg: Double,
    val pesoLevantadoKg: Double = 0.0,
    val repeticionesLogradas: Int = 0,
    val indiceGrasaPct: Double = 0.0,
    val masaMuscularKg: Double = 0.0,
    val imc: Double = 0.0,
    val comentario: String,
    val fecha: String
)
