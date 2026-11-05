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
    val pesoLevantadoKg: Double,
    val repeticionesLogradas: Int,
    val comentario: String,
    val fecha: String
)
