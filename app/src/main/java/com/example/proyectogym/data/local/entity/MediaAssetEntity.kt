package com.example.proyectogym.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_assets")
data class MediaAssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val ownerId: Int,
    val ownerType: String,
    val uriLocal: String,
    val urlRemota: String = "",
    val mimeType: String,
    val descripcion: String,
    val fechaRegistro: String
)
