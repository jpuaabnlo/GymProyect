package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val adminId: Int,
    val resumenProductos: String,
    val total: Double,
    val metodoPago: String,
    val fecha: String
)
