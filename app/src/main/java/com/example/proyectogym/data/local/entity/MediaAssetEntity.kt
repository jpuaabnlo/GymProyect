package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo preparado para Multimedia (fotos de perfil, fotos de progreso físico,
 * imágenes/videos demostrativos de ejercicios e imágenes de productos).
 */
@Entity(tableName = "media_assets")
data class MediaAssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ownerId: Int,
    val ownerType: String, // "PERFIL", "PROGRESO", "EJERCICIO", "PRODUCTO"
    val uriLocal: String,
    val urlRemota: String = "",
    val mimeType: String, // "image/jpeg", "image/png", "video/mp4"
    val descripcion: String,
    val fechaRegistro: String
)
