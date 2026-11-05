package com.example.proyectogym.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectogym.data.local.entity.MediaAssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MediaAssetDao {
    @Query("SELECT * FROM media_assets WHERE ownerId = :ownerId AND ownerType = :ownerType ORDER BY id DESC")
    fun observeMediaByOwner(ownerId: Int, ownerType: String): Flow<List<MediaAssetEntity>>

    @Query("SELECT * FROM media_assets ORDER BY id DESC")
    fun observeAllMedia(): Flow<List<MediaAssetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaAsset(mediaAsset: MediaAssetEntity): Long

    @Update
    suspend fun updateMediaAsset(mediaAsset: MediaAssetEntity)

    @Delete
    suspend fun deleteMediaAsset(mediaAsset: MediaAssetEntity)
}
