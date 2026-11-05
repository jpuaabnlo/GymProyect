package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memberships")
data class MembershipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val clienteNombre: String,
    val serviceId: Int,
    val servicioNombre: String,
    val montoPagado: Double,
    val metodoPago: String, // "EFECTIVO", "TARJETA", "TRANSFERENCIA"
    val fechaInicio: String,
    val fechaVencimiento: String,
    val diasRestantes: Int,
    val estado: String // "ACTIVA", "POR_VENCER", "VENCIDA"
)
