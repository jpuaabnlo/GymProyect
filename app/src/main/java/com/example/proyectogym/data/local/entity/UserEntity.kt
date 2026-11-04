package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val email: String,
    val password: String,
    val telefono: String,
    val rol: String, // "CLIENTE", "ENTRENADOR", "ADMIN"
    val objetivo: String = "",
    val horarioHabitual: String = "No definido",
    val fechaAlta: String = "2026-10-01",
    val coachId: Int? = null
)
