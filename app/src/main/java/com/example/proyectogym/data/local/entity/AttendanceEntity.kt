package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendances")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val usuarioNombre: String,
    val servicioNombre: String,
    val fecha: String,
    val hora: String,
    val origen: String // "MANUAL", "CHECKIN_APP"
)
