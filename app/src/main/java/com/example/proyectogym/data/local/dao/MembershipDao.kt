package com.example.proyectogym.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyectogym.data.local.entity.AttendanceEntity
import com.example.proyectogym.data.local.entity.MembershipEntity
import com.example.proyectogym.data.local.entity.ServiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MembershipDao {
    // --- Servicios ---
    @Query("SELECT * FROM services ORDER BY nombre ASC")
    fun observeAllServices(): Flow<List<ServiceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceEntity): Long

    @Update
    suspend fun updateService(service: ServiceEntity)

    @Delete
    suspend fun deleteService(service: ServiceEntity)

    // --- Membresías / Reinscripciones ---
    @Query("SELECT * FROM memberships ORDER BY diasRestantes ASC")
    fun observeAllMemberships(): Flow<List<MembershipEntity>>

    @Query("SELECT * FROM memberships WHERE userId = :userId ORDER BY id DESC LIMIT 1")
    fun observeLatestMembershipForUser(userId: Int): Flow<MembershipEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembership(membership: MembershipEntity): Long

    @Update
    suspend fun updateMembership(membership: MembershipEntity)

    @Delete
    suspend fun deleteMembership(membership: MembershipEntity)

    // --- Asistencias ---
    @Query("SELECT * FROM attendances WHERE userId = :userId ORDER BY id DESC")
    fun observeAttendancesByUser(userId: Int): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendances ORDER BY id DESC")
    fun observeAllAttendances(): Flow<List<AttendanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendance(attendance: AttendanceEntity): Long

    @Delete
    suspend fun deleteAttendance(attendance: AttendanceEntity)
}
